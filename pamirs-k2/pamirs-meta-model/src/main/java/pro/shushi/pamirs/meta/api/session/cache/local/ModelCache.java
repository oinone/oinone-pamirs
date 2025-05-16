package pro.shushi.pamirs.meta.api.session.cache.local;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.cache.api.ModelCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.extend.SessionCacheForKeySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @author wx@shushi.pro
 * @version 1.0.0
 */
public class ModelCache implements ModelCacheApi, SessionCacheForKeySet {

    private volatile Map<String/*model*/, ModelConfig> map;

    private volatile Map<String/*table*/, List<String>/*model*/> tableModelMap;

    private volatile Map<String/*name*/, String/*model*/> nameModelMap;

    @Override
    public void init() {
        map = new ConcurrentHashMap<>();
        tableModelMap = new ConcurrentHashMap<>();
        nameModelMap = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {
        if (null != map) {
            map.clear();
            map = null;
        }
        if (null != tableModelMap) {
            tableModelMap.clear();
            tableModelMap = null;
        }
        if (null != nameModelMap) {
            nameModelMap.clear();
            nameModelMap = null;
        }
    }

    @Override
    public String type() {
        return ModelConfig.class.getSimpleName();
    }

    @Override
    public ModelConfig get(String key) {
        if (null == map) {
            return null;
        }
        return map.get(key);
    }

    @Override
    public ModelConfig put(String key, ModelConfig value) {
        String table = value.getTable();
        if (StringUtils.isNotBlank(table)) {
            tableModelMap.putIfAbsent(table, new ArrayList<>());
            tableModelMap.get(table).add(key);
        }
        if (null != value.getName()) {
            nameModelMap.put(value.getName(), key);
        }
        return map.put(key, value);
    }

    @Override
    public ModelConfig putIfAbsent(String key, ModelConfig value) {
        String table = value.getTable();
        if (StringUtils.isNotBlank(table)) {
            tableModelMap.putIfAbsent(table, new ArrayList<>());
            tableModelMap.get(table).add(key);
        }
        nameModelMap.putIfAbsent(value.getName(), key);
        return map.putIfAbsent(key, value);
    }

    @Override
    public ModelConfig remove(String key) {
        ModelConfig modelConfig = map.remove(key);
        if (null != modelConfig) {
            String table = modelConfig.getTable();
            if (null != table) {
                List<String> modelsForTable = tableModelMap.get(table);
                if (null != modelsForTable) {
                    modelsForTable.remove(key);
                }
            }
            nameModelMap.remove(modelConfig.getName());
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return MapUtils.isEmpty(map);
    }

    @Override
    public ModelConfig getByName(String name) {
        String model = nameModelMap.get(name);
        if (StringUtils.isNotBlank(model)) {
            return map.get(model);
        }
        return null;
    }

    @Override
    public List<String> getModelsByTable(String table) {
        return tableModelMap.get(table);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

}
