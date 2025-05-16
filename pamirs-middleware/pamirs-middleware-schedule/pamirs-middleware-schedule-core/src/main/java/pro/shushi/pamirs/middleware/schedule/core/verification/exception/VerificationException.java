package pro.shushi.pamirs.middleware.schedule.core.verification.exception;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 19:40
 */
public class VerificationException extends RuntimeException {

    private final String key;

    private final Object value;

    public VerificationException(String message, String key, Object value) {
        super(message);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}