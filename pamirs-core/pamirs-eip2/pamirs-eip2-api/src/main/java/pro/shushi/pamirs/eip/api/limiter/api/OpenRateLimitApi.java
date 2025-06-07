package pro.shushi.pamirs.eip.api.limiter.api;

import pro.shushi.pamirs.eip.api.model.EipOpenRateLimitPolicy;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * @author yeshenyue on 2025/4/22 14:58.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface OpenRateLimitApi {

    /**
     * 注册限流
     */
    void registerPolicy(EipOpenRateLimitPolicy policy);

    /**
     * 注销限流
     */
    void unregisterPolicy(String appKey, String interfaceName);

    /**
     * 限流判断
     */
    boolean tryAcquire(String appKey, String interfaceName);

    /**
     * 限流配置是否存在
     */
    boolean isExist(String appKey, String interfaceName);
}
