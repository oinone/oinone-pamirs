package pro.shushi.pamirs.user.api.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.configure.UserConfiguration;
import pro.shushi.pamirs.user.api.configure.UserConfigure;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.constants.UserConstants;
import pro.shushi.pamirs.user.api.spi.UserCacheApi;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserCache<T> {

    private static final HoldKeeper<UserCacheApi> userCacheApiHolder = new HoldKeeper<>();

    public static UserCacheApi getUserCacheApi() {
        return userCacheApiHolder.supply(() -> {
            String mode = Optional.ofNullable(UserConfigure.getSessionConfig())
                    .map(UserConfiguration.SessionConfig::getMode)
                    .filter(StringUtils::isNotBlank)
                    .orElse(UserConstants.MULTIPLE_USER_CACHE_MODE);
            return Spider.getExtension(UserCacheApi.class, mode);
        });
    }

    public static String parseSessionId(String sessionId) {
        return UserConstant.USER_CACHE_KEY + sessionId;
    }

    public static PamirsUserDTO getCache(String key) {
        return getUserCacheApi().getSessionUser(key);
    }

    public static void putCache(String key, PamirsUserDTO user) {
        putCache(key, user, UserConstant.USER_EXPIRE_TIME);
    }

    public static Set<String> getSessionId(Long userId) {
        String userRelSessionKey = createUserRelSessionKey(userId);
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        Set<String> sessionId = redisTemplate.opsForSet().members(userRelSessionKey);
        return sessionId;
    }

    public static void putCache(String key, PamirsUserDTO user, Integer expireTime) {
        getUserCacheApi().setSessionUser(key, user, expireTime);
    }

    public static String createUserRelSessionKey(Long userId) {
        return "userSession:" + userId;
    }

    public static PamirsUserDTO get(String sessionId) {
        return get(sessionId, true);
    }

    public static PamirsUserDTO get(String sessionId, Boolean isHttp) {
        String cacheKey = parseSessionId(sessionId);
        PamirsUserDTO user = getCache(cacheKey);
        if (!ObjectUtils.isEmpty(user)) {
            // putCache(cacheKey.replace("\'", " "), user);
            return user;
        }
        return null;
    }

    /**
     * 根据userId清理所有该user登录的session
     *
     * @param userId
     */
    public static void clearSessionByUid(Long userId) {
        getUserCacheApi().clearSessionUserByUserId(userId);
    }

    public static void logout() {
        invalidCookie(PamirsSession.getSessionApi().getSessionId());
    }

    public static void invalidCookie(String sessionId) {
        String cacheKey = parseSessionId(sessionId);
        getUserCacheApi().clearSessionUser(cacheKey);
    }

    /**
     * 可以使用getToken(Long userId，String suffix)方法，支持定义后缀
     *
     * @param userId
     * @return
     */
    @Deprecated
    public static String getToken(Long userId) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        return redisTemplate.opsForValue().get(tokenKey(userId.toString()));
    }

    /**
     * 可以使用clearUserToken(Long userId，String suffix)方法，支持定义后缀
     *
     * @param userId
     * @return
     */
    @Deprecated
    public static void clearUserToken(Long userId) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        redisTemplate.delete(tokenKey(userId.toString()));
    }

    /**
     * 可以使用putToken(String token, Long userId，String suffix)方法，支持定义后缀
     *
     * @param token
     * @param userId
     * @return
     */
    @Deprecated
    public static String putToken(String token, Long userId) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        redisTemplate.opsForValue().set(tokenKey(userId.toString()), token);
        return token;
    }

    public static String getToken(Long userId, String suffix) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = userId.toString();
        if (StringUtils.isNotBlank(suffix)) {
            redisKey = redisKey + ":" + suffix;
        }
        return redisTemplate.opsForValue().get(tokenKey(redisKey));
    }

    public static void clearUserToken(Long userId, String suffix) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = userId.toString();
        if (StringUtils.isNotBlank(suffix)) {
            redisKey = redisKey + ":" + suffix;
        }
        redisTemplate.delete(tokenKey(redisKey));
    }

    public static String putToken(String token, Long userId, String suffix) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = userId.toString();
        if (StringUtils.isNotBlank(suffix)) {
            redisKey = redisKey + ":" + suffix;
        }
        redisTemplate.opsForValue().set(tokenKey(redisKey), token);
        return token;
    }

    private static String tokenKey(String userId) {
        return PamirsTenantSession.getTenant() + UserConstant.USER_TOKEN_KEY + userId;
    }

    public static String recordLoginPicCode(String login, boolean isRefresh) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        //获取用户验证码
        String code = redisTemplate.opsForValue().get(PamirsTenantSession.getTenant() + CharacterConstants.SEPARATOR_DOLLAR + login + UserConstant.LOGIN_PIC_CODE);
        if (isRefresh) {
            String verificationCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            redisTemplate.opsForValue().set(PamirsTenantSession.getTenant() + login + UserConstant.LOGIN_PIC_CODE, verificationCode, 360, TimeUnit.SECONDS);
            return verificationCode;
        } else {
            return code;
        }
    }

    @Deprecated
    public static String recordLoginErrorCount(String login, boolean isRefresh) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        if (isRefresh) {
            redisTemplate.opsForValue().set(login + UserConstant.LOGIN_ERROR_COUNT
                    , "0"
                    , 60
                    , TimeUnit.SECONDS
            );
        } else {
            String count = redisTemplate.opsForValue().get(login + UserConstant.LOGIN_ERROR_COUNT);
            redisTemplate.opsForValue().set(login + UserConstant.LOGIN_ERROR_COUNT
                    , StringUtils.isNotBlank(count) ? String.valueOf(Integer.parseInt(count) + 1) : String.valueOf(1)
                    , 60
                    , TimeUnit.SECONDS
            );
        }

        return redisTemplate.opsForValue().get(login + UserConstant.LOGIN_ERROR_COUNT);
    }

}
