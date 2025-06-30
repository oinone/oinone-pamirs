package pro.shushi.pamirs.eip.api.service.impl;

import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractEipApi;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipDistributionSupport;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EipDistributionSupportImpl implements EipDistributionSupport {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private EipZookeeperNodeListener nodeListener;

    private final Map<String /* tenantRootPath */, TreeCache> treeCacheMap = new ConcurrentHashMap<>();

    @Override
    public synchronized void start() throws Exception {
        zookeeperService.start();
        if (!treeCacheMap.isEmpty()) {
            close();
        }
        registerListeners();
    }

    @Override
    public synchronized void close() {
        for (Map.Entry<String, TreeCache> entry : treeCacheMap.entrySet()) {
            entry.getValue().close();
        }
        treeCacheMap.clear();
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
        InterfaceTypeEnum type = null;
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
        byte[] finalData = new byte[2];
        if (DataStatusEnum.ENABLED.equals(dataStatus)) {
            finalData[0] = ENABLED[0];
        } else {
            finalData[0] = DISABLED[0];
        }
        if (Boolean.TRUE.equals(eipInterface.getIsIgnoreLogFrequency())) {
            finalData[1] = ENABLED[0];
        } else {
            finalData[1] = DISABLED[0];
        }
        final String routePath;
        String tenant = PamirsTenantSession.getTenant();
        if (StringUtils.isEmpty(tenant)) {
            routePath = ZOOKEEPER_PARENT_NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + type.value() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
        } else {
            routePath = ZOOKEEPER_PARENT_NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + tenant + CharacterConstants.SEPARATOR_SLASH + type.getValue() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
        }
        try {
            this.zookeeperService.createOrUpdateData(routePath, finalData, DEFAULT_COMPARATOR);
        } catch (Exception e) {
            log.error("{}刷新失败 [interfaceName {}]", interfaceTypeName, interfaceName, e);
            return new Result<String>().error().setData(String.format("%s刷新失败 [interfaceName %s]", interfaceTypeName, interfaceName));
        }
        return new Result<>();
    }

    private void registerListeners() {
        List<String> tenantRootPathList = new ArrayList<>();

        String tenant = PamirsTenantSession.getTenant();
        EipCamelContext context = EipCamelContext.getContext();
        String rootPath;
        if (StringUtils.isEmpty(tenant)) {
            rootPath = EipDistributionSupport.ZOOKEEPER_PARENT_NODE_PATH_PREFIX;
        } else {
            rootPath = EipDistributionSupport.ZOOKEEPER_PARENT_NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + tenant;
        }
        List<RouteDefinition> routeDefinitionList = Optional.ofNullable(context).map(EipCamelContext::getCamelContext).map(ModelCamelContext::getRouteDefinitions).orElse(null);
        if (CollectionUtils.isNotEmpty(routeDefinitionList)) {
            try {
                String routePath;
                for (RouteDefinition routeDefinition : routeDefinitionList) {
                    String interfaceName = routeDefinition.getId();
                    InterfaceTypeEnum interfaceType = EipHelper.getInterfaceType(interfaceName);
                    routePath = rootPath + CharacterConstants.SEPARATOR_SLASH + interfaceType.getValue() + CharacterConstants.SEPARATOR_SLASH + interfaceName;

                    byte[] initialData = new byte[2];
                    initialData[0] = ENABLED[0];
                    initialData[1] = DISABLED[0];
                    this.zookeeperService.createOrUpdateData(routePath, initialData, EipDistributionSupport.DEFAULT_COMPARATOR);
                }
                tenantRootPathList.add(rootPath);
            } catch (Exception e) {
                log.error("Eip 开启分布式支持失败，tenant：[{}]", tenant, e);
            }
        }

        if (CollectionUtils.isNotEmpty(tenantRootPathList)) {
            registerListener(tenantRootPathList);
        }
    }
}
