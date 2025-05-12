package pro.shushi.pamirs.meta.api.core.orm.serialize.config;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.api.core.orm.serialize.api.PamirsObjectDeserializer;

import java.lang.reflect.Type;

/**
 * JSON反序列化配置
 * <p>
 * 2021/9/27 12:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsParserConfig extends ParserConfig {

    private static final Logger logger = LoggerFactory.getLogger(PamirsParserConfig.class);

    private final PamirsObjectDeserializer[] customDeserializers;

    public PamirsParserConfig(PamirsObjectDeserializer... customDeserializers) {
        this.customDeserializers = customDeserializers;
        this.setAutoTypeSupport(true);
    }

    @Override
    public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
        if (null != customDeserializers) {
            for (PamirsObjectDeserializer pamirsObjectDeserializer : customDeserializers) {
                if (pamirsObjectDeserializer.needHandle().apply(clazz, type)) {
                    ObjectDeserializer deserializer = getObjectDeserializerByAnnotation(clazz, type);
                    if (deserializer != null) return deserializer;
                    //这里替换了原来的反序列化器
                    return pamirsObjectDeserializer;
                }
            }
        }
        return super.getDeserializer(clazz, type);
    }

    private ObjectDeserializer getObjectDeserializerByAnnotation(Class<?> clazz, Type type) {
        ObjectDeserializer deserializer;
        Class<?> deserClass;
        JSONType jsonType = clazz.getAnnotation(JSONType.class);
        if (jsonType != null) {
            deserClass = jsonType.deserializer();
            try {
                deserializer = (ObjectDeserializer) deserClass.newInstance();
                this.putDeserializer(type, deserializer);
                return deserializer;
            } catch (Throwable error) {
                logger.error("getDeserializer error.", error);
                // skip
            }
        }
        return null;
    }

}