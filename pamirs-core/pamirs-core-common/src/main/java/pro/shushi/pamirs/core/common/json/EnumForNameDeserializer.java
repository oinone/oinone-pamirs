package pro.shushi.pamirs.core.common.json;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.meta.api.core.orm.serialize.api.PamirsObjectDeserializer;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.function.BiFunction;

/**
 * Enum for name 反序列化
 *
 * @author Adamancy Zhang at 14:11 on 2021-09-13
 */
public class EnumForNameDeserializer implements PamirsObjectDeserializer {

    public final static EnumForNameDeserializer INSTANCE = new EnumForNameDeserializer();

    private EnumForNameDeserializer() {
        //reject create object
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String value = StringHelper.valueOf(parser.parse());
        Class<IEnum> clazz = TypeUtils.getClass(type.getTypeName());
        return (T) Enums.getEnum(clazz, value);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    @Override
    public BiFunction<Class<?>, Type, Boolean> needHandle() {
        return (clazz, type) -> IEnum.class.isAssignableFrom(clazz);
    }
}
