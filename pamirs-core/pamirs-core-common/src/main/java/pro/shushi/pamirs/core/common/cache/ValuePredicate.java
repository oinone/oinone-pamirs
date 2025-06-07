package pro.shushi.pamirs.core.common.cache;

/**
 * value predicate
 *
 * @param <K> 键
 * @param <V> 值
 * @author Adamancy Zhang
 * @date 2020-11-25 11:18
 */
public interface ValuePredicate<K, V> {

    /**
     * 值判定
     *
     * @param key   键
     * @param value 值
     * @return 判定是否需要计算
     */
    boolean isNeedCompute(K key, V value);
}