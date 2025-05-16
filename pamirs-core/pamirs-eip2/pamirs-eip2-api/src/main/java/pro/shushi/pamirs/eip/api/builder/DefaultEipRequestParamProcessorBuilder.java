package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipParamProcessor;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipRequestParamProcessor;
import pro.shushi.pamirs.eip.api.processor.DefaultRequestProcessor;

public class DefaultEipRequestParamProcessorBuilder extends AbstractEipParamProcessorBuilder<SuperMap> {

    protected DefaultEipRequestParamProcessorBuilder(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        super(interfaceBuilder);
    }

    public static DefaultEipRequestParamProcessorBuilder newInstance(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        return new DefaultEipRequestParamProcessorBuilder(interfaceBuilder);
    }

    @Override
    protected IEipParamProcessor<SuperMap> build0(IEipIntegrationInterface<SuperMap> eipInterface) {
        return new DefaultEipRequestParamProcessor()
                .setProcessor(processor == null ? new DefaultRequestProcessor(eipInterface) : processor.apply(eipInterface))
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
