package pro.shushi.pamirs.meta.util;

import pro.shushi.pamirs.meta.common.util.BitUtil;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 位工具类
 * 2020/12/23 1:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class OptionsUtils {

    public static <T> T enable(T obj,
                               Function<T, Long> getter,
                               BiFunction<T, Long, T> setter,
                               long bit) {
        Long options = getter.apply(obj);
        if (null != options) {
            options = BitUtil.enable(options, bit);
            setter.apply(obj, options);
        }
        return obj;
    }

    public static <T> T disable(T obj,
                                Function<T, Long> getter,
                                BiFunction<T, Long, T> setter,
                                long bit) {
        Long options = getter.apply(obj);
        if (null != options) {
            options = BitUtil.disable(options, bit);
            setter.apply(obj, options);
        }
        return obj;
    }

    public static <T> boolean has(T obj, Function<T, Long> getter, long bit) {
        Long options = getter.apply(obj);
        return BitUtil.has(options, bit);
    }

    public static <T> boolean check(T obj, Function<T, Long> getter) {
        Long options = getter.apply(obj);
        if (null == options) {
            return true;
        }
        return BitUtil.check(options);
    }

}
