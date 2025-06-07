package pro.shushi.pamirs.auth.api.cache.service.impl;

import pro.shushi.pamirs.auth.api.cache.operation.optvalue.ValueEntity;
import pro.shushi.pamirs.auth.api.cache.operation.optvalue.ValueSetSessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.optvalue.ValueWithTimeoutEntity;
import pro.shushi.pamirs.auth.api.cache.operation.optvalue.ValueWithTimeoutSetSessionCallback;
import pro.shushi.pamirs.auth.api.cache.service.StandardValueCacheService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * 抽象Value类型缓存服务
 *
 * @author Adamancy Zhang at 14:06 on 2024-01-22
 */
public abstract class AbstractValueCacheService<K, V> extends AbstractCacheService<K, V> implements StandardValueCacheService<K, V> {

    @Override
    public V get(K key) {
        return getRedisTemplate().opsForValue().get(generatorCacheKey(key));
    }

    @Override
    public Map<K, V> get(Collection<K> keys) {
        return mget(keys, (operations, key) -> operations.opsForValue().get(key));
    }

    @Override
    public void set(K key, V value) {
        getRedisTemplate().opsForValue().set(generatorCacheKey(key), value);
    }

    @Override
    public void set(K key, V value, long expire, TimeUnit expireUnit) {
        getRedisTemplate().opsForValue().set(generatorCacheKey(key), value, expire, expireUnit);
    }

    @Override
    public void set(Collection<K> keys, Collection<V> values) {
        executePipelinedWithoutResult(new ValueSetSessionCallback<>(generatorSetEntities(keys, values)));
    }

    @Override
    public void set(Collection<K> keys, Collection<V> values, long timeout, TimeUnit unit) {
        executePipelinedWithoutResult(new ValueWithTimeoutSetSessionCallback<>(generatorSetEntities(keys, values, timeout, unit)));
    }

    @Override
    public void set(Map<K, V> map) {
        executePipelinedWithoutResult(new ValueSetSessionCallback<>(generatorSetEntities(map)));
    }

    @Override
    public void set(Map<K, V> map, long timeout, TimeUnit unit) {
        executePipelinedWithoutResult(new ValueWithTimeoutSetSessionCallback<>(generatorSetEntities(map, timeout, unit)));
    }

    protected List<ValueEntity<V>> generatorSetEntities(Collection<K> keys, Collection<V> values) {
        return this.generatorSetEntities(keys, values, ValueEntity::new);
    }

    protected List<ValueWithTimeoutEntity<V>> generatorSetEntities(Collection<K> keys, Collection<V> values, long timeout, TimeUnit unit) {
        return this.generatorSetEntities(keys, values, (key, value) -> new ValueWithTimeoutEntity<>(key, value, timeout, unit));
    }

    protected <E extends ValueEntity<V>> List<E> generatorSetEntities(Collection<K> keys, Collection<V> values, BiFunction<String, V, E> generatorEntityFunction) {
        List<E> entities = new ArrayList<>(keys.size());
        Iterator<K> keyIterator = keys.iterator();
        Iterator<V> valueIterator = values.iterator();
        while (keyIterator.hasNext() && valueIterator.hasNext()) {
            entities.add(generatorEntityFunction.apply(generatorCacheKey(keyIterator.next()), valueIterator.next()));
        }
        if (keyIterator.hasNext() || valueIterator.hasNext()) {
            throw new IllegalArgumentException("Keys and values do not match.");
        }
        return entities;
    }

    protected List<ValueEntity<V>> generatorSetEntities(Map<K, V> map) {
        return generatorSetEntities(map, ValueEntity::new);
    }

    protected List<ValueWithTimeoutEntity<V>> generatorSetEntities(Map<K, V> map, long timeout, TimeUnit unit) {
        return generatorSetEntities(map, (key, value) -> new ValueWithTimeoutEntity<>(key, value, timeout, unit));
    }

    protected <E extends ValueEntity<V>> List<E> generatorSetEntities(Map<K, V> map, BiFunction<String, V, E> generatorEntityFunction) {
        List<E> entities = new ArrayList<>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            entities.add(generatorEntityFunction.apply(generatorCacheKey(entry.getKey()), entry.getValue()));
        }
        return entities;
    }
}
