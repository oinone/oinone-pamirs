package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author Adamancy Zhang at 20:41 on 2021-02-27
 */
public class DefaultEipConvertParam<T> implements IEipConvertParam<T> {

    private final String inParam;

    private final String outParam;

    private Object defaultValue;

    private Boolean required;

    private Integer size;

    private final Map<String, String> convertMap;

    private ParamTypeEnum inParamType;

    private ParamTypeEnum outParamType;

    private ContextTypeEnum originContextType;

    private ContextTypeEnum targetContextType;

    private IEipParamConverterCallback<T> callback;

    public DefaultEipConvertParam(String inParam, String outParam) {
        this(inParam, outParam, new HashMap<>());
    }

    public DefaultEipConvertParam(String inParam, String outParam, Map<String, String> convertMap) {
        this.inParam = inParam;
        this.inParamType = ParamTypeEnum.OBJECT;
        this.outParam = outParam;
        this.outParamType = ParamTypeEnum.OBJECT;
        this.required = Boolean.FALSE;
        this.convertMap = convertMap;
        this.originContextType = ContextTypeEnum.INTERFACE;
        this.targetContextType = ContextTypeEnum.INTERFACE;
    }

    @Override
    public String getInParam() {
        return inParam;
    }

    @Override
    public String getOutParam() {
        return outParam;
    }

    @Override
    public Map<String, String> getConvertMap() {
        return this.convertMap;
    }

    public void convertMapForEach(BiConsumer<String, String> consumer) {
        for (Map.Entry<String, String> item : convertMap.entrySet())
            consumer.accept(item.getKey(), item.getValue());
    }

    @Override
    public String getConvertMapValue(String key) {
        return convertMap.get(key);
    }

    public DefaultEipConvertParam<T> putConvertValue(String key, String value) {
        convertMap.put(key, value);
        return this;
    }

    @Override
    public ParamTypeEnum getInParamType() {
        return inParamType;
    }

    public DefaultEipConvertParam<T> setInParamType(ParamTypeEnum inParamType) {
        this.inParamType = inParamType;
        return this;
    }

    @Override
    public ParamTypeEnum getOutParamType() {
        return outParamType;
    }

    public DefaultEipConvertParam<T> setOutParamType(ParamTypeEnum outParamType) {
        this.outParamType = outParamType;
        return this;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    public DefaultEipConvertParam<T> setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public Boolean getRequired() {
        return required;
    }

    public DefaultEipConvertParam<T> setRequired(Boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    public DefaultEipConvertParam<T> setSize(Integer size) {
        this.size = size;
        return this;
    }

    @Override
    public ContextTypeEnum getOriginContextType() {
        return originContextType;
    }

    public DefaultEipConvertParam<T> setOriginContextType(ContextTypeEnum originContextType) {
        this.originContextType = originContextType;
        return this;
    }

    @Override
    public IEipParamConverterCallback<T> getParamConverterCallback() {
        return callback;
    }

    public DefaultEipConvertParam<T> setParamConverterCallback(IEipParamConverterCallback<T> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public ContextTypeEnum getTargetContextType() {
        return targetContextType;
    }

    public DefaultEipConvertParam<T> setTargetContextType(ContextTypeEnum targetContextType) {
        this.targetContextType = targetContextType;
        return this;
    }

    @Override
    public IEipConvertParam<T> clone(String inParam, String outParam) {
        return new DefaultEipConvertParam<T>(inParam, outParam, convertMap)
                .setRequired(required)
                .setSize(size)
                .setInParamType(inParamType)
                .setOutParamType(outParamType)
                .setOriginContextType(originContextType)
                .setTargetContextType(targetContextType)
                .setParamConverterCallback(callback);
    }
}
