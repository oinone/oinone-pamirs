package pro.shushi.pamirs.framework.gateways.graph.java.debug.scene;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestResultDeal;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 异常时增加业务堆栈返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
@Slf4j
public class FrontRequestExceptionDeal4SceneAnalysis implements FrontRequestExceptionDeal, FrontRequestResultDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        Map<String, SceneAnalysisDebugTraceApi> sceneAnalysisDebugTraceApiMap = BeanDefinitionUtils.getBeansOfType(SceneAnalysisDebugTraceApi.class);
        List<SceneAnalysisDebugTraceApi> sceneAnalysisDebugTraceApis = new ArrayList<>(Objects.requireNonNull(sceneAnalysisDebugTraceApiMap).values());
        for (SceneAnalysisDebugTraceApi sceneAnalysisDebugTraceApi : sceneAnalysisDebugTraceApis) {
            try {
                Map<String, Object> traceRs = sceneAnalysisDebugTraceApi.stackTrace();
                if (traceRs == null) {
                    continue;
                }
                result.getErrors().add(ClientGraphQLError.build(ImmutablePair.of("stacktraceSceneAnalysis", "Scene trace:[" + sceneAnalysisDebugTraceApi.scene() + "]"), JsonUtils.toJSONString(traceRs)));
            } catch (Throwable throwable) {
                //忽略
                log.error("Scene exception trace execution failed: [{}], exception:{}", sceneAnalysisDebugTraceApi.getClass().getTypeName(), ExceptionUtils.getStackTrace(throwable));
            }
        }
    }

    @Override
    public void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput) {
        List<SceneAnalysisDebugTraceApi> sceneAnalysisDebugTraceApis = BeanDefinitionUtils.getBeansOfTypeByOrdered(SceneAnalysisDebugTraceApi.class);
        for (SceneAnalysisDebugTraceApi sceneAnalysisDebugTraceApi : sceneAnalysisDebugTraceApis) {
            try {
                Map<String, Object> traceRs = sceneAnalysisDebugTraceApi.stackTrace();
                if (traceRs == null) {
                    continue;
                }
                addDebugInfo(executionResult, ClientGraphQLError.build(ImmutablePair.of("stacktraceSceneAnalysis", "Scene trace:[" + sceneAnalysisDebugTraceApi.scene() + "]"), JsonUtils.toJSONString(traceRs)));
            } catch (Throwable throwable) {
                //忽略
                log.error("Scene exception trace execution failed: [{}], exception:{}", sceneAnalysisDebugTraceApi.getClass().getTypeName(), ExceptionUtils.getStackTrace(throwable));
            }
        }
    }

    @Override
    public int priority() {
        return 201;
    }
}
