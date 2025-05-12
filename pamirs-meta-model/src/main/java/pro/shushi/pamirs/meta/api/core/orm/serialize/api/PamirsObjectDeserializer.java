package pro.shushi.pamirs.meta.api.core.orm.serialize.api;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;
import java.util.function.BiFunction;

/**
 * JSON反序列化接口
 * <p>
 * 2021/9/27 12:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface PamirsObjectDeserializer extends ObjectDeserializer {

    BiFunction<Class<?>, Type, Boolean> needHandle();

}
