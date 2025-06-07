package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.eip.api.IEipContextSupplier;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;

import java.util.function.Function;

public abstract class AbstractEipInterfaceBuilder<T> {

    protected EipCamelContext context;

    protected String interfaceName;

    protected String uri;

    protected ExchangePatternEnum exchangePattern;

    protected Boolean isEnabledLog;

    protected IEipContextSupplier<T> contextSupplier;

    protected AbstractEipParamProcessorBuilder<T> requestParamProcessorBuilder;

    protected AbstractEipParamProcessorBuilder<T> responseParamProcessorBuilder;

    protected AbstractEipExceptionParamProcessorBuilder<T> exceptionParamProcessorBuilder;

    protected AbstractEipPagingBuilder<T> pagingBuilder;

    protected AbstractEipIncrementalBuilder<T> incrementalBuilder;

    protected Boolean isDynamic;

    protected Integer dynamicProtocolCacheSize;

    protected Boolean isIgnoreFrequency;

    protected AbstractEipInterfaceBuilder(EipCamelContext context, String interfaceName, String uri) {
        this.context = context;
        this.interfaceName = interfaceName;
        this.uri = uri;
        this.isEnabledLog = Boolean.FALSE;
        this.isDynamic = Boolean.FALSE;
        this.dynamicProtocolCacheSize = -1;
        this.isIgnoreFrequency = Boolean.FALSE;
    }

    public AbstractEipInterfaceBuilder<T> setContext(EipCamelContext context) {
        this.context = context;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> setExchangePattern(ExchangePatternEnum exchangePattern) {
        this.exchangePattern = exchangePattern;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> enableLog() {
        isEnabledLog = Boolean.TRUE;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> disableLog() {
        isEnabledLog = Boolean.FALSE;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> disableFrequency() {
        isIgnoreFrequency = Boolean.TRUE;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> enableFrequency() {
        isIgnoreFrequency = Boolean.FALSE;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> setContextSupplier(IEipContextSupplier<T> contextSupplier) {
        this.contextSupplier = contextSupplier;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> createRequestParamProcessor() {
        this.requestParamProcessorBuilder = createRequestParamProcessor0();
        return this.requestParamProcessorBuilder;
    }

    public AbstractEipParamProcessorBuilder<T> createResponseParamProcessor() {
        this.responseParamProcessorBuilder = createResponseParamProcessor0();
        return this.responseParamProcessorBuilder;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> createExceptionParamProcessor() {
        this.exceptionParamProcessorBuilder = createExceptionParamProcessor0();
        return this.exceptionParamProcessorBuilder;
    }

    public AbstractEipPagingBuilder<T> enablePaging() {
        this.pagingBuilder = createPagingProcessor0();
        return this.pagingBuilder;
    }

    public AbstractEipIncrementalBuilder<T> enableIncremental() {
        this.incrementalBuilder = createIncremental0();
        return this.incrementalBuilder;
    }

    public AbstractEipInterfaceBuilder<T> disableIncremental() {
        this.incrementalBuilder = null;
        return this;
    }

    public AbstractEipParamProcessorBuilder<T> createRequestParamProcessor(Function<AbstractEipInterfaceBuilder<T>, AbstractEipParamProcessorBuilder<T>> requestParamProcessorSupplier) {
        this.requestParamProcessorBuilder = requestParamProcessorSupplier.apply(this);
        return this.requestParamProcessorBuilder;
    }

    public AbstractEipParamProcessorBuilder<T> createResponseParamProcessor(Function<AbstractEipInterfaceBuilder<T>, AbstractEipParamProcessorBuilder<T>> responseParamProcessorSupplier) {
        this.responseParamProcessorBuilder = responseParamProcessorSupplier.apply(this);
        return this.responseParamProcessorBuilder;
    }

    public AbstractEipExceptionParamProcessorBuilder<T> createExceptionParamProcessor(Function<AbstractEipInterfaceBuilder<T>, AbstractEipExceptionParamProcessorBuilder<T>> exceptionParamProcessorSupplier) {
        this.exceptionParamProcessorBuilder = exceptionParamProcessorSupplier.apply(this);
        return this.exceptionParamProcessorBuilder;
    }

    public AbstractEipPagingBuilder<T> enablePaging(Function<AbstractEipInterfaceBuilder<T>, AbstractEipPagingBuilder<T>> pagingProcessorSupplier) {
        this.pagingBuilder = pagingProcessorSupplier.apply(this);
        return this.pagingBuilder;
    }

    public AbstractEipInterfaceBuilder<T> disablePaging() {
        this.pagingBuilder = null;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> enableDynamic(int dynamicProtocolCacheSize) {
        this.isDynamic = Boolean.TRUE;
        this.dynamicProtocolCacheSize = dynamicProtocolCacheSize;
        return this;
    }

    public AbstractEipInterfaceBuilder<T> disableDynamic() {
        this.isDynamic = Boolean.FALSE;
        this.dynamicProtocolCacheSize = -1;
        return this;
    }

    public IEipIntegrationInterface<T> build() {
        return build0().afterProperty();
    }

    public abstract AbstractEipParamProcessorBuilder<T> createRequestParamProcessor0();

    public abstract AbstractEipParamProcessorBuilder<T> createResponseParamProcessor0();

    public abstract AbstractEipExceptionParamProcessorBuilder<T> createExceptionParamProcessor0();

    public abstract AbstractEipPagingBuilder<T> createPagingProcessor0();

    public abstract AbstractEipIncrementalBuilder<T> createIncremental0();

    protected abstract IEipIntegrationInterface<T> build0();
}
