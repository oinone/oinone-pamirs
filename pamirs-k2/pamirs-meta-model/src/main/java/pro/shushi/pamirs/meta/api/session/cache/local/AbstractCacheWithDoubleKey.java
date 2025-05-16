package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheWithDoubleKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 双key缓存抽象类
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public abstract class AbstractCacheWithDoubleKey<T> implements SessionCacheWithDoubleKey<T>, SessionCacheInitApi {

    private volatile Map<String, T> map;

    public Map<String, T> getMap() {
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
    public T get(String key1, String key2) {
        if (null == map) {
            return null;
        }
        return map.get(keyGenerator().apply(key1, key2));
    }

    @Override
    public T put(String key1, String key2, T value) {
        return map.put(keyGenerator().apply(key1, key2), value);
    }

    @Override
    public T putIfAbsent(String key, String key2, T value) {
        return map.putIfAbsent(keyGenerator().apply(key, key2), value);
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
    public T remove(String key1, String key2) {
        return map.remove(keyGenerator().apply(key1, key2));
    }

    @Override
    public Map<String, T> getAll() {
        return map;
    }

}
