package pro.shushi.pamirs.eip.api.endpoint.function;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.util.EipFunctionHelper;
import pro.shushi.pamirs.meta.api.Fun;

/**
 * @author drome
 * @date 2021/8/612:12 下午
 */
public class PamirsFunctionProducer extends DefaultProducer {

    public PamirsFunctionProducer(PamirsFunctionEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        IEipContext<SuperMap> context = EipInterfaceContext.getExecutorContext(exchange);
        PamirsFunctionEndpoint endpoint = getEndpoint();
        Object[] argObjs = EipFunctionHelper.convertArguments(endpoint.getNamespace(), endpoint.getFun(), context.getInterfaceContext());
        Object result;
        if (argObjs == null) {
            result = Fun.run(endpoint.getNamespace(), endpoint.getFun());
        } else {
            result = Fun.run(endpoint.getNamespace(), endpoint.getFun(), argObjs);
        }
        exchange.getMessage().setBody(result);
    }

    @Override
    public PamirsFunctionEndpoint getEndpoint() {
        return (PamirsFunctionEndpoint) super.getEndpoint();
    }
}
