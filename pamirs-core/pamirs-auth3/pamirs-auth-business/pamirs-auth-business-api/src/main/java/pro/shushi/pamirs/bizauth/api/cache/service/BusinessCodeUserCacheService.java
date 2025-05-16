package pro.shushi.pamirs.bizauth.api.cache.service;

import pro.shushi.pamirs.auth.api.cache.service.StandardSetCacheService;
import pro.shushi.pamirs.bizauth.api.cache.entity.BusinessCodeCacheKey;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

@SPI(factory = SpringServiceLoaderFactory.class)
public interface BusinessCodeUserCacheService extends StandardSetCacheService<BusinessCodeCacheKey,Long> {
}
