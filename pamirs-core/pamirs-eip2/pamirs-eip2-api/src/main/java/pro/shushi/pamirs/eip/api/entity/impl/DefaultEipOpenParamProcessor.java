package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipOpenParamProcessor;
import pro.shushi.pamirs.eip.api.IEipParamConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.entity.AbstractEipOpenParamProcessor;

/**
 * 默认使用SuperMap作为上下文承载对象
 */
public class DefaultEipOpenParamProcessor extends AbstractEipOpenParamProcessor<SuperMap> implements IEipOpenParamProcessor<SuperMap> {

    @Override
    protected IEipParamConverter<SuperMap> getDefaultParamConverter() {
        return EipFunctionConstant.DEFAULT_PARAM_CONVERTER;
    }
}
