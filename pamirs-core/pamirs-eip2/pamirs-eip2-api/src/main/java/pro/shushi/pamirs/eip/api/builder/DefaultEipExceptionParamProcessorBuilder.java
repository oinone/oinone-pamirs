package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.processor.DefaultExceptionProcessor;

public class DefaultEipExceptionParamProcessorBuilder extends AbstractEipExceptionParamProcessorBuilder<SuperMap> {

    protected DefaultEipExceptionParamProcessorBuilder(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        super(interfaceBuilder);
    }

    public static DefaultEipExceptionParamProcessorBuilder newInstance(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        return new DefaultEipExceptionParamProcessorBuilder(interfaceBuilder);
    }

    @Override
    protected IEipExceptionParamProcessor<SuperMap> build0(IEipIntegrationInterface<SuperMap> eipInterface) {
        return new DefaultEipExceptionParamProcessor()
                .setProcessor(processor == null ? new DefaultExceptionProcessor(eipInterface) : processor.apply(eipInterface))
                .setConverter(converter)
                .setParamConverter(paramConverter)
                .setParamConverterCallback(paramConverterCallback)
                .setConvertParamList(convertParamList)
                .setExceptionPredict(exceptionPredict);
    }
}
