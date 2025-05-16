package pro.shushi.pamirs.meta.api.core.orm.serialize.type;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import pro.shushi.pamirs.meta.api.core.orm.serialize.api.PamirsObjectDeserializer;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.function.BiFunction;

/**
 * 枚举反序列化
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
public class EnumForNameDeserializer implements PamirsObjectDeserializer {

    public final static EnumForNameDeserializer instance = new EnumForNameDeserializer();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        Class<IEnum> clazz = TypeUtils.getClass(type.getTypeName());
        return (T) Enums.getEnum(clazz, (String) value);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }

    @Override
    public BiFunction<Class<?>, Type, Boolean> needHandle() {
        return (clazz, type) -> IEnum.class.isAssignableFrom(clazz);
    }

}
