package pro.shushi.pamirs.ux.quickfilling.action;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.ux.common.enumeration.UxCommonExpEnumerate;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingContext;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.model.QuickFilling;
import pro.shushi.pamirs.ux.quickfilling.model.QuickFillingFailure;
import pro.shushi.pamirs.ux.quickfilling.model.QuickFillingFailureDetail;
import pro.shushi.pamirs.ux.quickfilling.model.QuickFillingField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Gesi at 17:33 on 2025/9/10
 */
@Base
@Component
@Model.model(QuickFilling.MODEL_MODEL)
public class QuickFillingAction {

    private static final TypeReference<List<Map<String, String>>> PARAM_VALUE_TYPE_REFERENCE = new TypeReference<List<Map<String, String>>>() {
    };

    @Function.Advanced(displayName = "快速填报加载数据", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public QuickFilling loadData(QuickFilling quickFilling) {
        String model = quickFilling.getModel();
        LoadContextResult contexts = loadContexts(model, quickFilling.getFields());
        List<Map<String, String>> data = loadData(quickFilling.getValues());
        List<QuickFillingRow> rows = initRows(model, data.size());
        if (!contexts.basicList.isEmpty()) {
            basicConvert(contexts.basicList, data, rows);
        }
        if (!contexts.nonBasicList.isEmpty()) {
            nonBasicConvert(contexts.nonBasicList, data, rows);
        }
        return collectionResult(model, rows);
    }

    private List<Map<String, String>> loadData(String values) {
        return JsonUtils.parseObject(values, PARAM_VALUE_TYPE_REFERENCE);
    }

    private LoadContextResult loadContexts(String model, List<QuickFillingField> fillingFields) {
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (modelConfig == null) {
            throw PamirsException.construct(UxCommonExpEnumerate.MODEL_NOT_FOUND, model).errThrow();
        }
        Map<String, ModelFieldConfig> modelFieldConfigCache = modelConfig.getModelFieldConfigList().stream().collect(Collectors.toMap(ModelFieldConfig::getField, v -> v));
        LoadContextResult result = new LoadContextResult();
        for (QuickFillingField fillingField : fillingFields) {
            String field = fillingField.getField();
            if (StringUtils.isBlank(field)) {
                throw PamirsException.construct(UxCommonExpEnumerate.MODEL_FIELD_NOT_FOUND, model, field).errThrow();
            }
            ModelFieldConfig modelFieldConfig = modelFieldConfigCache.get(field);
            if (modelFieldConfig == null) {
                throw PamirsException.construct(UxCommonExpEnumerate.MODEL_FIELD_NOT_FOUND, model, field).errThrow();
            }
            QuickFillingContext context = new QuickFillingContext(modelConfig, modelFieldConfig, fillingField);
            if (context.getConverter().isBasicConverter()) {
                result.basicList.add(context);
            } else {
                result.nonBasicList.add(context);
            }
        }
        return result;
    }

    private List<QuickFillingRow> initRows(String model, int size) {
        List<QuickFillingRow> rows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            rows.add(new QuickFillingRow(model, i));
        }
        return rows;
    }

    private void basicConvert(List<QuickFillingContext> contexts, List<Map<String, String>> dataList, List<QuickFillingRow> rows) {
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, String> data = dataList.get(i);
            QuickFillingRow row = rows.get(i);
            for (QuickFillingContext context : contexts) {
                String origin = data.get(context.getField());
                context.getConverter().convert(row, origin);
            }
        }
    }

    private void nonBasicConvert(List<QuickFillingContext> contexts, List<Map<String, String>> dataList, List<QuickFillingRow> rows) {
        for (QuickFillingContext context : contexts) {
            String field = context.getField();
            QuickFillingConverter converter = context.getConverter();
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, String> data = dataList.get(i);
                QuickFillingRow row = rows.get(i);
                String origin = data.get(field);
                converter.collect(row, origin);
            }
            converter.fill();
        }
    }

    private QuickFilling collectionResult(String model, List<QuickFillingRow> rows) {
        QuickFilling result = new QuickFilling();
        List<Object> data = new ArrayList<>();
        List<QuickFillingFailure> failures = new ArrayList<>();
        for (QuickFillingRow row : rows) {
            boolean isAppendData = false;
            if (row.isNotEmpty()) {
                data.add(PamirsDataUtils.modelObjectToJsonObject(model, row.getData()));
                isAppendData = true;
            }
            if (collectionFailure(failures, row)) {
                if (!isAppendData) {
                    data.add(new HashMap<>());
                }
            }
        }
        if (!data.isEmpty()) {
            result.setValues(JsonUtils.toJSONString(data));
        }
        if (!failures.isEmpty()) {
            result.setFailures(failures);
        }
        return result;
    }

    private boolean collectionFailure(List<QuickFillingFailure> failures, QuickFillingRow row) {
        List<QuickFillingFailureDetail> failureDetails = row.getFailures();
        if (failureDetails.isEmpty()) {
            return false;
        }
        QuickFillingFailure failure = new QuickFillingFailure();
        failure.setRowNumber(row.getRowIndex());
        failure.setDetailList(failureDetails);
        failures.add(failure);
        return true;
    }

    private static class LoadContextResult {

        private final List<QuickFillingContext> basicList = new ArrayList<>();

        private final List<QuickFillingContext> nonBasicList = new ArrayList<>();

    }
}
