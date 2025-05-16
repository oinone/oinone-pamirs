package pro.shushi.pamirs.core.common.cache;

import pro.shushi.pamirs.core.common.entry.InitializationBody;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Adamancy Zhang
 * @date 2020-11-25 11:09
 */
public abstract class AbstractSearchCache<K, V, T> extends AbstractMapCache<K, V> implements SearchCache<K, V, T>, Cache<K, V> {

    protected T origin;

    protected AbstractSearchCache(T origin, Map<K, InitializationBody<K, V>> cache, UniqueKeyGenerator<V, K> keyGetter, ValuePredicate<K, V> computePredicate) {
        super(cache, keyGetter, key -> null, computePredicate);
        this.origin = origin;
    }

    /**
     * 获取下一个值
     *
     * @return 下一个值
     */
    protected abstract V next();

    public T getOrigin() {
        return origin;
    }

    @Override
    public void fill() {
        V value = next();
        while (value != null) {
            getInitializationBodyByValue(value);
            value = next();
        }
    }

    @Override
    public V compute(K key, ValueCompute<K, V> compute) {
        return computeInitializationBody(searchBody(key), computePredication, compute);
    }

    @Override
    public V computeIfAbsent(K key, ValueGenerator<K, V> getter, ValueCompute<K, V> compute) {
        InitializationBody<K, V> body = searchBodyNullable(key);
        if (body == null) {
            body = new InitializationBody<>(key, getter.apply(key));
            this.cache.put(key, body);
        }
        return computeInitializationBody(body, computePredication, compute);
    }

    @Override
    public void refresh(K key) {
        searchBody(key).reprocess();
    }

    @Override
    public void refreshIfAbsent(K key, ValueGenerator<K, V> getter) {
        InitializationBody<K, V> body = searchBodyNullable(key);
        if (body == null) {
            body = new InitializationBody<>(key, getter.apply(key));
            this.cache.put(key, body);
        }
        body.reprocess();
    }

    @Override
    public void refreshIfPresent(K key) {
        InitializationBody<K, V> body = searchBodyNullable(key);
        if (body == null) {
            return;
        }
        body.reprocess();
    }

    @Override
    public V get(K key) {
        return searchBody(key).getValue();
    }

    @Override
    public V getIfAbsent(K key, ValueGenerator<K, V> getter) {
        InitializationBody<K, V> body = searchBodyNullable(key);
        if (body == null) {
            body = new InitializationBody<>(key, getter.apply(key));
            this.cache.put(key, body);
        }
        return body.getValue();
    }

    @Override
    public V getIfPresent(K key) {
        InitializationBody<K, V> body = searchBodyNullable(key);
        if (body == null) {
            return null;
        }
        return body.getValue();
    }

    protected InitializationBody<K, V> searchBodyNullable(K key) {
        return searchBody(key, k -> null);
    }

    protected InitializationBody<K, V> searchBodyAndGetter(K key, Function<K, V> getter) {
        return searchBody(key, k -> {
            InitializationBody<K, V> body = new InitializationBody<>(key, getter.apply(key));
            this.cache.put(key, body);
            return body;
        });
    }

    protected InitializationBody<K, V> searchBody(K key) {
        return searchBody(key, k -> {
            InitializationBody<K, V> nullBody = new InitializationBody<>(key, null);
            this.cache.put(key, nullBody);
            return nullBody;
        });
    }

    protected InitializationBody<K, V> searchBody(K key, Function<K, InitializationBody<K, V>> ifAbsentFunction) {
        InitializationBody<K, V> body = this.cache.get(key);
        if (body == null) {
            V value = next();
            while (value != null) {
                body = getInitializationBodyByValue(value);
                if (key.equals(body.getKey())) {
                    return body;
                }
                value = next();
            }
            body = ifAbsentFunction.apply(key);
        }
        return body;
    }
}
