package pro.shushi.pamirs.framework.connectors.event.api;

import org.springframework.messaging.Message;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.util.EventUtil;

@FunctionalInterface
public interface NotifySendBefore {

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    void sendBefore(NotifyEvent notifyEvent);

    default Message sendBefore(Message message) {
        NotifyEvent notifyEvent = EventUtil.toNotifyEvent(message);
        sendBefore(notifyEvent);
        return EventUtil.toMessage(notifyEvent);
    }
}
