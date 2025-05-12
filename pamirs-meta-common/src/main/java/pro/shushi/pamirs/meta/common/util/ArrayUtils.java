package pro.shushi.pamirs.meta.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数组工具类
 * 2021/3/23 8:46 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ArrayUtils {

    public static Object[] toArray(Object obj) {
        if (obj instanceof Object[]) {
            return (Object[]) obj;
        } else {
            return org.apache.commons.lang3.ArrayUtils.toArray(obj);
        }
    }

    public static <T> List<T> toList(T[] array) {
        if (null == array || 0 == array.length) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(array));
    }

    public static <T> T[] filterNullItemForArray(T[] array) {
        if (org.apache.commons.lang3.ArrayUtils.isEmpty(array)) {
            return array;
        }
        List<T> result = new ArrayList<>();
        for (T item : array) {
            if (null != item) {
                result.add(item);
            }
        }
        return ListUtils.toArray(result);
    }

    public static <T> boolean hasNullItem(T[] array) {
        if (org.apache.commons.lang3.ArrayUtils.isEmpty(array)) {
            return true;
        }
        for (T item : array) {
            if (null == item) {
                return true;
            }
        }
        return false;
    }

}
