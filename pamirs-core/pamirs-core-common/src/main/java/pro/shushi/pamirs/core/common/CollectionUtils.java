package pro.shushi.pamirs.core.common;


import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Deprecated
public class CollectionUtils {

    /**
     * 无重复集合添加
     */
    public static <T> List<T> addAll(List<T> list, Iterator<T> iterator) {
        if (null == list) {
            list = new ArrayList<>();
        }
        while(iterator.hasNext()) {
            T next = iterator.next();
            if (!list.contains(next)) {
                list.add(next);
            }
        }
        return list;
    }

    /**
     * List BigDecimal 累加
     */
    public static <T> BigDecimal sumBigDecimal(List<T> list, Function<T, BigDecimal> getFun) {
        return list.stream()
                .map(getFun)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO,  BigDecimal::add);
    }

    /**
     * stream根据字段去重
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
        List<List<T>> result = new ArrayList<List<T>>();
        source = source.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        int remainder = source.size() % n;
        int size = (source.size() / n);
        for (int i = 0; i < size; i++) {
            List<T> subset = null;
            subset = ListUtils.sub(source,i * n, (i + 1) * n);
            result.add(subset);
        }
        if (remainder > 0) {
            List<T> subset = null;
            subset = ListUtils.sub(source,size * n, size * n + remainder);
            result.add(subset);
        }
        return result;
    }

    public static <T extends IdModel> Map<Long, String> fetchNameMapById(List<Long> ids, Class<T> tClass) {
        Map<Long, T> dataMap = fetchMapByIds(ids, tClass);
        if (null == dataMap || org.apache.commons.collections4.CollectionUtils.isEmpty(dataMap.keySet())) {
            return new HashMap<>(1);
        }
        Map<Long, String> nameMap = new HashMap<>(dataMap.keySet().size());
        for (Map.Entry<Long, T> entry : dataMap.entrySet()) {
            T value = entry.getValue();
            if (null != value) {
                Object name = FieldUtils.getFieldValue(value, "name");
                if (null != name) {
                    nameMap.put(entry.getKey(), (String) name);
                }
            }
        }
        return nameMap;
    }

    /**
     * 根据id获取Map
     */
    public static <T extends IdModel> Map<Long, T> fetchMapByIds(List<Long> ids, Class<T> rClass) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(ids)) {
            return new HashMap<>(1);
        }
        String modelModel = Models.api().getModel(rClass);
        List<T> result = Models.data().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getId, ids)
                .setModel(modelModel));
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(result)) {
            return new HashMap<>(1);
        }
        return result.stream().filter(x -> null != x.getId()).collect(Collectors.toMap(T::getId, Function.identity()));
    }
}
