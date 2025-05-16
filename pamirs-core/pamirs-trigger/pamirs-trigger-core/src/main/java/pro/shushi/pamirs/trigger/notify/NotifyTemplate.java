//package pro.shushi.pamirs.trigger.notify;
//
//import pro.shushi.pamirs.framework.connectors.event.api.NotifyEvent;
//import pro.shushi.pamirs.framework.connectors.event.api.NotifyProducer;
//import pro.shushi.pamirs.framework.connectors.event.api.NotifySendCallback;
//import pro.shushi.pamirs.framework.connectors.event.api.NotifySendResult;
//import pro.shushi.pamirs.framework.connectors.event.engine.EventEngine;
//import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyType;
//import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
//
//import javax.validation.constraints.NotBlank;
//import java.io.Serializable;
//
//public class NotifyTemplate {
//
//    private static final int ONEWAY_VALUE = 1;
//    private static final int HAS_SEND_CALLBACK_VALUE = 2;
//    private static final int IS_CUSTOM_TIMEOUT_VALUE = 4;
//
//    private NotifyProducer producer;
//
//    private NotifyEvent event;
//
//    private Integer isOneway;
//
//    private Integer hasSendCallback;
//
//    private Integer isCustomTimeout;
//
//    private NotifySendCallback sendCallback;
//
//    private long customTimeout;
//
//    private NotifyTemplate(NotifyProducer producer) {
//        this.producer = producer;
//        this.event = null;
//        this.isOneway = 0;
//        this.hasSendCallback = 0;
//        this.isCustomTimeout = 0;
//        this.sendCallback = null;
//        this.customTimeout = -1L;
//    }
//
//    public static NotifyTemplate getInstance(NotifyType notifyType) {
//        return new NotifyTemplate(EventEngine.getNotifyProducer(notifyType, PamirsTenantSession.getTenant()));
//    }
//
//    public <T extends Serializable> NotifyTemplate createNotifyEvent(@NotBlank String topic, @NotBlank String tags, T body) {
//        event = producer.createNotifyEvent(topic, tags, body);
//        return this;
//    }
//
//    public NotifyTemplate putProperty(String key, String value) {
//        event.putProperty(key, value);
//        return this;
//    }
//
//    public NotifyTemplate sendCallback(NotifySendCallback sendCallback) {
//        if (sendCallback != null) {
//            this.hasSendCallback = HAS_SEND_CALLBACK_VALUE;
//            this.sendCallback = sendCallback;
//        } else {
//            this.hasSendCallback = 0;
//            this.sendCallback = null;
//        }
//        return this;
//    }
//
//    public NotifyTemplate setIsOneway(Boolean isOneway) {
//        if (isOneway != null) {
//            if (isOneway)
//                this.isOneway = ONEWAY_VALUE;
//            else
//                this.isOneway = 0;
//        }
//        return this;
//    }
//
//    public NotifyTemplate setTimeout(long timeout) {
//        this.customTimeout = timeout;
//        if (timeout == -1L) {
//            this.isCustomTimeout = 0;
//        } else
//            this.isCustomTimeout = IS_CUSTOM_TIMEOUT_VALUE;
//        return this;
//    }
//
//    public NotifySendResult send() {
//        int selectValue = this.isOneway + this.hasSendCallback + this.isCustomTimeout;
//        switch (selectValue) {
//            case 1:
//                return producer.sendOneway(event);
//            case 2:
//            case 3:
//                return producer.send(event, sendCallback);
//            case 4:
//            case 5:
//                return producer.send(event, customTimeout);
//            case 6:
//            case 7:
//                return producer.send(event, sendCallback, customTimeout);
//            case 0:
//            default:
//                return producer.send(event);
//        }
//    }
//}
