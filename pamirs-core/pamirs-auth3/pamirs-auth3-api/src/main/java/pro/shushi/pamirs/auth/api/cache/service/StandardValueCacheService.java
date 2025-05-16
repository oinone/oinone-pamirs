package pro.shushi.pamirs.auth.api.cache.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 标准Value类型缓存服务
 *
 * @author Adamancy Zhang at 14:03 on 2024-01-22
 */
public interface StandardValueCacheService<K, V> extends StandardDeleteCacheService<K> {

    /**
     * 通过Key获取缓存
     *
     * @param key 指定缓存Key
     * @return 缓存结果
     */
    V get(K key);

    /**
     * 通过Keys批量获取缓存
     *
     * @param keys 指定缓存Key集合
     * @return 缓存结果
     */
    Map<K, V> get(Collection<K> keys);

    /**
     * 通过Key设置缓存
     *
     * @param key   指定缓存Key
     * @param value 指定缓存Value
     */
    void set(K key, V value);

    /**
     * 通过Key设置有时效性的缓存
     *
     * @param key     指定缓存Key
     * @param value   指定缓存Value
     * @param timeout 超时时间
     * @param unit    超时时间单位
     */
    void set(K key, V value, long timeout, TimeUnit unit);

    /**
     * 通过Keys批量设置缓存
     *
     * @param keys   指定缓存Key集合
     * @param values 指定缓存Value集合
     */
    void set(Collection<K> keys, Collection<V> values);

    /**
     * 通过Keys批量设置有时效性的缓存
     *
     * @param keys    指定缓存Key集合
     * @param values  指定缓存Value集合
     * @param timeout 超时时间
     * @param unit    超时时间单位
     */
    void set(Collection<K> keys, Collection<V> values, long timeout, TimeUnit unit);

    /**
     * 通过Keys批量设置有时效性的缓存
     *
     * @param map 指定缓存集合
     */
    void set(Map<K, V> map);

    /**
     * 通过Keys批量设置有时效性的缓存
     *
     * @param map     指定缓存集合
     * @param timeout 超时时间
     * @param unit    超时时间单位
     */
    void set(Map<K, V> map, long timeout, TimeUnit unit);
}
