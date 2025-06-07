package pro.shushi.pamirs.eip.api.processor;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.Message;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.eip.api.util.EipLogUtil;

public class DefaultExceptionProcessor extends AbstractEipIntegrationInterfaceProcessor<SuperMap> implements IEipProcessor<IEipIntegrationInterface<SuperMap>> {

    public DefaultExceptionProcessor(IEipIntegrationInterface<SuperMap> eipInterface) {
        super(eipInterface);
    }

    @Override
    public void processor(ExtendedExchange exchange) throws Exception {
        IEipIntegrationInterface<SuperMap> integrationInterface = getApi();
        Message message = exchange.getMessage();
        Object body = message.getBody();

        //获取执行器上下文
        IEipContext<SuperMap> context = EipInterfaceContext.getExecutorContext(exchange);

        //获取响应参数处理器
        IEipParamProcessor<SuperMap> paramProcessor = integrationInterface.getResponseParamProcessor();

        //序列化入参
        SuperMap interfaceContext;
        if (body instanceof SuperMap) {
            interfaceContext = (SuperMap) body;
        } else {
            interfaceContext = paramProcessor.getSerializable().serializable(body);
        }

        //更新执行器上下文
        context = refreshExecutorContext(exchange, context, interfaceContext);

        //获取Eip日志
        EipLog eipLog = EipLogUtil.getEipLog(context);
        if (eipLog != null) {

            //更新body数据
            message.setBody(interfaceContext);

            //更新响应数据
            EipLogUtil.updateResponseData(eipLog, exchange);
        }

        //获取异常参数处理器
        IEipExceptionParamProcessor<SuperMap> exceptionParamProcessor = integrationInterface.getExceptionParamProcessor();

        //参数转换
        EipHelper.paramConvert(context, exceptionParamProcessor, exchange);

        //更新执行器上下文
        context = EipInterfaceContext.getExecutorContext(exchange);
        interfaceContext = context.getInterfaceContext();

        //异常判定
        if (exceptionParamProcessor.getExceptionPredict().test(context)) {
            EipResult<SuperMap> errorResult = EipResult.error(context,
                    StringHelper.valueOf(context.getExecutorContextValue(IEipContext.DEFAULT_ERROR_CODE_KEY)),
                    StringHelper.valueOf(context.getExecutorContextValue(IEipContext.DEFAULT_ERROR_MESSAGE_KEY)),
                    body);

            //设置出参
            message.setBody(errorResult);

            if (eipLog != null) {
                EipLogUtil.failure(context, eipLog, exchange);
            }

            //路由中断
            exchange.setInterrupted(true);

            return;
        }

        //设置出参
        message.setBody(interfaceContext);
    }
}
