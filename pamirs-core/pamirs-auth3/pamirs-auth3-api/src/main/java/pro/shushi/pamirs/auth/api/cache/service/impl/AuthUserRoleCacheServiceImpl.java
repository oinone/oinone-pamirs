package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthUserRoleCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 用户角色缓存服务
 *
 * @author Adamancy Zhang at 10:01 on 2024-01-08
 */
@Order
@Component
@SPI.Service
public class AuthUserRoleCacheServiceImpl extends AbstractSetCacheService<Long, Long> implements AuthUserRoleCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<Long> authRedisTemplate;

    @Override
    protected RedisTemplate<String, Long> getRedisTemplate() {
        return this.authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(Long userId) {
        return AuthConstants.CURRENT_ROLES_CACHE_KEY_PREFIX + userId;
    }

    @Override
    protected Long[] cacheSetToArray(Set<Long> cacheSet) {
        return cacheSet.toArray(new Long[0]);
    }
}
