package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipIntegrationInterface;

public class DefaultEipInterfaceBuilder extends AbstractEipInterfaceBuilder<SuperMap> {

    protected DefaultEipInterfaceBuilder(EipCamelContext context, String interfaceName, String uri) {
        super(context, interfaceName, uri);
    }

    public static DefaultEipInterfaceBuilder newInstance(String interfaceName, String uri) {
        return new DefaultEipInterfaceBuilder(EipCamelContext.getContext(), interfaceName, uri);
    }

    public static DefaultEipInterfaceBuilder newInstance(EipCamelContext context, String interfaceName, String uri) {
        return new DefaultEipInterfaceBuilder(context, interfaceName, uri);
    }

    @Override
    public AbstractEipParamProcessorBuilder<SuperMap> createRequestParamProcessor0() {
        return new DefaultEipRequestParamProcessorBuilder(this);
    }

    @Override
    public AbstractEipParamProcessorBuilder<SuperMap> createResponseParamProcessor0() {
        return new DefaultEipResponseParamProcessorBuilder(this);
    }

    @Override
    public AbstractEipExceptionParamProcessorBuilder<SuperMap> createExceptionParamProcessor0() {
        return new DefaultEipExceptionParamProcessorBuilder(this);
    }

    @Override
    public AbstractEipPagingBuilder<SuperMap> createPagingProcessor0() {
        return new DefaultEipPagingBuilder(this);
    }

    @Override
    public AbstractEipIncrementalBuilder<SuperMap> createIncremental0() {
        return new DefaultEipIncrementalBuilder(this);
    }

    @Override
    protected IEipIntegrationInterface<SuperMap> build0() {
        DefaultEipIntegrationInterface eipInterface = (DefaultEipIntegrationInterface) new DefaultEipIntegrationInterface(context, interfaceName, uri)
                .setContextSupplier(contextSupplier)
                .setExchangePattern(exchangePattern);
        if (isDynamic) {
            eipInterface.setIsDynamic(Boolean.TRUE);
        } else {
            eipInterface.setIsDynamic(Boolean.FALSE);
        }
        if (dynamicProtocolCacheSize >= 1) {
            eipInterface.setDynamicProtocolCacheSize(dynamicProtocolCacheSize);
        }
        if (!isEnabledLog) {
            eipInterface.disableLog();
        }
        return eipInterface
                .setPaging(pagingBuilder == null ? null : pagingBuilder.build(eipInterface))
                .setIncrementalProcessor(incrementalBuilder == null ? null : incrementalBuilder.build())
                .setRequestParamProcessor(requestParamProcessorBuilder == null ?
                        new DefaultEipRequestParamProcessorBuilder(this).build(eipInterface)
                        : requestParamProcessorBuilder.build(eipInterface))
                .setResponseParamProcessor(responseParamProcessorBuilder == null ?
                        new DefaultEipResponseParamProcessorBuilder(this).build(eipInterface)
                        : responseParamProcessorBuilder.build(eipInterface))
                .setExceptionParamProcessor(exceptionParamProcessorBuilder == null ?
                        new DefaultEipExceptionParamProcessorBuilder(this).build(eipInterface)
                        : exceptionParamProcessorBuilder.build(eipInterface));
    }
}
