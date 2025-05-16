package pro.shushi.pamirs.meta.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Set工具类
 * <p>
 * 2021/2/25 11:22 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SetUtils {

    public static Set<String> newNonNullSet(Set<String> set) {
        return (null == set || set.isEmpty()) ? new HashSet<>() : new HashSet<>(set);
    }

    @SafeVarargs
    public static <T> Set<T> asSet(T... ts) {
        if (null == ts || 0 == ts.length) {
            return null;
        }
        Set<T> list = new HashSet<>(ts.length);
        Collections.addAll(list, ts);
        return list;
    }

}
