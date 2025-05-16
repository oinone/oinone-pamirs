package pro.shushi.pamirs.framework.connectors.event.engine;

import pro.shushi.pamirs.framework.connectors.event.api.NotifyExecuteLocalTransactionCallback;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyQueueSelector;
import pro.shushi.pamirs.framework.connectors.event.api.NotifySendCallback;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTagsGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated 即将移除.
 */
@Deprecated
public class NotifyEvent {

    private String key;
    private String group;
    private String topic;
    private String tags;
    private Object body;
    private Object extend;

    private boolean isTransaction;
    private String instanceId;

    private Map<String, String> properties;

    private Long timeout;
    private NotifySendCallback sendCallback;
    private NotifyQueueSelector queueSelector;
    private NotifyTagsGenerator tagsGenerator;
    private NotifyExecuteLocalTransactionCallback executeLocalTransactionCallback;

    public NotifyEvent(String topic, String tags, Object body) {
        this.topic = topic;
        this.tags = tags;
        this.body = body;
        this.properties = new HashMap<>();
        this.isTransaction = false;
    }

    public String getKey() {
        return key;
    }

    public NotifyEvent setKey(String key) {
        this.key = key;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public NotifyEvent setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public NotifyEvent setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public NotifyEvent setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public Object getBody() {
        return body;
    }

    public NotifyEvent setBody(Object body) {
        this.body = body;
        return this;
    }

    public Object getExtend() {
        return extend;
    }

    public NotifyEvent setExtend(Object extend) {
        this.extend = extend;
        return this;
    }

    public boolean getIsTransaction() {
        return isTransaction;
    }

    public NotifyEvent setIsTransaction(boolean isTransaction) {
        this.isTransaction = isTransaction;
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public NotifyEvent setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public NotifyEvent setProperties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    public NotifyEvent putProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public Long getTimeout() {
        return timeout;
    }

    public NotifyEvent setTimeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public NotifySendCallback getSendCallback() {
        return sendCallback;
    }

    public NotifyEvent setSendCallback(NotifySendCallback sendCallback) {
        this.sendCallback = sendCallback;
        return this;
    }

    public NotifyQueueSelector getQueueSelector() {
        return queueSelector;
    }

    public NotifyEvent setQueueSelector(NotifyQueueSelector queueSelector) {
        this.queueSelector = queueSelector;
        return this;
    }

    public NotifyTagsGenerator getTagsGenerator() {
        return tagsGenerator;
    }

    public NotifyEvent setTagsGenerator(NotifyTagsGenerator tagsGenerator) {
        this.tagsGenerator = tagsGenerator;
        return this;
    }

    public NotifyExecuteLocalTransactionCallback getExecuteLocalTransactionCallback() {
        return executeLocalTransactionCallback;
    }

    public NotifyEvent setExecuteLocalTransactionCallback(NotifyExecuteLocalTransactionCallback executeLocalTransactionCallback) {
        this.executeLocalTransactionCallback = executeLocalTransactionCallback;
        return this;
    }
}
