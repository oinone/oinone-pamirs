package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.auth.api.cache.entity.ActionCacheKeyByModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色动作权限缓存服务 - 基于模型
 *
 * @author Adamancy Zhang at 22:02 on 2024-01-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleActionByModelCacheService extends StandardHashCacheService<ActionCacheKeyByModel, String, Long> {
}
