package pro.shushi.pamirs.meta.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.function.Function;

public class MapUtils {

    public static void copy(Map<String, Object> source, Map<String, Object> target) {
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
        target.putAll(source);
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

    /**
     * Map拆分 (指定分组大小)
     *
     * @param map       Map
     * @param chunkSize 每个分组的大小 (>=1)
     * @param <K>       Key
     * @param <V>       Value
     * @return 子Map列表
     */
    public static <K, V> List<Map<K, V>> splitByChunkSize(Map<K, V> map, int chunkSize) {
        if (Objects.isNull(map) || map.isEmpty() || chunkSize < 1) {
            //空map或者分组大小<1，无法拆分
            return Collections.emptyList();
        }

        int mapSize = map.size(); //键值对总数
        int groupSize = mapSize / chunkSize + (mapSize % chunkSize == 0 ? 0 : 1); //计算分组个数
        List<Map<K, V>> list = Lists.newArrayListWithCapacity(groupSize); //子Map列表

        if (chunkSize >= mapSize) { //只能分1组的情况
            list.add(map);
            return list;
        }

        int count = 0; //每个分组的组内计数
        Map<K, V> subMap = Maps.newHashMapWithExpectedSize(chunkSize); //子Map

        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (count < chunkSize) {
                //给每个分组放chunkSize个键值对，最后一个分组可能会装不满
                subMap.put(entry.getKey(), entry.getValue());
                count++; //组内计数+1
            } else {
                //结束上一个分组
                list.add(subMap); //当前分组装满了->加入列表

                //开始下一个分组
                subMap = Maps.newHashMapWithExpectedSize(chunkSize); //新的分组
                subMap.put(entry.getKey(), entry.getValue()); //添加当前键值对
                count = 1; //组内计数重置为1
            }
        }

        list.add(subMap);  //添加最后一个分组
        return list;
    }

    /**
     * Map拆分(指定分组个数)
     *
     * @param map       Map
     * @param groupSize 分组个数 (>=1)
     * @param <K>       Key
     * @param <V>       Value
     * @return 子Map列表
     */
    public static <K, V> List<Map<K, V>> splitByGroupSize(Map<K, V> map, int groupSize) {
        if (Objects.isNull(map) || map.isEmpty() || groupSize < 1) {
            //空map或者分组数<1，无法拆分
            return Collections.emptyList();
        }

        List<Map<K, V>> list = Lists.newArrayListWithCapacity(groupSize);
        if (groupSize == 1) { //只有1个分组的情况
            list.add(map);
            return list;
        }

        int mapSize = map.size(); //键值对总数
        int chunkIndex = 0; //当前分组的下标，[0, groupSize-1]
        int restCount = mapSize % groupSize; //平均后剩余的键值对数
        int chunkSize0 = mapSize / groupSize; //每个分组键值对数量
        int chunkSize1 = chunkSize0 + 1; //多分一个
        int chunkSize = chunkIndex < restCount ? chunkSize1 : chunkSize0; //实际每组的大小（前面的部分分组可能会多分1个）
        int count = 0; //每个分组的组内计数
        Map<K, V> subMap = Maps.newHashMapWithExpectedSize(chunkSize);//子Map

        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (count < chunkSize) {
                //每个分组按实际分组大小（chunkSize）加入键值对
                subMap.put(entry.getKey(), entry.getValue());
                count++; //组内计数+1
            } else {
                //结束上一个分组
                list.add(subMap); //当前分组装满了->加入列表
                chunkIndex++; //分组个数+1

                //开始下一个分组
                chunkSize = chunkIndex < restCount ? chunkSize1 : chunkSize0; //重新计算分组大小
                subMap = Maps.newHashMapWithExpectedSize(chunkSize); //新的分组
                subMap.put(entry.getKey(), entry.getValue()); //添加当前键值对
                count = 1; //组内计数重置为1
            }
        }

        list.add(subMap); //添加最后一个分组
        return list;
    }

}

