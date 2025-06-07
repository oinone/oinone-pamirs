package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.*;

import java.util.List;

public abstract class AbstractEipParamProcessor<T> implements IEipParamProcessor<T> {

    private IEipProcessor<IEipIntegrationInterface<T>> processor;

    private IEipConverter<T> converter;

    private IEipAuthenticationProcessor<T> authenticationProcessor;

    private IEipSerializable<T> serializable;

    private IEipDeserialization<T> deserialization;

    private IEipParamConverter<T> paramConverter;

    private IEipParamConverterCallback<T> paramConverterCallback;

    private List<IEipConvertParam<T>> convertParamList;

    private String finalResultKey;

    private IEipInOutConverter inOutConverter;

    @Override
    public IEipProcessor<IEipIntegrationInterface<T>> getProcessor() {
        return processor;
    }

    public AbstractEipParamProcessor<T> setProcessor(IEipProcessor<IEipIntegrationInterface<T>> processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public IEipConverter<T> getConverter() {
        return converter;
    }

    public AbstractEipParamProcessor<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public IEipAuthenticationProcessor<T> getAuthenticationProcessor() {
        return authenticationProcessor;
    }

    public AbstractEipParamProcessor<T> setAuthenticationProcessor(IEipAuthenticationProcessor<T> authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
        return this;
    }

    @Override
    public IEipSerializable<T> getSerializable() {
        return serializable;
    }

    public AbstractEipParamProcessor<T> setSerializable(IEipSerializable<T> serializable) {
        this.serializable = serializable;
        return this;
    }

    @Override
    public IEipDeserialization<T> getDeserialization() {
        return deserialization;
    }

    public AbstractEipParamProcessor<T> setDeserialization(IEipDeserialization<T> deserialization) {
        this.deserialization = deserialization;
        return this;
    }

    @Override
    public IEipParamConverter<T> getParamConverter() {
        return paramConverter;
    }

    public AbstractEipParamProcessor<T> setParamConverter(IEipParamConverter<T> paramConverter) {
        this.paramConverter = paramConverter;
        return this;
    }

    @Override
    public IEipParamConverterCallback<T> getParamConverterCallback() {
        return paramConverterCallback;
    }

    public AbstractEipParamProcessor<T> setParamConverterCallback(IEipParamConverterCallback<T> paramConverterCallback) {
        this.paramConverterCallback = paramConverterCallback;
        return this;
    }

    @Override
    public List<IEipConvertParam<T>> getConvertParamList() {
        return convertParamList;
    }

    public AbstractEipParamProcessor<T> setConvertParamList(List<IEipConvertParam<T>> convertParamList) {
        this.convertParamList = convertParamList;
        return this;
    }

    @Override
    public String getFinalResultKey() {
        return finalResultKey;
    }

    public AbstractEipParamProcessor<T> setFinalResultKey(String finalResultKey) {
        this.finalResultKey = finalResultKey;
        return this;
    }

    @Override
    public IEipInOutConverter getInOutConverter() {
        return inOutConverter;
    }

    public AbstractEipParamProcessor<T> setInOutConverter(IEipInOutConverter inOutConverter) {
        this.inOutConverter = inOutConverter;
        return this;
    }

    @Override
    public AbstractEipParamProcessor<T> afterProperty() {
        if (getParamConverter() == null)
            setParamConverter(getDefaultParamConverter());
        if (getSerializable() == null)
            setSerializable(getDefaultSerializable());
        if (getDeserialization() == null)
            setDeserialization(getDefaultDeserialization());
        if (getInOutConverter() == null)
            setInOutConverter(getDefaultInOutConverter());
        return this;
    }

    protected abstract IEipParamConverter<T> getDefaultParamConverter();

    protected abstract IEipSerializable<T> getDefaultSerializable();

    protected abstract IEipDeserialization<T> getDefaultDeserialization();

    protected abstract IEipInOutConverter getDefaultInOutConverter();
}
