package pro.shushi.pamirs.meta.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 属性工具类
 * 2020/12/23 1:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AttributesUtils {

    @SuppressWarnings("unchecked")
    public static <T, R> T get(R obj,
                               Function<R, Map<?, ?>> getter, Object key) {
        Map<?, ?> attributes = getter.apply(obj);
        if (null == attributes) {
            return null;
        }
        return (T) attributes.get(key);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <R> R set(R obj,
                            Function<R, Map> getter,
                            BiFunction<R, Map, R> setter,
                            Object key, Object value) {
        Map attributes = getter.apply(obj);
        if (null == attributes) {
            attributes = new HashMap<>();
            setter.apply(obj, attributes);
        }
        attributes.put(key, value);
        return obj;
    }


    @SuppressWarnings({"rawtypes"})
    public static <R> R remove(R obj,
                               Function<R, Map> getter,
                               Object... keys) {
        Map attributes = getter.apply(obj);
        if (null == attributes) {
            return obj;
        }
        if (null != keys) {
            for (Object key : keys) {
                attributes.remove(key);
            }
        }
        return obj;
    }

}
