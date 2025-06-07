package pro.shushi.pamirs.middleware.canal.entity;

import java.io.Serializable;

/**
 * @author Adamancy Zhang
 * @date 2020-12-18 11:30
 */
public class SimpleReadonlyMessage implements Serializable {

    private static final long serialVersionUID = -5076316553102993633L;

    private final boolean success;

    private final String message;

    public SimpleReadonlyMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public String getMessage() {
        return message;
    }
}
