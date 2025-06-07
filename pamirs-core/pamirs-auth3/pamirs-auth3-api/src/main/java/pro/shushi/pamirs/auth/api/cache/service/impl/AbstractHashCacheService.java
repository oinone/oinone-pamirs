package pro.shushi.pamirs.auth.api.cache.service.impl;

import pro.shushi.pamirs.auth.api.cache.operation.opthash.HashDeleteSessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.opthash.HashEntity;
import pro.shushi.pamirs.auth.api.cache.operation.opthash.HashPutSessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.opthash.HashSetSessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.optset.SetEntity;
import pro.shushi.pamirs.auth.api.cache.service.StandardHashCacheService;

import java.util.*;
import java.util.function.Function;

/**
 * 抽象Hash类型缓存服务
 *
 * @author Adamancy Zhang at 17:02 on 2024-01-10
 */
public abstract class AbstractHashCacheService<K, HK, HV> extends AbstractCacheService<K, HV> implements StandardHashCacheService<K, HK, HV> {

    @Override
    public Map<HK, HV> get(K key) {
        return getRedisTemplate().<HK, HV>opsForHash().entries(generatorCacheKey(key));
    }

    @Override
    public Map<K, Map<HK, HV>> get(Collection<K> keys) {
        return mget(keys, (operations, key) -> operations.opsForHash().entries(key));
    }

    @Override
    public void set(K key, Map<HK, HV> cacheHash) {
        String storageKey = generatorCacheKey(key);
        executePipelinedWithoutResult(new HashSetSessionCallback<>(Collections.singletonList(new HashEntity<>(storageKey, cacheHash))));
    }

    @Override
    public void set(Collection<K> keys, Collection<Map<HK, HV>> cacheHash) {
        executePipelinedWithoutResult(new HashSetSessionCallback<>(generatorHashEntities(keys, cacheHash)));
    }

    @Override
    public void set(Map<K, Map<HK, HV>> map) {
        executePipelinedWithoutResult(new HashSetSessionCallback<>(generatorHashEntities(map)));
    }

    @Override
    public void put(K key, HK hashKey, HV hashValue) {
        getRedisTemplate().<HK, HV>opsForHash().put(generatorCacheKey(key), hashKey, hashValue);
    }

    @Override
    public Boolean putIfAbsent(K key, HK hashKey, HV hashValue) {
        return getRedisTemplate().<HK, HV>opsForHash().putIfAbsent(generatorCacheKey(key), hashKey, hashValue);
    }

    @Override
    public void putAll(K key, Map<HK, HV> cacheHash) {
        getRedisTemplate().<HK, HV>opsForHash().putAll(generatorCacheKey(key), cacheHash);
    }

    @Override
    public void putAll(Collection<K> keys, Collection<Map<HK, HV>> cacheHash) {
        executePipelinedWithoutResult(new HashPutSessionCallback<>(generatorHashEntities(keys, cacheHash)));
    }

    @Override
    public void putAll(Map<K, Map<HK, HV>> map) {
        executePipelinedWithoutResult(new HashPutSessionCallback<>(generatorHashEntities(map)));
    }

    @Override
    public Long remove(K key, HK hashKey) {
        return getRedisTemplate().<HK, HV>opsForHash().delete(generatorCacheKey(key), hashKey);
    }

    @Override
    public Long remove(K key, Set<HK> hashKeys) {
        return getRedisTemplate().<HK, HV>opsForHash().delete(generatorCacheKey(key), hashKeys.toArray(new Object[0]));
    }

    @Override
    public void remove(Collection<K> keys, Collection<Set<HK>> hashKeys) {
        executePipelinedWithoutResult(new HashDeleteSessionCallback<>(generatorSetEntities(keys, hashKeys, hashKey -> hashKey.toArray(new Object[0]))));
    }

    protected List<HashEntity<HK, HV>> generatorHashEntities(Collection<K> keys, Collection<Map<HK, HV>> cacheHashCollection) {
        List<HashEntity<HK, HV>> entities = new ArrayList<>(keys.size());
        Iterator<K> keysIterator = keys.iterator();
        Iterator<Map<HK, HV>> cacheHashIterator = cacheHashCollection.iterator();
        while (keysIterator.hasNext() && cacheHashIterator.hasNext()) {
            K key = keysIterator.next();
            Map<HK, HV> cacheHash = cacheHashIterator.next();
            entities.add(new HashEntity<>(generatorCacheKey(key), cacheHash));
        }
        if (keysIterator.hasNext() || cacheHashIterator.hasNext()) {
            throw new IllegalArgumentException("Keys and cache hash list do not match.");
        }
        return entities;
    }

    protected List<HashEntity<HK, HV>> generatorHashEntities(Map<K, Map<HK, HV>> map) {
        List<HashEntity<HK, HV>> entities = new ArrayList<>(map.size());
        for (Map.Entry<K, Map<HK, HV>> entry : map.entrySet()) {
            entities.add(new HashEntity<>(generatorCacheKey(entry.getKey()), entry.getValue()));
        }
        return entities;
    }

    protected <R> List<SetEntity<R>> generatorSetEntities(Collection<K> keys, Collection<Set<HK>> cacheSets, Function<Set<HK>, R[]> cacheSetToArray) {
        List<SetEntity<R>> entities = new ArrayList<>(keys.size());
        Iterator<K> keysIterator = keys.iterator();
        Iterator<Set<HK>> cacheSetsIterator = cacheSets.iterator();
        while (keysIterator.hasNext() && cacheSetsIterator.hasNext()) {
            K key = keysIterator.next();
            Set<HK> cacheSet = cacheSetsIterator.next();
            entities.add(new SetEntity<>(generatorCacheKey(key), cacheSetToArray.apply(cacheSet)));
        }
        if (keysIterator.hasNext() || cacheSetsIterator.hasNext()) {
            throw new IllegalArgumentException("Keys and cache set list do not match.");
        }
        return entities;
    }
}
