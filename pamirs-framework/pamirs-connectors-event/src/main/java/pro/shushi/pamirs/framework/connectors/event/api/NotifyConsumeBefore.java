package pro.shushi.pamirs.framework.connectors.event.api;

import org.springframework.messaging.Message;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.util.EventUtil;

@FunctionalInterface
public interface NotifyConsumeBefore {

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    void consumeBefore(NotifyEvent notifyEvent);

    default Message consumeBefore(Message message) {
        NotifyEvent notifyEvent = EventUtil.toNotifyEvent(message);
        consumeBefore(notifyEvent);
        return EventUtil.toMessage(notifyEvent);
    }
}
