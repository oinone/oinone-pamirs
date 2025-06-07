package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContextSupplier;
import pro.shushi.pamirs.eip.api.IEipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipParamProcessor;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.entity.AbstractEipIntegrationInterface;

/**
 * 默认使用SuperMap作为上下文承载对象
 *
 * @author Adamancy Zhang
 * @date 2020-11-05 21:27
 */
public class DefaultEipIntegrationInterface extends AbstractEipIntegrationInterface<SuperMap> implements IEipIntegrationInterface<SuperMap> {

    public DefaultEipIntegrationInterface(EipCamelContext context, String interfaceName, String uri) {
        super(context, interfaceName, uri);
    }

    @Override
    protected IEipContextSupplier<SuperMap> getDefaultContextSupplier() {
        return EipFunctionConstant.DEFAULT_CONTEXT_SUPPLIER;
    }

    @Override
    protected IEipParamProcessor<SuperMap> getDefaultRequestParamProcessor() {
        return EipFunctionConstant.DEFAULT_REQUEST_PARAM_PROCESSOR;
    }

    @Override
    protected IEipParamProcessor<SuperMap> getDefaultResponseParamProcessor() {
        return EipFunctionConstant.DEFAULT_RESPONSE_PARAM_PROCESSOR;
    }

    @Override
    protected IEipExceptionParamProcessor<SuperMap> getDefaultExceptionParamProcessor() {
        return EipFunctionConstant.DEFAULT_EXCEPTION_PARAM_PROCESSOR;
    }
}
