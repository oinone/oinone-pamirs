package pro.shushi.pamirs.meta.api.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.errorprone.annotations.CompatibleWith;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 缓存代理
 * <p>
 * 2021/8/20 12:40 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class CacheProxy<K, V> implements Cache<K, V> {

    private static final LocalCachePrefixApi CACHE_PREFIX_API = Spider.getDefaultExtension(LocalCachePrefixApi.class);

    private final Cache<K, V> cache;

    public static <K, V> CacheProxy<K, V> getInstance(Cache<K, V> cache) {
        return new CacheProxy<>(cache);
    }

    public CacheProxy(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public @Nullable V getIfPresent(@CompatibleWith("K") @NonNull Object o) {
        return cache.getIfPresent(CACHE_PREFIX_API.prefix(o));
    }

    @Override
    public @Nullable V get(@NonNull K k, @NonNull Function<? super K, ? extends V> function) {
        return cache.get(CACHE_PREFIX_API.prefix(k), function);
    }

    @Override
    public @NonNull Map<K, V> getAllPresent(@NonNull Iterable<?> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(@NonNull K k, @NonNull V v) {
        cache.put(CACHE_PREFIX_API.prefix(k), v);
    }

    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> map) {
        for (K k : map.keySet()) {
            put(CACHE_PREFIX_API.prefix(k), map.get(k));
        }
    }

    @Override
    public void invalidate(@NonNull Object o) {
        cache.invalidate(CACHE_PREFIX_API.prefix(o));
    }

    @Override
    public void invalidateAll(@NonNull Iterable<?> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public @NonNegative long estimatedSize() {
        return cache.estimatedSize();
    }

    @Override
    public @NonNull CacheStats stats() {
        return cache.stats();
    }

    @Override
    public @NonNull ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }

    @Override
    public @NonNull Policy<K, V> policy() {
        return cache.policy();
    }

}
