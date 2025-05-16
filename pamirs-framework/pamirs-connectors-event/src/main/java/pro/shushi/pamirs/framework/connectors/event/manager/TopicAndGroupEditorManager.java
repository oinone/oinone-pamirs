package pro.shushi.pamirs.framework.connectors.event.manager;

import pro.shushi.pamirs.framework.connectors.event.spi.ConsumerGroupEditorApi;
import pro.shushi.pamirs.framework.connectors.event.spi.NotifyTopicEditorApi;
import pro.shushi.pamirs.framework.connectors.event.spi.SystemConsumerGroupEditorApi;
import pro.shushi.pamirs.framework.connectors.event.spi.SystemTopicEditorApi;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * TopicAndGroupEditorManager
 *
 * @author yakir on 2023/12/09 15:24.
 */
public class TopicAndGroupEditorManager {

    public static String editTopic(String topic) {
        topic = BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyTopicEditorApi.class).get(0).handlerTopic(topic);
        topic = BeanDefinitionUtils.getBeansOfTypeByOrdered(SystemTopicEditorApi.class).get(0).handlerTopic(topic);
        return topic;
    }

    public static String editConsumerGroup(String group) {
        group = BeanDefinitionUtils.getBeansOfTypeByOrdered(ConsumerGroupEditorApi.class).get(0).handlerConsumerGroup(group);
        group = BeanDefinitionUtils.getBeansOfTypeByOrdered(SystemConsumerGroupEditorApi.class).get(0).handlerConsumerGroup(group);
        return group;
    }
}
