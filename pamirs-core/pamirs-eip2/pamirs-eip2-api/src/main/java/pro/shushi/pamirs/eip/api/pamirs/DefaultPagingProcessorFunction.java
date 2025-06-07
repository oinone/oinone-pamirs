package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipPagingProcessor;

/**
 * 实现平台Function机制
 */
public class DefaultPagingProcessorFunction<T> extends AbstractExecuteFunction implements IEipPagingProcessor<T> {

    public DefaultPagingProcessorFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void process(IEipContext<T> context, ExtendedExchange exchange) {
        ignoreHookCall(context, exchange);
    }
}
