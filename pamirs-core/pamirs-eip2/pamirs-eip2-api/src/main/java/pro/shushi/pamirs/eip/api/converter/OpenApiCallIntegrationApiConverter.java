package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;
import pro.shushi.pamirs.eip.api.config.EipOpenApiSwitchCondition;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.entity.openapi.OpenEipResult;
import pro.shushi.pamirs.eip.api.strategy.exception.CircuitBreakerOpenException;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

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

    @Autowired
    private EipExecuteService<SuperMap> eipExecuteService;

    @Function.fun(EipFunctionConstant.DEFAULT_OPEN_API_CALL_INTEGRATION_API_FUN)
    @Function.Advanced(displayName = "开放接口调用集成接口")
    @Function(name = EipFunctionConstant.DEFAULT_OPEN_API_CALL_INTEGRATION_API_FUN)
    public Object convertFunction(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        convert(context, exchange);
        return null;
    }

    @Override
    public void convert(IEipContext<SuperMap> context, ExtendedExchange exchange) {
        SuperMap body = convertExecutorContext(context.getInterfaceContext(), context.getExecutorContext());
        body = convertInterfaceContext(body);
        EipResult<SuperMap> result = eipExecuteService.callByInterfaceName(context.getApi().getInterfaceName(), context.getExecutorContext(), body);
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

    private SuperMap convertInterfaceContext(SuperMap interfaceContext) {
        if (CollectionUtils.isEmpty(interfaceContext)) {
            return null;
        }

        SuperMap queryParams = (SuperMap) interfaceContext.getIteration(IEipContext.URL_QUERY_PARAMS_KEY);
        if (queryParams != null) {
            for (String queryKey : queryParams.keySet()) {
                interfaceContext.putIteration(queryKey, queryParams.get(queryKey));
            }
        }
        return interfaceContext;
    }

    private SuperMap convertExecutorContext(SuperMap interfaceContext, SuperMap executorContext) {
        if (CollectionUtils.isEmpty(executorContext)) {
            return null;
        }
        if (interfaceContext == null) {
            interfaceContext = new SuperMap();
        }

        Object headerParamsObj = executorContext.getIteration(IEipContext.HEADER_PARAMS_KEY);
        if (headerParamsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> headerParams = (Map<String, Object>) headerParamsObj;
            headerParams.forEach(interfaceContext::putIteration);
        } else if (headerParamsObj != null) {
            log.error("Header parameter conversion failed: expected a Map, but found {}", headerParamsObj.getClass().getName());
        }
        return interfaceContext;
    }
}
