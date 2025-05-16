package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.*;

import java.util.List;

public abstract class AbstractEipOpenParamProcessor<T> implements IEipOpenParamProcessor<T> {

    private IEipConverter<T> converter;

    private IEipParamConverter<T> paramConverter;

    private IEipParamConverterCallback<T> paramConverterCallback;

    private List<IEipConvertParam<T>> convertParamList;


    @Override
    public IEipConverter<T> getConverter() {
        return converter;
    }

    public AbstractEipOpenParamProcessor<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public IEipParamConverter<T> getParamConverter() {
        return paramConverter;
    }

    public AbstractEipOpenParamProcessor<T> setParamConverter(IEipParamConverter<T> paramConverter) {
        this.paramConverter = paramConverter;
        return this;
    }

    @Override
    public IEipParamConverterCallback<T> getParamConverterCallback() {
        return paramConverterCallback;
    }

    public AbstractEipOpenParamProcessor<T> setParamConverterCallback(IEipParamConverterCallback<T> paramConverterCallback) {
        this.paramConverterCallback = paramConverterCallback;
        return this;
    }

    @Override
    public List<IEipConvertParam<T>> getConvertParamList() {
        return convertParamList;
    }

    public AbstractEipOpenParamProcessor<T> setConvertParamList(List<IEipConvertParam<T>> convertParamList) {
        this.convertParamList = convertParamList;
        return this;
    }

    @Override
    public AbstractEipOpenParamProcessor<T> afterProperty() {
        if (getParamConverter() == null)
            setParamConverter(getDefaultParamConverter());

        return this;
    }

    protected abstract IEipParamConverter<T> getDefaultParamConverter();

}
