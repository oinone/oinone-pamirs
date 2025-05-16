package pro.shushi.pamirs.framework.connectors.event.manager;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.io.Serializable;

/**
 * AbstractNotifyListener
 *
 * @author yakir on 2023/12/13 10:20.
 */
abstract
public class AbstractNotifyListener<CONSUMER> {

    protected final NotifyConsumer<? extends Serializable> notifyConsumer;
    protected final NotifyListenerWrapper                  notifyListener;

    protected String topic;

    protected CONSUMER consumer;

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    protected NotifyEventListener eventListener;

    protected AbstractNotifyListener(NotifyListenerWrapper notifyListener, NotifyConsumer<? extends Serializable> notifyConsumer) {
        this.notifyListener = notifyListener;
        this.notifyConsumer = notifyConsumer;
        this.topic = TopicAndGroupEditorManager.editTopic(notifyListener.topic());
        this.eventListener = null;
    }

    protected String group(String appCfgGroup) {

        if (StringUtils.isNotBlank(notifyListener.group())) {
            return notifyListener.group();
        }

        if (StringUtils.isNotBlank(appCfgGroup)) {
            return appCfgGroup;
        }

        return BeanDefinitionUtils.getEnvironment().getProperty("spring.application.name");
    }

    public String topic() {
        return this.topic;
    }

    public NotifyConsumer<?> getNotifyConsumer() {
        return notifyConsumer;
    }

    public NotifyListenerWrapper getNotifyListener() {
        return notifyListener;
    }

    public abstract void start();

    public abstract void destroy();

    // todo 获取Instance
}
