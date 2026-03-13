package pro.shushi.pamirs.eip.api.service.distribution.impl;

import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.config.EipDistributionSwitchCondition;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractEipApi;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.distribution.EipDistributionSupport;
import pro.shushi.pamirs.eip.api.service.distribution.EipZookeeperNodeListener;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.eip.api.util.EipInitializationUtil;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * EIP 分布式支持
 *
 * @author Adamancy Zhang at 13:04 on 2020-09-27
 */
@Slf4j
@Order(0)
@Service
@Conditional(EipDistributionSwitchCondition.class)
public class EipDistributionSupportImpl implements EipDistributionSupport {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private EipZookeeperNodeListener nodeListener;

    private final Map<String, TreeCache> treeCacheMap = new ConcurrentHashMap<>();

    private final AtomicBoolean isStart = new AtomicBoolean(false);

    @Override
    public boolean isStart() {
        return isStart.get();
    }

    @Override
    public synchronized void start() throws Exception {
        zookeeperService.start();
        if (!treeCacheMap.isEmpty()) {
            close();
        }
        registerListeners();
        isStart.set(true);
    }

    @Override
    public synchronized void close() {
        for (Map.Entry<String, TreeCache> entry : treeCacheMap.entrySet()) {
            entry.getValue().close();
        }
        treeCacheMap.clear();
        isStart.set(false);
    }

    @Override
    public void registerListener(List<String> tenantRootPathList) {
        for (String tenantRootPath : tenantRootPathList) {
            TreeCache treeCache = zookeeperService.registerTreeCache(tenantRootPath, nodeListener);
            if (treeCache != null) {
                treeCacheMap.put(tenantRootPath, treeCache);
            }
        }
    }

    @Override
    public synchronized Result<String> refreshRouteDefinition(EipRouteDefinition eipInterface) {
        return refresh0(eipInterface);
    }

    @Override
    public synchronized Result<String> refreshInterface(EipIntegrationInterface eipInterface) {
        return refresh0(eipInterface);
    }

    @Override
    public synchronized Result<String> refreshOpenInterface(EipOpenInterface eipInterface) {
        return refresh0(eipInterface);
    }

    private Result<String> refresh0(AbstractEipApi eipInterface) {
        String interfaceTypeName;
        InterfaceTypeEnum type;
        if (eipInterface instanceof EipIntegrationInterface) {
            interfaceTypeName = "集成接口";
            type = InterfaceTypeEnum.INTEGRATION;
        } else if (eipInterface instanceof EipOpenInterface) {
            interfaceTypeName = "开放接口";
            type = InterfaceTypeEnum.OPEN;
        } else if (eipInterface instanceof EipRouteDefinition) {
            interfaceTypeName = "路由定义";
            type = InterfaceTypeEnum.ROUTE;
        } else {
            return new Result<String>().error().setData("无法识别的接口类型");
        }
        String interfaceName = eipInterface.getInterfaceName();
        if (!zookeeperService.isEnabled()) {
            return new Result<String>().error().setData(String.format("Zookeeper连接状态异常，无法刷新%s [interfaceName %s]", interfaceTypeName, interfaceName));
        }
        DataStatusEnum dataStatus = eipInterface.getDataStatus();
        if (dataStatus == null) {
            return new Result<String>().error().setData(String.format("无法获取%s状态，刷新失败 [interfaceName %s]", interfaceTypeName, interfaceName));
        }
        byte[] finalData = createData(DataStatusEnum.ENABLED.equals(dataStatus), new Date());
        final String routePath;
        String tenant = PamirsTenantSession.getTenant();
        if (StringUtils.isEmpty(tenant)) {
            routePath = NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + type.value() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
        } else {
            routePath = NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + tenant + CharacterConstants.SEPARATOR_SLASH + type.getValue() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
        }
        try {
            this.zookeeperService.createOrUpdateData(routePath, finalData, this::defaultComparator);
        } catch (Exception e) {
            log.error("{} refresh failed [interfaceName {}]", interfaceTypeName, interfaceName, e);
            return new Result<String>().error().setData(String.format("%s刷新失败 [interfaceName %s]", interfaceTypeName, interfaceName));
        }
        return new Result<>();
    }

    private void registerListeners() {
        String rootPath = getRootPath();

        List<RouteDefinition> routeDefinitionList = Optional.ofNullable(EipCamelContext.getContext()).map(EipCamelContext::getCamelContext).map(ModelCamelContext::getRouteDefinitions).orElse(null);
        if (CollectionUtils.isNotEmpty(routeDefinitionList)) {
            String tenant = PamirsTenantSession.getTenant();
            try {
                for (RouteDefinition routeDefinition : routeDefinitionList) {
                    String routeDefinitionId = routeDefinition.getId();
                    InterfaceTypeEnum interfaceType = EipHelper.getInterfaceType(routeDefinitionId);
                    String interfaceName = EipInitializationUtil.parseInterfaceNameByRouteId(routeDefinitionId);
                    String routePath = rootPath + CharacterConstants.SEPARATOR_SLASH + interfaceType.value() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
                    this.zookeeperService.createOrUpdateData(routePath, ENABLED, this::defaultComparator);
                }
            } catch (Exception e) {
                log.error("Eip failed to enable distributed support, tenant: [{}]", tenant, e);
            }
        }

        registerListener(Collections.singletonList(rootPath));
    }

    private String getRootPath() {
        String tenant = PamirsTenantSession.getTenant();
        String rootPath;
        if (StringUtils.isEmpty(tenant)) {
            rootPath = NODE_PATH_PREFIX;
        } else {
            rootPath = NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + tenant;
        }
        return rootPath;
    }

    private byte[] createData(boolean isEnabled, Date date) {
        byte[] updateFactor = Long.toString(date.getTime()).getBytes();
        byte[] finalData = new byte[1 + updateFactor.length];
        if (isEnabled) {
            finalData[0] = ENABLED[0];
        } else {
            finalData[0] = DISABLED[0];
        }
        System.arraycopy(updateFactor, 0, finalData, 1, updateFactor.length);
        return finalData;
    }

    private boolean defaultComparator(byte[] originData, byte[] data) {
        if (originData == null) {
            return Boolean.TRUE;
        }
        if (data == null) {
            return Boolean.FALSE;
        }
        if (data.length == 1) {
            if (originData[0] == data[0]) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
    }
}
