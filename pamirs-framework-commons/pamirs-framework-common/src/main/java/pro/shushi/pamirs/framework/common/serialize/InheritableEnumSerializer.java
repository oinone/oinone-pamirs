package pro.shushi.pamirs.framework.common.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class InheritableEnumSerializer implements ObjectSerializer, ObjectDeserializer {

    public static final InheritableEnumSerializer instance = new InheritableEnumSerializer();

    public InheritableEnumSerializer() {
    }

    @SuppressWarnings("unchecked")
    //反序列化过程
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        return value == null ? null : (T) BaseEnum.getEnumByValue(TypeUtils.getClass(type.getTypeName()), (String) value);
    }

    public int getFastMatchToken() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    //序列化过程
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        Object value = object;
        if (null != object && BaseEnum.class.isAssignableFrom(object.getClass())) {
            value = ((BaseEnum) object).value();
        } else if (null != object && IEnum.class.isAssignableFrom(object.getClass())) {
            value = ((IEnum) object).value();
        }
        serializer.write(value);
    }

}