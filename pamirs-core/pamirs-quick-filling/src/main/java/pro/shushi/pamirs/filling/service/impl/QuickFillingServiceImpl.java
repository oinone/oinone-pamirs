package pro.shushi.pamirs.filling.service.impl;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.filling.converter.*;
import pro.shushi.pamirs.filling.enumeration.QuickFillingExpEnumerate;
import pro.shushi.pamirs.filling.model.QuickFilling;
import pro.shushi.pamirs.filling.model.QuickFillingFailure;
import pro.shushi.pamirs.filling.model.QuickFillingFailureDetail;
import pro.shushi.pamirs.filling.model.QuickFillingField;
import pro.shushi.pamirs.filling.service.QuickFillingService;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;

/**
 * @author Gesi at 18:02 on 2025/9/10
 */
@Slf4j
@Service
public class QuickFillingServiceImpl implements QuickFillingService {

    private static final TypeReference<List<Map<String, String>>> PARAM_VALUE_TYPE_REFERENCE = new TypeReference<List<Map<String, String>>>() {
    };

    @Override
    public QuickFilling loadData(QuickFilling quickFilling) {
        ModelConfig modelConfig = loadModelConfig(quickFilling.getModel());
        Map<String, QuickFillingContext> fieldContexts = loadFieldContexts(modelConfig, quickFilling.getFields());
        List<Map<String, String>> values = loadValues(quickFilling.getValues());
        String model = modelConfig.getModel();
        List<Object> results = new ArrayList<>();
        List<QuickFillingFailure> failures = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Map<String, String> rowData = values.get(i);
            Object modelObject = PamirsDataUtils.jsonObjectToModelObject(model, new HashMap<>());
            QuickFillingFailure quickFillingFailure = new QuickFillingFailure();
            quickFillingFailure.setRowNumber(i);
            List<QuickFillingFailureDetail> detailList = new ArrayList<>(rowData.size());
            quickFillingFailure.setDetailList(detailList);
            boolean isResolved = false;
            for (QuickFillingContext fieldContext : fieldContexts.values()) {
                fieldContext.clear();
                fieldContext.setTarget(modelObject);
                String field = fieldContext.getField();
                String origin = rowData.get(field);
                if (StringUtils.isBlank(origin)) {
                    continue;
                }
                origin = origin.trim();
                Object target = transformObjectValue(fieldContext, origin);
                isResolved = true;
                if (fieldContext.isFailed()) {
                    detailList.addAll(fieldContext.getFailures());
                }
                if (target != null) {
                    FieldUtils.setFieldValue(modelObject, field, target);
                }
            }
            if (CollectionUtils.isNotEmpty(detailList)) {
                failures.add(quickFillingFailure);
            }
            if (isResolved) {
                modelObject = Fun.run(model, FunctionConstants.construct, modelObject);
                if (modelObject != null) {
                    modelObject = PamirsDataUtils.modelObjectToJsonObject(model, modelObject);
                }
                results.add(modelObject);
            }
        }
        quickFilling.setFailures(failures);
        quickFilling.setValues(JsonUtils.toJSONString(results));
        return quickFilling;
    }

    private ModelConfig loadModelConfig(String model) {
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (modelConfig == null) {
            throw PamirsException.construct(QuickFillingExpEnumerate.MODEL_NOT_FIND).appendMsg("模型" + model + "找不到").errThrow();
        }
        return modelConfig;
    }

    private Map<String, QuickFillingContext> loadFieldContexts(ModelConfig modelConfig, List<QuickFillingField> fields) {
        RequestContext requestContext = PamirsSession.getContext();
        String model = modelConfig.getModel();
        Map<String, QuickFillingContext> fieldContexts = new LinkedHashMap<>();
        for (QuickFillingField field : fields) {
            ModelFieldConfig modelFieldConfig = requestContext.getModelField(model, field.getField());
            if (modelFieldConfig == null) {
                throw PamirsException.construct(QuickFillingExpEnumerate.FIELD_NOT_FIND).appendMsg(field + "字段在模型" + model + "内找不到").errThrow();
            }
            QuickFillingContext fieldContext = new QuickFillingContext(modelConfig, modelFieldConfig, field);
            fieldContexts.put(field.getField(), fieldContext);
        }
        return fieldContexts;
    }

    private List<Map<String, String>> loadValues(String values) {
        return JsonUtils.parseObject(values, PARAM_VALUE_TYPE_REFERENCE);
    }

    private Object transformObjectValue(QuickFillingContext context, String value) {
        ModelFieldConfig modelFieldConfig = context.getModelFieldConfig();
        String ttypeValue = modelFieldConfig.getTtype();
        if (TtypeEnum.RELATED.value().equals(ttypeValue)) {
            ttypeValue = modelFieldConfig.getRelatedTtype();
        }
        QuickFillingValueConverter converter = null;
        switch (ttypeValue) {
            case "string":
            case "text":
            case "html":
            case "phone":
            case "email":
                converter = StringConverter.INSTANCE;
                break;
            case "integer":
            case "float":
            case "uid":
            case "money":
                converter = NumberConverter.INSTANCE;
                break;
            case "bool":
                converter = BooleanConverter.INSTANCE;
                break;
            case "datetime":
            case "date":
            case "time":
            case "year":
                converter = DateConverter.INSTANCE;
                break;
            case "enum":
                converter = EnumConverter.INSTANCE;
                break;
            case "m2o":
            case "o2o":
                converter = M2OConverter.INSTANCE;
                break;
            case "m2m":
            case "o2m":
                converter = M2MConverter.INSTANCE;
                break;
            case "map":
                return transformMapValue(context, value);
        }
        if (converter != null) {
            try {
                return converter.convert(context, value);
            } catch (Throwable e) {
                log.error("quick filling {} value error.", ttypeValue, e);
                context.fail();
                return null;
            }
        }
        context.fail(ttypeValue + "类型不支持自动填报");
        return null;
    }

    private Object transformMapValue(QuickFillingContext context, String value) {
        try {
            if (value == null) {
                return null;
            }
            if (JsonUtils.isJSONArray(value)) {
                return JsonUtils.parseObjectList(value);
            } else if (JsonUtils.isJSONObject(value)) {
                return JsonUtils.parseObject(value);
            }
            context.fail();
            return null;
        } catch (Throwable e) {
            log.error("quick filling map value convert error.", e);
            context.fail();
            return null;
        }
    }
}
