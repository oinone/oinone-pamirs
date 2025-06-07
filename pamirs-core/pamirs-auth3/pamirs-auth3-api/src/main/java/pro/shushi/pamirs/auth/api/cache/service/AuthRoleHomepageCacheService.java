package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色首页权限缓存服务
 *
 * @author Adamancy Zhang at 09:50 on 2024-01-22
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleHomepageCacheService extends StandardHashCacheService<Long, String, Long> {
}
