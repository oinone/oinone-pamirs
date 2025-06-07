package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthPathMappingCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 权限路径映射缓存服务
 *
 * @author Adamancy Zhang at 15:29 on 2024-03-25
 */
@Order
@Component
@SPI.Service
public class AuthPathMappingCacheServiceImpl extends AbstractSetCacheService<String, String> implements AuthPathMappingCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<String> authRedisTemplate;

    @Override
    protected RedisTemplate<String, String> getRedisTemplate() {
        return this.authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(String pathCode) {
        return AuthConstants.AUTH_PATH_MAPPING_CACHE_KEY_PREFIX + pathCode;
    }

    @Override
    protected String[] cacheSetToArray(Set<String> cacheSet) {
        return cacheSet.toArray(new String[0]);
    }
}
