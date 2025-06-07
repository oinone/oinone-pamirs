package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 权限路径映射缓存服务
 *
 * @author Adamancy Zhang at 15:28 on 2024-03-25
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthPathMappingCacheService extends StandardSetCacheService<String, String> {
}
