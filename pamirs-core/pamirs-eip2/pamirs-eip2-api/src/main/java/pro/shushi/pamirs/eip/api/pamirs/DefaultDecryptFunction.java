package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipDecryptProcessor;

/**
 * 实现平台Function机制
 */
public class DefaultDecryptFunction extends AbstractExecuteFunction implements IEipDecryptProcessor {

    public DefaultDecryptFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void processor(ExtendedExchange exchange) {
        ignoreHookCall(exchange);
    }
}
