package pro.shushi.pamirs.framework.connectors.event.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.constant.EventConstants;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyType;

import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * PamirsEventProperties
 *
 * @author yakir on 2023/12/07 16:34.
 */
@Configuration
@ConfigurationProperties(ConfigureConstants.PAMIRS_EVENT_CONFIG_PREFIX)
@Conditional(NotifySwitchCondition.class)
public class PamirsEventProperties implements Serializable {

    private static final long serialVersionUID = 1215656981708433572L;

    // 默认值
    public static final boolean DEFAULT_VALUE = true;

    private boolean enabled = DEFAULT_VALUE;

    private String topicPrefix;

    private Map<String, String> notifyMap;

    public boolean isEnabled() {
        return enabled;
    }

    public PamirsEventProperties setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public PamirsEventProperties setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
        return this;
    }

    public Map<String, String> getNotifyMap() {
        return notifyMap;
    }

    public PamirsEventProperties setNotifyMap(Map<String, String> notifyMap) {
        this.notifyMap = notifyMap;
        return this;
    }

    @PostConstruct
    public void init() {
        Map<String, String> notifyMap = this.getNotifyMap();
        if (notifyMap == null) {
            notifyMap = new HashMap<>();
            this.setNotifyMap(notifyMap);
        }
        String defaultNotifyType = getDefaultNotifyType(notifyMap);
        notifyMap.putIfAbsent(EventConstants.EVENT_SYS_SYSTEM, defaultNotifyType);
        notifyMap.putIfAbsent(EventConstants.EVENT_SYS_BIZ, defaultNotifyType);
        notifyMap.putIfAbsent(EventConstants.EVENT_SYS_LOGGER, defaultNotifyType);
    }

    private String getDefaultNotifyType(Map<String, String> notifyMap) {
        String notifyType = notifyMap.get(EventConstants.EVENT_SYS_SYSTEM);
        if (notifyType != null) {
            return notifyType;
        }
        notifyType = notifyMap.get(EventConstants.EVENT_SYS_BIZ);
        if (notifyType != null) {
            return notifyType;
        }
        notifyType = notifyMap.get(EventConstants.EVENT_SYS_LOGGER);
        if (notifyType != null) {
            return notifyType;
        }
        return NotifyType.ROCKETMQ.name();
    }
}
