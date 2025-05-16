package pro.shushi.pamirs.framework.gateways.graph.java.debug.exception;

import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.StackTraceConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;

/**
 * 默认前端请求异常处理接口
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
public class DefaultFrontRequestFullExceptionDeal implements FrontRequestExceptionDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_EXCEPTION, ExceptionUtils.getStackTrace(exception)));
    }

    @Override
    public int priority() {
        return 103;
    }
}
