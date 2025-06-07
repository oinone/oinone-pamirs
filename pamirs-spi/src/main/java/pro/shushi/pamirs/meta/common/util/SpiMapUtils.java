package pro.shushi.pamirs.meta.common.util;

import com.alibaba.fastjson.JSONException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.function.Function;

public class SpiMapUtils {

    public static void copy(Map<String, Object> source, Map<String, Object> target) throws JSONException {
        if (Objects.isNull(source)) {
            return;
        }
        if (Objects.isNull(target)) {
            target = new HashMap<>();
        }
        List<String> removeKey = Lists.newArrayList();
        for (Map.Entry<String, Object> m : target.entrySet()) {
            if (!source.containsKey(m.getKey())) {
                removeKey.add(m.getKey());
            }
        }
        if (CollectionUtils.isNotEmpty(removeKey)) {
            for (String key : removeKey) {
                target.put(key, null);
            }
        }
        for (Map.Entry<String, Object> m : source.entrySet()) {
            target.put(m.getKey(), m.getValue());
        }
    }


    /**
     * 移除map的空key
     *
     * @param map map
     */
    @SuppressWarnings({"rawtypes", "unused"})
    public static void removeNullKey(Map map) {
        Set set = map.keySet();
        //noinspection unchecked
        set.removeIf(Objects::isNull);
    }

    /**
     * 移除map中的value空值
     *
     * @param map map
     */
    @SuppressWarnings("rawtypes")
    public static void removeNullValue(Map map) {
        Set set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            Object obj = iterator.next();
            Object value = map.get(obj);
            if (null == value) {
                iterator.remove();
            }
        }
    }

    @SuppressWarnings("unused")
    public static Map<String, Object> listToMap(List<Map<String, Object>> mapList, String keyField) {
        Map<String, Object> map = new HashMap<>();
        for (Map<String, Object> item : mapList) {
            map.put((String) item.get(keyField), item);
        }
        return map;
    }

    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    public static <K, V> V concurrentComputeIfAbsent(Map<K, V> concurrentHashMap, K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(concurrentHashMap);
        V v = concurrentHashMap.get(key);
        if (v != null) {
            return v;
        }
        return concurrentHashMap.computeIfAbsent(key, mappingFunction);
    }

}

