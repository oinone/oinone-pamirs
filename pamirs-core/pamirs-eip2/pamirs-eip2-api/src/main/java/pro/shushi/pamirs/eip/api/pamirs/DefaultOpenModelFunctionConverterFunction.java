package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;

/**
 * 实现平台Function机制
 */
public class DefaultOpenModelFunctionConverterFunction<T> extends AbstractExecuteFunction implements IEipConverter<T> {

    public DefaultOpenModelFunctionConverterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void convert(IEipContext<T> context, ExtendedExchange exchange) {
        IEipOpenInterface<T> openApi = (IEipOpenInterface<T>) context.getApi();

        Object result = ignoreHookCall(context.getInterfaceContext());

        context.putInterfaceContextValue(openApi.getFinalResultKey(), OpenEipResult.success(result));
    }
}
