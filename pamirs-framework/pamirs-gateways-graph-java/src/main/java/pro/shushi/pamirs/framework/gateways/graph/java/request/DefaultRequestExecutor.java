package pro.shushi.pamirs.framework.gateways.graph.java.request;

import com.google.common.collect.Lists;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.execution.ExecutionStrategy;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.common.api.StackTraceApi;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.holder.GraphQLSchemaApiHolder;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.PamirsAsyncExecutionStrategy;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.provider.PamirsPreparsedDocumentProvider;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.RequestHelper;
import pro.shushi.pamirs.framework.gateways.graph.spi.DataLoaderRegistryApi;
import pro.shushi.pamirs.framework.gateways.graph.spi.InstrumentationApi;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.protocol.RequestExecutor;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.cache.holder.RequestMetaDataCacheApiHolder;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.gateways.graph.enmu.GqlExpEnumerate.BASE_GRAPHQL_EMPTY_ERROR;

/**
 * 请求执行器实现
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:09 上午
 */
@Slf4j
@Component
public class DefaultRequestExecutor implements RequestExecutor {

    private static GraphQLSchema schema;

    public static GraphQLSchema getSchema() {
        return schema;
    }

    public static void addSchema(GraphQLSchema schema) {
        DefaultRequestExecutor.schema = schema;
    }

    @Override
    public PamirsRequestResult execute(PamirsRequestParam param) {
        if (log.isDebugEnabled()) {
            String traceId1 = PamirsSession.getRequestVariables().getTraceId();
            long start1 = System.currentTimeMillis();
            RequestMetaDataCacheApiHolder.get().computeMetaData(param);
            long end1 = System.currentTimeMillis() - start1;
            log.debug("trace id: {}, RequestMetaDataCacheApi#computeMetaData cost time: {} ms", traceId1, end1);
            return executeWithHandleResult(param, (p) -> {
                String traceId2 = PamirsSession.getRequestVariables().getTraceId();
                long start2 = System.currentTimeMillis();
                ExecutionResult result = execute(GraphQL::execute, p);
                long end2 = System.currentTimeMillis() - start2;
                log.debug("trace id: {}, GraphQL#execute cost time: {} ms", traceId2, end2);
                return result;
            });
        }
        // 根据URI进行元数据缓存预热
        RequestMetaDataCacheApiHolder.get().computeMetaData(param);
        // 执行请求
        return executeWithHandleResult(param, (p) -> execute(GraphQL::execute, p));
    }

    @Override
    public List<PamirsRequestResult> executeAsync(List<PamirsRequestParam> pamirsRequestParamList) {
        int size = pamirsRequestParamList.size();
        List<PamirsRequestResult> resultList = new ArrayList<>(size);
        List<GQLAsyncExecutionResult> futureResultList = new ArrayList<>(size);
        for (PamirsRequestParam param : pamirsRequestParamList) {
            if (param == null) {
                futureResultList.add(null);
                continue;
            }
            PamirsRequestResult result = param.getResult();
            if (result == null) {
                // 根据URI进行元数据缓存预热
                RequestMetaDataCacheApiHolder.get().computeMetaData(param);
                // 执行请求
                try {
                    resultList.add(null);
                    futureResultList.add(execute((gql, input) -> new GQLAsyncExecutionResult(input, gql.executeAsync(input)), param));
                } catch (Exception e) {
                    result = new PamirsRequestResult();
                    resultList.add(handleException(result, e));
                    futureResultList.add(null);
                }
            } else {
                resultList.add(result);
                futureResultList.add(null);
            }
        }
        List<PamirsRequestResult> handleResultList = new ArrayList<>(size);
        if (SceneAnalysisDebugTraceApi.isDebug()) {
            for (GQLAsyncExecutionResult executionResult : futureResultList) {
                if (null != executionResult) {
                    try {
                        ExecutionResult result = executionResult.getResult().get();
                        if (CollectionUtils.isEmpty(result.getErrors())) {
                            Spider.getDefaultExtension(StackTraceApi.class).stackTrace(result, executionResult.getInput());
                        }
                        handleResultList.add(handleResult(new PamirsRequestResult(), result));
                    } catch (Exception e) {
                        handleResultList.add(handleException(new PamirsRequestResult(), e));
                    }
                } else {
                    handleResultList.add(null);
                }
            }
        } else {
            for (GQLAsyncExecutionResult executionResult : futureResultList) {
                if (null != executionResult) {
                    try {
                        handleResultList.add(handleResult(new PamirsRequestResult(), executionResult.getResult().get()));
                    } catch (Exception e) {
                        handleResultList.add(handleException(new PamirsRequestResult(), e));
                    }
                } else {
                    handleResultList.add(null);
                }
            }
        }
        return RequestHelper.mergeList(handleResultList, resultList);
    }

    private GQLExecutionInput buildGraphQLInput(GraphQL graphQL, PamirsRequestParam param) {
        return new GQLExecutionInput(graphQL, ExecutionInput.newExecutionInput()
                .query(param.getQuery())
                .variables(param.getVariables().getVariables())
                .dataLoaderRegistry(DataLoaderRegistryApi.get().dataLoader())
                .build());
    }

    private <T> T execute(BiFunction<GraphQL, ExecutionInput, T> executor, PamirsRequestParam param) {
        // 获取GraphQL
        GraphQL graphQL = buildGraphQL(param);
        if (null == graphQL) {
            throw PamirsException.construct(BASE_GRAPHQL_EMPTY_ERROR).errThrow();
        }
        GQLExecutionInput gqlExecutionInput = buildGraphQLInput(graphQL, param);
        T result;
        ExecutionInput executionInput = gqlExecutionInput.getInput();
        if (SceneAnalysisDebugTraceApi.isDebug()) {
            Spider.getDefaultExtension(StackTraceApi.class).init(executionInput);
            result = executor.apply(graphQL, executionInput);
            if (result instanceof ExecutionResult && CollectionUtils.isEmpty(((ExecutionResult) result).getErrors())) {
                Spider.getDefaultExtension(StackTraceApi.class).stackTrace((ExecutionResult) result, executionInput);
            }
        } else {
            result = executor.apply(graphQL, executionInput);
        }
        return result;
    }

    private PamirsRequestResult executeWithHandleResult(PamirsRequestParam param, Function<PamirsRequestParam, ExecutionResult> executor) {
        PamirsRequestResult result = new PamirsRequestResult();
        try {
            // 执行
            ExecutionResult executionResult = executor.apply(param);
            // 处理结果
            return handleResult(result, executionResult);
        } catch (Exception e) {
            return handleException(result, e);
        }
    }

    private PamirsRequestResult handleResult(PamirsRequestResult result, ExecutionResult executionResult) {
        result.setData(executionResult.getData());
        result.setExtensions(executionResult.getExtensions());
        result.setErrors(executionResult.getErrors().stream().map(GraphQLError::toSpecification).collect(Collectors.toList()));
        return result;
    }

    private PamirsRequestResult handleException(PamirsRequestResult result, Exception e) {
        log.error(FwExpEnumerate.SYSTEM_ERROR.getMsg(), e);
        Map<String, Object> errorMap = new HashMap<>(2);
        if (e instanceof PamirsException) {
            PamirsException pamirsException = (PamirsException) e;
            errorMap.put(ClientGraphQLError.MESSAGE, pamirsException.getMessage());
            errorMap.put(ClientGraphQLError.EXTENSIONS, ClientGraphQLError.getExtensions(pamirsException));
        } else {
            errorMap.put(ClientGraphQLError.MESSAGE, FwExpEnumerate.SYSTEM_ERROR.getMsg());
            errorMap.put(ClientGraphQLError.EXTENSIONS, ClientGraphQLError.getExtensions(FwExpEnumerate.SYSTEM_ERROR));
        }
        return result.setErrors(Lists.newArrayList(errorMap));
    }

    public static GraphQL buildGraphQL() {
        Instrumentation instrumentation = Spider.getDefaultExtension(InstrumentationApi.class).build();
        return build(getSchema(), instrumentation);
    }

    public GraphQL buildGraphQL(PamirsRequestParam param) {
        GraphQLSchema graphQLSchema;
        if (log.isDebugEnabled()) {
            String traceId = PamirsSession.getRequestVariables().getTraceId();
            long start = System.currentTimeMillis();
            graphQLSchema = GraphQLSchemaApiHolder.get().build(param);
            log.debug("traceId: {}, dynamic gql build cost time: {} ms.", traceId, System.currentTimeMillis() - start);
        } else {
            graphQLSchema = GraphQLSchemaApiHolder.get().build(param);
        }
        Instrumentation instrumentation = Spider.getDefaultExtension(InstrumentationApi.class).build();
        return build(graphQLSchema, instrumentation);
    }

    private static GraphQL build(GraphQLSchema graphQLSchema, Instrumentation instrumentation) {
        GraphQL.Builder builder = GraphQL.newGraphQL(graphQLSchema);
        if (null != instrumentation) {
            builder = builder.instrumentation(instrumentation);
        }
        dealExecutionAndExceptionStrategy(builder);
        return builder.preparsedDocumentProvider(new PamirsPreparsedDocumentProvider(graphQLSchema, instrumentation))
                .build();
    }

    private static void dealExecutionAndExceptionStrategy(GraphQL.Builder builder) {
        ExecutionStrategy executionStrategy = new PamirsAsyncExecutionStrategy(DefaultRequestExceptionHandler.INSTANCE);
        builder.queryExecutionStrategy(executionStrategy);
    }

    private static class GQLExecutionInput {

        private final GraphQL graphQL;

        private final ExecutionInput input;

        public GQLExecutionInput(GraphQL graphQL, ExecutionInput input) {
            this.graphQL = graphQL;
            this.input = input;
        }

        public GraphQL getGraphQL() {
            return graphQL;
        }

        public ExecutionInput getInput() {
            return input;
        }
    }

    @Data
    private static class GQLAsyncExecutionResult {

        private final ExecutionInput input;

        private final CompletableFuture<ExecutionResult> result;

        public GQLAsyncExecutionResult(ExecutionInput input, CompletableFuture<ExecutionResult> result) {
            this.input = input;
            this.result = result;
        }

        public ExecutionInput getInput() {
            return input;
        }

        public CompletableFuture<ExecutionResult> getResult() {
            return result;
        }
    }
}
