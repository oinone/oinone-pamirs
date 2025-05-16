package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.eip.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractEipExceptionParamProcessorBuilder<T> extends AbstractBaseBuilder<T> {

    protected Function<IEipIntegrationInterface<T>, IEipProcessor<IEipIntegrationInterface<T>>> processor;

    protected IEipConverter<T> converter;

    protected IEipParamConverter<T> paramConverter;

    protected IEipParamConverterCallback<T> paramConverterCallback;

    protected List<IEipConvertParam<T>> convertParamList;

    protected IEipExceptionPredict<T> exceptionPredict;

    protected String finalResultKey;

    public AbstractEipExceptionParamProcessorBuilder(AbstractEipInterfaceBuilder<T> interfaceBuilder) {
        super(interfaceBuilder);
    }

    public AbstractEipExceptionParamProcessorBuilder<T> setProcessor(Function<IEipIntegrationInterface<T>, IEipProcessor<IEipIntegrationInterface<T>>> processor) {
        this.processor = processor;
        return this;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> setParamConverter(IEipParamConverter<T> paramConverter) {
        this.paramConverter = paramConverter;
        return this;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> setParamConverterCallback(IEipParamConverterCallback<T> paramConverterCallback) {
        this.paramConverterCallback = paramConverterCallback;
        return this;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> setConvertParamList(List<IEipConvertParam<T>> convertParamList) {
        this.convertParamList = convertParamList;
        return this;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> addConvertParam(IEipConvertParam<T> convertParam) {
        if (this.convertParamList == null)
            this.convertParamList = new ArrayList<>();
        this.convertParamList.add(convertParam);
        return this;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> setExceptionPredict(IEipExceptionPredict<T> exceptionPredict) {
        this.exceptionPredict = exceptionPredict;
        return this;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> setFinalResultKey(String finalResultKey) {
        this.finalResultKey = finalResultKey;
        return this;
    }

    public IEipExceptionParamProcessor<T> build(IEipIntegrationInterface<T> eipInterface) {
        return build0(eipInterface).afterProperty();
    }

    protected abstract IEipExceptionParamProcessor<T> build0(IEipIntegrationInterface<T> eipInterface);
}
