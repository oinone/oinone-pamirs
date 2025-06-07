package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.auth.api.cache.entity.RowCacheKey;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 角色行权限缓存服务
 *
 * @author Adamancy Zhang at 14:11 on 2024-01-20
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleRowCacheService extends StandardSetCacheService<RowCacheKey, String> {
}
