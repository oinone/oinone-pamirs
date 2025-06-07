package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipOpenInterface;
import pro.shushi.pamirs.eip.api.processor.DefaultOpenInterfaceProcessor;

public class DefaultEipOpenApiBuilder extends AbstractEipOpenApiBuilder<SuperMap> {

    protected DefaultEipOpenApiBuilder(EipCamelContext context, String interfaceName, String uri) {
        super(context, interfaceName, uri);
    }

    public static DefaultEipOpenApiBuilder newInstance(EipCamelContext context, String interfaceName, String uri) {
        return new DefaultEipOpenApiBuilder(context, interfaceName, uri);
    }

    @Override
    protected IEipOpenInterface<SuperMap> build0() {
        DefaultEipOpenInterface eipOpenApi = (DefaultEipOpenInterface) new DefaultEipOpenInterface(context, interfaceName, uri)
                .setContextSupplier(contextSupplier)
                .setConverter(converter)
                .setRequestDecryptProcessor(requestDecryptProcessor)
                .setRequestParamProcessor(requestParamProcessor)
                .setResponseParamProcessor(responseParamProcessor)
                .setResponseEncryptionProcessor(responseEncryptionProcessor)
                .setAuthenticationProcessor(authenticationProcessor)
                .setSerializable(serializable)
                .setDeserialization(deserialization)
                .setFinalResultKey(finalResultKey)
                .setInOutConverter(inOutConverter)
                .setExchangePattern(exchangePattern);
        if (!isEnabledLog) {
            eipOpenApi.disableLog();
        }
        return eipOpenApi
                .setProcessor(processor == null ? new DefaultOpenInterfaceProcessor(eipOpenApi) : processor.apply(eipOpenApi));
    }
}
