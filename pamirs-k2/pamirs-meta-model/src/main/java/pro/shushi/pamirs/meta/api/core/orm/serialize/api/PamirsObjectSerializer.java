package pro.shushi.pamirs.meta.api.core.orm.serialize.api;

import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.util.function.Function;

/**
 * JSON序列化接口
 * <p>
 * 2021/9/27 12:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface PamirsObjectSerializer extends ObjectSerializer {

    Function<Class<?>, Boolean> needHandle();

}
