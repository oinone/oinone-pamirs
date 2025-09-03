package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.entity.AbstractEipOpenInterface;

/**
 * 默认使用SuperMap作为上下文承载对象
 */
public class DefaultEipOpenInterface extends AbstractEipOpenInterface<SuperMap> implements IEipOpenInterface<SuperMap> {

    public DefaultEipOpenInterface(EipCamelContext context, String interfaceName, String uri) {
        super(context, interfaceName, uri);
    }

    @Override
    protected IEipContextSupplier<SuperMap> getDefaultContextSupplier() {
        return EipFunctionConstant.DEFAULT_CONTEXT_SUPPLIER;
    }

    @Override
    protected IEipOpenParamProcessor<SuperMap> getDefaultRequestParamProcessor() {
        return EipFunctionConstant.DEFAULT_OPEN_REQUEST_PARAM_PROCESSOR;
    }

    @Override
    protected IEipOpenParamProcessor<SuperMap> getDefaultResponseParamProcessor() {
        return EipFunctionConstant.DEFAULT_OPEN_RESPONSE_PARAM_PROCESSOR;
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
