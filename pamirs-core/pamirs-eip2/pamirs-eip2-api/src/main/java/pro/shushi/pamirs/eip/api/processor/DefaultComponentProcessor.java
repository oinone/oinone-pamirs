package pro.shushi.pamirs.eip.api.processor;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.Message;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.model.EipComponentDefinition;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;

public class DefaultComponentProcessor extends AbstractEipComponentProcessor {

    public DefaultComponentProcessor(EipRouteDefinition routeDefinition, EipComponentDefinition eipComponentDefinition) {
        super(routeDefinition, eipComponentDefinition);
    }

    @Override
    public void processor(ExtendedExchange exchange) throws Exception {
        IEipContext<SuperMap> context = EipInterfaceContext.getExecutorContext(exchange);
        Message message = exchange.getMessage();
        Object body = message.getBody();

        //总是使用请求入参
        //从上下文获取是否使用入参. 而不是从接口上获取
        if (Boolean.TRUE.equals(eipComponentDefinition.getAlwaysUsingRequestParams())) {
            Object requestParams = context.getExecutorContextValue(IEipContext.REQUEST_ALWAYS_USING_REQUEST_STORE_KEY);
            if (requestParams == null) {
                context.putExecutorContextValue(IEipContext.REQUEST_ALWAYS_USING_REQUEST_STORE_KEY, body);
            } else {
                body = requestParams;
                message.setBody(body);
            }
        }
    }


}
