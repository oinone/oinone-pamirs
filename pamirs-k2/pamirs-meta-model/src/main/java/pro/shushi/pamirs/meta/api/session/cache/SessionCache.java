package pro.shushi.pamirs.meta.api.session.cache;

/**
 * session缓存接口
 * <p>
 * 2021/8/19 1:00 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SessionCache<T> {

    void clear();

    String type();

    T get(String key);

    T put(String key, T value);

    T putIfAbsent(String key, T value);

    T remove(String key);
}
