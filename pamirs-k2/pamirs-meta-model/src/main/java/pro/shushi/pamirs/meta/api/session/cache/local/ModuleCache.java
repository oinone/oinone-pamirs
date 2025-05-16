package pro.shushi.pamirs.meta.api.session.cache.local;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.session.cache.api.ModuleCacheApi;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模块缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModuleCache implements ModuleCacheApi {

    private volatile Map<String/*module*/, ModuleDefinition> map;

    private volatile Map<String/*name*/, String/*module*/> nameModuleMap;

    @Override
    public void init() {
        map = new ConcurrentHashMap<>();
        nameModuleMap = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {
        if (null != map) {
            map.clear();
            map = null;
        }
        if (null != nameModuleMap) {
            nameModuleMap.clear();
            nameModuleMap = null;
        }
    }

    @Override
    public String type() {
        return ModuleDefinition.class.getSimpleName();
    }

    @Override
    public ModuleDefinition get(String key) {
        return map.get(key);
    }

    @Override
    public ModuleDefinition getByName(String name) {
        String module = nameModuleMap.get(name);
        if (StringUtils.isNotBlank(module)) {
            return map.get(module);
        }
        return null;
    }

    @Override
    public ModuleDefinition put(String key, ModuleDefinition value) {
        nameModuleMap.put(value.getName(), key);
        return map.put(key, value);
    }

    @Override
    public ModuleDefinition putIfAbsent(String key, ModuleDefinition value) {
        nameModuleMap.putIfAbsent(value.getName(), key);
        return map.putIfAbsent(key, value);
    }

    @Override
    public ModuleDefinition remove(String key) {
        ModuleDefinition moduleDefinition = map.remove(key);
        if (null != moduleDefinition) {
            nameModuleMap.remove(moduleDefinition.getName());
        }
        return moduleDefinition;
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }
}
