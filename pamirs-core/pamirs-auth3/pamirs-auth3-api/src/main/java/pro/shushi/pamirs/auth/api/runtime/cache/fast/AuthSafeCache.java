package pro.shushi.pamirs.auth.api.runtime.cache.fast;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 权限线程安全缓存
 *
 * @author Adamancy Zhang at 21:02 on 2024-01-20
 */
public class AuthSafeCache<K> {

    private final Map<K, Object> CACHE = new ConcurrentHashMap<>(16);

    @SuppressWarnings("unchecked")
    public <T> T compute(K key, Function<T, T> fetcher) {
        return (T) CACHE.compute(key, (k, v) -> fetcher.apply((T) v));
    }

    @SuppressWarnings("unchecked")
    public <T> T computeIfAbsent(K key, Supplier<T> fetcher) {
        return (T) CACHE.computeIfAbsent(key, (k) -> fetcher.get());
    }

    public Set<Map.Entry<K, Object>> entrySet() {
        return CACHE.entrySet();
    }
}
