package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 权限分享页面缓存服务
 *
 * @author Adamancy Zhang at 14:39 on 2024-04-12
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthSharedPageCacheService extends StandardValueCacheService<String, Set<String>> {
}
