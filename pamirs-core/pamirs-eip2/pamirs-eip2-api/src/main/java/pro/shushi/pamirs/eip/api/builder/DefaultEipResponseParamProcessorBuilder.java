package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipParamProcessor;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipResponseParamProcessor;
import pro.shushi.pamirs.eip.api.processor.DefaultResponseProcessor;

public class DefaultEipResponseParamProcessorBuilder extends AbstractEipParamProcessorBuilder<SuperMap> {

    protected DefaultEipResponseParamProcessorBuilder(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        super(interfaceBuilder);
    }

    public static DefaultEipResponseParamProcessorBuilder newInstance(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        return new DefaultEipResponseParamProcessorBuilder(interfaceBuilder);
    }

    @Override
    protected IEipParamProcessor<SuperMap> build0(IEipIntegrationInterface<SuperMap> eipInterface) {
        return new DefaultEipResponseParamProcessor()
                .setProcessor(processor == null ? new DefaultResponseProcessor(eipInterface) : processor.apply(eipInterface))
                .setConverter(converter)
                .setAuthenticationProcessor(authenticationProcessor)
                .setSerializable(serializable)
                .setDeserialization(deserialization)
                .setParamConverter(paramConverter)
                .setParamConverterCallback(paramConverterCallback)
                .setConvertParamList(convertParamList)
                .setFinalResultKey(finalResultKey)
                .setInOutConverter(inOutConverter);
    }
}
