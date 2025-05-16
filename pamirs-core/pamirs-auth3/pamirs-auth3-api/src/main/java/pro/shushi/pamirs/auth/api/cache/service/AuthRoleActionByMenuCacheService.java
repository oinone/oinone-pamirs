package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.auth.api.cache.entity.ActionCacheKeyByMenu;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色动作权限缓存服务 - 基于菜单
 *
 * @author Adamancy Zhang at 21:52 on 2024-01-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleActionByMenuCacheService extends StandardHashCacheService<ActionCacheKeyByMenu, String, Long> {
}
