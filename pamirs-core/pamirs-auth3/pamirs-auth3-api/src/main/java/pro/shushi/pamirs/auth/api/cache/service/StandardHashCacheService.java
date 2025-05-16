package pro.shushi.pamirs.auth.api.cache.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 标准Hash类型缓存服务
 *
 * @author Adamancy Zhang at 16:47 on 2024-01-10
 */
public interface StandardHashCacheService<K, HK, HV> extends StandardDeleteCacheService<K> {

    /**
     * 通过Key获取缓存
     *
     * @param key 指定缓存Key
     * @return 缓存结果
     */
    Map<HK, HV> get(K key);

    /**
     * 通过Keys批量获取缓存
     *
     * @param keys 指定缓存Key集合
     * @return 缓存结果
     */
    Map<K, Map<HK, HV>> get(Collection<K> keys);

    /**
     * 通过Key设置缓存
     *
     * @param key       指定缓存Key
     * @param cacheHash 指定缓存Hash
     */
    void set(K key, Map<HK, HV> cacheHash);

    /**
     * 通过Keys批量设置缓存
     *
     * @param keys      指定缓存Key集合
     * @param cacheHash 指定缓存Hash集合
     */
    void set(Collection<K> keys, Collection<Map<HK, HV>> cacheHash);

    /**
     * 批量设置缓存
     *
     * @param map 指定缓存集合
     */
    void set(Map<K, Map<HK, HV>> map);

    /**
     * 通过Key追加指定Hash缓存
     *
     * @param key       指定缓存Key
     * @param hashKey   指定Hash键
     * @param hashValue 指定Hash值
     */
    void put(K key, HK hashKey, HV hashValue);

    /**
     * 通过Key追加指定Hash缓存（若存在则不添加）
     *
     * @param key       指定缓存Key
     * @param hashKey   指定Hash键
     * @param hashValue 指定Hash值
     * @return 是否追加成功
     */
    Boolean putIfAbsent(K key, HK hashKey, HV hashValue);

    /**
     * 通过Key追加缓存
     *
     * @param key       指定缓存Key
     * @param cacheHash 指定缓存Hash
     */
    void putAll(K key, Map<HK, HV> cacheHash);

    /**
     * 通过Keys批量追加缓存
     *
     * @param keys      指定缓存Key集合
     * @param cacheHash 指定缓存Hash集合
     */
    void putAll(Collection<K> keys, Collection<Map<HK, HV>> cacheHash);

    /**
     * 批量追加缓存
     *
     * @param map 指定缓存集合
     */
    void putAll(Map<K, Map<HK, HV>> map);

    /**
     * 通过Key从缓存中移除指定缓存集合
     *
     * @param key     指定缓存Key
     * @param hashKey 指定Hash键
     * @return 成功移除数量
     */
    Long remove(K key, HK hashKey);

    /**
     * 通过Key从缓存中移除指定缓存集合
     *
     * @param key      指定缓存Key
     * @param hashKeys 指定Hash键集合
     * @return 成功移除数量
     */
    Long remove(K key, Set<HK> hashKeys);

    /**
     * 通过Keys从缓存中批量移除指定缓存集合
     *
     * @param keys     指定缓存Key集合
     * @param hashKeys 指定缓存Hash键集合
     */
    void remove(Collection<K> keys, Collection<Set<HK>> hashKeys);
}
