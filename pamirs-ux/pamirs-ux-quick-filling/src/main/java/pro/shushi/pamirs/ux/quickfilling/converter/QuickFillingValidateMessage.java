package pro.shushi.pamirs.ux.quickfilling.converter;

import pro.shushi.pamirs.ux.quickfilling.model.QuickFillingFailureDetail;

/**
 * 快速填报验证信息
 *
 * @author Adamancy Zhang at 15:26 on 2025-11-27
 */
public class QuickFillingValidateMessage {

    private final String field;

    private String message;

    private boolean isValid;

    public QuickFillingValidateMessage(String field, String message, boolean isValid) {
        this.field = field;
        this.message = message;
        this.isValid = isValid;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public QuickFillingFailureDetail of() {
        QuickFillingFailureDetail detail = new QuickFillingFailureDetail();
        detail.setField(field);
        detail.setMsg(message);
        return detail;
    }
}
