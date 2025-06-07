package pro.shushi.pamirs.framework.connectors.event.api;

import org.springframework.messaging.Message;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.util.EventUtil;

@FunctionalInterface
public interface NotifyConsumeAfter {

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    void consumeAfter(NotifyEvent notifyEvent);

    default void consumeAfter(Message message) {
        NotifyEvent notifyEvent = EventUtil.toNotifyEvent(message);
        consumeAfter(notifyEvent);
        message = EventUtil.toMessage(notifyEvent);
    }
}
