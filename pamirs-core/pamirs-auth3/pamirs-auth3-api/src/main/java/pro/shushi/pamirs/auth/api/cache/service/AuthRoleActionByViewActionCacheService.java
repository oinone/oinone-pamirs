package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.auth.api.cache.entity.ActionCacheKeyByViewAction;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色动作权限缓存服务 - 基于跳转动作
 *
 * @author Adamancy Zhang at 15:44 on 2024-01-24
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleActionByViewActionCacheService extends StandardHashCacheService<ActionCacheKeyByViewAction, String, Long> {
}
