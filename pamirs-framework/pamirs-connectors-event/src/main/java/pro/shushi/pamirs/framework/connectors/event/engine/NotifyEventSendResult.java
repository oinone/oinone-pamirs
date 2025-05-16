package pro.shushi.pamirs.framework.connectors.event.engine;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @deprecated 即将移除.
 */
@Deprecated
public class NotifyEventSendResult {

    private final boolean success;
    private final NotifyEvent notifyEvent;
    private final Object notifyResult;
    private final Throwable throwable;

    private NotifyEventSendResult(boolean success, NotifyEvent notifyEvent, @Nullable Object notifyResult, @Nullable Throwable throwable) {
        this.success = success;
        this.notifyResult = notifyResult;
        this.throwable = throwable;
        this.notifyEvent = notifyEvent;
    }

    public static NotifyEventSendResult ok(@NonNull NotifyEvent notifyEvent) {
        return new NotifyEventSendResult(true, notifyEvent, null, null);
    }

    public static NotifyEventSendResult ok(@NonNull NotifyEvent notifyEvent, @Nullable Object message) {
        return new NotifyEventSendResult(true, notifyEvent, message, null);
    }

    public static NotifyEventSendResult error(@NonNull NotifyEvent notifyEvent) {
        return new NotifyEventSendResult(false, notifyEvent, null, null);
    }

    public static NotifyEventSendResult error(@NonNull NotifyEvent notifyEvent, @Nullable Throwable throwable) {
        return new NotifyEventSendResult(false, notifyEvent, null, throwable);
    }

    public static NotifyEventSendResult error(@NonNull NotifyEvent notifyEvent, @Nullable Object message, @Nullable Throwable throwable) {
        return new NotifyEventSendResult(false, notifyEvent, message, throwable);
    }

    public boolean isSuccess() {
        return success;
    }

    public NotifyEvent getNotifyEvent() {
        return notifyEvent;
    }

    public Object getNotifyResult() {
        return notifyResult;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
