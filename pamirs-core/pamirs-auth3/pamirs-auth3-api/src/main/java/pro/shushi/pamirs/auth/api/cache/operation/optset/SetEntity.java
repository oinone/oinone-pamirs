package pro.shushi.pamirs.auth.api.cache.operation.optset;

/**
 * Set类型缓存实体
 *
 * @param <V> 缓存值类型
 * @author Adamancy Zhang at 11:24 on 2024-01-08
 */
public class SetEntity<V> {

    private final String key;

    private final V[] cacheSet;

    public SetEntity(String key, V[] cacheSet) {
        this.key = key;
        this.cacheSet = cacheSet;
    }

    public String getKey() {
        return key;
    }

    public V[] getCacheSet() {
        return cacheSet;
    }
}