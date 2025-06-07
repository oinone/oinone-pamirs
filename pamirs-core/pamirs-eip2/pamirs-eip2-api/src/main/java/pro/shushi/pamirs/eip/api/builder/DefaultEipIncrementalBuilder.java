package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipIncrementalProcessor;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.processor.DefaultIncrementalProcessor;

public class DefaultEipIncrementalBuilder extends AbstractEipIncrementalBuilder<SuperMap> {

    public DefaultEipIncrementalBuilder(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        super(interfaceBuilder);
    }

    @Override
    protected IEipIncrementalProcessor<SuperMap> build0() {
        return new DefaultIncrementalProcessor<SuperMap>()
                .setConverter(converter)
                .setIncrementalParamConverter(incrementalParamConverter == null ? EipFunctionConstant.DEFAULT_INCREMENTAL_PARAM_CONVERTER : incrementalParamConverter)
                .setIncrementalParamConverterCallback(incrementalParamConverterCallback)
                .setIncrementalParamList(incrementalParamList);
    }
}
