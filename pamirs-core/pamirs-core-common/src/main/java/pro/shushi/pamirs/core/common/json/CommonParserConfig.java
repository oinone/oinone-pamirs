package pro.shushi.pamirs.core.common.json;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.lang.reflect.Type;

/**
 * {@link }扩展
 *
 * @author Adamancy Zhang at 14:10 on 2021-09-13
 */
public class CommonParserConfig extends ParserConfig {

    private static final Logger logger = LoggerFactory.getLogger(CommonParserConfig.class);

    public static final CommonParserConfig INSTANCE = new CommonParserConfig();

    public CommonParserConfig() {
        this.setAutoTypeSupport(false);
    }

    @Override
    public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
        if (IEnum.class.isAssignableFrom(clazz)) {
            ObjectDeserializer deserializer = fetchJSONTypeDeserializer(clazz, type);
            if (deserializer == null) {
                //这里替换了原来的序列化器
                return EnumForNameDeserializer.INSTANCE;
            }
        }
        return super.getDeserializer(clazz, type);
    }

    private ObjectDeserializer fetchJSONTypeDeserializer(Class<?> clazz, Type type) {
        JSONType jsonType = clazz.getAnnotation(JSONType.class);
        if (jsonType != null) {
            Class<?> deserializerClazz = jsonType.deserializer();
            try {
                ObjectDeserializer deserializer = (ObjectDeserializer) deserializerClazz.newInstance();
                this.putDeserializer(type, deserializer);
                return deserializer;
            } catch (Throwable error) {
                logger.error("getDeserializer error.", error);
            }
        }
        return null;
    }
}
