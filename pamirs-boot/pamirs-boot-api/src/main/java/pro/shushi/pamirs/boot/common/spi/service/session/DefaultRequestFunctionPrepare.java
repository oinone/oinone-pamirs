package pro.shushi.pamirs.boot.common.spi.service.session;

import graphql.ExecutionInput;
import graphql.GraphQLException;
import graphql.ParseAndValidateResult;
import graphql.language.*;
import graphql.parser.InvalidSyntaxException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.spi.RequestFunctionPrepareApi;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.parser.PamirsGQLDocumentParser;
import pro.shushi.pamirs.framework.gateways.graph.util.GraphQLUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.constant.RequestParamConstants;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 默认session处理实现
 *
 * @author Adamancy Zhang on 2021-04-20 17:59
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultRequestFunctionPrepare implements RequestFunctionPrepareApi {

    @Override
    public void prepare(HttpServletRequest request, PamirsRequestParam requestParam) {
        // __schema 请求GQL的Schema
        if (requestParam.getQuery() == null || requestParam.getQuery().contains("__schema")) {
            return;
        }
        Map<String, Object> requestInfoMap = requestParam.getVariables().getRequestInfoMap();
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(requestParam.getQuery())
                .variables(requestParam.getVariables().getVariables())
                .build();
        ParseAndValidateResult parseResult = PamirsGQLDocumentParser.getParseResult(executionInput);
        Document document = parseResult.getDocument();
        if (document == null) {
            InvalidSyntaxException exception = parseResult.getSyntaxException();
            if (exception != null) {
                throw exception;
            }
            throw new GraphQLException("Invalid gql: " + requestParam.getQuery());
        }
        OperationDefinition definition = (OperationDefinition) document.getDefinitions().get(0);
        Field funField = (Field) definition.getSelectionSet().getSelections().get(0);

        requestInfoMap.put(RequestParamConstants.QUERY_OR_MUTATION_TYPE_NAME, funField.getName());
        String modelName = GraphQLUtils.toModelName(funField.getName());
        log.debug("===DefaultRequestFunctionPrepare#after:modelName:{}", modelName);
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfigByName(modelName);
        if (modelConfig == null) {
            log.warn("===DefaultRequestFunctionPrepare#Fun-ModelConfig is null: modelName:{}", modelName);
            return;
        }
        requestInfoMap.put(RequestParamConstants.FUN_NAMESPACE, modelConfig.getModel());
        NodeUtil.GetOperationResult getOperationResult = NodeUtil.getOperation(document, executionInput.getOperationName());
        OperationDefinition operationDefinition = getOperationResult.operationDefinition;
        for (Selection selection : operationDefinition.getSelectionSet().getSelections()) {
            for (Selection funSelection : ((Field) selection).getSelectionSet().getSelections()) {
                String funName = ((Field) funSelection).getName();
                log.debug("===DefaultRequestFunctionPrepare#after:funName:{}", funName);
                requestInfoMap.put(RequestParamConstants.FUN_NAME, funName);
                break;
            }
        }
    }
}
