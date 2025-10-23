package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.tmodel.QuickFilling;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailure;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.enmu.QuickFillingExpEnumerate;
import pro.shushi.pamirs.boot.web.service.QuickFillingService;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.boot.web.service.impl.filling.*;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        loadModelConfig(quickFilling);
        loadParamValues(quickFilling);
        List<Map<String, String>> paramValues = quickFilling.getValues();
        quickFilling.setResultValues(new ArrayList<>(paramValues.size()));
        quickFilling.setFailures(new ArrayList<>(paramValues.size()));

        String model = quickFilling.getModel();

        for (int i = 0; i < paramValues.size(); i++) {
            Map<String, String> paramValue = paramValues.get(i);
            Object modelObject = PamirsDataUtils.jsonObjectToModelObject(model, new JSONObject());

            final Object finalModelObject = modelObject;
            final Integer rowIndex = i;
            QuickFillingFailure quickFillingFailure = new QuickFillingFailure();
            quickFillingFailure.setRowNumber(rowIndex);
            quickFillingFailure.setDetailList(new ArrayList<>(paramValue.size()));
            Map<String, QuickFillingField> fields = quickFilling.getFields();
            for (Map.Entry<String, QuickFillingField> entry : fields.entrySet()) {
                String field = entry.getKey();
                QuickFillingField header = entry.getValue();
                String value = paramValue.get(field);

                QuickFillingFailureDetail failureDetail = new QuickFillingFailureDetail(field, value);
                Object transformedValue = transformObjectValue(header, value, failureDetail);
                if (failureDetail.isFailed()) {
                    quickFillingFailure.getDetailList().add(failureDetail);
                } else {
                    FieldUtils.setFieldValue(finalModelObject, field, transformedValue);
                }
            }

            if (CollectionUtils.isNotEmpty(quickFillingFailure.getDetailList())) {
                quickFilling.getFailures().add(quickFillingFailure);
            }

            modelObject = Fun.run(model, FunctionConstants.construct, modelObject);
            if (modelObject != null) {
                modelObject = PamirsDataUtils.modelObjectToJsonObject(model, modelObject);
            }
            quickFilling.getResultValues().add(modelObject);
        }

        quickFilling.setValuesStr(JsonUtils.toJSONString(quickFilling.getResultValues()));

        return quickFilling;
    }

    private void loadModelConfig(QuickFilling quickFilling) {
        String model = quickFilling.getModel();
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (modelConfig == null) {
            throw PamirsException.construct(QuickFillingExpEnumerate.MODEL_NOT_FIND).appendMsg("模型" + model + "找不到").errThrow();
        }
        quickFilling.setModelConfig(modelConfig);
        Map<String, QuickFillingField> fields = new HashMap<>();
        for (QuickFillingField fieldHeader : quickFilling.getFieldHeaders()) {
            String field = fieldHeader.getField();
            ModelFieldConfig modelFieldConfig = modelConfig.getModelFieldConfigList().stream().filter(fieldConfig -> StringUtils.equals(fieldConfig.getField(), field)).findFirst().orElse(null);
            if (modelFieldConfig == null) {
                throw PamirsException.construct(QuickFillingExpEnumerate.FIELD_NOT_FIND).appendMsg(field + "字段在模型" + model + "内找不到").errThrow();
            }
            fieldHeader.setModelConfigField(modelFieldConfig);
            fields.put(field, fieldHeader);
        }
        quickFilling.setFields(fields);
    }

    private void loadParamValues(QuickFilling quickFilling) {
        String valuesStr = quickFilling.getValuesStr();
        List<Map<String, String>> values = JsonUtils.parseObject(valuesStr, PARAM_VALUE_TYPE_REFERENCE);
        values.removeIf(value -> {
            if (MapUtils.isEmpty(value)) {
                return true;
            }
            boolean isEmpty = true;
            for (Map.Entry<String, String> entry : value.entrySet()) {
                if (StringUtils.isNotBlank(entry.getValue())) {
                    isEmpty = false;
                    break;
                }
            }
            return isEmpty;
        });
        quickFilling.setValues(values);
    }

    private Object transformObjectValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelConfigField = quickFillingField.getModelConfigField();
        String ttypeValue = modelConfigField.getTtype();
        if (TtypeEnum.RELATED.value().equals(ttypeValue)) {
            ttypeValue = modelConfigField.getRelatedTtype();
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
                return transformMapValue(quickFillingField, value, failureDetail);
        }
        if (converter != null) {
            try {
                return converter.transformObjectValue(quickFillingField, value, failureDetail);
            } catch (Throwable e) {
                log.error("quick filling {} value error.", ttypeValue, e);
                failureDetail.fail();
                return null;
            }
        }
        failureDetail.fail(ttypeValue + "类型不支持自动填报");
        return null;
    }

    private Object transformMapValue(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        try {
            if (JsonUtils.isJSONArray(value)) {
                return JsonUtils.parseObjectList(value);
            } else if (JsonUtils.isJSONObject(value)) {
                return JsonUtils.parseObject(value);
            }
            failureDetail.fail();
            return null;
        } catch (Throwable e) {
            log.error("quick filling map value convert error.", e);
            failureDetail.fail();
            return null;
        }
    }
}
