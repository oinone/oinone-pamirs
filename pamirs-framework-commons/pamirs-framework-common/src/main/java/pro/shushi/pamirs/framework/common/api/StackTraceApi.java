package pro.shushi.pamirs.framework.common.api;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 请求堆栈追踪API
 * <p>
 * 2024/4/3 5:53 下午
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface StackTraceApi {

    void stackTraceException(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable e, ExecutionPath path);

    void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput);

    void init(ExecutionInput executionInput);

    void init(Invoker<?> invoker, Invocation invocation);

}