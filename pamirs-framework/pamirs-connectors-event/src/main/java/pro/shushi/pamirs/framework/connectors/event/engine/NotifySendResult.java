package pro.shushi.pamirs.framework.connectors.event.engine;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * NotifySendResult
 *
 * @author yakir on 2023/12/11 17:57.
 */
public class NotifySendResult implements Serializable {

    private static final long serialVersionUID = -5494552101706700287L;

    private final boolean success;
    private final Object notifyResult;
    private final Throwable throwable;

    private NotifySendResult(boolean success, @Nullable Object notifyResult, @Nullable Throwable throwable) {
        this.success = success;
        this.notifyResult = notifyResult;
        this.throwable = throwable;
    }

    public static NotifySendResult ok(@Nullable Object notifyResult) {
        return new NotifySendResult(true, notifyResult, null);
    }

    public static NotifySendResult error(@Nullable Throwable throwable) {
        return new NotifySendResult(false, null, throwable);
    }

    public static NotifySendResult error(@Nullable Object notifyResult) {
        return new NotifySendResult(false, notifyResult, null);
    }

    public static NotifySendResult error(@NonNull Object message, @Nullable Throwable throwable) {
        return new NotifySendResult(false, message, throwable);
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getNotifyResult() {
        return notifyResult;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "NotifySendResult{" +
                "success=" + success +
                ", notifyResult=" + notifyResult +
                ", throwable=" + throwable +
                '}';
    }
}
