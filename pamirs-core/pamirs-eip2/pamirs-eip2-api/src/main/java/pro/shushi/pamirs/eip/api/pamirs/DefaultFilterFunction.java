package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipFilter;

/**
 * 实现平台Function机制
 */
public class DefaultFilterFunction<T> extends AbstractExecuteFunction implements IEipFilter<T> {

    public DefaultFilterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public boolean matches(IEipContext<T> context, ExtendedExchange exchange) {
        return (boolean) ignoreHookCall(context, exchange);
    }
}
