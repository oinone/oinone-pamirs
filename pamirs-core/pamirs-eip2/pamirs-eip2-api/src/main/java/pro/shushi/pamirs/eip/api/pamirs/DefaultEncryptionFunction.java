package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipEncryptionProcessor;

/**
 * 实现平台Function机制
 */
public class DefaultEncryptionFunction extends AbstractExecuteFunction implements IEipEncryptionProcessor {

    public DefaultEncryptionFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public Object processor(ExtendedExchange exchange, Object body) {
        return ignoreHookCall(exchange, body);
    }
}
