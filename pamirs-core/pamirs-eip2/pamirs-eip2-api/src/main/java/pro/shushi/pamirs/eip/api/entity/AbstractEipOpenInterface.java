package pro.shushi.pamirs.eip.api.entity;

import org.apache.camel.processor.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;

public abstract class AbstractEipOpenInterface<T> extends AbstractEipApi implements IEipOpenInterface<T> {

    private IEipProcessor<IEipOpenInterface<T>> processor;

    private IEipContextSupplier<T> contextSupplier;

    private IEipOpenParamProcessor<T> requestParamProcessor;

    private IEipOpenParamProcessor<T> responseParamProcessor;

    private IEipAuthenticationProcessor<T> authenticationProcessor;

    private IEipSerializable<T> serializable;

    private IEipDeserialization<T> deserialization;

    private IEipConverter<T> converter;

    private String finalResultKey;

    private IEipInOutConverter inOutConverter;

    private IEipDecryptProcessor requestDecryptProcessor;

    private IEipEncryptionProcessor responseEncryptionProcessor;

    private ErrorHandler errorHandler;

    public AbstractEipOpenInterface(EipCamelContext context, String interfaceName, String uri) {
        super(context, interfaceName, uri);
    }

    @Override
    public IEipProcessor<IEipOpenInterface<T>> getProcessor() {
        return processor;
    }

    public AbstractEipOpenInterface<T> setProcessor(IEipProcessor<IEipOpenInterface<T>> processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public IEipContextSupplier<T> getContextSupplier() {
        return contextSupplier;
    }

    public AbstractEipOpenInterface<T> setContextSupplier(IEipContextSupplier<T> contextSupplier) {
        this.contextSupplier = contextSupplier;
        return this;
    }

    @Override
    public IEipOpenParamProcessor<T> getRequestParamProcessor() {
        return requestParamProcessor;
    }

    public AbstractEipOpenInterface<T> setRequestParamProcessor(IEipOpenParamProcessor<T> requestParamProcessor) {
        this.requestParamProcessor = requestParamProcessor;
        return this;
    }

    @Override
    public IEipOpenParamProcessor<T> getResponseParamProcessor() {
        return responseParamProcessor;
    }

    public AbstractEipOpenInterface<T> setResponseParamProcessor(IEipOpenParamProcessor<T> responseParamProcessor) {
        this.responseParamProcessor = responseParamProcessor;
        return this;
    }

    @Override
    public IEipAuthenticationProcessor<T> getAuthenticationProcessor() {
        return authenticationProcessor;
    }

    public AbstractEipOpenInterface<T> setAuthenticationProcessor(IEipAuthenticationProcessor<T> authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
        return this;
    }

    @Override
    public IEipSerializable<T> getSerializable() {
        return serializable;
    }

    public AbstractEipOpenInterface<T> setSerializable(IEipSerializable<T> serializable) {
        this.serializable = serializable;
        return this;
    }

    @Override
    public IEipDeserialization<T> getDeserialization() {
        return deserialization;
    }

    public AbstractEipOpenInterface<T> setDeserialization(IEipDeserialization<T> deserialization) {
        this.deserialization = deserialization;
        return this;
    }

    @Override
    public IEipConverter<T> getConverter() {
        return converter;
    }

    public AbstractEipOpenInterface<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public String getFinalResultKey() {
        return finalResultKey;
    }

    public AbstractEipOpenInterface<T> setFinalResultKey(String finalResultKey) {
        this.finalResultKey = finalResultKey;
        return this;
    }

    @Override
    public IEipInOutConverter getInOutConverter() {
        return inOutConverter;
    }

    public AbstractEipOpenInterface<T> setInOutConverter(IEipInOutConverter inOutConverter) {
        this.inOutConverter = inOutConverter;
        return this;
    }

    @Override
    public IEipDecryptProcessor getRequestDecryptProcessor() {
        return requestDecryptProcessor;
    }

    public AbstractEipOpenInterface<T> setRequestDecryptProcessor(IEipDecryptProcessor requestDecryptProcessor) {
        this.requestDecryptProcessor = requestDecryptProcessor;
        return this;
    }

    @Override
    public IEipEncryptionProcessor getResponseEncryptionProcessor() {
        return responseEncryptionProcessor;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public AbstractEipOpenInterface<T> setResponseEncryptionProcessor(IEipEncryptionProcessor responseEncryptionProcessor) {
        this.responseEncryptionProcessor = responseEncryptionProcessor;
        return this;
    }

    @Override
    public IEipOpenInterface<T> afterProperty() {
        if (getExchangePattern() == null) {
            setExchangePattern(ExchangePatternEnum.InOut);
        }

        if (getContextSupplier() == null) {
            setContextSupplier(getDefaultContextSupplier());
        }
        if (getRequestParamProcessor() == null) {
            setRequestParamProcessor(getDefaultRequestParamProcessor());
        }
        if (getResponseParamProcessor() == null) {
            setResponseParamProcessor(getDefaultResponseParamProcessor());
        }
        if (getSerializable() == null) {
            setSerializable(getDefaultSerializable());
        }
        if (getDeserialization() == null) {
            setDeserialization(getDefaultDeserialization());
        }
        if (getInOutConverter() == null) {
            setInOutConverter(getDefaultInOutConverter());
        }

        if (StringUtils.isBlank(getFinalResultKey())) {
            setFinalResultKey(EipConfigurationConstant.DEFAULT_RESULT_KEY);
        }
        return this;
    }

    protected abstract IEipContextSupplier<T> getDefaultContextSupplier();

    protected abstract IEipOpenParamProcessor<T> getDefaultRequestParamProcessor();

    protected abstract IEipOpenParamProcessor<T> getDefaultResponseParamProcessor();

    protected abstract IEipSerializable<T> getDefaultSerializable();

    protected abstract IEipDeserialization<T> getDefaultDeserialization();

    protected abstract IEipInOutConverter getDefaultInOutConverter();
}
