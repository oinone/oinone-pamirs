package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;

/**
 * 实现平台Function机制
 */
public class DefaultInOutConverterFunction extends AbstractExecuteFunction implements IEipInOutConverter {

    public DefaultInOutConverterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
        return ignoreHookCall(exchange, inObject);
    }
}
