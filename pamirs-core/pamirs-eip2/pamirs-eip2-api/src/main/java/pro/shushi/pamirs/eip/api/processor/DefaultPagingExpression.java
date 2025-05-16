package pro.shushi.pamirs.eip.api.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipPaging;
import pro.shushi.pamirs.eip.api.IEipPagingProcessor;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.util.EipHelper;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultPagingExpression implements Expression {

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        IEipContext context = EipInterfaceContext.getExecutorContext(exchange);
        IEipIntegrationInterface eipInterface = (IEipIntegrationInterface) context.getApi();
        IEipPaging paging = eipInterface.getPaging();
        final String pagingEnabledKey = IEipContext.PAGING_PREFIX + eipInterface.getInterfaceName() + IEipContext.PAGING_ENABLED_SUFFIX;
        Boolean isNeedPaging = (Boolean) context.getExecutorContextValue(pagingEnabledKey);
        if (isNeedPaging != null && isNeedPaging) {
            IEipPagingProcessor processor = paging.getProcessor();
            if (processor != null) {
                processor.process(context, (ExtendedExchange) exchange);
                isNeedPaging = (Boolean) context.getExecutorContextValue(pagingEnabledKey);
            }
            if (isNeedPaging != null && isNeedPaging) {
                exchange.getMessage().setBody(context.getExecutorContextValue(IEipContext.REQUEST_STORE_PREFIX + eipInterface.getInterfaceName()));
                return (T) (EipHelper.generatorDirectUri(eipInterface.getInterfaceName()));
            }
        }
        return null;
    }
}
