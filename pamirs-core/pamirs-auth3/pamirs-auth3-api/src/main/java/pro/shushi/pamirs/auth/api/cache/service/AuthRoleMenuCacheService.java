package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.auth.api.cache.entity.MenuCacheKey;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色菜单权限缓存服务
 *
 * @author Adamancy Zhang at 18:09 on 2024-01-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleMenuCacheService extends StandardHashCacheService<MenuCacheKey, String, Long> {
}
