package pro.shushi.pamirs.framework.connectors.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * KafkaNotifyProducer
 *
 * @author yakir on 2023/12/13 10:13.
 */
@Slf4j
public class KafkaNotifyProducer extends NotifyAbstractProducer<KafkaTemplate<String, Object>> {

    public KafkaNotifyProducer(KafkaTemplate<String, Object> defaultTemplate) {
        super(defaultTemplate);
    }

    @Override
    public <T> NotifySendResult send(String topic, String tag, T msg) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, _topic);
        Message<T> message = EventUtil.message(_topic, tag, null, msg, headers);
        return handle(message, _message -> getTemplate(topic).send(_message));
    }

    @Override
    public <T> NotifySendResult sendTx(String topic, String tag, String txGroup, T msg, Object extArg) {
        throw new UnsupportedOperationException("未支持");
    }

    @Override
    public <T> NotifySendResult send(String topic, String tag, T msg, Map<String, Object> headers) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        headers.put(KafkaHeaders.TOPIC, _topic);
        Message<T> message = EventUtil.message(_topic, tag, null, msg, headers);
        return handle(message, _message -> getTemplate(topic).send(_message));
    }

    @Override
    public <T, R extends Serializable> NotifySendResult sendOrderly(String topic, String tag, T msg, Getter<T, R> orderlyFieldgetter) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, _topic);
        String hashKey = Optional.ofNullable(orderlyFieldgetter.apply(msg))
                .map(String::valueOf)
                .orElse("0");
        headers.put(KafkaHeaders.MESSAGE_KEY, hashKey);
        Message<T> message = EventUtil.message(_topic, tag, null, msg, headers);
        return handle(message, _message -> getTemplate(topic).send(_message));
    }

    @Override
    public <T> NotifySendResult sendOrderly(String topic, String tag, T msg, String hashKey) {
        final String _topic = TopicAndGroupEditorManager.editTopic(topic);
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, _topic);
        headers.put(KafkaHeaders.MESSAGE_KEY, hashKey);
        Message<T> message = EventUtil.message(_topic, tag, null, msg, headers);
        return handle(message, _message -> getTemplate(topic).send(_message));
    }

    private <T> NotifySendResult handle(Message<T> message, Function<Message<T>, ListenableFuture<SendResult<String, Object>>> func) {
        NotifySendResult result = null;
        try {
            for (NotifySendBefore sendBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifySendBefore.class)) {
                message = sendBefore.sendBefore(message);
            }
            SendResult<String, Object> sendJoin = func.apply(message)
                    .completable()
                    .get(1, TimeUnit.MINUTES);
            if (log.isDebugEnabled()) {
                log.debug("Kafka 发送详情:[{}]", sendJoin);
            }
            result = NotifySendResult.ok(sendJoin.getProducerRecord());
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

