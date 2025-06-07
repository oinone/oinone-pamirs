package pro.shushi.pamirs.user.api.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.framework.common.utils.kryo.KryoUtils;
import pro.shushi.pamirs.framework.connectors.data.api.orm.BatchSizeHintApi;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;
import pro.shushi.pamirs.framework.connectors.data.template.BytesRedisTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.login.UserInfoCache;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.concurrent.TimeUnit;

import static pro.shushi.pamirs.user.api.spi.DefaultPamirsUserInfoCacheApi.RedisTemplateHolders.byteOps;


@Slf4j
@Order
@Component
@SPI.Service
public class DefaultPamirsUserInfoCacheApi implements PamirsUserInfoCacheApi {

    @Override
    public void init() {
        UserInfoCache.cacheInit();
    }

    @Override
    public PamirsUser queryUserById(Long userId) {
        return UserInfoCache.get().computeIfAbsent(userId, this::rawQueryUserById);
    }

    protected PamirsUser rawQueryUserById(Long userId) {
        BytesRedisTemplate redisTemplate = byteOps();
        String userInfoCacheKey = userInfoCacheKey(userId);

        byte[] bytes = redisTemplate.opsForValue().get(userInfoCacheKey);
        if (!ObjectUtils.isEmpty(bytes)) {
            return KryoUtils.deserialize(bytes, PamirsUser.class);
        } else {
            try (BatchSizeHintApi batchSize = BatchSizeHintApi.use(-1)) {
                PamirsUser user = new PamirsUser().setId(userId).queryById();
                if (user != null) {
                    user.fieldQuery(PamirsUser::getLang);
                    redisTemplate.opsForValue().set(userInfoCacheKey, KryoUtils.serialize(user), UserConstant.USER_INFO_EXPIRE_TIME, TimeUnit.SECONDS);
                }
                return user;
            }
        }
    }

    @Override
    public void putUserInfo(PamirsUser user) {
        if (user == null) {
            return;
        }
        Long userId = user.getId();
        if (userId == null) {
            return;
        }
        String userInfoCacheKey = userInfoCacheKey(user.getId());
        byteOps().opsForValue().set(userInfoCacheKey, KryoUtils.serialize(user), UserConstant.USER_INFO_EXPIRE_TIME, TimeUnit.SECONDS);
        UserInfoCache.get().put(userId, user);
    }

    @Override
    public void clearUserById(Long userId) {
        String userInfoCacheKey = userInfoCacheKey(userId);
        byteOps().delete(userInfoCacheKey);
        UserInfoCache.get().remove(userId);
    }

    private static String userInfoCacheKey(Long userId) {
        return UserConstant.USER_INFO_CACHE_KEY + userId;
    }

    static class RedisTemplateHolders {

        private RedisTemplateHolders() {
        }

        private static final BytesRedisTemplate bytesRedisTpl;

        static {
            bytesRedisTpl = new BytesRedisTemplate();
            bytesRedisTpl.setKeySerializer(BeanDefinitionUtils.getBean(PamirsStringRedisSerializer.class));
        }

        public static BytesRedisTemplate byteOps() {
            return bytesRedisTpl;
        }
    }
}
