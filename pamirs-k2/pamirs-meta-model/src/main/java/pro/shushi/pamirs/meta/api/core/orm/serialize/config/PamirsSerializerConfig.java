package pro.shushi.pamirs.meta.api.core.orm.serialize.config;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.api.core.orm.serialize.api.PamirsObjectSerializer;

/**
 * JSON序列化配置
 * <p>
 * 2021/9/27 12:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsSerializerConfig extends SerializeConfig {

    private static final Logger logger = LoggerFactory.getLogger(PamirsParserConfig.class);

    private final PamirsObjectSerializer[] customSerializers;

    public PamirsSerializerConfig(PamirsObjectSerializer... customSerializers) {
        this.customSerializers = customSerializers;
    }

    @Override
    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        if (null != customSerializers) {
            for (PamirsObjectSerializer pamirsObjectSerializer : customSerializers) {
                if (pamirsObjectSerializer.needHandle().apply(clazz)) {
                    ObjectSerializer serializer = getObjectSerializerByAnnotation(clazz);
                    if (serializer != null) return serializer;
                    //这里替换了原来的序列化器
                    return pamirsObjectSerializer;
                }
            }
        }
        return super.getObjectWriter(clazz);
    }

    private ObjectSerializer getObjectSerializerByAnnotation(Class<?> clazz) {
        ObjectSerializer serializer;
        Class<?> serializeClass;
        JSONType jsonType = clazz.getAnnotation(JSONType.class);
        if (jsonType != null) {
            serializeClass = jsonType.serializer();
            try {
                serializer = (ObjectSerializer) serializeClass.newInstance();
                this.put(clazz, serializer);
                return serializer;
            } catch (Throwable error) {
                logger.error("getDeserializer error.", error);
                // skip
            }
        }
        return null;
    }

}