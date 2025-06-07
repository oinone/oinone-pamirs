package pro.shushi.pamirs.framework.gateways.graph.java.debug.exception;

import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.StackTraceConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 异常时增加业务堆栈返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
public class FrontRequestExceptionDeal4RequestContext implements FrontRequestExceptionDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        Map<String, Object> gqlContext = new LinkedHashMap<>();
        gqlContext.put("请求路径", path.toString());
        gqlContext.put("请求参数", handlerParameters.getArgumentValues());
        gqlContext.put("当前Source", handlerParameters.getDataFetchingEnvironment().getSource());
        gqlContext.put("Root", handlerParameters.getDataFetchingEnvironment().getRoot());
        gqlContext.put("上下文", handlerParameters.getDataFetchingEnvironment().getContext());
        gqlContext.put("LocalContext", handlerParameters.getDataFetchingEnvironment().getLocalContext());

        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_GQL_REQUEST_CONTEXT, JsonUtils.toJSONString(gqlContext)));
    }

    @Override
    public int priority() {
        return 301;
    }
}
