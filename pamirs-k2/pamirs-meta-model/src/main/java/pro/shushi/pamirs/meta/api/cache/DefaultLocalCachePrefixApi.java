package pro.shushi.pamirs.meta.api.cache;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 缓存前缀API默认实现
 * <p>
 * 2021/8/20 12:57 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
@Component
public class DefaultLocalCachePrefixApi implements LocalCachePrefixApi {

    @Override
    public <K> K prefix(K origin) {
        return origin;
    }

}
