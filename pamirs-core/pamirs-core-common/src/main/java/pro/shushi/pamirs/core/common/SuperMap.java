package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Adamancy Zhang
 * @date 2020-12-04 23:19
 */
public class SuperMap extends LinkedHashMap<String, Object> implements Map<String, Object>, Serializable {

    private static final long serialVersionUID = 6151582463548828496L;

    public SuperMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SuperMap(int initialCapacity) {
        super(initialCapacity);
    }

    public SuperMap() {
        super();
    }

    public SuperMap(Map<? extends String, ?> map) {
        super(map);
    }

    public Object getIteration(String key) {
        return MapHelper.getIteration(this, key);
    }

    public boolean putIteration(String key, Object value) {
        return MapHelper.putIteration(this, key, value);
    }

    public boolean putAllIteration(Map<? extends String, ?> map) {
        return MapHelper.putAllIteration(this, map);
    }

    public String getString(String key) {
        return getString0(key, this::get);
    }

    public String getStringByIteration(String key) {
        return getString0(key, this::getIteration);
    }

    public Boolean getBoolean(String key) {
        return getBoolean0(key, this::get);
    }

    public Boolean getBooleanByIteration(String key) {
        return getBoolean0(key, this::getIteration);
    }

    private String getString0(String key, Function<String, Object> getter) {
        return getObject0(key, getter, StringHelper::valueOf);
    }

    private Boolean getBoolean0(String key, Function<String, Object> getter) {
        return getObject0(key, getter, BooleanHelper::isTrue);
    }

    private <V> V getObject0(String key, Function<String, Object> getter, Function<Object, V> converter) {
        Object value = getter.apply(key);
        if (value == null) {
            return null;
        }
        return converter.apply(value);
    }
}
