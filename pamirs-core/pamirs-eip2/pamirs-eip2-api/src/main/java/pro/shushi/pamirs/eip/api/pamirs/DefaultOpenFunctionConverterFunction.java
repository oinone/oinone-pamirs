package pro.shushi.pamirs.eip.api.pamirs;

import org.apache.camel.ExtendedExchange;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;
import pro.shushi.pamirs.eip.api.util.EipOpenFunctionHelper;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.JsonUtils;

/**
 * 实现平台Function机制
 */
public class DefaultOpenFunctionConverterFunction<T> extends AbstractExecuteFunction implements IEipConverter<T> {

    public static final String OPEN_FUNCTION_CONVERTER_ARGS = "DefaultOpenFunctionConverterFunction.args";
    public static final String OPEN_FUNCTION_CONVERTER_RETURN = "DefaultOpenFunctionConverterFunction.return";

    public DefaultOpenFunctionConverterFunction(String namespace, String fun) {
        super(namespace, fun);
    }

    @Override
    public void convert(IEipContext<T> context, ExtendedExchange exchange) {
        Function function = PamirsSession.getContext().getFunction(getNamespace(), getFun());
        // 根据函数定义,从上下文中获取入参
        Object[] argObjs = EipOpenFunctionHelper.convertArguments(function, (SuperMap) context.getInterfaceContextValue(OPEN_FUNCTION_CONVERTER_ARGS));
        Object result;
        PamirsSession.directive().enableFromClient();
        PamirsSession.directive().enableHook();
        PamirsSession.directive().enableExtPoint();
        if (argObjs == null) {
            result = call();
        } else {
            result = call(argObjs);
        }
        String returnModel = function.getReturnType().getModel();
        if (result != null && StringUtils.isNotBlank(returnModel)) {
            result = ClientDataConverter.get().out(returnModel, result);
        }
        context.putInterfaceContextValue(OPEN_FUNCTION_CONVERTER_RETURN,
                JsonUtils.parseMap(JsonUtils.toJSONString(OpenEipResult.success(result)))
        );
    }
}
