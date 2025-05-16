package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.entity.AbstractEipParamProcessor;

/**
 * 默认使用SuperMap作为上下文承载对象
 */
public class DefaultEipResponseParamProcessor extends AbstractEipParamProcessor<SuperMap> implements IEipParamProcessor<SuperMap> {

    @Override
    protected IEipParamConverter<SuperMap> getDefaultParamConverter() {
        return EipFunctionConstant.DEFAULT_PARAM_CONVERTER;
    }

    @Override
    protected IEipSerializable<SuperMap> getDefaultSerializable() {
        return EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE;
    }

    @Override
    protected IEipDeserialization<SuperMap> getDefaultDeserialization() {
        return EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE;
    }

    @Override
    protected IEipInOutConverter getDefaultInOutConverter() {
        return EipFunctionConstant.DEFAULT_IN_OUT_CONVERTER;
    }
}
