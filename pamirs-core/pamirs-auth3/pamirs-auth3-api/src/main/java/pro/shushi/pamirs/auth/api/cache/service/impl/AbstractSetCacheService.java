package pro.shushi.pamirs.auth.api.cache.service.impl;

import pro.shushi.pamirs.auth.api.cache.operation.optset.SetAddSessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.optset.SetEntity;
import pro.shushi.pamirs.auth.api.cache.operation.optset.SetRemoveSessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.optset.SetSetSessionCallback;
import pro.shushi.pamirs.auth.api.cache.service.StandardSetCacheService;

import java.util.*;
import java.util.function.Function;

/**
 * 抽象Set类型缓存服务
 *
 * @author Adamancy Zhang at 10:58 on 2024-01-08
 */
public abstract class AbstractSetCacheService<K, V> extends AbstractCacheService<K, V> implements StandardSetCacheService<K, V> {

    /**
     * 缓存集合转换为数组
     *
     * @param cacheSet 缓存集合
     * @return 缓存数组
     */
    protected abstract V[] cacheSetToArray(Set<V> cacheSet);

    @Override
    public Set<V> get(K key) {
        return getRedisTemplate().opsForSet().members(generatorCacheKey(key));
    }

    @Override
    public Map<K, Set<V>> get(Collection<K> keys) {
        return mget(keys, (operations, key) -> operations.opsForSet().members(key));
    }

    @Override
    public void set(K key, Set<V> cacheSet) {
        String storageKey = generatorCacheKey(key);
        V[] storageCacheSet = cacheSetToArray(cacheSet);
        executePipelinedWithoutResult(new SetSetSessionCallback<>(Collections.singletonList(new SetEntity<>(storageKey, storageCacheSet))));
    }

    @Override
    public void set(Collection<K> keys, Collection<Set<V>> cacheSets) {
        executePipelinedWithoutResult(new SetSetSessionCallback<>(generatorSetEntities(keys, cacheSets)));
    }

    @Override
    public void set(Map<K, Set<V>> map) {
        executePipelinedWithoutResult(new SetSetSessionCallback<>(generatorSetEntities(map)));
    }

    @Override
    public Long add(K key, Set<V> cacheSet) {
        return getRedisTemplate().opsForSet().add(generatorCacheKey(key), cacheSetToArray(cacheSet));
    }

    @Override
    public void add(Collection<K> keys, Collection<Set<V>> cacheSets) {
        executePipelinedWithoutResult(new SetAddSessionCallback<>(generatorSetEntities(keys, cacheSets)));
    }

    @Override
    public void add(Map<K, Set<V>> map) {
        executePipelinedWithoutResult(new SetAddSessionCallback<>(generatorSetEntities(map)));
    }

    @Override
    public Long remove(K key, Set<V> cacheSet) {
        return getRedisTemplate().opsForSet().remove(generatorCacheKey(key), cacheSet.toArray(new Object[0]));
    }

    @Override
    public void remove(Collection<K> keys, Collection<Set<V>> cacheSets) {
        executePipelinedWithoutResult(new SetRemoveSessionCallback<>(generatorSetEntities(keys, cacheSets, cacheSet -> cacheSet.toArray(new Object[0]))));
    }

    @Override
    public void remove(Map<K, Set<V>> map) {
        executePipelinedWithoutResult(new SetRemoveSessionCallback<>(generatorSetEntities(map, cacheSet -> cacheSet.toArray(new Object[0]))));
    }

    protected List<SetEntity<V>> generatorSetEntities(Collection<K> keys, Collection<Set<V>> cacheSets) {
        return generatorSetEntities(keys, cacheSets, this::cacheSetToArray);
    }

    protected <R> List<SetEntity<R>> generatorSetEntities(Collection<K> keys, Collection<Set<V>> cacheSets, Function<Set<V>, R[]> cacheSetToArray) {
        List<SetEntity<R>> entities = new ArrayList<>(keys.size());
        Iterator<K> keysIterator = keys.iterator();
        Iterator<Set<V>> cacheSetsIterator = cacheSets.iterator();
        while (keysIterator.hasNext() && cacheSetsIterator.hasNext()) {
            K key = keysIterator.next();
            Set<V> cacheSet = cacheSetsIterator.next();
            entities.add(new SetEntity<>(generatorCacheKey(key), cacheSetToArray.apply(cacheSet)));
        }
        if (keysIterator.hasNext() || cacheSetsIterator.hasNext()) {
            throw new IllegalArgumentException("Keys and cache set list do not match.");
        }
        return entities;
    }

    protected List<SetEntity<V>> generatorSetEntities(Map<K, Set<V>> map) {
        return generatorSetEntities(map, this::cacheSetToArray);
    }

    protected <R> List<SetEntity<R>> generatorSetEntities(Map<K, Set<V>> map, Function<Set<V>, R[]> cacheSetToArray) {
        List<SetEntity<R>> entities = new ArrayList<>(map.size());
        for (Map.Entry<K, Set<V>> entry : map.entrySet()) {
            entities.add(new SetEntity<>(generatorCacheKey(entry.getKey()), cacheSetToArray.apply(entry.getValue())));
        }
        return entities;
    }
}
