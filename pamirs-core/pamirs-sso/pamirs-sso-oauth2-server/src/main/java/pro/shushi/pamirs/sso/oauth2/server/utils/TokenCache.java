package pro.shushi.pamirs.sso.oauth2.server.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.enmu.SsoExpEnumerate;

import java.util.*;
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


//    public static void putSsoCacheAKSet(List<String> clientIds, String openId, String clientId, Long expires) {
//        try {
//            StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
//            String redisKey = SsoConfigurationConstant.PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_AK_SUFFIX;
////            String script = "local key = KEYS[1]\n" +
////                    "local values = cjson.decode(ARGV[1])\n" +
////                    "local expires = tonumber(ARGV[2])\n" +
////                    "local addedCount = 0\n" +
////                    "\n" +
////                    "for _, v in ipairs(values) do\n" +
////                    "    addedCount = addedCount + redis.call('SADD', key, v)\n" +
////                    "end\n" +
////                    "\n" +
////                    "redis.call('EXPIRE', key, expires)\n" +
////                    "\n" +
////                    "return addedCount\n";
////            RedisScript<Long> luaScript = new DefaultRedisScript<>(script, Long.class);
////            Long addedCount = redisTemplate.execute(luaScript, Collections.singletonList(redisKey), JSON.toJSONString(clientIds), expires);
//
////            for (String clientId : clientIds) {
////                redisTemplate.opsForSet().add(redisKey, clientId);
////                redisTemplate.expire(redisKey, expires, TimeUnit.SECONDS);
////            }
//
//
//            SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
//                @Override
//                public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
//                    operations.multi();
//                    for (String clientId : clientIds) {
//                        operations.opsForSet().add((K) redisKey, (V) clientId);
//                    }
//                    operations.expire((K) redisKey, expires, TimeUnit.SECONDS);
//                    return operations.exec();
//                }
//            };
//
//            List<Object> results = redisTemplate.execute(sessionCallback);
//            if (results == null) {
//                throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_SERVER_SET_CACHE_ERROR).errThrow();
//            }
//        } catch (Exception e) {
//            // 在需要的情况下进行异常处理
//            throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_SERVER_SET_CACHE_ERROR).errThrow();
//        }
//    }
//
//
//    public static void addSsoCacheAKSet(String clientId, String openId) {
//        try {
//            if (StringUtils.isNotEmpty(openId)) {
//                StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
//                String redisKey = SsoConfigurationConstant.PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_AK_SUFFIX;
//                redisTemplate.opsForSet().add(redisKey, clientId);
//            }
//        } catch (Exception e) {
//            throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_SERVER_SET_CACHE_ERROR).errThrow();
//        }
//    }
//
//
//    public static void putSsoCacheRKSet(List<String> clientIds, String openId, String clientId, Long expires) {
//        try {
//            StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
//            String redisKey = SsoConfigurationConstant.PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX + openId + ":" + clientId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_RK_SUFFIX;
////            String script = "local key = KEYS[1]\n" +
////                    "local values = cjson.decode(ARGV[1])\n" +
////                    "local expires = tonumber(ARGV[2])\n" +
////                    "local addedCount = 0\n" +
////                    "\n" +
////                    "for _, v in ipairs(values) do\n" +
////                    "    addedCount = addedCount + redis.call('SADD', key, v)\n" +
////                    "end\n" +
////                    "\n" +
////                    "redis.call('EXPIRE', key, expires)\n" +
////                    "\n" +
////                    "return addedCount\n";
////            RedisScript<Long> luaScript = new DefaultRedisScript<>(script, Long.class);
////            Long addedCount = redisTemplate.execute(luaScript, Collections.singletonList(redisKey), JSON.toJSONString(clientIds), expires.toString());
//
//            SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
//                @Override
//                public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
//                    operations.multi();
//                    for (String clientId : clientIds) {
//                        operations.opsForSet().add((K) redisKey, (V) clientId);
//                    }
//                    operations.expire((K) redisKey, expires, TimeUnit.SECONDS);
//                    return operations.exec();
//                }
//            };
//
//            List<Object> results = redisTemplate.execute(sessionCallback);
//            if (results == null) {
//                throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_SERVER_SET_CACHE_ERROR).errThrow();
//            }
//        } catch (Exception e) {
//            // 在需要的情况下进行异常处理
//            throw PamirsException.construct(SsoExpEnumerate.SSO_PAMIRS_SERVER_SET_CACHE_ERROR).errThrow();
//        }
//    }
//
//
//    public static void cleanSsoCacheAKSet(String openId) {
//        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
//        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_AK_SUFFIX;
//        redisTemplate.delete(redisKey);
//    }
//
//    public static void cleanSsoCacheAKByClientId(String openId, List<String> clientIds) {
//        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
//        String redisKey = SsoConfigurationConstant.PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_AK_SUFFIX;
////
////        String script = "local key = KEYS[1]\n" +
////                "local elements = ARGV\n" +
////                "local removedCount = 0\n" +
////                "\n" +
////                "for i, element in ipairs(elements) do\n" +
////                "    removedCount = removedCount + redis.call('SREM', key, element)\n" +
////                "end\n" +
////                "\n" +
////                "if redis.call('SCARD', key) == 0 then\n" +
////                "    redis.call('DEL', key)\n" +
////                "end\n" +
////                "\n" +
////                "return removedCount\n";
////        redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(redisKey), (Object[]) clientIds.toArray(new String[0]));
//        if (clientIds != null) {
//            for (String clientId : clientIds) {
//                redisTemplate.opsForSet().remove(redisKey, clientId);
//            }
//        }
//    }
//
//
////    public static boolean isUserLoggedIn(String openId, String clientId) {
////        boolean flag = false;
////        if (StringUtils.isNotEmpty(openId)) {
////            StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
////            String redisKey = SsoConfigurationConstant.PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX + openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_AK_SUFFIX;
////            flag = redisTemplate.opsForSet().isMember(redisKey, clientId);
////        }
////        return flag;
////    }
//
//    public static boolean isUserLoggedInRefresh(String clientId, String refreshOpenId, String refreshClientId) {
//        boolean flag = false;
//        if (StringUtils.isNotEmpty(refreshOpenId)) {
//            StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
//            String redisKey = SsoConfigurationConstant.PAMIRS_SSO_SERVER_REDIS_KEY_PREFIX + refreshClientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + refreshOpenId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_RK_SUFFIX;
//            flag = redisTemplate.opsForSet().isMember(redisKey, clientId);
//        }
//        return flag;
//    }

    public static void markExpireSoon(String clientId, String openId, Long expires) {
        StringRedisTemplate redisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String redisCacheAK = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER + openId + SsoConfigurationConstant.PAMIRS_SSO_ACCESS_TOKEN_LIKE;
        String redisRefreshKey = SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_PREFIX + clientId + SsoConfigurationConstant.PAMIRS_SSO_TOKEN_DELIMITER+ openId + SsoConfigurationConstant.PAMIRS_SSO_REDIS_KEY_RK_SUFFIX;

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
