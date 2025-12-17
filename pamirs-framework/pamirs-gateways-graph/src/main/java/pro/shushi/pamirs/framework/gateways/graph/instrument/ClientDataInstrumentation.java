package pro.shushi.pamirs.framework.gateways.graph.instrument;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import pro.shushi.pamirs.framework.gateways.graph.spi.FunctionResolverApi;
import pro.shushi.pamirs.framework.gateways.graph.util.GraphQLUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientArgumentHandlerApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.RequestParamConstants;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ClientDataInstrumentation extends SimpleInstrumentation {

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        String queryOrMutationTypeName = (String) PamirsSession.getRequestInfo(RequestParamConstants.QUERY_OR_MUTATION_TYPE_NAME);
        String funNamespace = (String) PamirsSession.getRequestInfo(RequestParamConstants.FUN_NAMESPACE);
        String funName = (String) PamirsSession.getRequestInfo(RequestParamConstants.FUN_NAME);

        //请求是GraphQL文档的情况下funNamespace 和 funName为Null
        if (funNamespace != null && funName != null) {
            if (log.isDebugEnabled()) {
                long start = System.currentTimeMillis();
                resultConvert(executionResult);
                log.debug("trace id: {}, queryOrMutationTypeName: {}, namespace: {}, name: {}, client orm cost time: {}",
                        PamirsSession.getRequestVariables().getTraceId(), queryOrMutationTypeName, funNamespace, funName, System.currentTimeMillis() - start);
            } else {
                resultConvert(executionResult);
            }
        }

        return super.instrumentExecutionResult(executionResult, parameters);
    }

    private void resultConvert(ExecutionResult executionResult) {
        Map<String, Map<String, Object>> queryOrMutationResults = Optional.ofNullable(executionResult).<Map<String, Map<String, Object>>>map(ExecutionResult::getData).orElse(null);
        if (queryOrMutationResults == null) {
            return;
        }
        for (Map.Entry<String, Map<String, Object>> queryOrMutationResultEntry : queryOrMutationResults.entrySet()) {
            String queryOrMutationTypeName = queryOrMutationResultEntry.getKey();
            Map<String, Object> queryOrMutationResult = queryOrMutationResultEntry.getValue();
            if (queryOrMutationResult == null) {
                continue;
            }
            String modelName = GraphQLUtils.toModelName(queryOrMutationTypeName);
            ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfigByName(modelName);
            String funNamespace = modelConfig.getModel();
            for (Map.Entry<String, Object> functionResultEntry : queryOrMutationResult.entrySet()) {
                String funName = functionResultEntry.getKey();
                Object functionResult = functionResultEntry.getValue();
                if (functionResult == null) {
                    continue;
                }
                Function function = FunctionResolverApi.get().resolveFunction(funNamespace, funName);
                functionResultEntry.setValue(out(function, functionResult));
            }
        }
    }

    private Object out(Function function, Object obj) {
        ClientArgumentHandlerApi argumentHandlerApi = Spider.getDefaultExtension(ClientArgumentHandlerApi.class);
        Object result = argumentHandlerApi.out(function.getReturnType().getModel(), function.getNamespace(), obj);
        outAfter(function.getReturnType().getModel(), result);
        return result;
    }

    private void outAfter(String model, Object obj) {
        ClientDataInstrumentationAfterClear clearApi = BeanDefinitionUtils.getBean(ClientDataInstrumentationAfterClear.class);
        String objModel = Models.api().getModel(obj);
        clearApi.clear(model, obj);
        if (Pagination.MODEL_MODEL.equals(objModel)) {
            Map<?, ?> pagination = (Map<?, ?>) obj;
            clearApi.clear(model, pagination.get(Pagination.CONTENT));
        }
    }

}
