package pro.shushi.pamirs.eip.api.strategy.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.config.EipDistributionSwitchCondition;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.AbstractEipApi;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.strategy.EipLogStrategy;
import pro.shushi.pamirs.eip.api.strategy.context.EipLogStrategyContext;
import pro.shushi.pamirs.eip.api.strategy.entity.EipLogStrategyEntity;
import pro.shushi.pamirs.eip.api.strategy.listener.EipLogStrategyChangeListener;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyDistributionSupport;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyService;
import pro.shushi.pamirs.framework.common.utils.kryo.KryoUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adamancy Zhang at 10:16 on 2025-08-18
 */
@Slf4j
@Order(10)
@Service
@Conditional(EipDistributionSwitchCondition.class)
public class EipLogStrategyDistributionSupportImpl implements EipLogStrategyDistributionSupport {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private EipLogStrategyChangeListener nodeListener;

    @Autowired
    private EipLogStrategyService eipLogStrategyService;

    private final Map<String, TreeCache> treeCacheMap = new ConcurrentHashMap<>();

    @Override
    public void start() throws Exception {
        zookeeperService.start();
        if (!treeCacheMap.isEmpty()) {
            close();
        }
        registerListeners();
    }

    @Override
    public void close() {
        for (Map.Entry<String, TreeCache> entry : treeCacheMap.entrySet()) {
            entry.getValue().close();
        }
        treeCacheMap.clear();
    }

    @Override
    public Result<String> refreshLogStrategy(EipLogStrategyEntity logStrategy) {
        String interfaceName = logStrategy.getInterfaceName();
        InterfaceTypeEnum type = logStrategy.getInterfaceType();
        final String routePath;
        String tenant = PamirsTenantSession.getTenant();
        if (StringUtils.isEmpty(tenant)) {
            routePath = NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + type.value() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
        } else {
            routePath = NODE_PATH_PREFIX + CharacterConstants.SEPARATOR_SLASH + tenant + CharacterConstants.SEPARATOR_SLASH + type.getValue() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
        }
        try {
            if (log.isInfoEnabled()) {
                log.info("ready refresh log strategy: {}", JSON.toJSONString(logStrategy));
            }
            this.zookeeperService.createOrUpdateData(routePath, createData(logStrategy));
        } catch (Exception e) {
            log.error("刷新失败 [interfaceName {}]", interfaceName, e);
            return new Result<String>().error().setData(String.format("刷新失败 [interfaceName %s]", interfaceName));
        }
        return new Result<>();
    }

    private void registerListener(String rootPath) {
        TreeCache treeCache = zookeeperService.registerTreeCache(rootPath, nodeListener);
        if (treeCache != null) {
            treeCacheMap.put(rootPath, treeCache);
        }
    }

    private void init() {
        List<EipLogStrategy> logStrategies = queryValidLogStrategies();
        EipLogStrategyEntity defaultLogStrategy = EipLogStrategyContext.defaultLogStrategy();
        for (EipLogStrategy logStrategy : logStrategies) {
            String interfaceName = logStrategy.getInterfaceName();
            InterfaceTypeEnum interfaceType = logStrategy.getInterfaceType();
            EipLogStrategyEntity logStrategyEntity = EipLogStrategyContext.getOrCreate(interfaceType, interfaceName);
            logStrategyEntity.setIgnoreLogFrequency(true);
            logStrategyEntity.setFrequency(defaultLogStrategy.getFrequency());
        }
        List<EipIntegrationInterface> integrationInterfaceList = queryValidEnabledLogApi(EipIntegrationInterface.MODEL_MODEL);
        for (EipIntegrationInterface integrationInterface : integrationInterfaceList) {
            EipLogStrategyEntity logStrategyEntity = EipLogStrategyContext.getOrCreate(InterfaceTypeEnum.INTEGRATION, integrationInterface.getInterfaceName());
            logStrategyEntity.setEnabled(true);
        }
        List<EipOpenInterface> openInterfaceList = queryValidEnabledLogApi(EipOpenInterface.MODEL_MODEL);
        for (EipOpenInterface openInterface : openInterfaceList) {
            EipLogStrategyEntity logStrategyEntity = EipLogStrategyContext.getOrCreate(InterfaceTypeEnum.OPEN, openInterface.getInterfaceName());
            logStrategyEntity.setEnabled(true);
        }
    }

    private void registerListeners() {
        init();

        String rootPath = getRootPath();

        List<EipLogStrategyEntity> logStrategies = EipLogStrategyContext.values();
        if (CollectionUtils.isNotEmpty(logStrategies)) {
            try {
                for (EipLogStrategyEntity logStrategy : logStrategies) {
                    String routePath = rootPath + CharacterConstants.SEPARATOR_SLASH + logStrategy.getInterfaceType().value() + CharacterConstants.SEPARATOR_SLASH + logStrategy.getInterfaceName();
                    this.zookeeperService.createOrUpdateData(routePath, createData(logStrategy));
                }
            } catch (Exception e) {
                log.error("eip log strategy distribution init error.", e);
            }
        }

        registerListener(rootPath);
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

    private <T extends AbstractEipApi> List<T> queryValidEnabledLogApi(String model) {
        return Models.origin().queryListByWrapper(Pops.<T>lambdaQuery()
                .from(model)
                .eq(AbstractEipApi::getIsEnabledLog, true));
    }

    private List<EipLogStrategy> queryValidLogStrategies() {
        return Models.origin().queryListByWrapper(Pops.<EipLogStrategy>lambdaQuery()
                .from(EipLogStrategy.MODEL_MODEL)
                .eq(EipLogStrategy::getIsIgnoreFrequency, true));
    }

    private byte[] createData(EipLogStrategyEntity data) {
        return KryoUtils.serialize(data);
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
