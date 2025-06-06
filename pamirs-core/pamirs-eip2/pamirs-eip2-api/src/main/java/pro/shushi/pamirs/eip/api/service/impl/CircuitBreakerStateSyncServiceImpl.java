package pro.shushi.pamirs.eip.api.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.service.CircuitBreakerStateSyncService;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yeshenyue on 2025/4/16 15:24.
 */
@Slf4j
@Service
public class CircuitBreakerStateSyncServiceImpl implements CircuitBreakerStateSyncService {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private EipCircuitBreakerStatusChangeListener nodeListener;

    private static final AtomicBoolean IS_REGISTERED = new AtomicBoolean(false);

    @Override
    public void startListener() {
        if (IS_REGISTERED.compareAndSet(false, true)) {
            String rootPath = buildRootPath();
            log.info("熔断器注册监听-path:{}", rootPath);
            TreeCache treeCache = zookeeperService.registerTreeCache(rootPath, nodeListener);
        }
    }

    @Override
    public synchronized void syncState(String interfaceName, CircuitBreakerStatusEnum statusEnum) {
        if (Boolean.FALSE.equals(zookeeperService.isEnabled())) {
            log.error("zookeeper服务未启用，集成接口熔断器状态变更失败");
            return;
        }

        byte[] data = CircuitBreakerStateSyncService.convertStatus(statusEnum);
        String path = buildPath(interfaceName);
        try {
            zookeeperService.createOrUpdateData(path, data, DEFAULT_COMPARATOR);
        } catch (Exception e) {
            log.error("熔断状态刷新失败 [interfaceName:{}]", interfaceName, e);
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_REFRESH_ERROR).errThrow();
        }
    }

    @Override
    public synchronized void removeStateNode(String interfaceName) {
        if (Boolean.FALSE.equals(zookeeperService.isEnabled())) {
            log.error("zookeeper服务未启用，集成接口熔断器注销失败");
            return;
        }

        String path = buildPath(interfaceName);
        try {
            if (zookeeperService.checkExists(path)) {
                zookeeperService.delete(path);
            }
        } catch (Exception e) {
            log.error("熔断器注销失败 [interfaceName:{}]", interfaceName, e);
            throw PamirsException.construct(EipExpEnumerate.EIP_CB_REFRESH_ERROR).errThrow();
        }
    }

    private String buildPath(String interfaceName) {
        return buildRootPath() + CharacterConstants.SEPARATOR_SLASH + interfaceName;
    }

    public String buildRootPath() {
        String tenant = PamirsTenantSession.getTenant();
        String suffix = StringUtils.isBlank(tenant) ? "" : ("#" + tenant);
        return CB_ZK_ROOT_PATH + suffix;
    }
}
