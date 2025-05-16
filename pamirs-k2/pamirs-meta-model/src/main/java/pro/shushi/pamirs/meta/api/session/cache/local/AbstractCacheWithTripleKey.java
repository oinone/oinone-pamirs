package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheWithTripleKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 三key缓存抽象类
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public abstract class AbstractCacheWithTripleKey<T> implements SessionCacheWithTripleKey<T>, SessionCacheInitApi {

    private volatile Map<String, T> map;

    protected Map<String, T> getMap() {
        return map;
    }

    @Override
    public void init() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {
        if (null != map) {
            map.clear();
            map = null;
        }
    }

    @Override
    public T get(String key1, String key2, String key3) {
        if (null == map) {
            return null;
        }
        return map.get(keyGenerator().apply(key1, key2, key3));
    }

    @Override
    public T put(String key1, String key2, String key3, T value) {
        return map.put(keyGenerator().apply(key1, key2, key3), value);
    }

    @Override
    public T putIfAbsent(String key, String key2, String key3, T value) {
        return map.putIfAbsent(keyGenerator().apply(key, key2, key3), value);
    }

    @Override
    public T put(String key, T value) {
        return map.put(key, value);
    }

    @Override
    public T putIfAbsent(String key, T value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public T remove(String key1, String key2, String key3) {
        return map.remove(keyGenerator().apply(key1, key2, key3));
    }

}
