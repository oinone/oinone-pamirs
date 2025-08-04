package pro.shushi.pamirs.framework.connectors.event.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumeAfter;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumeBefore;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.kafka.marshalling.PamirsKafkaMarshalling;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.manager.NotifyListenerWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * KafkaNotifyListener
 *
 * @author yakir on 2023/12/13 10:18.
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class KafkaNotifyListener<T extends Serializable> extends AbstractNotifyListener<ConcurrentMessageListenerContainer<String, T>> {

    private final KafkaProperties properties;

    private final CommonErrorHandler errorHandler;

    public KafkaNotifyListener(NotifyListenerWrapper notifyListener, NotifyConsumer<? extends Serializable> notifyConsumer,
                               KafkaProperties properties) {
        super(notifyListener, notifyConsumer);
        this.properties = properties;
        KafkaTemplate kafkaTemplate = BeanDefinitionUtils.getBean(KafkaTemplate.class);
        BackOff backOff = new ExponentialBackOff();
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        this.errorHandler = new DefaultErrorHandler(recoverer, backOff);
    }

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    public KafkaNotifyListener(NotifyListenerWrapper notifyListener, NotifyEventListener eventListener,
                               KafkaProperties properties) {
        super(notifyListener, null);
        this.properties = properties;
        KafkaTemplate kafkaTemplate = BeanDefinitionUtils.getBean(KafkaTemplate.class);
        BackOff backOff = new ExponentialBackOff();
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        this.errorHandler = new DefaultErrorHandler(recoverer, backOff);
    }

    @Override
    public void start() {
        initKafkaConsumer();
        this.consumer.start();
    }

    @Override
    public void destroy() {
        consumer.stop(true);
    }

    public void initKafkaConsumer() {
        ConsumerFactory<?, ?> factory = initKafkaConsumerFactory();
        log.info("注册Kafka消费者:[{}]", this.topic);
        ContainerProperties containerProperties = new ContainerProperties(this.topic);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL);
        containerProperties.setMessageListener(new MessageListener<Object, Object>() {
            @Override
            public void onMessage(ConsumerRecord<Object, Object> data) {
                try {
                    Map<String, Object> headerMap = new HashMap<>();
                    Headers headers = data.headers();
                    if (null != headers) {
                        for (Header header : headers) {
                            headerMap.put(header.key(), new String(header.value(), StandardCharsets.UTF_8));
                        }
                    }
                    headerMap.put("msgId", String.valueOf(data.timestamp()));
                    Message msg = MessageBuilder.createMessage(data.value(), new MessageHeaders(headerMap));
                    if (null != notifyConsumer) {
                        for (NotifyConsumeBefore consumeBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeBefore.class)) {
                            msg = consumeBefore.consumeBefore(msg);
                        }
                        notifyConsumer.consume(msg);
                        for (NotifyConsumeAfter consumeAfter : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeAfter.class)) {
                            consumeAfter.consumeAfter(msg);
                        }
                    }

                    if (null != eventListener) {
                        NotifyEvent notifyEvent = new NotifyEvent(data.topic(), null, data.value());
                        Map<String, String> props = new HashMap<>();
                        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                            props.put(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                        notifyEvent.setProperties(props);
                        notifyEvent.setExtend(null);
                        for (NotifyConsumeBefore consumeBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeBefore.class)) {
                            consumeBefore.consumeBefore(notifyEvent);
                        }
                        eventListener.consumer(notifyEvent);
                        for (NotifyConsumeAfter consumeAfter : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeAfter.class)) {
                            consumeAfter.consumeAfter(notifyEvent);
                        }
                    }
                } finally {
                    PamirsSession.clear();
                }
            }
        });
        this.consumer = new ConcurrentMessageListenerContainer(factory, containerProperties);
        this.consumer.setCommonErrorHandler(errorHandler);
    }

    private ConsumerFactory<?, ?> initKafkaConsumerFactory() {
        Map<String, Object> consumerProps = this.properties.buildConsumerProperties();
        if (null != properties.getConsumer().getKeyDeserializer()) {
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, properties.getConsumer().getKeyDeserializer());
        } else {
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, PamirsKafkaMarshalling.class);
        }
        if (null != properties.getConsumer().getValueDeserializer()) {
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, properties.getConsumer().getValueDeserializer());
        } else {
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PamirsKafkaMarshalling.class);
        }
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }
}
