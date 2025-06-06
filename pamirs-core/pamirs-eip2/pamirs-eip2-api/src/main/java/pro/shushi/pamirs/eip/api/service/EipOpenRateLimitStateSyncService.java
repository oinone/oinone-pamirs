package pro.shushi.pamirs.eip.api.service;

/**
 * 开放接口流控状态同步服务
 *
 * @author yeshenyue on 2025/4/24 19:31.
 */
public interface EipOpenRateLimitStateSyncService {

    String OPEN_RATE_LIMIT_ZK_ROOT_PATH = "/eip_open_rate_limit";
    byte[] CONFIG_UPDATE = new byte[]{0};

    /**
     * 初始化监听
     */
    void startListener();

    /**
     * 刷新流控配置
     */
    void handleUpdate(String appKey, String interfaceName);

    /**
     * 注销流控配置
     */
    void handleRemove(String appKey, String interfaceName);
}
