package pro.shushi.pamirs.user.api.spi.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.configure.UserConfigure;
import pro.shushi.pamirs.user.api.spi.UserCacheApi;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 默认用户Session缓存实现
 *
 * @author Adamancy Zhang at 15:56 on 2024-06-15
 */
@Order
@Component
@SPI.Service
public class DefaultUserCache implements UserCacheApi {

    private static final Set<String> DEFAULT_FILTER_URIS = Collections.singleton("/pamirs/message");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PamirsUserDTO getSessionUser(String key) {
        String objectValue = getUserCacheAndRenewed(key);
        if (StringUtils.isNotBlank(objectValue)) {
            return JSON.parseObject(objectValue, PamirsUserDTO.class);
        }
        return null;
    }

    @Override
    public void setSessionUser(String key, PamirsUserDTO user, Integer expire) {
        user.setPassword(null);
        expire = getExpire(expire);
        stringRedisTemplate.opsForValue().set(key.replace("'", " "), JSON.toJSONString(user), expire, TimeUnit.SECONDS);
        // 当前的实现是一个user可以在多个客户端登录，需要在管理端修改user权限后强制清除掉该用户已登录的session，所以需要记录uid对应所有已登录的sessionId
        String userRelSessionKey = UserCache.createUserRelSessionKey(user.getUserId());
        stringRedisTemplate.opsForSet().add(userRelSessionKey, key);
        stringRedisTemplate.expire(userRelSessionKey, expire, TimeUnit.SECONDS);
    }

    @Override
    public void clearSessionUser(String key) {
        PamirsUserDTO pamirsUserDTO = getSessionUser(key);
        if (null != pamirsUserDTO) {
            stringRedisTemplate.opsForSet().remove(UserCache.createUserRelSessionKey(pamirsUserDTO.getUserId()), key);
            stringRedisTemplate.delete(key);
        }
    }

    @Override
    public void clearSessionUserByUserId(Long userId) {
        String cacheKey = UserCache.createUserRelSessionKey(userId);
        Set<String> sessionKeySet = stringRedisTemplate.opsForSet().members(cacheKey);
        if (sessionKeySet != null) {
            sessionKeySet.forEach(sessionKey -> stringRedisTemplate.delete(sessionKey));
            stringRedisTemplate.delete(cacheKey);
        }
    }

    protected int getExpire(Integer expire) {
        if (expire == null) {
            expire = UserConfigure.getDefaultSessionExpire();
        }
        return expire;
    }

    protected int getRenewedExpire() {
        return UserConfigure.getDefaultSessionRenewedExpire();
    }

    protected Set<String> getRenewedFilterUrls() {
        return Sets.union(UserConfigure.getRenewedFilterUrls(), DEFAULT_FILTER_URIS);
    }

    protected String getUserCacheAndRenewed(String key) {
        String currentUri = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            String uri = Optional.ofNullable(PamirsSession.getRequestVariables())
                    .map(PamirsRequestVariables::getURI)
                    .map(URI::getPath)
                    .orElse(null);
            if (StringUtils.isNotBlank(uri)) {
                currentUri = uri;
            }
        }
        if (StringUtils.isNotBlank(currentUri) && getRenewedFilterUrls().contains(currentUri)) {
            return stringRedisTemplate.opsForValue().get(key);
        }
        int ttl = getRenewedExpire();
        if (ttl <= 0) {
            return stringRedisTemplate.opsForValue().get(key);
        }
        List<Object> result = stringRedisTemplate.executePipelined(new SessionCallback<Void>() {
            @SuppressWarnings({"unchecked", "NullableProblems"})
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                operations.opsForValue().get(key);
                operations.expire(key, ttl, TimeUnit.SECONDS);
                return null;
            }
        });
        if (result.size() >= 1) {
            return (String) result.get(0);
        }
        return null;
    }
}
