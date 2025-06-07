package pro.shushi.pamirs.core.common.cache;

import pro.shushi.pamirs.core.common.entry.InitializationBody;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程安全的内存缓存
 *
 * @author Adamancy Zhang on 2021-03-05 11:31
 */
public class SafeCache<K, V> extends AbstractMapCache<K, V> implements MapCache<K, V> {

    public SafeCache(int initialCapacity, ValueGenerator<K, V> valueGetter) {
        super(new ConcurrentHashMap<>(initialCapacity), v -> null, valueGetter, (k, v) -> v != null);
    }

    @Override
    public K keyGenerator(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean refreshComputedStatus(K key) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            return false;
        }
        if (body.isProcessed()) {
            synchronized (this) {
                if (body.isProcessed()) {
                    body.reprocess();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public synchronized boolean refreshAllComputedStatus() {
        return super.refreshAllComputedStatus();
    }

    @Override
    protected InitializationBody<K, V> getInitializationBody(K key, ValueGenerator<K, V> getter) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            synchronized (this) {
                body = this.cache.get(key);
                if (body == null) {
                    body = new InitializationBody<>(key, getter.apply(key));
                    this.cache.put(key, body);
                }
            }
        }
        return body;
    }

    @Override
    protected V computeInitializationBody(InitializationBody<K, V> body, ValuePredicate<K, V> computePredication, ValueCompute<K, V> compute) {
        V value = body.getValue();
        V computedValue;
        if (body.isProcessed()) {
            computedValue = value;
        } else {
            synchronized (this) {
                if (body.isProcessed()) {
                    computedValue = value;
                } else {
                    K key = body.getKey();
                    if (computePredication.isNeedCompute(key, value)) {
                        computedValue = compute.compute(key, value);
                        body.setValue(computedValue);
                    } else {
                        computedValue = value;
                    }
                    body.processed();
                }
            }
        }
        return computedValue;
    }
}
