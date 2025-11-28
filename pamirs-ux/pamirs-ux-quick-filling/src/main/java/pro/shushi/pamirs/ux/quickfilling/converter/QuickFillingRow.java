package pro.shushi.pamirs.ux.quickfilling.converter;

import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;
import pro.shushi.pamirs.ux.quickfilling.model.QuickFillingFailureDetail;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 快速填报行
 *
 * @author Adamancy Zhang at 13:49 on 2025-11-27
 */
public class QuickFillingRow {

    private final String model;

    private Object data;

    private final int rowIndex;

    private final Set<String> validatedFields;

    private final List<QuickFillingValidateMessage> validateMessages;

    public QuickFillingRow(String model, int rowIndex) {
        this.model = model;
        this.rowIndex = rowIndex;
        this.validatedFields = new HashSet<>();
        this.validateMessages = new ArrayList<>();
    }

    public String getModel() {
        return model;
    }

    public Object getData() {
        return data;
    }

    public boolean isNotEmpty() {
        return data != null;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public boolean isFailure(String field) {
        return validatedFields.contains(field);
    }

    public List<QuickFillingFailureDetail> getFailures() {
        if (data == null) {
            return validateMessages.stream().filter(QuickFillingValidateMessage::isValid).map(QuickFillingValidateMessage::of).collect(Collectors.toList());
        }
        return validateMessages.stream().map(QuickFillingValidateMessage::of).collect(Collectors.toList());
    }

    public void setValue(String field, Object value) {
        if (this.data == null) {
            Object data = PamirsDataUtils.jsonObjectToModelObject(model, new HashMap<>());
            data = Fun.run(model, FunctionConstants.construct, data);
            this.data = data;
        }
        FieldUtils.setFieldValue(data, field, value);
    }

    public void validateRequired(String field) {
        validateRequired(field, QuickFillingExpEnumerate.FIELD_VALIDATE_REQUIRED_ERROR.msg());
    }

    public void validateRequired(String field, String message) {
        if (isRepeat(field)) {
            return;
        }
        validateMessages.add(new QuickFillingValidateMessage(field, message, false));
    }

    public void validateError(String field) {
        validateError(field, QuickFillingExpEnumerate.CONVERT_ERROR.msg());
    }

    public void validateError(String field, String message) {
        if (isRepeat(field)) {
            for (QuickFillingValidateMessage validateMessage : validateMessages) {
                if (field.equals(validateMessage.getField())) {
                    validateMessage.setMessage(message);
                    validateMessage.setValid(true);
                    break;
                }
            }
            return;
        }
        validateMessages.add(new QuickFillingValidateMessage(field, message, true));
    }

    private boolean isRepeat(String value) {
        int lastSize = validatedFields.size();
        validatedFields.add(value);
        return lastSize == validatedFields.size();
    }
}
