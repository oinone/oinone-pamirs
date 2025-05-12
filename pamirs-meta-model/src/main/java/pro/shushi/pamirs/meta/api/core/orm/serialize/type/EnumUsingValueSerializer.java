package pro.shushi.pamirs.meta.api.core.orm.serialize.type;

import com.alibaba.fastjson.serializer.JSONSerializer;
import pro.shushi.pamirs.meta.api.core.orm.serialize.api.PamirsObjectSerializer;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.lang.reflect.Type;
import java.util.function.Function;

import static pro.shushi.pamirs.meta.api.core.orm.serialize.KernelJsonUtils.NULL_VALUE_USING_STRING;

/**
 * 枚举序列化
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
public class EnumUsingValueSerializer implements PamirsObjectSerializer {

    public final static EnumUsingValueSerializer instance = new EnumUsingValueSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        IEnum<?> iEnum = (IEnum<?>) object;
        Object value = null;
        if (null != iEnum) {
            value = iEnum.value();
            if (null == value) {
                value = NULL_VALUE_USING_STRING;
            }
        }
        serializer.write(value);
    }

    @Override
    public Function<Class<?>, Boolean> needHandle() {
        return IEnum.class::isAssignableFrom;
    }

}
