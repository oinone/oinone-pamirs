package pro.shushi.pamirs.draft.cache.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import pro.shushi.pamirs.framework.common.serialize.KryoSerializer;

/**
 * 权限缓存Kryo值序列化
 *
 * @author Adamancy Zhang at 20:03 on 2024-01-06
 */
public class DraftRedisKryoValueSerializer implements RedisSerializer<Object> {

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return KryoSerializer.serialize(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return KryoSerializer.deserialize(bytes);
    }
}
