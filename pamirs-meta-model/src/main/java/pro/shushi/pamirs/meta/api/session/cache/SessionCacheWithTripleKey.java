package pro.shushi.pamirs.meta.api.session.cache;

/**
 * 三key session缓存接口
 * <p>
 * 2021/8/19 1:00 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SessionCacheWithTripleKey<T> {

    void clear();

    String type();

    TripleFunction<String, String, String, String> keyGenerator();

    T get(String key1, String key2, String key3);

    T put(String key1, String key2, String key3, T value);

    T putIfAbsent(String key1, String key2, String key3, T value);

    T put(String key, T value);

    T putIfAbsent(String key, T value);

    T remove(String key1, String key2, String key3);

}
