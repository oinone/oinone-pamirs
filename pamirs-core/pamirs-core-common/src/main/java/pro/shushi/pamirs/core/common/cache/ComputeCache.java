package pro.shushi.pamirs.core.common.cache;

/**
 * 可计算缓存
 *
 * @author Adamancy Zhang on 2021-03-01 08:54
 */
public interface ComputeCache<K, V> extends Cache<K, V> {

    /**
     * 获取缓存值并进行首次获取计算
     *
     * @param key     键
     * @param compute 值计算函数
     * @return 值
     */
    V compute(K key, ValueCompute<K, V> compute);

    /**
     * 获取缓存值并进行首次获取计算（使用指定方法获取新值）
     *
     * @param key     键
     * @param getter  获取新值getter方法
     * @param compute 值计算函数
     * @return 值
     */
    V computeIfAbsent(K key, ValueGenerator<K, V> getter, ValueCompute<K, V> compute);

    /**
     * 获取缓存值并进行首次获取计算（不会生成新值）
     *
     * @param key     键
     * @param compute 值计算函数
     * @return 值
     */
    V computeIfPresent(K key, ValueCompute<K, V> compute);

    /**
     * 刷新计算状态
     *
     * @param key 键
     * @return 刷新结果
     */
    boolean refreshComputedStatus(K key);

    /**
     * 刷新全部计算状态
     *
     * @return 刷新结果
     */
    boolean refreshAllComputedStatus();
}
