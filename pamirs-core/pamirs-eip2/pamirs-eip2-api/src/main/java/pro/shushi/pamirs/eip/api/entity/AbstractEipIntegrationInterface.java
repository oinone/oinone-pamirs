package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;

/**
 * 默认使用T作为接口传递对象
 */
public abstract class AbstractEipIntegrationInterface<T> extends AbstractEipApi implements IEipIntegrationInterface<T> {

    private IEipContextSupplier<T> contextSupplier;

    private IEipParamProcessor<T> requestParamProcessor;

    private IEipParamProcessor<T> responseParamProcessor;

    private IEipExceptionParamProcessor<T> exceptionParamProcessor;

    private IEipPaging<T> paging;

    private IEipIncrementalProcessor<T> incrementalProcessor;

    private Boolean isDynamic;

    private Integer dynamicProtocolCacheSize;

    public AbstractEipIntegrationInterface(EipCamelContext context, String interfaceName, String uri) {
        super(context, interfaceName, uri);
    }

    @Override
    public IEipContextSupplier<T> getContextSupplier() {
        return contextSupplier;
    }

    public AbstractEipIntegrationInterface<T> setContextSupplier(IEipContextSupplier<T> contextSupplier) {
        this.contextSupplier = contextSupplier;
        return this;
    }

    @Override
    public IEipParamProcessor<T> getRequestParamProcessor() {
        return requestParamProcessor;
    }

    public AbstractEipIntegrationInterface<T> setRequestParamProcessor(IEipParamProcessor<T> requestParamProcessor) {
        this.requestParamProcessor = requestParamProcessor;
        return this;
    }

    @Override
    public IEipParamProcessor<T> getResponseParamProcessor() {
        return responseParamProcessor;
    }

    public AbstractEipIntegrationInterface<T> setResponseParamProcessor(IEipParamProcessor<T> responseParamProcessor) {
        this.responseParamProcessor = responseParamProcessor;
        return this;
    }

    @Override
    public IEipExceptionParamProcessor<T> getExceptionParamProcessor() {
        return exceptionParamProcessor;
    }

    public AbstractEipIntegrationInterface<T> setExceptionParamProcessor(IEipExceptionParamProcessor<T> exceptionParamProcessor) {
        this.exceptionParamProcessor = exceptionParamProcessor;
        return this;
    }

    @Override
    public IEipPaging<T> getPaging() {
        return paging;
    }

    public AbstractEipIntegrationInterface<T> setPaging(IEipPaging<T> paging) {
        this.paging = paging;
        return this;
    }

    @Override
    public IEipIncrementalProcessor<T> getIncrementalProcessor() {
        return incrementalProcessor;
    }

    public AbstractEipIntegrationInterface<T> setIncrementalProcessor(IEipIncrementalProcessor<T> incrementalProcessor) {
        this.incrementalProcessor = incrementalProcessor;
        return this;
    }

    @Override
    public Boolean getIsDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(Boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    @Override
    public Integer getDynamicProtocolCacheSize() {
        return dynamicProtocolCacheSize;
    }

    public void setDynamicProtocolCacheSize(Integer dynamicProtocolCacheSize) {
        this.dynamicProtocolCacheSize = dynamicProtocolCacheSize;
    }

    @Override
    public AbstractEipIntegrationInterface<T> afterProperty() {
        if (getExchangePattern() == null) {
            setExchangePattern(ExchangePatternEnum.InOut);
        }

        if (getContextSupplier() == null) {
            setContextSupplier(getDefaultContextSupplier());
        }

        if (getRequestParamProcessor() == null) {
            setRequestParamProcessor(getDefaultRequestParamProcessor());
        }
        getRequestParamProcessor().afterProperty();

        if (getResponseParamProcessor() == null) {
            setResponseParamProcessor(getDefaultResponseParamProcessor());
        }
        getResponseParamProcessor().afterProperty();

        if (getExceptionParamProcessor() == null) {
            setExceptionParamProcessor(getDefaultExceptionParamProcessor());
        }
        getExceptionParamProcessor().afterProperty();

        if (getIsDynamic() == null) {
            setIsDynamic(Boolean.FALSE);
        }

        if (getDynamicProtocolCacheSize() == null) {
            setDynamicProtocolCacheSize(-1);
        }
        return this;
    }

    protected abstract IEipContextSupplier<T> getDefaultContextSupplier();

    protected abstract IEipParamProcessor<T> getDefaultRequestParamProcessor();

    protected abstract IEipParamProcessor<T> getDefaultResponseParamProcessor();

    protected abstract IEipExceptionParamProcessor<T> getDefaultExceptionParamProcessor();
}
