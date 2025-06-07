package pro.shushi.pamirs.framework.connectors.event.manager;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
import pro.shushi.pamirs.framework.connectors.event.enumeration.ConsumerType;

import java.util.Map;

/**
 * NotifyListenerWrapper
 *
 * @author yakir on 2023/12/12 18:17.
 */
public class NotifyListenerWrapper {

    private final Map<String, Object> attributes;
    private final NotifyListener notifyListener;

    private String group;

    private final boolean isFromAttr;

    public static NotifyListenerWrapper fromAttr(Map<String, Object> attr) {
        return new NotifyListenerWrapper(attr);
    }

    public static NotifyListenerWrapper fromAnno(NotifyListener anno) {
        return new NotifyListenerWrapper(anno);
    }

    private NotifyListenerWrapper(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.notifyListener = null;
        this.isFromAttr = true;
    }

    private NotifyListenerWrapper(NotifyListener notifyListener) {
        this.notifyListener = notifyListener;
        this.attributes = null;
        this.isFromAttr = false;
    }

    public String topic() {
        if (isFromAttr) {
            return (String) attributes.get("topic");
        } else {
            return notifyListener.topic();
        }
    }

    public String selectorExpression() {
        if (isFromAttr) {
            return (String) attributes.get("tags");
        } else {
            return notifyListener.tags();
        }
    }

    public String group() {
        if (StringUtils.isNotBlank(group)) {
            return group;
        }
        if (isFromAttr) {
            return (String) attributes.get("group");
        } else {
            return notifyListener.group();
        }
    }

    public NotifyListenerWrapper group(String group) {
        this.group = group;
        return this;
    }

    public boolean orderly() {
        if (isFromAttr) {
            return ConsumerType.ORDERLY.equals(attributes.get("consumerType"));
        } else {
            return ConsumerType.ORDERLY.equals(notifyListener.consumerType());
        }
    }

    public boolean transactional() {
        if (isFromAttr) {
            return (boolean) attributes.get("transactional");
        } else {
            return notifyListener.transactional();
        }
    }

    public String selectorType() {
        if (isFromAttr) {
            return (String) attributes.get("selectorType");
        } else {
            return notifyListener.selectorType();
        }
    }

    public String tlsEnable() {
        if (isFromAttr) {
            return (String) attributes.get("tlsEnable");
        } else {
            return notifyListener.tlsEnable();
        }
    }

    public String namespace() {
        if (isFromAttr) {
            return (String) attributes.get("namespace");
        } else {
            return notifyListener.namespace();
        }
    }

    public String instanceName() {
        if (isFromAttr) {
            return (String) attributes.get("instanceName");
        } else {
            return notifyListener.instanceName();
        }
    }

    public int suspendCurrentQueueTimeMillis() {
        if (isFromAttr) {
            return (int) attributes.get("suspendCurrentQueueTimeMillis");
        } else {
            return notifyListener.suspendCurrentQueueTimeMillis();
        }
    }

    public int delayLevelWhenNextConsume() {
        if (isFromAttr) {
            return (int) attributes.get("delayLevelWhenNextConsume");
        } else {
            return notifyListener.delayLevelWhenNextConsume();
        }
    }

    public Class bodyClass() {
        if (isFromAttr) {
            return (Class) attributes.get("bodyClass");
        } else {
            return notifyListener.bodyClass();
        }
    }

    public boolean enableMsgTrace() {
        if (isFromAttr) {
            return (boolean) attributes.get("enableMsgTrace");
        } else {
            return notifyListener.enableMsgTrace();
        }
    }

    public String customizedTraceTopic() {
        if (isFromAttr) {
            return (String) attributes.get("customizedTraceTopic");
        } else {
            return notifyListener.customizedTraceTopic();
        }
    }
}
