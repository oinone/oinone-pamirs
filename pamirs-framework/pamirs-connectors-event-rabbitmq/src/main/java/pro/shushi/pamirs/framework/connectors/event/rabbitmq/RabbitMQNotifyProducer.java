package pro.shushi.pamirs.framework.connectors.event.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.messaging.Message;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyAbstractProducer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifySendAfter;
import pro.shushi.pamirs.framework.connectors.event.api.NotifySendBefore;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;
import pro.shushi.pamirs.framework.connectors.event.manager.TopicAndGroupEditorManager;
import pro.shushi.pamirs.framework.connectors.event.util.EventUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * RabbitMQNotifyProducer
 *
 * @author yakir on 2023/12/13 10:28.
 */
@Slf4j
public class RabbitMQNotifyProducer extends NotifyAbstractProducer<RabbitMessagingTemplate> {

    public RabbitMQNotifyProducer(RabbitMessagingTemplate defaultTemplate) {
        super(defaultTemplate);
    }

    @Override
    public <T> NotifySendResult send(String topic, String tag, T msg) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        Message<T> message = EventUtil.message(_topic, tag, null, msg, null);
        return handle(message, _message -> {
            getTemplate(topic).send(_topic, _topic, _message);
            return _message;
        });
    }

    @Override
    public <T> NotifySendResult send(String topic, String tag, T msg, Map<String, Object> headers) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        Message<T> message = EventUtil.message(_topic, tag, null, msg, headers);
        return handle(message, _message -> {
            getTemplate(topic).send(_topic, _topic, _message);
            return _message;
        });
    }

    @Override
    public <T> NotifySendResult sendTx(String topic, String tag, String txGroup, T msg, Object extArg) {
        throw new UnsupportedOperationException("未支持");
    }

    @Override
    public <T, R extends Serializable> NotifySendResult sendOrderly(String topic, String tag, T msg, Getter<T, R> orderlyFieldgetter) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        String hashKey = Optional.ofNullable(orderlyFieldgetter.apply(msg))
                .map(String::valueOf)
                .orElse("0");
        Message<T> message = EventUtil.message(_topic, tag, hashKey, msg, null);
        return handle(message, _message -> {
            getTemplate(topic).send(_topic, _topic, _message);
            return _message;
        });
    }

    @Override
    public <T> NotifySendResult sendOrderly(String topic, String tag, T msg, String hashKey) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        Message<T> message = EventUtil.message(_topic, tag, hashKey, msg, null);
        return handle(message, _message -> {
            getTemplate(topic).send(_topic, _topic, _message);
            return _message;
        });
    }

    private <T> NotifySendResult handle(Message<T> message, Function<Message<T>, Message<T>> func) {
        NotifySendResult result = null;
        try {
            for (NotifySendBefore sendBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifySendBefore.class)) {
                message = sendBefore.sendBefore(message);
            }
            Object object = func.apply(message);
            if (null == object) {
                result = NotifySendResult.error(false);
            } else {
                result = NotifySendResult.ok(object);
            }
        } catch (Throwable throwable) {
            result = NotifySendResult.error(throwable);
        }

        for (NotifySendAfter sendAfter : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifySendAfter.class)) {
            sendAfter.sendAfter(result);
        }

        return result;
    }

    @Override
    public void destroy() {

    }
}
