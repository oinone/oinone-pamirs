package pro.shushi.pamirs.ux.draft.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.ux.draft.constant.DraftConstants;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;

/**
 * 草稿 RedisTemplate
 *
 * @author Adamancy Zhang at 12:25 on 2025-10-21
 */
@Component(DraftConstants.REDIS_TEMPLATE_BEAN_NAME)
public class DraftRedisTemplate<V> extends RedisTemplate<String, V> {

    public DraftRedisTemplate(
            @Autowired RedisConnectionFactory redisConnectionFactory,
            @Autowired PamirsStringRedisSerializer pamirsStringRedisSerializer
    ) {
        this.setConnectionFactory(redisConnectionFactory);
        this.setKeySerializer(new DraftRedisKeySerializer(pamirsStringRedisSerializer));
        this.setHashKeySerializer(RedisSerializer.string());
        RedisSerializer<Object> valueSerializer = new DraftRedisKryoValueSerializer();
        this.setValueSerializer(valueSerializer);
        this.setHashValueSerializer(valueSerializer);
    }
}
