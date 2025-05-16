package pro.shushi.pamirs.auth.api.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;

/**
 * 权限RedisTemplate
 *
 * @author Adamancy Zhang at 18:49 on 2024-01-06
 */
@Component(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
public class AuthRedisTemplate<V> extends RedisTemplate<String, V> {

    public AuthRedisTemplate(
            @Autowired RedisConnectionFactory redisConnectionFactory,
            @Autowired PamirsStringRedisSerializer pamirsStringRedisSerializer
    ) {
        this.setConnectionFactory(redisConnectionFactory);
        this.setKeySerializer(new AuthRedisKeySerializer(pamirsStringRedisSerializer));
        this.setHashKeySerializer(RedisSerializer.string());
        RedisSerializer<Object> valueSerializer = new AuthRedisKryoValueSerializer();
        this.setValueSerializer(valueSerializer);
        this.setHashValueSerializer(valueSerializer);
    }
}
