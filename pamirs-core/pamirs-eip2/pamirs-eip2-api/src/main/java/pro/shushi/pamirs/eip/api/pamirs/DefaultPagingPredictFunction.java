package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipPagingPredict;

/**
 * 实现平台Function机制
 */
public class DefaultPagingPredictFunction<T> extends AbstractExecuteFunction implements IEipPagingPredict<T> {

    public DefaultPagingPredictFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public boolean predict(IEipContext<T> context, ExtendedExchange exchange) {
        return (Boolean) ignoreHookCall(context, exchange);
    }
}
