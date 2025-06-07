package pro.shushi.pamirs.framework.gateways.graph.java.debug.common;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.StackTraceConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestResultDeal;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.Map;

/**
 * 异常时增加业务堆栈返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
public class FrontRequestExceptionDeal4PamirsSession implements FrontRequestExceptionDeal, FrontRequestResultDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        Map<String, String> sessionMap = PamirsSession.getSessionApi().fetchSessionMap();
        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_SESSION, JsonUtils.toJSONString(sessionMap)));
    }

    @Override
    public void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput) {
        Map<String, String> sessionMap = PamirsSession.getSessionApi().fetchSessionMap();
        addDebugInfo(executionResult, ClientGraphQLError.build(StackTraceConstants.STACKTRACE_SESSION, JsonUtils.toJSONString(sessionMap)));
    }

    @Override
    public int priority() {
        return 302;
    }
}
