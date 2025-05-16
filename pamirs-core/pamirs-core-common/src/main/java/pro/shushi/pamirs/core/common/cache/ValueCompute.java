package pro.shushi.pamirs.core.common.cache;

/**
 * value compute
 *
 * @param <K> 键
 * @param <V> 值
 * @author Adamancy Zhang
 * @date 2020-11-25 12:15
 */
public interface ValueCompute<K, V> {

    /**
     * 值计算
     *
     * @param key   键
     * @param value 值
     * @return 值
     */
    V compute(K key, V value);
}
