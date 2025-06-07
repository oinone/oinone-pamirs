package pro.shushi.pamirs.bizauth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.impl.AbstractSetCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.bizauth.api.cache.entity.BusinessCodeCacheKey;
import pro.shushi.pamirs.bizauth.api.cache.service.BusinessCodeUserCacheService;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

@Order
@Component
@SPI.Service
public class BusinessCodeUserCacheServiceImpl extends AbstractSetCacheService<BusinessCodeCacheKey, Long> implements BusinessCodeUserCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<Long> authRedisTemplate;

    @Override
    protected RedisTemplate<String, Long> getRedisTemplate() {
        return this.authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(BusinessCodeCacheKey key) {
        return AuthConstants.CURRENT_ROLES_CACHE_KEY_PREFIX + key.getUserId() + CharacterConstants.SEPARATOR_COLON +
                key.getBusinessCode();
    }

    @Override
    protected Long[] cacheSetToArray(Set<Long> cacheSet) {
        return cacheSet.toArray(new Long[0]);
    }
}
