package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色模块权限缓存服务
 *
 * @author Adamancy Zhang at 16:05 on 2024-01-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleModuleCacheService extends StandardHashCacheService<Long, String, Long> {
}
