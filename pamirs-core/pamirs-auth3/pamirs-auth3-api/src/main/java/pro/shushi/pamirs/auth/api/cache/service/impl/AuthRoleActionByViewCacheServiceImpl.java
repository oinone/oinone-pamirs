package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.entity.ActionCacheKeyByView;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthRoleActionByViewCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 角色动作权限缓存服务 - 基于视图
 *
 * @author Adamancy Zhang at 21:59 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class AuthRoleActionByViewCacheServiceImpl extends AbstractHashCacheService<ActionCacheKeyByView, String, Long> implements AuthRoleActionByViewCacheService {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<Long> authRedisTemplate;

    @Override
    protected RedisTemplate<String, Long> getRedisTemplate() {
        return this.authRedisTemplate;
    }

    @Override
    protected String generatorCacheKey(ActionCacheKeyByView key) {
        return AuthConstants.AUTH_CACHE_KEY_PREFIX + key.getRoleId() + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.RESOURCE_PERMISSION_KEY + CharacterConstants.SEPARATOR_COLON +
                AuthConstants.VIEW_TYPE + CharacterConstants.SEPARATOR_COLON +
                key.getModule() + CharacterConstants.SEPARATOR_COLON +
                key.getView();
    }
}
