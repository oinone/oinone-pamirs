package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthSharedCodeCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * @author Adamancy Zhang at 19:11 on 2024-04-19
 */
@Order
@Component
@SPI.Service
public class AuthSharedCodeCacheServiceImpl extends AbstractValueCacheService<String, String> implements AuthSharedCodeCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<String> authRedisTemplate;

    @Override
    protected RedisTemplate<String, String> getRedisTemplate() {
        return authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(String sharedCode) {
        return AuthConstants.SHARED_CODE_CACHE_KEY_PREFIX + sharedCode;
    }
}
