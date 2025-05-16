package pro.shushi.pamirs.core.common.cache;

/**
 * 基本缓存
 *
 * @author Adamancy Zhang
 * @date 2020-12-12 17:20
 */
public interface Cache<K, V> {

    /**
     * 生成缓存键值
     *
     * @param value 值
     * @return 键
     */
    K keyGenerator(V value);

    /**
     * 获取缓存值
     *
     * @param key 键
     * @return 值
     */
    V get(K key);

    /**
     * 获取缓存值（使用指定方法获取新值）
     *
     * @param key    键
     * @param getter 获取新值getter方法
     * @return 值
     */
    V getIfAbsent(K key, ValueGenerator<K, V> getter);

    /**
     * 获取缓存值（不会生成新值）
     *
     * @param key 键
     * @return 值
     */
    V getIfPresent(K key);
}
