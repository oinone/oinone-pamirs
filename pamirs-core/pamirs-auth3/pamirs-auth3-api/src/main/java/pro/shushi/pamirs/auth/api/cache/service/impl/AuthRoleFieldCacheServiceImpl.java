package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.entity.FieldCacheKey;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthRoleFieldCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 角色字段权限缓存服务
 *
 * @author Adamancy Zhang at 14:16 on 2024-01-20
 */
@Order
@Component
@SPI.Service
public class AuthRoleFieldCacheServiceImpl extends AbstractHashCacheService<FieldCacheKey, String, Long> implements AuthRoleFieldCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<Long> authRedisTemplate;

    @Override
    protected RedisTemplate<String, Long> getRedisTemplate() {
        return authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(FieldCacheKey key) {
        return AuthConstants.AUTH_CACHE_KEY_PREFIX + key.getRoleId() + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.DATA_PERMISSION_KEY + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.FIELD_TYPE + CharacterConstants.SEPARATOR_COLON +
                key.getModel();
    }
}
