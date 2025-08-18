package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.Exchange;
import pro.shushi.pamirs.eip.api.IEipErrorHandler;

/**
 * @author Gesi at 11:10 on 2025/8/12
 */
public class DefaultErrorHandlerFunction extends AbstractExecuteFunction implements IEipErrorHandler {

    public DefaultErrorHandlerFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ignoreHookCall(exchange);
    }

}
