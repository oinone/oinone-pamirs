package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;
import pro.shushi.pamirs.eip.api.executor.EipExecutor;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.strategy.exception.CircuitBreakerOpenException;
import pro.shushi.pamirs.eip.api.util.EipParamConverterHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 开放接口调用集成接口
 *
 * @author Adamancy Zhang at 11:22 on 2024-03-09
 */
@Slf4j
@Component
@Fun(EipOpenInterface.MODEL_MODEL)
@Conditional(EipOpenApiSwitchCondition.class)
public class OpenApiCallIntegrationApiConverter implements IEipConverter<SuperMap> {

    @Function.fun(EipFunctionConstant.DEFAULT_OPEN_API_CALL_INTEGRATION_API_FUN)
    @Function.Advanced(displayName = "开放接口调用集成接口")
    @Function(name = EipFunctionConstant.DEFAULT_OPEN_API_CALL_INTEGRATION_API_FUN)
    public Object convertFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        convert(context, exchange);
        return null;
    }

    @Override
    public void convert(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        SuperMap executorContext = convertExecutorContext(context.getExecutorContext());
        SuperMap body = context.getInterfaceContext();
        String interfaceName = context.getApi().getInterfaceName();
        EipResult<SuperMap> result = EipExecutor.newInstance(executorContext)
                .setting(interfaceName)
                .setRequestParamConvertFunction(OpenApiCallIntegrationApiParamConverter.INSTANCE)
                .and()
                .call(interfaceName, body);
        Object returnResult = result.getResult();
        if (returnResult instanceof HttpOperationFailedException) {
            HttpOperationFailedException httpOperationFailedException = (HttpOperationFailedException) returnResult;
            log.error("Integration interface call error.", httpOperationFailedException);
            returnResult = httpOperationFailedException.getResponseBody();
        } else if (returnResult instanceof CircuitBreakerOpenException) {
            // 熔断不打印堆栈
            returnResult = OpenEipResult.error(result.getErrorCode(), result.getErrorMessage());
        } else if (returnResult instanceof Throwable) {
            Throwable throwable = (Throwable) returnResult;
            log.error("Integration interface call error.", throwable);
            returnResult = OpenEipResult.error(result.getErrorCode(), result.getErrorMessage());
        }
        if (returnResult == null) {
            if (result.getSuccess()) {
                returnResult = OpenEipResult.success(null);
            } else {
                returnResult = OpenEipResult.error(result.getErrorCode(), result.getErrorMessage());
            }
        }
        context.putInterfaceContextValue(EipContextConstant.RESULT_KEY, returnResult);
    }

    private SuperMap convertExecutorContext(SuperMap executorContext) {
        SuperMap target = MapHelper.deepClone(executorContext, SuperMap::new);
        target.removeIteration(IEipContext.REQUEST_STORE_PREFIX);
        return target;
    }

    private static class OpenApiCallIntegrationApiParamConverter implements IEipParamConverter<SuperMap> {

        public static IEipParamConverter<SuperMap> INSTANCE = new OpenApiCallIntegrationApiParamConverter();

        @Override
        public void convert(IEipContext<SuperMap> context, List<IEipConvertParam<SuperMap>> convertParamList, IEipParamConverterCallback<SuperMap> callback) {
            for (IEipConvertParam<SuperMap> convertParam : convertParamList) {
                String outParam = convertParam.getOutParam();
                Object value = EipParamConverterHelper.convertValue(convertParam, EipParamConverterHelper.getContextValue(convertParam.getTargetContextType(), context, outParam));
                if (value == null) {
//                    if (Boolean.TRUE.equals(convertParam.getRequired())) {
//                        throw PamirsException.construct(EipExpEnumerate.PARAM_REQUIRED).appendMsg(outParam).errThrow();
//                    }
                    if (Boolean.TRUE.equals(convertParam.getIsKeepNull())) {
                        EipParamConverterHelper.putContextValue(convertParam.getTargetContextType(), context, outParam, null);
                    }
                    return;
                }
                EipParamConverterHelper.putContextValue(convertParam.getTargetContextType(), context, outParam, value);
            }
        }
    }
}
