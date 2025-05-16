package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.*;

import java.util.List;

public abstract class AbstractEipExceptionParamProcessor<T> implements IEipExceptionParamProcessor<T> {

    private IEipProcessor<IEipIntegrationInterface<T>> processor;

    private IEipConverter<T> converter;

    private IEipParamConverter<T> paramConverter;

    private IEipParamConverterCallback<T> paramConverterCallback;

    private List<IEipConvertParam<T>> convertParamList;

    private IEipExceptionPredict<T> exceptionPredict;

    @Override
    public IEipProcessor<IEipIntegrationInterface<T>> getProcessor() {
        return processor;
    }

    public AbstractEipExceptionParamProcessor<T> setProcessor(IEipProcessor<IEipIntegrationInterface<T>> processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public IEipConverter<T> getConverter() {
        return converter;
    }

    public AbstractEipExceptionParamProcessor<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public IEipParamConverter<T> getParamConverter() {
        return paramConverter;
    }

    public AbstractEipExceptionParamProcessor<T> setParamConverter(IEipParamConverter<T> paramConverter) {
        this.paramConverter = paramConverter;
        return this;
    }

    @Override
    public IEipParamConverterCallback<T> getParamConverterCallback() {
        return paramConverterCallback;
    }

    public AbstractEipExceptionParamProcessor<T> setParamConverterCallback(IEipParamConverterCallback<T> paramConverterCallback) {
        this.paramConverterCallback = paramConverterCallback;
        return this;
    }

    @Override
    public List<IEipConvertParam<T>> getConvertParamList() {
        return convertParamList;
    }

    public AbstractEipExceptionParamProcessor<T> setConvertParamList(List<IEipConvertParam<T>> convertParamList) {
        this.convertParamList = convertParamList;
        return this;
    }

    @Override
    public IEipExceptionPredict<T> getExceptionPredict() {
        return exceptionPredict;
    }

    public AbstractEipExceptionParamProcessor<T> setExceptionPredict(IEipExceptionPredict<T> exceptionPredict) {
        this.exceptionPredict = exceptionPredict;
        return this;
    }

    @Override
    public IEipExceptionParamProcessor<T> afterProperty() {
        if (getParamConverter() == null)
            setParamConverter(getDefaultParamConverter());

        if (getExceptionPredict() == null)
            setExceptionPredict(getDefaultExceptionPredict());

        return this;
    }

    protected abstract IEipParamConverter<T> getDefaultParamConverter();

    protected abstract IEipExceptionPredict<T> getDefaultExceptionPredict();
}
