package pro.shushi.pamirs.core.common.json;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.api.PamirsObjectSerializer;
import pro.shushi.pamirs.meta.api.core.orm.serialize.config.PamirsSerializerConfig;
import pro.shushi.pamirs.meta.api.core.orm.serialize.type.EnumUsingValueSerializer;

/**
 * {@link PamirsSerializerConfig}扩展
 *
 * @author Adamancy Zhang at 19:35 on 2021-08-30
 */
@Slf4j
public class CommonSerializerConfig extends PamirsSerializerConfig {

    private static final Logger logger = LoggerFactory.getLogger(CommonSerializerConfig.class);

    public static final CommonSerializerConfig INSTANCE = new CommonSerializerConfig(EnumUsingValueSerializer.instance);

    public CommonSerializerConfig(PamirsObjectSerializer enumSerializer) {
        super(enumSerializer);
    }

    @Override
    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        if (Long.class.isAssignableFrom(clazz)) {
            ObjectSerializer serializer = fetchJSONTypeSerializer(clazz);
            if (serializer == null) {
                //这里替换了原来的序列化器
                return ToStringSerializer.INSTANCE;
            }
        }
        return super.getObjectWriter(clazz);
    }

    private ObjectSerializer fetchJSONTypeSerializer(Class<?> clazz) {
        JSONType jsonType = clazz.getAnnotation(JSONType.class);
        if (jsonType != null) {
            Class<?> serializeClass = jsonType.serializer();
            try {
                ObjectSerializer serializer = (ObjectSerializer) serializeClass.newInstance();
                this.put(clazz, serializer);
                return serializer;
            } catch (Throwable error) {
                logger.error("getSerializer error.", error);
            }
        }
        return null;
    }
}
