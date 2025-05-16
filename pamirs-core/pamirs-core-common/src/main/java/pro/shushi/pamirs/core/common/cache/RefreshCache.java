package pro.shushi.pamirs.core.common.cache;

/**
 * 可刷新缓存
 *
 * @author Adamancy Zhang on 2021-03-01 08:52
 */
public interface RefreshCache<K, V> extends Cache<K, V> {

    /**
     * 刷新缓存值
     *
     * @param key 键
     */
    void refresh(K key);

    /**
     * 刷新缓存值（使用指定方法获取新值）
     *
     * @param key    键
     * @param getter 获取新值getter方法
     */
    void refreshIfAbsent(K key, ValueGenerator<K, V> getter);

    /**
     * 刷新缓存值（不会生成新值）
     *
     * @param key 键
     */
    void refreshIfPresent(K key);

    /**
     * 清理缓存
     *
     * @param key 键
     * @return 旧值
     */
    V invalidate(K key);
}
