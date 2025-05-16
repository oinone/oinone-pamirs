package pro.shushi.pamirs.core.common.cache;

import java.util.HashMap;

/**
 * 线程不安全的内存缓存
 *
 * @author Adamancy Zhang on 2021-01-12 15:56
 */
public class UnsafeCache<K, V> extends AbstractMapCache<K, V> implements MapCache<K, V> {

    public UnsafeCache(int initialCapacity, ValueGenerator<K, V> valueGetter) {
        super(new HashMap<>(initialCapacity), v -> null, valueGetter, (k, v) -> v != null);
    }

    @Override
    public K keyGenerator(V value) {
        throw new UnsupportedOperationException();
    }
}