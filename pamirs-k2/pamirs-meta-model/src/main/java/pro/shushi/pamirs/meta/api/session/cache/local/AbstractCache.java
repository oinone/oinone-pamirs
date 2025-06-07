package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.SessionCache;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存抽象类
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public abstract class AbstractCache<T> implements SessionCache<T>, SessionCacheInitApi {

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
    public T get(String key) {
        if (null == map) {
            return null;
        }
        return map.get(key);
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
    public T remove(String key) {
        return map.remove(key);
    }

}
