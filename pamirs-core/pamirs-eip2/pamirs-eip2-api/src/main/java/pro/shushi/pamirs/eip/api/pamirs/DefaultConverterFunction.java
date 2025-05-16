package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;

/**
 * 实现平台Function机制
 */
public class DefaultConverterFunction<T> extends AbstractExecuteFunction implements IEipConverter<T> {

    public DefaultConverterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void convert(IEipContext<T> context, ExtendedExchange exchange) {
        ignoreHookCall(context, exchange);
    }
}
