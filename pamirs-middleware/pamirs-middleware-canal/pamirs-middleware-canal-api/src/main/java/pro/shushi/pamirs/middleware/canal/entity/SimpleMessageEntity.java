package pro.shushi.pamirs.middleware.canal.entity;

/**
 * @author Adamancy Zhang
 * @date 2020-12-18 14:00
 */
public class SimpleMessageEntity {

    private boolean success;

    private String message;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
