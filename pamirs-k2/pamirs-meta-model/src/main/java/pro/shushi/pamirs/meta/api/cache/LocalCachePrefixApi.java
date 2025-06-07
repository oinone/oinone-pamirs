package pro.shushi.pamirs.meta.api.cache;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 缓存前缀API
 * <p>
 * 2021/8/20 12:57 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface LocalCachePrefixApi {

    <K> K prefix(K origin);

}
