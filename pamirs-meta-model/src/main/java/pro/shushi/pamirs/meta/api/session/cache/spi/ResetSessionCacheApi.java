package pro.shushi.pamirs.meta.api.session.cache.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 重置元数据缓存API
 *
 * @author Adamancy Zhang at 12:36 on 2024-10-26
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ResetSessionCacheApi {

    default void resetMetadataCache() {
    }

}
