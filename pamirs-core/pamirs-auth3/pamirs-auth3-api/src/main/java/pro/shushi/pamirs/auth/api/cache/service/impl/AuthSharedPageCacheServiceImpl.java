package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthSharedPageCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 权限分享页面缓存服务
 *
 * @author Adamancy Zhang at 14:41 on 2024-04-12
 */
@Order
@Component
@SPI.Service
public class AuthSharedPageCacheServiceImpl extends AbstractValueCacheService<String, Set<String>> implements AuthSharedPageCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<Set<String>> authRedisTemplate;

    @Override
    protected RedisTemplate<String, Set<String>> getRedisTemplate() {
        return authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(String authorizationCode) {
        return AuthConstants.ANONYMOUS_USER_CACHE_KEY_PREFIX + authorizationCode;
    }
}
