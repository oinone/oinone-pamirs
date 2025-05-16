package pro.shushi.pamirs.meta.common.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 列表操作类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/13 2:23 下午
 */
public class SpiListUtils {

    @SafeVarargs
    public static <T> List<T> asList(T... ts) {
        if (null == ts || 0 == ts.length) {
            return null;
        }
        List<T> list = new ArrayList<>(ts.length);
        Collections.addAll(list, ts);
        return list;
    }

    public static <T> List<T> toList(Object ts) {
        if (null == ts) {
            return null;
        }
        List<T> list = new ArrayList<>();
        if (Collection.class.isAssignableFrom(ts.getClass())) {
            // noinspection unchecked
            list.addAll((Collection<T>) ts);
        } else if (ts.getClass().isArray()) {
            //noinspection unchecked
            list.addAll(Arrays.asList((T[]) ts));
        } else {
            //noinspection unchecked
            list.add((T) ts);
        }
        return list;
    }

    public static <T> T[] toArray(List<T> list) {
        if (list != null && !list.isEmpty()) {
            @SuppressWarnings("unchecked") T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
            list.toArray(array);
            return array;
        }
        return null;
    }

    public static <T> T[] toArray(Class<?> cls, Collection<T> list) {
        if (list != null && !list.isEmpty()) {
            @SuppressWarnings("unchecked") T[] array = (T[]) Array.newInstance(cls, list.size());
            list.toArray(array);
            return array;
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static <T> boolean isNullOrEmpty(Object ts) {
        if (null == ts) {
            return true;
        }
        if (Collection.class.isAssignableFrom(ts.getClass())) {
            // noinspection unchecked
            return CollectionUtils.isEmpty((Collection<T>) ts);
        } else if (ts.getClass().isArray()) {
            //noinspection unchecked
            return ArrayUtils.isEmpty((T[]) ts);
        }
        return true;
    }

    public static boolean isNoNullArray(Object... array) {
        if (null == array || 0 == array.length) {
            return Boolean.FALSE;
        }
        boolean isNoNull = Boolean.TRUE;
        for (Object arg : array) {
            if (null == arg) {
                isNoNull = Boolean.FALSE;
                break;
            }
        }
        return isNoNull;
    }

    public static List<String> uniqueUnion(List<String> list1, List<String> list2) {
        if (CollectionUtils.isEmpty(list2)) {
            return list1;
        }
        if (CollectionUtils.isEmpty(list1)) {
            return new ArrayList<>(list2);
        }
        list1.removeAll(list2);
        list1.addAll(list2);
        return list1;
    }

}
