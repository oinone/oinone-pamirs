package pro.shushi.pamirs.user.api.spi.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.constants.UserConstants;

import java.util.concurrent.TimeUnit;

/**
 * 单用户Session缓存实现
 *
 * @author Adamancy Zhang at 17:01 on 2024-06-15
 */
@Order
@Component
@SPI.Service(UserConstants.SINGLE_USER_CACHE_MODE)
public class SingleUserCache extends DefaultUserCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PamirsUserDTO getSessionUser(String key) {
        String cacheKey = getUserCacheAndRenewed(key);
        if (StringUtils.isBlank(cacheKey)) {
            return null;
        }
        PamirsUserDTO user = super.getSessionUser(cacheKey);
        if (user == null || !key.equals(user.getSessionKey())) {
            return null;
        }
        return user;
    }

    @Override
    public void setSessionUser(String key, PamirsUserDTO user, Integer expire) {
        user.setPassword(null);
        String cacheKey = generatorSessionKey(user.getUserId());
        PamirsUserDTO oldUser = getSessionUser(cacheKey);
        if (oldUser != null) {
            String sessionKey = oldUser.getSessionKey();
            if (StringUtils.isBlank(sessionKey)) {
                stringRedisTemplate.delete(sessionKey);
            }
        }
        expire = getExpire(expire);
        user.setSessionKey(key);
        stringRedisTemplate.opsForValue().set(key, cacheKey, expire, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(user), expire, TimeUnit.SECONDS);
    }

    @Override
    public void clearSessionUser(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public void clearSessionUserByUserId(Long userId) {
        stringRedisTemplate.delete(generatorSessionKey(userId));
    }

    protected String generatorSessionKey(Long userId) {
        return UserConstant.USER_CACHE_KEY + userId;
    }
}