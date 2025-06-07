package pro.shushi.pamirs.framework.connectors.event.util;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * EventUtil
 *
 * @author yakir on 2023/12/26 16:31.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EventUtil {

    public static <T> Message<T> message(String topic, String tag, String keys, T payload, Map<String, Object> headers) {
        if (null == headers) {
            headers = new HashMap<>();
        }
        headers.put("topic", topic);
        headers.put("tag", tag);
        headers.put("keys", keys);
        return MessageBuilder.createMessage(payload, new MessageHeaders(headers));
    }

    public static NotifyEvent toNotifyEvent(Message<? extends Serializable> message) {
        Serializable payload = message.getPayload();
        MessageHeaders msgHeaders = message.getHeaders();
        String topic = msgHeaders.get("topic", String.class);
        String tag = msgHeaders.get("tag", String.class);
        String keys = msgHeaders.get("keys", String.class);
        NotifyEvent event = new NotifyEvent(topic, tag, payload);
        event.setKey(keys);
        Map<String, String> props = new HashMap<>();
        for (Map.Entry<String, Object> entry : msgHeaders.entrySet()) {
            props.put(entry.getKey(), null == entry.getValue() ? null : String.valueOf(entry.getValue()));
        }
        event.setProperties(props);
        return event;
    }

    public static Message<?> toMessage(NotifyEvent notifyEvent) {
        Object payload = notifyEvent.getBody();
        Map props = notifyEvent.getProperties();
        return MessageBuilder.createMessage(payload, new MessageHeaders(props));
    }
}