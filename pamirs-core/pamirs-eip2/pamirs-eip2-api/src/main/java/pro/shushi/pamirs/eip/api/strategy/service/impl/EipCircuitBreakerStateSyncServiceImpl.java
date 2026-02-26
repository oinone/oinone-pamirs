package pro.shushi.pamirs.eip.api.strategy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.strategy.listener.EipCircuitBreakerStatusChangeListener;
import pro.shushi.pamirs.eip.api.strategy.service.EipCircuitBreakerStateSyncService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

/**
 * @author yeshenyue on 2025/4/16 15:24.
 */
@Slf4j
@Service
public class EipCircuitBreakerStateSyncServiceImpl extends AbstractEipDistributedConfigSync implements EipCircuitBreakerStateSyncService {

    @Autowired
    private EipCircuitBreakerStatusChangeListener nodeListener;

    @Override
    protected String getRootPath() {
        return CB_ZK_ROOT_PATH;
    }

    @Override
    protected String getDisplayName() {
        return "集成接口熔断器";
    }

    @Override
    public void startListener() {
        this.startListener(nodeListener);
    }

    @Override
    public void handleUpdate(String interfaceName, CircuitBreakerStatusEnum config) {
        byte[] data = serialize(config);
        this.syncConfig(interfaceName, data, false);
    }

    @Override
    public void handleUpdateConfig(String interfaceName) {
        this.syncConfig(interfaceName, CONFIG_UPDATE, true);
    }

    @Override
    public void handleRemove(String interfaceName) {
        this.removeConfig(interfaceName);
    }

    protected static byte[] serialize(CircuitBreakerStatusEnum status) {
        if (CircuitBreakerStatusEnum.OPEN.equals(status)) {
            return OPEN;
        } else if (CircuitBreakerStatusEnum.CLOSED.equals(status)) {
            return CLOSED;
        } else if (CircuitBreakerStatusEnum.HALF_OPEN.equals(status)) {
            return HALF_OPEN;
        }
        throw PamirsException.construct(EipExpEnumerate.EIP_CB_NOT_STATUS).errThrow();
    }
}
