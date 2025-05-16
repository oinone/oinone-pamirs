package pro.shushi.pamirs.eip.api.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author yeshenyue on 2025/4/15 10:15.
 */
@Component(EipLongRedisTemplate.REDIS_TEMPLATE_BEAN_NAME)
public class EipLongRedisTemplate extends RedisTemplate<String, Long> {

    public static final String REDIS_TEMPLATE_BEAN_NAME = "eipRedisTemplate";

    public EipLongRedisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        this.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = RedisSerializer.string();
        this.setKeySerializer(stringSerializer);
        this.setHashKeySerializer(stringSerializer);

        Jackson2JsonRedisSerializer<Long> valueSerializer = new Jackson2JsonRedisSerializer<>(Long.class);
        valueSerializer.setObjectMapper(new ObjectMapper());
        this.setValueSerializer(valueSerializer);
        this.setHashValueSerializer(valueSerializer);

        this.afterPropertiesSet();
    }
}
