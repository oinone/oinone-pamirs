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
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 异常时增加模型信息返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
public class FrontRequestExceptionDeal4Model implements FrontRequestExceptionDeal, FrontRequestResultDeal {

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {

        String modelName = SessionExtendUtils.gqlSegmentNameToModelNameUtils(path.getParent().getSegmentName());
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfigByName(modelName);
        Map<String, Object> msg = new LinkedHashMap<>();

        Map<String, Object> modelMsg = new LinkedHashMap<>();
        List<Object> fields = new ArrayList<>();
        fillMsg(modelConfig, modelMsg, fields);
        msg.put("模型摘要信息", modelMsg);
        msg.put("字段摘要信息", fields);
        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_MODEL, JsonUtils.toJSONString(msg)));
    }

    @Override
    public void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput) {
        List<Map<String, Object>> msgs = new ArrayList<>();
        ParseAndValidateResult parseResult = PamirsGQLDocumentParser.getParseResult(executionInput);
        Document document = parseResult.getDocument();
        OperationDefinition definition = (OperationDefinition) document.getDefinitions().get(0);
        for (Selection field : definition.getSelectionSet().getSelections()) {
            if (field instanceof Field) {
                String modelName = SessionExtendUtils.gqlSegmentNameToModelNameUtils(((Field) field).getName());
                ModelConfig modelConfig = PamirsSession.getContext().getModelConfigByName(modelName);
                Map<String, Object> msg = new LinkedHashMap<>();
                Map<String, Object> modelMsg = new LinkedHashMap<>();
                List<Object> fields = new ArrayList<>();
                fillMsg(modelConfig, modelMsg, fields);
                msg.put("模型摘要信息", modelMsg);
                msg.put("字段摘要信息", fields);
                msgs.add(msg);
            }
        }
        addDebugInfo(executionResult, ClientGraphQLError.build(StackTraceConstants.STACKTRACE_MODEL, JsonUtils.toJSONString(msgs)));
    }

    private void fillMsg(ModelConfig modelConfig, Map<String, Object> modelMsg, List<Object> fields) {
        if (modelConfig.getModelDefinition().getModel() != null)
            modelMsg.put("model", modelConfig.getModelDefinition().getModel());
        if (modelConfig.getModelDefinition().getLname() != null)
            modelMsg.put("模型所在类", modelConfig.getModelDefinition().getLname());
        if (modelConfig.getModelDefinition().getType() != null)
            modelMsg.put("模型类型", modelConfig.getModelDefinition().getType());
        if (modelConfig.getModelDefinition().getProxy() != null)
            modelMsg.put("proxy", modelConfig.getModelDefinition().getProxy());
        if (modelConfig.getModelDefinition().getOrdering() != null)
            modelMsg.put("排序信息", modelConfig.getModelDefinition().getOrdering());
        if (modelConfig.getModelDefinition().getSuperModels() != null)
            modelMsg.put("父模型", modelConfig.getModelDefinition().getSuperModels());
        if (modelConfig.getModelDefinition().getPk() != null)
            modelMsg.put("pk", modelConfig.getModelDefinition().getPk());
        if (modelConfig.getModelDefinition().getUniques() != null)
            modelMsg.put("唯一键", modelConfig.getModelDefinition().getUniques());
        if (modelConfig.getModelDefinition().getIndexes() != null)
            modelMsg.put("索引", modelConfig.getModelDefinition().getIndexes());

        if (modelConfig.getModelDefinition().getLabelFields() != null)
            modelMsg.put("标签字段", modelConfig.getModelDefinition().getLabelFields());
        if (modelConfig.getModelDefinition().getSys() != null)
            modelMsg.put("sys", modelConfig.getModelDefinition().getSys());
        if (modelConfig.getModelDefinition().getSystemSource() != null)
            modelMsg.put("模型来源", modelConfig.getModelDefinition().getSystemSource());
        if (modelConfig.getModelDefinition().getModule() != null)
            modelMsg.put("模块", modelConfig.getModelDefinition().getModule());
        if (modelConfig.getModelDefinition().getDsModule() != null)
            modelMsg.put("Ds模块", modelConfig.getModelDefinition().getDsModule());
        if (modelConfig.getModelDefinition().getDsKey() != null)
            modelMsg.put("DsKey", modelConfig.getModelDefinition().getDsKey());
        if (modelConfig.getModelDefinition().getOptimisticLocker() != null)
            modelMsg.put("乐观锁字段", modelConfig.getModelDefinition().getOptimisticLocker());

        for (ModelField modelField : modelConfig.getModelDefinition().getModelFields()) {
            Map<String, Object> field = new LinkedHashMap<>();
            if (modelField.getTtype() != null) field.put("类型", modelField.getTtype());
            if (modelField.getName() != null) field.put("名称", modelField.getName());
            if (modelField.getLtype() != null) field.put("java类型", modelField.getLtype());
            if (modelField.getLtypeT() != null) field.put("java范型", modelField.getLtypeT());
            if (modelField.getLname() != null) field.put("java名", modelField.getLname());
            if (modelField.getStore() != null) field.put("是否存储", modelField.getStore());
            if (modelField.getColumn() != null) field.put("数据库列", modelField.getColumn());
            if (modelField.getDomain() != null) field.put("字段过滤", modelField.getDomain());
            if (modelField.getImmutable() != null) field.put("是否支持修改", modelField.getImmutable());
            if (modelField.getFormat() != null) field.put("格式", modelField.getFormat());
            if (modelField.getMax() != null) field.put("最大值", modelField.getMax());
            if (modelField.getMin() != null) field.put("最小值", modelField.getMin());
            if (modelField.getModelThrough() != null) field.put("中间表模型", modelField.getModelThrough());
            if (modelField.getThroughRelationFields() != null)
                field.put("中间表模型关系字段", modelField.getThroughRelationFields());
            if (modelField.getThroughReferenceFields() != null)
                field.put("中间表模型关联字段", modelField.getThroughReferenceFields());
            if (modelField.getRelationFields() != null) field.put("关系字段", modelField.getRelationFields());
            if (modelField.getModelReferences() != null) field.put("关联模型", modelField.getModelReferences());
            if (modelField.getReferences() != null) field.put("关联字段", modelField.getReferences());
            if (modelField.getRelated() != null) field.put("引用字段", modelField.getRelated());
            fields.add(field);
        }
    }

    @Override
    public int priority() {
        return 402;
    }

}
