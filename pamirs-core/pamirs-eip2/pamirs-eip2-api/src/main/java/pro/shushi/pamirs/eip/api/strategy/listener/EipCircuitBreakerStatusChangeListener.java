package pro.shushi.pamirs.eip.api.strategy.listener;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.strategy.manager.CircuitBreakerManager;
import pro.shushi.pamirs.eip.api.strategy.service.EipCircuitBreakerRuleService;
import pro.shushi.pamirs.eip.api.strategy.service.EipCircuitBreakerStateSyncService;
import pro.shushi.pamirs.eip.api.util.EipZkHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

/**
 * @author yeshenyue on 2025/4/16 14:11.
 */
@Slf4j
@Component
public class EipCircuitBreakerStatusChangeListener implements TreeCacheListener {

    @Autowired
    private CircuitBreakerManager circuitBreakerManager;
    @Autowired
    private ZookeeperService zookeeperService;
    @Autowired
    private EipCircuitBreakerRuleService eipCircuitBreakerRuleService;

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) {
        String path = event.getData() != null ? event.getData().getPath() : null;
        if (StringUtils.isBlank(path)) {
            return;
        }

        String rootPath = zookeeperService.getRootPath() + EipCircuitBreakerStateSyncService.CB_ZK_ROOT_PATH;
        if (!path.startsWith(rootPath) || path.length() == rootPath.length()) {
            return;
        }

        String dataPath = EipZkHelper.processorListenerPath(rootPath, path);
        if (StringUtils.isBlank(dataPath)) {
            log.info("Circuit breaker detected top-level or unexpected node change, ignored, path: {}", path);
            return;
        }

        String[] pathList = dataPath.split(CharacterConstants.SEPARATOR_SLASH);
        if (pathList.length != 1) {
            log.info("Circuit breaker detected top-level or unexpected node change, ignored, path: {}", path);
            return;
        }

        String interfaceName = pathList[0];
        byte[] dataBytes = event.getData().getData();
        if (dataBytes[0] == EipCircuitBreakerStateSyncService.CONFIG_UPDATE[0]) {
            log.info("zk listens to circuit breaker config change, refresh local circuit breaker config, interfaceName: {}", interfaceName);
            registerInterface(interfaceName);
        } else {
            CircuitBreakerStatusEnum status = deserialize(dataBytes);
            switch (event.getType()) {
                case NODE_ADDED:
                case NODE_REMOVED:
                    registerInterface(interfaceName);
                    break;
                case NODE_UPDATED:
                    updateCircuitBreakerState(interfaceName, status);
                    break;
                default:
                    break;
            }
        }
    }

    private void registerInterface(String interfaceName) {
        try {
            eipCircuitBreakerRuleService.register(interfaceName);
        } catch (Exception e) {
            log.error("Circuit breaker registration failed, interface: {}", interfaceName, e);
        }
    }

    private void updateCircuitBreakerState(String interfaceName, CircuitBreakerStatusEnum status) {
        try {
            log.info("zk notifies circuit breaker status change, interfaceName: {}, status: {}", interfaceName, status.displayName());
            circuitBreakerManager.updateState(interfaceName, status);
        } catch (Exception e) {
            log.error("Failed to update circuit breaker status, interface: {}, status: {}", interfaceName, status.displayName(), e);
        }
    }

    public static CircuitBreakerStatusEnum deserialize(byte[] byteDate) {
        byte data = byteDate[0];
        if (data == EipCircuitBreakerStateSyncService.OPEN[0]) {
            return CircuitBreakerStatusEnum.OPEN;
        } else if (data == EipCircuitBreakerStateSyncService.CLOSED[0]) {
            return CircuitBreakerStatusEnum.CLOSED;
        } else if (data == EipCircuitBreakerStateSyncService.HALF_OPEN[0]) {
            return CircuitBreakerStatusEnum.HALF_OPEN;
        }
        throw PamirsException.construct(EipExpEnumerate.EIP_CB_NOT_STATUS).errThrow();
    }
}
