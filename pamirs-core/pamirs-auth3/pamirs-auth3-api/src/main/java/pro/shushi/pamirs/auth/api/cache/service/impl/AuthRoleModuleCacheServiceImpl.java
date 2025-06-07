package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthRoleModuleCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 角色模块权限缓存服务
 *
 * @author Adamancy Zhang at 16:29 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class AuthRoleModuleCacheServiceImpl extends AbstractHashCacheService<Long, String, Long> implements AuthRoleModuleCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<Long> authRedisTemplate;

    @Override
    protected RedisTemplate<String, Long> getRedisTemplate() {
        return this.authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(Long roleId) {
        return AuthConstants.AUTH_CACHE_KEY_PREFIX + roleId + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.RESOURCE_PERMISSION_KEY + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.MODULE_TYPE;
    }
}
