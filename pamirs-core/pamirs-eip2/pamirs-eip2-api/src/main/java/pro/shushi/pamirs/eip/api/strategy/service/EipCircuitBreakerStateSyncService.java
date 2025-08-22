package pro.shushi.pamirs.eip.api.strategy.service;

import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerStatusEnum;

/**
 * 熔断器状态同步服务
 *
 * @author yeshenyue on 2025/4/16 15:09.
 */
public interface EipCircuitBreakerStateSyncService {

    String CB_ZK_ROOT_PATH = "/eip/strategy/circuit_breaker";
    byte[] OPEN = new byte[]{0};
    byte[] CLOSED = new byte[]{1};
    byte[] HALF_OPEN = new byte[]{2};
    byte[] CONFIG_UPDATE = new byte[]{10};

    /**
     * 初始化监听
     */
    void startListener();

    /**
     * 刷新熔断器
     */
    void handleUpdate(String interfaceName, CircuitBreakerStatusEnum statusEnum);

    /**
     * 刷新熔断器配置
     */
    void handleUpdateConfig(String interfaceName);

    /**
     * 注销熔断器
     */
    void handleRemove(String interfaceName);
}
