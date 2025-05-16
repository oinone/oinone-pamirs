package pro.shushi.pamirs.core.common.cache;

import pro.shushi.pamirs.core.common.entry.InitializationBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Adamancy Zhang
 * @date 2020-12-12 17:22
 */
public abstract class AbstractMapCache<K, V> implements MapCache<K, V> {

    protected final Map<K, InitializationBody<K, V>> cache;

    protected final UniqueKeyGenerator<V, K> keyGetter;

    protected final ValueGenerator<K, V> valueGetter;

    protected final ValuePredicate<K, V> computePredication;

    protected final AtomicBoolean isRunner = new AtomicBoolean(false);

    protected AbstractMapCache(Map<K, InitializationBody<K, V>> cache,
                               UniqueKeyGenerator<V, K> keyGetter, ValueGenerator<K, V> valueGetter,
                               ValuePredicate<K, V> computePredication) {
        this.cache = cache;
        this.keyGetter = keyGetter;
        this.valueGetter = valueGetter;
        this.computePredication = computePredication;
    }

    @Override
    public Map<K, V> getCache() {
        return collectionCacheMap(v -> false);
    }

    @Override
    public Map<K, V> getComputedCache() {
        return collectionCacheMap(v -> !v.isProcessed());
    }

    @Override
    public Map<K, V> getNotComputedCache() {
        return collectionCacheMap(InitializationBody::isProcessed);
    }

    @Override
    public V compute(K key, ValueCompute<K, V> compute) {
        return computeIfAbsent(key, valueGetter, compute);
    }

    @Override
    public V computeIfAbsent(K key, ValueGenerator<K, V> getter, ValueCompute<K, V> compute) {
        return computeInitializationBody(getInitializationBody(key, getter), computePredication, compute);
    }

    @Override
    public V computeIfPresent(K key, ValueCompute<K, V> compute) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            return null;
        }
        return computeInitializationBody(body, computePredication, compute);
    }

    @Override
    public boolean refreshComputedStatus(K key) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            return false;
        }
        if (body.isProcessed()) {
            body.reprocess();
            return true;
        }
        return false;
    }

    @Override
    public boolean refreshAllComputedStatus() {
        for (InitializationBody<K, V> body : this.cache.values()) {
            body.reprocess();
        }
        return true;
    }

    @Override
    public void refresh(K key) {
        refreshIfAbsent(key, valueGetter);
    }

    @Override
    public void refreshIfAbsent(K key, ValueGenerator<K, V> getter) {
        getInitializationBody(key, getter).reprocess();
    }

    @Override
    public void refreshIfPresent(K key) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            return;
        }
        body.setValue(valueGetter.apply(key));
        body.reprocess();
    }

    @Override
    public V invalidate(K key) {
        InitializationBody<K, V> body = this.cache.remove(key);
        if (body == null) {
            return null;
        }
        return body.getValue();
    }

    @Override
    public K keyGenerator(V value) {
        return keyGetter.generator(value);
    }

    @Override
    public V get(K key) {
        return getInitializationBody(key, valueGetter).getValue();
    }

    @Override
    public V getIfAbsent(K key, ValueGenerator<K, V> getter) {
        return getInitializationBody(key, getter).getValue();
    }

    @Override
    public V getIfPresent(K key) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            return null;
        }
        return body.getValue();
    }

    protected InitializationBody<K, V> getInitializationBody(K key, ValueGenerator<K, V> getter) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            body = new InitializationBody<>(key, getter.apply(key));
            this.cache.put(key, body);
        }
        return body;
    }

    protected InitializationBody<K, V> getInitializationBodyByValue(V value) {
        return getInitializationBody(keyGenerator(value), k -> value);
    }

    protected V computeInitializationBody(InitializationBody<K, V> body, ValuePredicate<K, V> computePredication, ValueCompute<K, V> compute) {
        V value = body.getValue();
        if (body.isProcessed()) {
            return value;
        }
        K key = body.getKey();
        V computedValue;
        if (computePredication.isNeedCompute(key, value)) {
            computedValue = compute.compute(key, value);
            body.setValue(computedValue);
        } else {
            computedValue = value;
        }
        body.processed();
        return computedValue;
    }

    protected Map<K, V> collectionCacheMap(Predicate<InitializationBody<K, V>> isContinue) {
        Map<K, V> map = new HashMap<>(cache.size());
        for (Map.Entry<K, InitializationBody<K, V>> entry : cache.entrySet()) {
            InitializationBody<K, V> value = entry.getValue();
            if (isContinue.test(value)) {
                continue;
            }
            V realValue = value.getValue();
            if (realValue == null) {
                continue;
            }
            map.put(entry.getKey(), realValue);
        }
        return map;
    }

    /**
     * 排他锁（不可重复读）
     *
     * @param key      键
     * @param function 读取函数
     * @return 值
     */
    protected V exclusive(String key, Function<String, V> function) {
        if (isRunner.compareAndSet(false, true)) {
            try {
                return function.apply(key);
            } finally {
                isRunner.set(false);
            }
        }
        return null;
    }
}