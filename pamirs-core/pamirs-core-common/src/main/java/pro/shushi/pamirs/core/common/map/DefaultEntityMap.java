package pro.shushi.pamirs.core.common.map;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认双向实体映射Map
 *
 * @author Adamancy Zhang at 22:54 on 2021-08-27
 */
public class DefaultEntityMap<K, V> implements EntityMap<K, V> {

    private final Map<K, V> keyToValueMap;

    private final Map<V, K> valueToKeyMap;

    public DefaultEntityMap() {
        this(new HashMap<>(), new HashMap<>());
    }

    public DefaultEntityMap(Map<K, V> keyToValueMap, Map<V, K> valueToKeyMap) {
        this.keyToValueMap = keyToValueMap;
        this.valueToKeyMap = valueToKeyMap;
    }

    @Override
    public void put(K key, V value) {
        this.keyToValueMap.put(key, value);
        this.valueToKeyMap.put(value, key);
    }

    @Override
    public V getValueByKey(K key) {
        return this.keyToValueMap.get(key);
    }

    @Override
    public K getKeyByValue(V value) {
        return this.valueToKeyMap.get(value);
    }
}
