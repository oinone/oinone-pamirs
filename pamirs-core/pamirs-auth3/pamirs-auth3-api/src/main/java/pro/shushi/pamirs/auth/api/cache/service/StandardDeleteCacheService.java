package pro.shushi.pamirs.auth.api.cache.service;

import java.util.Set;

/**
 * 标准删除缓存服务
 *
 * @author Adamancy Zhang at 19:10 on 2024-01-22
 */
public interface StandardDeleteCacheService<K> {

    /**
     * 通过Key删除缓存
     *
     * @param key 指定缓存Key
     * @return 是否删除成功
     */
    Boolean delete(K key);

    /**
     * 通过Key批量删除缓存
     *
     * @param keys 指定缓存Key集合
     * @return 成功删除数量
     */
    Long delete(Set<K> keys);
}
