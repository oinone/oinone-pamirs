package pro.shushi.pamirs.framework.gateways.graph.java.request;

import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.common.api.StackTraceApi;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 默认请求异常处理
 *
 * @author Adamancy Zhang at 18:49 on 2025-04-01
 */
@Slf4j
public class DefaultRequestExceptionHandler implements DataFetcherExceptionHandler {

    public static final DataFetcherExceptionHandler INSTANCE = new DefaultRequestExceptionHandler();

    @Override
    public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
        // 错误处理
        Throwable exception = handlerParameters.getException();
        if (null != exception.getCause() && exception.getCause() instanceof PamirsException) {
            exception = exception.getCause();
        }
        SourceLocation sourceLocation = handlerParameters.getSourceLocation();
        ExecutionPath path = handlerParameters.getPath();
        ExceptionWhileDataFetching error = new ExceptionWhileDataFetching(path, exception, sourceLocation);
        if (log.isWarnEnabled()) {
            log.warn(error.getMessage(), exception);
        }
        DataFetcherExceptionHandlerResult result;
        if (exception instanceof PamirsException) {
            result = DataFetcherExceptionHandlerResult.newResult()
                    .error(ClientGraphQLError.build((PamirsException) exception)).build();
        } else {
            result = DataFetcherExceptionHandlerResult.newResult().error(error).build();
        }
        if (SceneAnalysisDebugTraceApi.isDebug()) {
            Spider.getDefaultExtension(StackTraceApi.class).stackTraceException(handlerParameters, result, exception, path);
        }
        return result;
    }
}
