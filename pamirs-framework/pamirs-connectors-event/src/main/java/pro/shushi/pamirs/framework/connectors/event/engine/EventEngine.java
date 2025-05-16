package pro.shushi.pamirs.framework.connectors.event.engine;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyProducer;
import pro.shushi.pamirs.meta.common.lambda.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_BIZ_KEY;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_LOGGER_KEY;
import static pro.shushi.pamirs.framework.connectors.event.constant.EventConstants.EVENT_SYS_SYSTEM_KEY;

public class EventEngine {

    private static final AtomicBoolean isOk = new AtomicBoolean(false);

    /* key pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyTyp 支持业务扩展，自己注册 */
    private static final Map<String, NotifyProducer<?>> producerMap = new ConcurrentHashMap<>();

    public static boolean ok() {
        return isOk.compareAndSet(false, true);
    }

    public static boolean isOk() {
        return isOk.get();
    }

    public static NotifyProducer<?> systemNotifyProducer() {
        return producerMap.get(EVENT_SYS_SYSTEM_KEY);
    }

    public static NotifyProducer<?> bizNotifyProducer() {
        return producerMap.get(EVENT_SYS_BIZ_KEY);
    }

    public static NotifyProducer<?> loggerNotifyProducer() {
        return producerMap.get(EVENT_SYS_LOGGER_KEY);

    }

    public static NotifyProducer<?> get(String bizType) {
        if (StringUtils.isBlank(bizType)) {
            return null;
        }
        return producerMap.get(bizType);
    }

    public static NotifyProducer<?> register(String bizType, NotifyProducer<?> notifyProducer) {
        if (StringUtils.isBlank(bizType)) {
            return null;
        }
        return producerMap.put(bizType, notifyProducer);
    }

    public static <T> NotifySendResult send(String topic, String tag, T msg) {
        return producerMap.get(EVENT_SYS_BIZ_KEY).send(topic, tag, msg);
    }

    public static <T> NotifySendResult send(String topic, String tag, T msg, Map<String, Object> headers) {
        return producerMap.get(EVENT_SYS_BIZ_KEY).send(topic, tag, msg, headers);
    }

    public static <T, R extends Serializable> NotifySendResult sendOrderly(String topic, String tag, T msg, Getter<T, R> orderlyFieldgetter) {
        return producerMap.get(EVENT_SYS_BIZ_KEY).sendOrderly(topic, tag, msg, orderlyFieldgetter);
    }

    public static <T> NotifySendResult sendOrderly(String topic, String tag, T msg, String hashKey) {
        return producerMap.get(EVENT_SYS_BIZ_KEY).sendOrderly(topic, tag, msg, hashKey);
    }

    public static void registerProducer(String notifyType, NotifyProducer<?> producer) {
        producerMap.put(notifyType, producer);
    }
}
