package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.operation.optset.SetEntity;
import pro.shushi.pamirs.auth.api.cache.operation.optset.SetSetSessionCallback;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.runtime.spi.CurrentRolesCacheApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 默认当前角色缓存
 *
 * @author Adamancy Zhang at 11:04 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class DefaultCurrentRolesCache implements CurrentRolesCacheApi {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    private AuthRedisTemplate<Long> authRedisTemplate;

    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> get() {
        String key = this.generatorCacheKey();
        if (key == null) {
            return null;
        }
        List<Object> results = executePipelined(new SessionCallback<Void>() {

            @SuppressWarnings({"unchecked", "NullableProblems"})
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                operations.hasKey(key);
                operations.opsForSet().members(key);
                return null;
            }
        });
        if (results.size() != 2) {
            return null;
        }
        Boolean hasKey = (Boolean) results.get(0);
        if (hasKey == null) {
            return null;
        }
        if (hasKey) {
            return (Set<Long>) results.get(1);
        }
        return null;
    }

    @Override
    public void set(Set<Long> roleIds) {
        String key = this.generatorCacheKey();
        if (key == null) {
            return;
        }
        executePipelinedWithoutResult(new SetSetSessionCallback<>(Collections.singletonList(new SetEntity<>(key, roleIds.toArray(new Long[0])))));
    }

    protected String generatorCacheKey() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            return null;
        }
        return AuthConstants.CURRENT_ROLES_CACHE_KEY_PREFIX + userId;
    }

    private List<Object> executePipelined(SessionCallback<?> sessionCallback) {
        return authRedisTemplate.executePipelined(sessionCallback);
    }

    private void executePipelinedWithoutResult(SessionCallback<?> sessionCallback) {
        authRedisTemplate.executePipelined(sessionCallback, null);
    }
}
