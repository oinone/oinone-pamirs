package pro.shushi.pamirs.eip.api.protocol.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pro.shushi.pamirs.core.common.SuperMap;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * MCP协议请求/响应体对象
 *
 * @author Gesi at 15:24 on 2025/8/4
 */
public class McpSuperMap extends SuperMap {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SuperMap delegate;

    public McpSuperMap() {
        this(new SuperMap());
    }

    public McpSuperMap(@NotNull SuperMap map) {
        super(0);
        this.delegate = map;
    }

    protected SuperMap getDelegate() {
        return delegate;
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return delegate.get(key);
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
        return super.removeEldestEntry(eldest);
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<Object> values() {
        return delegate.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        delegate.replaceAll(function);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public Object put(String key, Object value) {
        return delegate.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        delegate.putAll(m);
    }

    @Override
    public Object remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return delegate.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }

    @Override
    public Object clone() {
        return new McpSuperMap((SuperMap) delegate.clone());
    }

    public <T> T getMCPSchemaObject(TypeReference<T> schemaTypeReference) {
        return OBJECT_MAPPER.convertValue(this, schemaTypeReference);
    }

}
