package pro.shushi.pamirs.framework.connectors.event.kafka.marshalling;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import pro.shushi.pamirs.framework.connectors.event.manager.TopicClassCacheManager;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * PamirsKafkaMarshalling
 *
 * @author yakir on 2023/12/19 17:45.
 */
public class PamirsKafkaMarshalling<T extends Serializable> implements Serializer<T>, Deserializer<T> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // do nothing ...
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return this.deserialize(topic, null, data);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return JsonUtils.parseObject(new String(data, StandardCharsets.UTF_8), TopicClassCacheManager.get(topic));
    }


    @Override
    public byte[] serialize(String topic, Serializable data) {
        return serialize(topic, null, data);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Serializable data) {
        if (null != data) {
            return JsonUtils.toJSONString(data).getBytes(StandardCharsets.UTF_8);
        }
        return new byte[0];
    }

    @Override
    public void close() {
        // do nothing ...
    }
}
