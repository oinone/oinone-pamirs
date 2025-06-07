package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.IEipExceptionPredict;
import pro.shushi.pamirs.eip.api.IEipParamConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.entity.AbstractEipExceptionParamProcessor;

/**
 * 默认使用SuperMap作为上下文承载对象
 */
public class DefaultEipExceptionParamProcessor extends AbstractEipExceptionParamProcessor<SuperMap> implements IEipExceptionParamProcessor<SuperMap> {

    @Override
    protected IEipParamConverter<SuperMap> getDefaultParamConverter() {
        return EipFunctionConstant.DEFAULT_PARAM_CONVERTER;
    }

    @Override
    protected IEipExceptionPredict<SuperMap> getDefaultExceptionPredict() {
        return EipFunctionConstant.DEFAULT_EXCEPTION_PREDICT;
    }
}
