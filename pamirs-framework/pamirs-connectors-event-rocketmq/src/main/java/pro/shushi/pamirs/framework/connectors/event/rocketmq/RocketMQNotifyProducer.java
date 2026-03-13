package pro.shushi.pamirs.framework.connectors.event.rocketmq;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
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

import static org.apache.rocketmq.client.producer.SendStatus.SEND_OK;

/**
 * RocketMQNotifyProducer
 *
 * @author yakir on 2023/12/11 16:02.
 */
@Slf4j
public class RocketMQNotifyProducer extends NotifyAbstractProducer<RocketMQTemplate> {

    public RocketMQNotifyProducer(RocketMQTemplate rocketMQTemplate) {
        super(rocketMQTemplate);
    }

    @Override
    public <T> NotifySendResult send(String topic, String tag, T msg) {
        String _topic = TopicAndGroupEditorManager.editTopic(topic);
        if (StringUtils.isBlank(tag)) {
            _topic = _topic + ":" + tag;
        }
        final String destination = _topic;
        Message<T> message = EventUtil.message(_topic, tag, null, msg, null);
        return handle(message, _message -> getTemplate(topic).syncSend(destination, _message));
    }

    @Override
    public <T> NotifySendResult send(String topic, String tag, T msg, Map<String, Object> headers) {
        String _topic = TopicAndGroupEditorManager.editTopic(topic);
        if (StringUtils.isBlank(tag)) {
            _topic = _topic + ":" + tag;
        }
        final String destination = _topic;
        Message<T> message = EventUtil.message(_topic, tag, null, msg, headers);
        return handle(message, _message -> getTemplate(topic).syncSend(destination, _message));
    }

    @Override
    public <T> NotifySendResult sendTx(String topic, String tag, String txGroup, T msg, Object extArg) {
        String _topic = TopicAndGroupEditorManager.editTopic(topic);
        if (StringUtils.isBlank(tag)) {
            _topic = _topic + ":" + tag;
        }
        final String destination = _topic;
        Message<T> message = EventUtil.message(_topic, tag, null, msg, null);
        return handle(message, _message -> getTemplate(txGroup).sendMessageInTransaction(destination, _message, extArg));
    }

    @Override
    public <T, R extends Serializable> NotifySendResult sendOrderly(String topic, String tag, T msg, Getter<T, R> orderlyFieldgetter) {
        String _topic = TopicAndGroupEditorManager.editTopic(topic);
        if (StringUtils.isBlank(tag)) {
            _topic = _topic + ":" + tag;
        }
        final String destination = _topic;
        Message<T> message = EventUtil.message(_topic, tag, null, msg, null);
        return handle(message, _message -> {
            String hashKey = Optional.ofNullable(orderlyFieldgetter.apply(msg))
                    .map(String::valueOf)
                    .orElse("0");
            return getTemplate(topic).syncSendOrderly(destination, _message, hashKey);
        });
    }

    @Override
    public <T> NotifySendResult sendOrderly(String topic, String tag, T msg, String hashKey) {
        String _topic = TopicAndGroupEditorManager.editTopic(topic);
        if (StringUtils.isBlank(tag)) {
            _topic = _topic + ":" + tag;
        }
        final String destination = _topic;
        Message<T> message = EventUtil.message(_topic, tag, null, msg, null);
        return handle(message, _message -> getTemplate(topic).syncSendOrderly(destination, _message, hashKey));
    }

    private <T> NotifySendResult handle(Message<T> message, Function<Message<T>, SendResult> func) {
        NotifySendResult result = null;
        try {
            for (NotifySendBefore sendBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifySendBefore.class)) {
                message = sendBefore.sendBefore(message);
            }
            SendResult sendResult = func.apply(message);
            SendStatus sendStatus = sendResult.getSendStatus();
            switch (sendStatus) {
                case SEND_OK:
                    result = NotifySendResult.ok(SEND_OK);
                    break;
                case FLUSH_DISK_TIMEOUT:
                case SLAVE_NOT_AVAILABLE:
                case FLUSH_SLAVE_TIMEOUT:
                    result = NotifySendResult.error(sendStatus);
                    break;
                default:
                    result = NotifySendResult.error("没有匹配的发送状态");
                    break;
            }
        } catch (Throwable throwable) {
            log.error("Send message exception", throwable);
            result = NotifySendResult.error(throwable);
        }

        for (NotifySendAfter sendAfter : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifySendAfter.class)) {
            sendAfter.sendAfter(result);
        }

        return result;
    }

    @Override
    public void destroy() {
        for (Map.Entry<String, RocketMQTemplate> entry : templatecMap.entrySet()) {
            entry.getValue().destroy();
        }
    }

}
