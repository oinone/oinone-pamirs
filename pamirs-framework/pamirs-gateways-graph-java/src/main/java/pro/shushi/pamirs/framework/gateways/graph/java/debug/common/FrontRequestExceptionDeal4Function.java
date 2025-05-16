package pro.shushi.pamirs.framework.gateways.graph.java.debug.common;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.ParseAndValidateResult;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.language.Selection;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.StackTraceConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestResultDeal;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.parser.PamirsGQLDocumentParser;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.SessionExtendUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 异常时增加函数信息返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
public class FrontRequestExceptionDeal4Function implements FrontRequestExceptionDeal, FrontRequestResultDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters,DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        String modelName = SessionExtendUtils.gqlSegmentNameToModelNameUtils(path.getParent().getSegmentName());
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfigByName(modelName);
        Function function = PamirsSession.getContext().getFunction(modelConfig.getModel(),path.getSegmentName());
        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_FUNCTION, JsonUtils.toJSONString(function)));
    }

    @Override
    public void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput) {
        List<Function> functions = new ArrayList<>();
        ParseAndValidateResult parseResult = PamirsGQLDocumentParser.getParseResult(executionInput);
        Document document = parseResult.getDocument();
        OperationDefinition definition = (OperationDefinition)document.getDefinitions().get(0);
        for (Selection selection : definition.getSelectionSet().getSelections()){
            String modelName = SessionExtendUtils.gqlSegmentNameToModelNameUtils(((Field) selection).getName());
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfigByName(modelName);
            for (Selection funSelection : ((Field) selection).getSelectionSet().getSelections()) {
                String funName = ((Field) funSelection).getName();
                Function function = PamirsSession.getContext().getFunctionByName(modelConfig.getModel(), funName);
                if (null != function) {
                    functions.add(function);
                }
            }
        }
        addDebugInfo(executionResult,ClientGraphQLError.build(StackTraceConstants.STACKTRACE_FUNCTION, JsonUtils.toJSONString(functions)));
    }

    @Override
    public int priority() {
        return 401;
    }
}
