package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.auth.api.cache.entity.ActionCacheKeyByView;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色动作权限缓存服务 - 基于视图
 *
 * @author Adamancy Zhang at 21:57 on 2024-01-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleActionByViewCacheService extends StandardHashCacheService<ActionCacheKeyByView, String, Long> {
}
