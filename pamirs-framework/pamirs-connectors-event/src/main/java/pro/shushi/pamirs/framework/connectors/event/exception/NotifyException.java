package pro.shushi.pamirs.framework.connectors.event.exception;

public class NotifyException extends RuntimeException {

    public NotifyException(String message) {
        super(message);
    }

    public NotifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotifyException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}