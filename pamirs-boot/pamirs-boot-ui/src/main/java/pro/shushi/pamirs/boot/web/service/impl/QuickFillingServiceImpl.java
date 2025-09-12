package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFilling;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailure;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingService;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gesi at 18:02 on 2025/9/10
 */
@Service
public class QuickFillingServiceImpl implements QuickFillingService {

    @Autowired
    private List<QuickFillingValueConverter> valueConverters;

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
            quickFilling.getFields().forEach((field, header) -> {
                String value = paramValue.get(field);
                QuickFillingField quickFillingField = quickFilling.getFields().get(field);
                QuickFillingFailureDetail failureDetail = new QuickFillingFailureDetail(field, value);

                Object transformedValue = transformObjectValue(quickFillingField, value, failureDetail);

                if (failureDetail.isFailed()) {
                    quickFillingFailure.getDetailList().add(failureDetail);
                } else {
                    FieldUtils.setFieldValue(finalModelObject, field, transformedValue);
                }
            });

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
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            throw new RuntimeException();
        }
        quickFilling.setModelConfig(modelConfig);
        Map<String, QuickFillingField> fields = new HashMap<>();
        for (QuickFillingField fieldHeader : quickFilling.getFieldHeaders()) {
            String field = fieldHeader.getField();
            ModelFieldConfig modelFieldConfig = modelConfig.getModelFieldConfigList().stream().filter(fieldConfig -> StringUtils.equals(fieldConfig.getField(), field)).findFirst().orElse(null);
            if (modelFieldConfig == null) {
                throw new RuntimeException();
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
                if (StringUtils.isNotEmpty(entry.getValue())) {
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
        TtypeEnum ttype = TtypeEnum.getEnumByValue(TtypeEnum.class, modelConfigField.getTtype());
        for (QuickFillingValueConverter valueConverter : valueConverters) {
            if (valueConverter.canTransform(ttype)) {
                return valueConverter.transformObjectValue(quickFillingField, value, failureDetail);
            }
        }
        failureDetail.fail(QuickFillingFailCodeEnum.UNSUPPORTED_TYPE, ttype + "类型不支持自动填报");
        return null;
    }

}
