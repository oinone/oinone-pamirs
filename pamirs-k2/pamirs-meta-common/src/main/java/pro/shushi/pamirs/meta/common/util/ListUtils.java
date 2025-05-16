package pro.shushi.pamirs.meta.common.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 列表操作类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/13 2:23 下午
 */
public class ListUtils {

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

    public static <T> Collection<T> filterNullItemForCollection(Collection<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        Collection<T> result = new ArrayList<>();
        for (T item : list) {
            if (null != item) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> List<T> filterNullItemForList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (null != item) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> boolean isAllNullList(Collection<T> list) {
        if (null == list || 0 == list.size()) {
            return Boolean.TRUE;
        }
        for (T item : list) {
            if (null != item) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
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

    public static <T> Collection<T> uniqueUnion(Collection<T> list1, Collection<T> list2) {
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

    public static List<String> uniqueStringListUnion(List<String> list1, List<String> list2) {
        return (List<String>) uniqueUnion(list1, list2);
    }

    /**
     * 将一组数据固定分组，每组n个元素
     *
     * @param source 要分组的数据源
     * @param n      每组n个元素
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> fixedGrouping(List<T> source, int n) {
        if (null == source || source.size() == 0 || n <= 0)
            return null;
        List<List<T>> result = new ArrayList<>();
        source = source.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        int remainder = source.size() % n;
        int size = (source.size() / n);
        for (int i = 0; i < size; i++) {
            List<T> subset = ListUtils.sub(source,i * n, (i + 1) * n);
            result.add(subset);
        }
        if (remainder > 0) {
            List<T> subset = ListUtils.sub(source,size * n, size * n + remainder);
            result.add(subset);
        }
        return result;
    }

    /**
     * stream根据字段去重
     */
    public static <T, R> List<R> transform(List<T> list , Function<T, R> mapper) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<T> nonNullList = new ArrayList<>();
        for (T e : list) {
            try {
                mapper.apply(e);
                nonNullList.add(e);
            } catch (Exception e1) {
                //过滤空指针
            }
        }
        if (CollectionUtils.isEmpty(nonNullList)) {
            return new ArrayList<>();
        }
        return nonNullList.stream().filter(Objects::nonNull).map(mapper).distinct().collect(Collectors.toList());
    }

    /**
     * 截取集合的部分
     *
     * @param <T>        集合元素类型
     * @param collection 被截取的数组
     * @param start      开始位置（包含）
     * @param end        结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回null
     */
    public static <T> List<T> sub(Collection<T> collection, int start, int end) {
        return sub(collection, start, end, 1);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空集合
     * @since 4.0.6
     */
    public static <T> List<T> sub(Collection<T> list, int start, int end, int step) {
        if (list == null) {
            return null;
        }

        return sub(new ArrayList<>(list), start, end, step);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     * @since 4.0.6
     */
    public static <T> List<T> sub(List<T> list, int start, int end, int step) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return new ArrayList<>(0);
        }

        final int size = list.size();
        if (start < 0) {
            start += size;
        }
        if (end < 0) {
            end += size;
        }
        if (start == size) {
            return new ArrayList<>(0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > size) {
            if (start >= size) {
                return new ArrayList<>(0);
            }
            end = size;
        }

        final List<T> result = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            result.add(list.get(i));
        }
        return result;
    }

}
