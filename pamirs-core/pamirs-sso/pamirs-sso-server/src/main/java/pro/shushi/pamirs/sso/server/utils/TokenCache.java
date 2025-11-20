package pro.shushi.pamirs.sso.server.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TokenCache {

    public static void putAK(String accessToken, String clientId, String openId, String randomAkId, Long expires) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + randomAkId;
        redisTemplate.opsForValue().set(redisKey, accessToken, expires, TimeUnit.SECONDS);
    }

    public static void putRK(String refreshToken, String clientId, String openId, Long expires) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + ":" + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_RK_SUFFIX;
        redisTemplate.opsForValue().set(redisKey, refreshToken, expires, TimeUnit.SECONDS);
    }


    public static String getAK(String clientId, String openId, String randomAkId) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + randomAkId;
        return redisTemplate.opsForValue().get(redisKey);
    }

    public static String getRK(String clientId, String openId) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_RK_SUFFIX;
        return redisTemplate.opsForValue().get(redisKey);
    }

    public static void putRefreshAK(String accessToken, String clientId, String openId, Long expires) {
        String randomAkId = UUIDUtil.getUUIDNumberString();
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + randomAkId;
        redisTemplate.opsForValue().set(redisKey, accessToken, expires, TimeUnit.SECONDS);
    }

    public static void cleanRK(String clientId, String openId) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + ":" + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_RK_SUFFIX;
        redisTemplate.delete(redisKey);
    }

    public static void cleanAK(String clientId, String openId, String randomAkId) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + randomAkId;
        redisTemplate.delete(redisKey);
    }

    public static void markExpireSoon(String clientId, String openId, Long expires) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisCacheAK = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_ACCESS_TOKEN_LIKE;
        String redisRefreshKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_RK_SUFFIX;

        Set<String> keys = redisTemplate.keys(redisCacheAK);
        keys.remove(redisRefreshKey);
        for (String key : keys) {
            Long redisExpires = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (redisExpires != null && expires.compareTo(redisExpires) < 0) {
                redisTemplate.expire(key, expires, TimeUnit.SECONDS);
            }
        }
    }
}
