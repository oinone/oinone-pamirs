package pro.shushi.pamirs.filling.converter;

import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.filling.enumeration.QuickFillingExpEnumerate;
import pro.shushi.pamirs.filling.model.QuickFillingFailureDetail;
import pro.shushi.pamirs.filling.model.QuickFillingField;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Adamancy Zhang at 14:08 on 2025-11-11
 */
public class QuickFillingContext {

    private final ModelConfig modelConfig;

    private final ModelFieldConfig modelFieldConfig;

    private final String ltype;

    private final String field;

    private final boolean required;

    private final List<String> labelFields;

    private Object target;

    private final Set<String> failureFields;

    private final List<QuickFillingFailureDetail> failures;

    public QuickFillingContext(ModelConfig modelConfig, ModelFieldConfig modelFieldConfig, QuickFillingField field) {
        this.modelConfig = modelConfig;
        this.modelFieldConfig = modelFieldConfig;
        if (Boolean.TRUE.equals(modelFieldConfig.getMulti())) {
            this.ltype = modelFieldConfig.getLtype();
        } else {
            this.ltype = modelFieldConfig.getLtypeT();
        }
        this.field = field.getField();
        this.required = Boolean.TRUE.equals(field.getRequired());
        this.labelFields = field.getLabelFields();
        this.failureFields = new HashSet<>();
        this.failures = new ArrayList<>();
    }

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    public ModelFieldConfig getModelFieldConfig() {
        return modelFieldConfig;
    }

    public String getLtype() {
        return ltype;
    }

    public String getField() {
        return field;
    }

    public boolean isRequired() {
        return required;
    }

    public List<String> getLabelFields() {
        return labelFields;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<QuickFillingFailureDetail> getFailures() {
        return failures;
    }

    public void clear() {
        target = null;
        failureFields.clear();
        failures.clear();
    }

    public boolean isFailed() {
        return !failures.isEmpty();
    }

    public void fail() {
        fail(QuickFillingExpEnumerate.CONVERT_ERROR.msg());
    }

    public void fail(String msg) {
        fail(field, msg);
    }

    public void fail(String field, String msg) {
        if (ObjectHelper.isRepeat(failureFields, field)) {
            for (QuickFillingFailureDetail failure : failures) {
                if (field.equals(failure.getField())) {
                    failure.setMsg(msg);
                    break;
                }
            }
            return;
        }
        QuickFillingFailureDetail detail = new QuickFillingFailureDetail();
        detail.setField(field);
        detail.setMsg(msg);
        failures.add(detail);
    }
}
