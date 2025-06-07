package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 分享码缓存服务
 *
 * @author Adamancy Zhang at 19:10 on 2024-04-19
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthSharedCodeCacheService extends StandardValueCacheService<String, String> {
}
