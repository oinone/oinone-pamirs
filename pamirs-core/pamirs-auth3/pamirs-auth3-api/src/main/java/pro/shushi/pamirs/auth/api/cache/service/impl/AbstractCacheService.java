package pro.shushi.pamirs.auth.api.cache.service.impl;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import pro.shushi.pamirs.auth.api.cache.operation.IteratorSessionCallback;
import pro.shushi.pamirs.auth.api.cache.service.StandardDeleteCacheService;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 抽象缓存服务
 *
 * @author Adamancy Zhang at 19:03 on 2024-01-22
 */
public abstract class AbstractCacheService<K, V> implements StandardDeleteCacheService<K> {

    /**
     * 获取Redis操作模板
     *
     * @return Redis操作模板
     */
    protected abstract RedisTemplate<String, V> getRedisTemplate();

    /**
     * 获取缓存Key
     *
     * @param key 指定缓存Key
     * @return 缓存Key
     */
    protected abstract String generatorCacheKey(K key);

    @Override
    public Boolean delete(K key) {
        return getRedisTemplate().delete(generatorCacheKey(key));
    }

    @Override
    public Long delete(Set<K> keys) {
        return getRedisTemplate().delete(keys.stream().map(this::generatorCacheKey).collect(Collectors.toSet()));
    }

    /**
     * <h>批量获取模板</h>
     * <p>
     * PS: operationsConsumer 仅允许执行一个Redis操作
     * </p>
     *
     * @param keys               指定缓存Key集合
     * @param operationsConsumer Redis操作
     * @param <R>                任意Redis操作返回值
     * @return 缓存结果集
     */
    @SuppressWarnings("unchecked")
    protected <R> Map<K, R> mget(Collection<K> keys, BiConsumer<RedisOperations<String, V>, String> operationsConsumer) {
        Set<String> storageKeys = new LinkedHashSet<>(keys.size());
        Set<K> filterKeys = new LinkedHashSet<>(keys.size());
        for (K key : keys) {
            filterKeys.add(key);
            storageKeys.add(generatorCacheKey(key));
        }
        if (storageKeys.isEmpty()) {
            return new HashMap<>();
        }
        List<Object> results = executePipelined(new IteratorSessionCallback<>(storageKeys, operationsConsumer));
        Map<K, R> resultMap = new LinkedHashMap<>();
        Iterator<K> filterKeyIterator = filterKeys.iterator();
        Iterator<Object> resultIterator = results.iterator();
        while (filterKeyIterator.hasNext() && resultIterator.hasNext()) {
            K key = filterKeyIterator.next();
            Object result = resultIterator.next();
            resultMap.put(key, (R) result);
        }
        if (filterKeyIterator.hasNext() || resultIterator.hasNext()) {
            throw new IllegalArgumentException("Keys and values do not match.");
        }
        return resultMap;
    }

    protected List<Object> executePipelined(SessionCallback<?> sessionCallback) {
        return getRedisTemplate().executePipelined(sessionCallback);
    }

    protected void executePipelinedWithoutResult(SessionCallback<?> sessionCallback) {
        getRedisTemplate().executePipelined(sessionCallback, null);
    }

}
