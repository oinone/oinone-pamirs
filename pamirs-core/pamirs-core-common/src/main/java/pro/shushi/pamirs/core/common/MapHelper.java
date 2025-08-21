package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import static pro.shushi.pamirs.core.common.FetchUtil.cast;

/**
 * @author Adamancy Zhang
 * @date 2020-11-06 10:33
 */
@Slf4j
public class MapHelper {

    private MapHelper() {
        //reject create object
    }

    public static <K, V> MapBuilder<K, V, Map<K, V>> newInstance() {
        return newInstance(new HashMap<>(64));
    }

    public static <K, V, T extends Map<K, V>> MapBuilder<K, V, T> newInstance(T map) {
        return new MapBuilder<>(map);
    }

    public static class MapBuilder<K, V, T extends Map<K, V>> {

        private final T map;

        public MapBuilder(T map) {
            this.map = map;
        }

        public MapBuilder<K, V, T> put(K key, V value) {
            this.map.put(key, value);
            return this;
        }

        public MapBuilder<K, V, T> put(Map<? extends K, ? extends V> map) {
            this.map.putAll(map);
            return this;
        }

        public <O extends V> MapBuilder<K, V, T> filter(K key, O value, BiFunction<K, O, Boolean> filter) {
            if (filter.apply(key, value)) {
                this.map.put(key, value);
            }
            return this;
        }

        public T build() {
            return this.map;
        }
    }

    public static <K, V> boolean isContainAllKeys(Map<K, V> data, Collection<K> keys) {
        return allKeyPredicate(data, keys, (origin, key, value, isContain) -> isContain);
    }

    public static <K, V> boolean isContainAllValues(Map<K, V> data, Collection<K> keys) {
        return allKeyPredicate(data, keys, (origin, key, value, isContain) -> value == null);
    }

    public static <K, V> boolean allKeyPredicate(Map<K, V> data, Collection<K> keys, AllKeyPredicate<K, V> broken) {
        if (MapUtils.isEmpty(data) || CollectionUtils.isEmpty(keys)) {
            return false;
        }
        for (K key : keys) {
            V value = data.get(key);
            boolean isContain;
            if (value == null) {
                isContain = data.containsKey(key);
            } else {
                isContain = true;
            }
            if (broken.predicate(data, key, value, isContain)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 全键值判定
     *
     * @param <K> 键
     * @param <V> 值
     */
    public interface AllKeyPredicate<K, V> {

        /**
         * 执行判定
         *
         * @param origin    源数据
         * @param key       键
         * @param value     值
         * @param isContain 是否包含
         * @return 是否中断
         */
        boolean predicate(Map<K, V> origin, K key, V value, boolean isContain);
    }

    public static <K, V> void getIfPresent(Map<K, V> origin, K key, Consumer<V> consumer) {
        V value = origin.get(key);
        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * 根据表达式解析方式将指定值添加到指定map中
     *
     * @param origin 指定map
     * @param key    键
     * @param value  值
     * @return 是否添加成功
     */
    @SuppressWarnings("unchecked")
    public static boolean putIteration(Map<String, Object> origin, String key, Object value) {
        if (origin == null || key == null) {
            return false;
        }
        String[] keys = key.split("\\.");
        Map<String, Object> originPoint = origin;
        for (int i = 0; i < keys.length; i++) {
            String sk = keys[i];
            int li = sk.indexOf("[");
            int ri = sk.indexOf("]");
            Object object;
            if (i == keys.length - 1) {
                if (li == -1 && ri == -1) {
                    originPoint.put(sk, value);
                } else if (li != -1 && ri != -1) {
                    String tsk = sk.substring(0, li);
                    object = originPoint.get(tsk);
                    List<Object> listPoint;
                    if (object instanceof List) {
                        listPoint = (List<Object>) object;
                    } else {
                        //为空时创建List，否则强制替换成List
                        listPoint = new ArrayList<>();
                        originPoint.put(tsk, listPoint);
                    }
                    int index = -1;
                    String indexString = sk.substring(li + 1, ri);
                    if (StringUtils.isNotBlank(indexString)) {
                        try {
                            index = Integer.parseInt(indexString);
                        } catch (NumberFormatException ignored) {
                        }
                        if (index == -1) {
                            return false;
                        }
                        while (index >= listPoint.size()) {
                            listPoint.add(null);
                        }
                    }
                    li = ri + 1;
                    while (li < sk.length()) {
                        li = sk.indexOf("[", li);
                        ri = sk.indexOf("]", li);
                        if (li != -1 && ri != -1) {
                            indexString = sk.substring(li + 1, ri);
                            object = listPoint.get(index);
                            List<Object> tempL;
                            if (object instanceof List) {
                                tempL = (List<Object>) object;
                            } else {
                                tempL = new ArrayList<>();
                            }
                            int tempIndex = -1;
                            if (StringUtils.isNotBlank(indexString)) {
                                try {
                                    tempIndex = Integer.parseInt(indexString);
                                } catch (NumberFormatException ignored) {
                                }
                                if (tempIndex == -1) {
                                    return false;
                                }
                                while (tempIndex >= tempL.size()) {
                                    tempL.add(null);
                                }
                                listPoint.set(index, tempL);
                            } else {
                                tempL = new ArrayList<>();
                                listPoint.add(tempL);
                            }
                            listPoint = tempL;
                            index = tempIndex;
                        } else {
                            return false;
                        }
                        li = ri + 1;
                    }
                    if (index == -1) {
                        listPoint.add(value);
                    } else {
                        listPoint.set(index, value);
                    }
                } else {
                    return false;
                }
                return true;
            } else {
                if (li == -1 && ri == -1) {
                    object = originPoint.get(sk);
                    if (object instanceof Map) {
                        originPoint = (Map<String, Object>) object;
                    } else {
                        //为空时创建Map，否则强制替换成Map
                        Map<String, Object> temp = new SuperMap();
                        originPoint.put(sk, temp);
                        originPoint = temp;
                    }
                } else if (li != -1 && ri != -1) {
                    String tsk = sk.substring(0, li);
                    object = originPoint.get(tsk);
                    List<Object> listPoint;
                    if (object instanceof List) {
                        listPoint = (List<Object>) object;
                    } else {
                        //为空时创建List，否则强制替换成List
                        listPoint = new ArrayList<>();
                        originPoint.put(tsk, listPoint);
                    }
                    int index = -1;
                    String indexString = sk.substring(li + 1, ri);
                    if (StringUtils.isNotBlank(indexString)) {
                        try {
                            index = Integer.parseInt(indexString);
                        } catch (NumberFormatException ignored) {
                        }
                        if (index == -1) {
                            return false;
                        }
                        while (index >= listPoint.size()) {
                            listPoint.add(null);
                        }
                    }
                    li = ri + 1;
                    while (li < sk.length()) {
                        li = sk.indexOf("[", li);
                        ri = sk.indexOf("]", li);
                        if (li != -1 && ri != -1) {
                            indexString = sk.substring(li + 1, ri);
                            object = listPoint.get(index);
                            List<Object> tempL;
                            if (object instanceof List) {
                                tempL = (List<Object>) object;
                            } else {
                                tempL = new ArrayList<>();
                            }
                            int tempIndex = -1;
                            if (StringUtils.isNotBlank(indexString)) {
                                try {
                                    tempIndex = Integer.parseInt(indexString);
                                } catch (NumberFormatException ignored) {
                                }
                                if (tempIndex == -1) {
                                    return false;
                                }
                                while (tempIndex >= tempL.size()) {
                                    tempL.add(null);
                                }
                                listPoint.set(index, tempL);
                            } else {
                                tempL = new ArrayList<>();
                                listPoint.add(tempL);
                            }
                            listPoint = tempL;
                            index = tempIndex;
                        } else {
                            return false;
                        }
                        li = ri + 1;
                    }
                    Map<String, Object> temp = new SuperMap();
                    if (index == -1) {
                        listPoint.add(temp);
                        originPoint = temp;
                    } else {
                        object = listPoint.get(index);
                        if (object instanceof Map) {
                            originPoint = (Map<String, Object>) object;
                        } else {
                            listPoint.set(index, temp);
                            originPoint = temp;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 根据表达式解析方式从指定map中获取值
     *
     * @param origin 指定map
     * @param key    键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static Object getIteration(Map<String, Object> origin, String key) {
        if (origin == null || key == null) {
            return null;
        }
        String[] keys = key.split("\\.");
        Map<String, Object> originPoint = origin;
        for (int i = 0; i < keys.length; i++) {
            String sk = keys[i];
            int li = sk.indexOf("[");
            int ri = sk.indexOf("]");
            Object object;
            if (i == keys.length - 1) {
                if (li == -1 && ri == -1) {
                    return originPoint.get(sk);
                } else if (li != -1 && ri != -1) {
                    String tsk = sk.substring(0, li);
                    object = originPoint.get(tsk);
                    List<Object> listPoint;
                    if (object instanceof List) {
                        listPoint = (List<Object>) object;
                    } else {
                        return null;
                    }
                    String indexString = sk.substring(li + 1, ri);
                    int index = -1;
                    if (StringUtils.isNotBlank(indexString)) {
                        try {
                            index = Integer.parseInt(indexString);
                        } catch (NumberFormatException ignored) {
                        }
                        if (index == -1) {
                            return null;
                        }
                        if (index >= listPoint.size()) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    li = ri + 1;
                    while (li < sk.length()) {
                        li = sk.indexOf("[", li);
                        ri = sk.indexOf("]", li);
                        if (li != -1 && ri != -1) {
                            indexString = sk.substring(li + 1, ri);
                            object = listPoint.get(index);
                            List<Object> tempL;
                            if (object instanceof List) {
                                tempL = (List<Object>) object;
                            } else {
                                return null;
                            }
                            int tempIndex = -1;
                            if (StringUtils.isNotBlank(indexString)) {
                                try {
                                    tempIndex = Integer.parseInt(indexString);
                                } catch (NumberFormatException ignored) {
                                }
                                if (tempIndex == -1) {
                                    return null;
                                }
                                if (tempIndex >= tempL.size()) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                            listPoint = tempL;
                            index = tempIndex;
                        } else {
                            return null;
                        }
                        li = ri + 1;
                    }
                    return listPoint.get(index);
                } else {
                    return null;
                }
            } else {
                if (li == -1 && ri == -1) {
                    object = originPoint.get(sk);
                    if (object instanceof Map) {
                        originPoint = (Map<String, Object>) object;
                    } else {
                        return null;
                    }
                } else if (li != -1 && ri != -1) {
                    String tsk = sk.substring(0, li);
                    object = originPoint.get(tsk);
                    List<Object> listPoint;
                    if (object instanceof List) {
                        listPoint = (List<Object>) object;
                    } else {
                        return null;
                    }
                    String indexString = sk.substring(li + 1, ri);
                    int index = -1;
                    if (StringUtils.isNotBlank(indexString)) {
                        try {
                            index = Integer.parseInt(indexString);
                        } catch (NumberFormatException ignored) {
                        }
                        if (index == -1) {
                            return null;
                        }
                        if (index >= listPoint.size()) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    li = ri + 1;
                    while (li < sk.length()) {
                        li = sk.indexOf("[", li);
                        ri = sk.indexOf("]", li);
                        if (li != -1 && ri != -1) {
                            indexString = sk.substring(li + 1, ri);
                            object = listPoint.get(index);
                            List<Object> tempL;
                            if (object instanceof List) {
                                tempL = (List<Object>) object;
                            } else {
                                return null;
                            }
                            int tempIndex = -1;
                            if (StringUtils.isNotBlank(indexString)) {
                                try {
                                    tempIndex = Integer.parseInt(indexString);
                                } catch (NumberFormatException ignored) {
                                }
                                if (tempIndex == -1) {
                                    return null;
                                }
                                if (tempIndex >= listPoint.size()) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                            listPoint = tempL;
                            index = tempIndex;
                        } else {
                            return null;
                        }
                        li = ri + 1;
                    }
                    object = listPoint.get(index);
                    if (object instanceof Map) {
                        originPoint = (Map<String, Object>) object;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 根据表达式解析方式从指定map中获取值
     *
     * @param origin 指定map
     * @param key    键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static Object removeIteration(Map<String, Object> origin, String key) {
        if (origin == null || key == null) {
            return null;
        }
        String[] keys = key.split("\\.");
        Map<String, Object> originPoint = origin;
        for (int i = 0; i < keys.length; i++) {
            String sk = keys[i];
            int li = sk.indexOf("[");
            int ri = sk.indexOf("]");
            Object object;
            if (i == keys.length - 1) {
                if (li == -1 && ri == -1) {
                    return originPoint.remove(sk);
                } else if (li != -1 && ri != -1) {
                    String tsk = sk.substring(0, li);
                    object = originPoint.get(tsk);
                    List<Object> listPoint;
                    if (object instanceof List) {
                        listPoint = (List<Object>) object;
                    } else {
                        return null;
                    }
                    String indexString = sk.substring(li + 1, ri);
                    int index = -1;
                    if (StringUtils.isNotBlank(indexString)) {
                        try {
                            index = Integer.parseInt(indexString);
                        } catch (NumberFormatException ignored) {
                        }
                        if (index == -1) {
                            return null;
                        }
                        if (index >= listPoint.size()) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    li = ri + 1;
                    while (li < sk.length()) {
                        li = sk.indexOf("[", li);
                        ri = sk.indexOf("]", li);
                        if (li != -1 && ri != -1) {
                            indexString = sk.substring(li + 1, ri);
                            object = listPoint.get(index);
                            List<Object> tempL;
                            if (object instanceof List) {
                                tempL = (List<Object>) object;
                            } else {
                                return null;
                            }
                            int tempIndex = -1;
                            if (StringUtils.isNotBlank(indexString)) {
                                try {
                                    tempIndex = Integer.parseInt(indexString);
                                } catch (NumberFormatException ignored) {
                                }
                                if (tempIndex == -1) {
                                    return null;
                                }
                                if (tempIndex >= tempL.size()) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                            listPoint = tempL;
                            index = tempIndex;
                        } else {
                            return null;
                        }
                        li = ri + 1;
                    }
                    return listPoint.remove(index);
                } else {
                    return null;
                }
            } else {
                if (li == -1 && ri == -1) {
                    object = originPoint.get(sk);
                    if (object instanceof Map) {
                        originPoint = (Map<String, Object>) object;
                    } else {
                        return null;
                    }
                } else if (li != -1 && ri != -1) {
                    String tsk = sk.substring(0, li);
                    object = originPoint.get(tsk);
                    List<Object> listPoint;
                    if (object instanceof List) {
                        listPoint = (List<Object>) object;
                    } else {
                        return null;
                    }
                    String indexString = sk.substring(li + 1, ri);
                    int index = -1;
                    if (StringUtils.isNotBlank(indexString)) {
                        try {
                            index = Integer.parseInt(indexString);
                        } catch (NumberFormatException ignored) {
                        }
                        if (index == -1) {
                            return null;
                        }
                        if (index >= listPoint.size()) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    li = ri + 1;
                    while (li < sk.length()) {
                        li = sk.indexOf("[", li);
                        ri = sk.indexOf("]", li);
                        if (li != -1 && ri != -1) {
                            indexString = sk.substring(li + 1, ri);
                            object = listPoint.get(index);
                            List<Object> tempL;
                            if (object instanceof List) {
                                tempL = (List<Object>) object;
                            } else {
                                return null;
                            }
                            int tempIndex = -1;
                            if (StringUtils.isNotBlank(indexString)) {
                                try {
                                    tempIndex = Integer.parseInt(indexString);
                                } catch (NumberFormatException ignored) {
                                }
                                if (tempIndex == -1) {
                                    return null;
                                }
                                if (tempIndex >= listPoint.size()) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                            listPoint = tempL;
                            index = tempIndex;
                        } else {
                            return null;
                        }
                        li = ri + 1;
                    }
                    object = listPoint.get(index);
                    if (object instanceof Map) {
                        originPoint = (Map<String, Object>) object;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 使用递归方式将目标map中的所有值添加到指定map中
     *
     * @param origin 指定map
     * @param target 目标map
     * @return 是否添加成功
     */
    @SuppressWarnings("unchecked")
    public static boolean putAllIteration(Map<String, Object> origin, Map<? extends String, ?> target) {
        if (origin == null || target == null) {
            return false;
        }
        for (Map.Entry<? extends String, ?> entry : target.entrySet()) {
            String key = entry.getKey();
            Object targetValue = entry.getValue();
            if (key == null) {
                continue;
            }
            Object originValue = origin.get(key);
            if (originValue instanceof Map && targetValue instanceof Map) {
                putAllIteration((Map<String, Object>) originValue, (Map<? extends String, ?>) targetValue);
            } else {
                MapHelper.putIteration(origin, key, targetValue);
            }
        }
        return true;
    }

    /**
     * 浅拷贝
     *
     * @param map 任意Map
     * @param <K> 键
     * @param <V> 值
     * @return 新HashMap
     */
    public static <K, V> Map<K, V> shallowClone(Map<K, V> map) {
        return shallowClone(map, new HashMap<>(map.size()));
    }

    /**
     * 浅拷贝
     *
     * @param map    任意Map
     * @param newMap 新Map
     * @param <K>    键
     * @param <V>    值
     * @return 新Map
     */
    public static <K, V> Map<K, V> shallowClone(Map<K, V> map, Map<K, V> newMap) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }
        return newMap;
    }

    public static <R extends Map<String, Object>> R deepClone(Map<String, Object> map, PamirsSupplier<R> supplier) {
        R newMap = supplier.get();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                newMap.put(key, deepClone(cast(value), supplier));
            } else {
                newMap.put(key, value);
            }
        }
        return newMap;
    }

    public static <K, V> Map<K, V> shallowMerge(Map<K, V> origin, Map<K, V> target) {
        for (Map.Entry<K, V> entry : target.entrySet()) {
            V value = entry.getValue();
            if (entry.getValue() == null) {
                continue;
            }
            origin.put(entry.getKey(), value);
        }
        return origin;
    }

    @SuppressWarnings("unchecked")
    public static void deepMerge(Map<String, Object> origin, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : target.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            String key = entry.getKey();
            Object originValue = origin.get(key);
            if (value instanceof Map) {
                Map<String, Object> originMap;
                if (originValue == null) {
                    originMap = newMapInstance(value);
                    origin.put(key, originMap);
                } else if (originValue instanceof Map) {
                    originMap = (Map<String, Object>) originValue;
                } else {
                    log.warn("deep merge error. type is not same. originType={}, targetType={}", originValue.getClass(), value.getClass());
                    continue;
                }
                deepMerge(originMap, (Map<String, Object>) value);
            } else if (value instanceof Collection) {
                Collection<Object> originCollection;
                if (originValue == null) {
                    originCollection = newCollectionInstance(value);
                    origin.put(key, originCollection);
                } else if (originValue instanceof Collection) {
                    originCollection = (Collection<Object>) originValue;
                } else {
                    log.warn("deep merge error. type is not same. originType={}, targetType={}", originValue.getClass(), value.getClass());
                    continue;
                }
                originCollection.addAll((Collection<?>) value);
            } else {
                origin.put(key, value);
            }
        }
    }

    private static Map<String, Object> newMapInstance(Object value) {
        if (value instanceof LinkedHashMap) {
            return new LinkedHashMap<>();
        } else if (value instanceof ConcurrentHashMap) {
            return new ConcurrentHashMap<>();
        } else {
            return new HashMap<>();
        }
    }

    private static Collection<Object> newCollectionInstance(Object value) {
        if (value instanceof Set) {
            if (value instanceof LinkedHashSet) {
                return new LinkedHashSet<>();
            } else {
                return new HashSet<>();
            }
        } else {
            if (value instanceof LinkedList) {
                return new LinkedList<>();
            } else {
                return new ArrayList<>();
            }
        }
    }

    public static boolean isNotBlank(String key, String value) {
        return StringUtils.isNoneBlank(key, value);
    }

    /**
     * @return {@link java.util.stream.Collectors#throwingMerger}
     */
    public static <U> BinaryOperator<U> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
