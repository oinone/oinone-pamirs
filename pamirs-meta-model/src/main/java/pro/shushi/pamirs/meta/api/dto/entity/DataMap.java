package pro.shushi.pamirs.meta.api.dto.entity;

import com.google.common.collect.Sets;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.bit.DataMetaBit;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.constant.FieldConstants;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 数据map
 * <p>
 * 2020/6/29 7:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DataMap implements DMap, Serializable, DataMetaBit, Map<String, Object> {

    private static final long serialVersionUID = -5320889588745498481L;

    public final static Set<String> CONFLICT_KEYS = Sets.newHashSet(FieldConstants.SIZE);

    private Map<String, Object> data;

    @SuppressWarnings("unused")
    public DataMap() {
        super();
        data = new HashMap<>();
    }

    public DataMap(String model) {
        super();
        data = new HashMap<>();
        setModel(model);
    }

    public DataMap(Map<String, Object> data) {
        super();
        if (null == data) {
            data = new HashMap<>();
            this.data = data;
        } else {
            this.data = data;
        }
    }

    public String getModel() {
        return Models.api().getModel(data);
    }

    public DataMap setModel(String model) {
        Models.api().setModel(this, model);
        return this;
    }

    public DataMap setValue(String key, Object value) {
        this.put(key, value);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T, R> DataMap setValue(Getter<T, R> fn, Object value) {
        return setValue(LambdaUtil.fetchFieldName(fn), value);
    }

    public <T, R> Object getValue(Getter<T, R> fn) {
        return get(LambdaUtil.fetchFieldName(fn));
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return data.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (CONFLICT_KEYS.contains(key)) {
            data.put(key + CharacterConstants.SEPARATOR_UNDERLINE + NamespaceConstants.pamirs, value);
        }
        return data.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return data.remove(key);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void putAll(Map<? extends String, ?> m) {
        data.putAll(m);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Set<String> keySet() {
        return data.keySet();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        data.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        data.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return data.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return data.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return data.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return data.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return data.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return data.computeIfPresent(key, remappingFunction);
    }

    @SuppressWarnings("InfiniteRecursion")
    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return compute(key, remappingFunction);
    }

    @SuppressWarnings("InfiniteRecursion")
    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return merge(key, value, remappingFunction);
    }

    protected Map<String, Object> getData() {
        return this.data;
    }
}
