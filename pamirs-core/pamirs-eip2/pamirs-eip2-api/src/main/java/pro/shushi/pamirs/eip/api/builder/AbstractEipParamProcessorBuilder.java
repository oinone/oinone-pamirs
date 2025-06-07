package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.eip.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractEipParamProcessorBuilder<T> extends AbstractBaseBuilder<T> {

    protected Function<IEipIntegrationInterface<T>, IEipProcessor<IEipIntegrationInterface<T>>> processor;

    protected IEipConverter<T> converter;

    protected IEipAuthenticationProcessor<T> authenticationProcessor;

    protected IEipSerializable<T> serializable;

    protected IEipDeserialization<T> deserialization;

    protected IEipParamConverter<T> paramConverter;

    protected IEipParamConverterCallback<T> paramConverterCallback;

    protected List<IEipConvertParam<T>> convertParamList;

    protected String finalResultKey;

    protected IEipInOutConverter inOutConverter;

    public AbstractEipParamProcessorBuilder(AbstractEipInterfaceBuilder<T> interfaceBuilder) {
        super(interfaceBuilder);
    }

    public AbstractEipParamProcessorBuilder<T> setProcessor(Function<IEipIntegrationInterface<T>, IEipProcessor<IEipIntegrationInterface<T>>> processor) {
        this.processor = processor;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setAuthenticationProcessor(IEipAuthenticationProcessor<T> authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setSerializable(IEipSerializable<T> serializable) {
        this.serializable = serializable;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setDeserialization(IEipDeserialization<T> deserialization) {
        this.deserialization = deserialization;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setParamConverter(IEipParamConverter<T> paramConverter) {
        this.paramConverter = paramConverter;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setParamConverterCallback(IEipParamConverterCallback<T> paramConverterCallback) {
        this.paramConverterCallback = paramConverterCallback;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setConvertParamList(List<IEipConvertParam<T>> convertParamList) {
        this.convertParamList = convertParamList;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> addConvertParam(IEipConvertParam<T> convertParam) {
        if (this.convertParamList == null) {
            this.convertParamList = new ArrayList<>();
        }
        this.convertParamList.add(convertParam);
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setFinalResultKey(String finalResultKey) {
        this.finalResultKey = finalResultKey;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> setInOutConverter(IEipInOutConverter inOutConverter) {
        this.inOutConverter = inOutConverter;
        return this;
    }

    public IEipParamProcessor<T> build(IEipIntegrationInterface<T> eipInterface) {
        return build0(eipInterface).afterProperty();
    }

    protected abstract IEipParamProcessor<T> build0(IEipIntegrationInterface<T> eipInterface);
}
