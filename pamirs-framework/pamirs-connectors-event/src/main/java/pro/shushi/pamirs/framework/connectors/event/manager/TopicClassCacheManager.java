package pro.shushi.pamirs.framework.connectors.event.manager;

import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.util.EventTypeUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TopicClassCacheManager
 *
 * @author yakir on 2023/12/21 14:59.
 */
public class TopicClassCacheManager {

    private static final Map<String, Type> typeMap = new ConcurrentHashMap<>();

    public static void registerType(String topic, NotifyConsumer<? extends Serializable> notifyConsumer) {
        typeMap.computeIfAbsent(topic,
                _topic -> EventTypeUtil.getMessageType(notifyConsumer));
    }

    public static void registerType(String topic, Type type) {
        typeMap.put(topic, type);
    }

    public static Type get(String topic) {
        return typeMap.getOrDefault(topic, HashMap.class);
    }
}
