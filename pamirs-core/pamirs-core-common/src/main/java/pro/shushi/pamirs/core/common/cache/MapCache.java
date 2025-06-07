package pro.shushi.pamirs.core.common.cache;

import java.util.Map;

/**
 * @author Adamancy Zhang on 2021-02-27 12:02
 */
public interface MapCache<K, V> extends Cache<K, V>, RefreshCache<K, V>, ComputeCache<K, V> {

    /**
     * 获取已缓存结果集
     *
     * @return 缓存结果集
     */
    Map<K, V> getCache();

    /**
     * 获取缓存中已计算的结果集
     *
     * @return 已计算的缓存结果集
     */
    Map<K, V> getComputedCache();

    /**
     * 获取缓存中未被计算的结果集
     *
     * @return 未被计算的缓存结果集
     */
    Map<K, V> getNotComputedCache();
}
