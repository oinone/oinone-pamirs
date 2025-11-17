package pro.shushi.pamirs.framework.configure.sys;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 启动时MetaModelFetcher缓存，启动结束后清理并无法继续使用
 *
 * @author Adamancy Zhang at 19:58 on 2025-10-31
 */
public class MetaModelFetcherCache {

    private static Map<String, Object> cache = new ConcurrentHashMap<>();

    @SuppressWarnings({"unchecked"})
    public static <T> T get(String key, Supplier<T> supplier) {
        if (cache == null) {
            return supplier.get();
        }
        Object value = cache.get(key);
        if (value == null) {
            synchronized (MetaModelFetcherCache.class) {
                value = cache.get(key);
                if (value == null) {
                    value = supplier.get();
                    cache.put(key, value);
                }
            }
        }
        return (T) value;
    }

    public static void clear() {
        cache.clear();
        cache = null;
    }
}
