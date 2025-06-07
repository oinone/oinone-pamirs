package pro.shushi.pamirs.auth.api.cache.operation.optvalue;

/**
 * Value类型缓存实体
 *
 * @author Adamancy Zhang at 14:14 on 2024-01-22
 */
public class ValueEntity<V> {

    private final String key;

    private final V value;

    public ValueEntity(String key, V value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
