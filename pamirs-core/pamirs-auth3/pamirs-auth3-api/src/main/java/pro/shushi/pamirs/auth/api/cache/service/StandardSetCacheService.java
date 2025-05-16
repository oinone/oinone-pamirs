package pro.shushi.pamirs.auth.api.cache.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 标准Set类型缓存服务
 *
 * @author Adamancy Zhang at 10:19 on 2024-01-08
 */
public interface StandardSetCacheService<K, V> extends StandardDeleteCacheService<K> {

    /**
     * 通过Key获取缓存
     *
     * @param key 指定缓存Key
     * @return 缓存结果
     */
    Set<V> get(K key);

    /**
     * 通过Keys批量获取缓存
     *
     * @param keys 指定缓存Key集合
     * @return 缓存结果
     */
    Map<K, Set<V>> get(Collection<K> keys);

    /**
     * 通过Key设置缓存
     *
     * @param key      指定缓存Key
     * @param cacheSet 指定缓存集合
     */
    void set(K key, Set<V> cacheSet);

    /**
     * 通过Keys批量设置缓存
     *
     * @param keys      指定缓存Key集合
     * @param cacheSets 指定缓存集合
     */
    void set(Collection<K> keys, Collection<Set<V>> cacheSets);

    /**
     * 批量设置缓存
     *
     * @param map 指定缓存集合
     */
    void set(Map<K, Set<V>> map);

    /**
     * 通过Key追加缓存
     *
     * @param key      指定缓存Key
     * @param cacheSet 指定缓存集合
     * @return 成功追加数量
     */
    Long add(K key, Set<V> cacheSet);

    /**
     * 通过Keys批量追加缓存
     *
     * @param keys      指定缓存Key集合
     * @param cacheSets 指定缓存集合
     */
    void add(Collection<K> keys, Collection<Set<V>> cacheSets);

    /**
     * 批量追加缓存
     *
     * @param map 指定缓存集合
     */
    void add(Map<K, Set<V>> map);

    /**
     * 通过Key从缓存中移除指定缓存集合
     *
     * @param key      指定缓存Key
     * @param cacheSet 指定缓存集合
     * @return 成功移除数量
     */
    Long remove(K key, Set<V> cacheSet);

    /**
     * 通过Keys从缓存中批量移除指定缓存集合
     *
     * @param keys      指定缓存Key集合
     * @param cacheSets 指定缓存集合
     */
    void remove(Collection<K> keys, Collection<Set<V>> cacheSets);

    /**
     * 从缓存中批量移除指定缓存集合
     *
     * @param map 指定缓存集合
     */
    void remove(Map<K, Set<V>> map);
}
