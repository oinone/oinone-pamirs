package pro.shushi.pamirs.meta.api.dto.entity;

import pro.shushi.pamirs.meta.common.lambda.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据map封装类
 * <p>
 * 2020/6/29 7:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class MapWrapper {

    private DataMap dataMap;

    public static MapWrapper wrap(Map<String, Object> data) {
        return new MapWrapper(data);
    }

    public static MapWrapper wrap(Map<String, Object> data, String model) {
        return new MapWrapper(data).setModel(model);
    }

    @SuppressWarnings("unused")
    public MapWrapper() {
        super();
        dataMap = new DataMap(new HashMap<>());
    }

    public MapWrapper(String model) {
        super();
        dataMap = new DataMap(new HashMap<>());
        setModel(model);
    }

    public MapWrapper(Map<String, Object> data) {
        super();
        dataMap = new DataMap(data);
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public Map<String, Object> getData() {
        return dataMap.getData();
    }

    public String getModel() {
        return dataMap.getModel();
    }

    public MapWrapper setModel(String model) {
        dataMap.setModel(model);
        return this;
    }

    public MapWrapper setValue(String key, Object value) {
        dataMap.put(key, value);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T, R> MapWrapper setValue(Getter<T, R> fn, Object value) {
        dataMap.setValue(fn, value);
        return this;
    }

    public <T, R> Object getValue(Getter<T, R> fn) {
        return dataMap.getValue(fn);
    }

}
