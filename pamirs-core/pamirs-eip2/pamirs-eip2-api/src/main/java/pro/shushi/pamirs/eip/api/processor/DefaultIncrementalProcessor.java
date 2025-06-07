package pro.shushi.pamirs.eip.api.processor;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultIncrementalProcessor<T> implements IEipIncrementalProcessor<T> {

    private IEipConverter<T> converter;

    private IEipIncrementalParamConverter<T> incrementalParamConverter;

    private IEipIncrementalParamConverterCallback<T> incrementalParamConverterCallback;

    private List<IEipIncrementalParam> incrementalParamList;

    @Override
    public IEipConverter<T> getConverter() {
        return converter;
    }

    public DefaultIncrementalProcessor<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public IEipIncrementalParamConverter<T> getIncrementalParamConverter() {
        return incrementalParamConverter;
    }

    public DefaultIncrementalProcessor<T> setIncrementalParamConverter(IEipIncrementalParamConverter<T> incrementalParamConverter) {
        this.incrementalParamConverter = incrementalParamConverter;
        return this;
    }

    @Override
    public List<IEipIncrementalParam> getIncrementalParamList(String tags) {
        if (StringUtils.isBlank(tags)) {
            return incrementalParamList;
        }
        if (incrementalParamList == null) {
            return null;
        }
        List<IEipIncrementalParam> list = new ArrayList<>();
        for (IEipIncrementalParam incrementalParam : incrementalParamList) {
            if (tags.equals(incrementalParam.getTags())) {
                list.add(incrementalParam);
            }
        }
        return list;
    }

    public DefaultIncrementalProcessor<T> setIncrementalParamList(List<IEipIncrementalParam> incrementalParamList) {
        this.incrementalParamList = incrementalParamList;
        return this;
    }

    @Override
    public IEipIncrementalParamConverterCallback<T> getIncrementalParamConverterCallback() {
        return incrementalParamConverterCallback;
    }

    public DefaultIncrementalProcessor<T> setIncrementalParamConverterCallback(IEipIncrementalParamConverterCallback<T> incrementalParamConverterCallback) {
        this.incrementalParamConverterCallback = incrementalParamConverterCallback;
        return this;
    }

    @Override
    public void commit(IEipContext<T> context) {
    }
}
