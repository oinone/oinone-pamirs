package pro.shushi.pamirs.core.common.cache.service.impl;

import pro.shushi.pamirs.core.common.cache.NullValue;
import pro.shushi.pamirs.core.common.cache.service.SimpleCacheService;
import pro.shushi.pamirs.core.common.cache.service.template.AbstractCacheServiceTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象简单缓存服务
 *
 * @author Adamancy Zhang at 16:31 on 2021-06-20
 */
public abstract class AbstractSimpleCacheService<T> extends AbstractCacheServiceTemplate<T, T> implements SimpleCacheService<T> {

    private final Map<String, Object> cache;

    public AbstractSimpleCacheService() {
        this.cache = new ConcurrentHashMap<>(16);
    }

    public AbstractSimpleCacheService(Map<String, Object> cache) {
        this.cache = cache;
    }

    @Override
    protected T serializable(String key, T data) {
        return data;
    }

    @Override
    protected T deserialization(String key, T data) {
        return data;
    }

    @Override
    protected void setEmptyObject(String key) {
        this.cache.put(key, NullValue.getInstance());
    }

    @Override
    protected boolean isEmptyObject(String key, Object data) {
        return NullValue.getInstance().equals(data);
    }

    @Override
    protected Object getCacheData(String key) {
        return cache.get(key);
    }

    @Override
    protected void setCacheData(String key, T data) {
        cache.put(key, data);
    }

    @Override
    public void clear(String key) {
        cache.remove(key);
    }
}
