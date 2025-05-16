package pro.shushi.pamirs.meta.api.session.cache;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * 双key session缓存接口
 * <p>
 * 2021/8/19 1:00 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SessionCacheWithDoubleKey<T> {

    void clear();

    String type();

    BiFunction<String, String, String> keyGenerator();

    T get(String key1, String key2);

    T put(String key1, String key2, T value);

    T putIfAbsent(String key1, String key2, T value);

    T put(String key, T value);

    T putIfAbsent(String key, T value);

    T remove(String key1, String key2);

    default Map<String, T> getAll() {
        return null;
    }

}
