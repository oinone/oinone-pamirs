package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;

import java.util.function.Function;

public abstract class AbstractEipOpenApiBuilder<T> {

    protected EipCamelContext context;

    protected String interfaceName;

    protected String uri;

    protected ExchangePatternEnum exchangePattern;

    protected Boolean isEnabledLog;

    protected IEipContextSupplier<T> contextSupplier;

    protected Function<IEipOpenInterface<T>, IEipProcessor<IEipOpenInterface<T>>> processor;

    protected IEipConverter<T> converter;

    protected IEipOpenParamProcessor<T> requestParamProcessor;

    protected IEipOpenParamProcessor<T> responseParamProcessor;

    protected IEipAuthenticationProcessor<T> authenticationProcessor;

    protected IEipSerializable<T> serializable;

    protected IEipDeserialization<T> deserialization;

    protected String finalResultKey;

    protected IEipInOutConverter inOutConverter;

    protected IEipDecryptProcessor requestDecryptProcessor;

    protected IEipEncryptionProcessor responseEncryptionProcessor;

    protected AbstractEipOpenApiBuilder(EipCamelContext context, String interfaceName, String uri) {
        this.context = context;
        this.interfaceName = interfaceName;
        this.uri = uri;
        this.isEnabledLog = Boolean.TRUE;
    }

    public AbstractEipOpenApiBuilder<T> setContext(EipCamelContext context) {
        this.context = context;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setExchangePattern(ExchangePatternEnum exchangePattern) {
        this.exchangePattern = exchangePattern;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> enableLog() {
        isEnabledLog = Boolean.TRUE;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> disableLog() {
        isEnabledLog = Boolean.FALSE;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setContextSupplier(IEipContextSupplier<T> contextSupplier) {
        this.contextSupplier = contextSupplier;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setProcessor(Function<IEipOpenInterface<T>, IEipProcessor<IEipOpenInterface<T>>> processor) {
        this.processor = processor;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setRequestParamProcessor(IEipOpenParamProcessor<T> requestParamProcessor) {
        this.requestParamProcessor = requestParamProcessor;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setResponseParamProcessor(IEipOpenParamProcessor<T> responseParamProcessor) {
        this.responseParamProcessor = responseParamProcessor;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setAuthenticationProcessor(IEipAuthenticationProcessor<T> authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setSerializable(IEipSerializable<T> serializable) {
        this.serializable = serializable;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setDeserialization(IEipDeserialization<T> deserialization) {
        this.deserialization = deserialization;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setFinalResultKey(String finalResultKey) {
        this.finalResultKey = finalResultKey;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setInOutConverter(IEipInOutConverter inOutConverter) {
        this.inOutConverter = inOutConverter;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setRequestDecryptProcessor(IEipDecryptProcessor requestDecryptProcessor) {
        this.requestDecryptProcessor = requestDecryptProcessor;
        return this;
    }

    public AbstractEipOpenApiBuilder<T> setResponseEncryptionProcessor(IEipEncryptionProcessor responseEncryptionProcessor) {
        this.responseEncryptionProcessor = responseEncryptionProcessor;
        return this;
    }

    public IEipOpenInterface<T> build() {
        return build0().afterProperty();
    }

    protected abstract IEipOpenInterface<T> build0();
}
