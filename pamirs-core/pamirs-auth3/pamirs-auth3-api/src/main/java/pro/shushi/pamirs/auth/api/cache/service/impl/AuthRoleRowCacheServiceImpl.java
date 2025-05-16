package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.entity.RowCacheKey;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthRoleRowCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 角色行权限缓存服务
 *
 * @author Adamancy Zhang at 14:17 on 2024-01-20
 */
@Order
@Component
@SPI.Service
public class AuthRoleRowCacheServiceImpl extends AbstractSetCacheService<RowCacheKey, String> implements AuthRoleRowCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<String> authRedisTemplate;

    @Override
    protected RedisTemplate<String, String> getRedisTemplate() {
        return authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(RowCacheKey key) {
        return AuthConstants.AUTH_CACHE_KEY_PREFIX + key.getRoleId() + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.DATA_PERMISSION_KEY + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.ROW_TYPE + CharacterConstants.SEPARATOR_COLON +
                key.getModel() + CharacterConstants.SEPARATOR_COLON +
                key.getType().name().toLowerCase();
    }

    @Override
    protected String[] cacheSetToArray(Set<String> cacheSet) {
        return cacheSet.toArray(new String[0]);
    }
}
