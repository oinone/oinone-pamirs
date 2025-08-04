package pro.shushi.pamirs.core.common;

import jakarta.annotation.Nonnull;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Adamancy Zhang on 2021-04-27 19:42
 */
public class IterationMap extends LinkedHashMap<String, Object> implements Map<String, Object>, Serializable {

    private static final long serialVersionUID = 8861452316167024825L;

    public IterationMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IterationMap(int initialCapacity) {
        super(initialCapacity);
    }

    public IterationMap() {
        super();
    }

    public IterationMap(Map<? extends String, ?> map) {
        super(map);
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return MapHelper.getIteration(this, StringHelper.valueOfNullable(key));
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        Object value = MapHelper.getIteration(this, StringHelper.valueOfNullable(key));
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        super.replaceAll(function);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (MapHelper.putIteration(this, StringHelper.valueOfNullable(key), value)) {
            return value;
        }
        return null;
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ?> m) {
        MapHelper.putAllIteration(this, m);
    }

    @Override
    public Object remove(Object key) {
        return super.remove(key);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        Object oldValue = MapHelper.getIteration(this, StringHelper.valueOfNullable(key));
        if (oldValue == null) {
            if (MapHelper.putIteration(this, StringHelper.valueOfNullable(key), value)) {
                return value;
            }
        }
        return oldValue;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return super.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return super.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return super.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return super.merge(key, value, remappingFunction);
    }
}
