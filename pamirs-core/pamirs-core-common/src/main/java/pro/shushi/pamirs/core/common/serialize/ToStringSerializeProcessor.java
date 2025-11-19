package pro.shushi.pamirs.core.common.serialize;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;

/**
 * ToString 序列化处理器
 *
 * @author Adamancy Zhang at 11:16 on 2025-10-21
 */
@Slf4j
@Component
public class ToStringSerializeProcessor implements Serializer<Object, String> {

    public static final String TYPE = "TO_STRING";

    @Override
    public Object serialize(String ltype, Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    @Override
    public Object deserialize(String ltype, String ltypeT, String value, String format) {
        return value;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
