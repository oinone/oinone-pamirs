package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Optional;

/**
 * 实现平台Function机制
 */
@Slf4j
public class DefaultOpenEipFunctionConverterFunction<T> extends AbstractExecuteFunction implements IEipConverter<T> {

    public DefaultOpenEipFunctionConverterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void convert(IEipContext<T> context, ExtendedExchange exchange) {
        Object result = ignoreHookCall(context, exchange);
        // 返回结果约定使用OpenEipResult
        if (!(result instanceof OpenEipResult)) {
            log.warn("Open interface function return result does not use OpenEipResult, interface:{}", Optional.ofNullable(context).map(IEipContext::getApi).map(IEipApi::getInterfaceName).orElse(null));
        }

        context.putInterfaceContextValue(EipConfigurationConstant.DEFAULT_RESULT_KEY, result);
    }
}
