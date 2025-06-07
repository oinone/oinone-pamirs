package pro.shushi.pamirs.framework.connectors.event.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumeAfter;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumeBefore;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.manager.NotifyListenerWrapper;
import pro.shushi.pamirs.framework.connectors.event.rabbitmq.marshalling.PamirsMessageJsonConverter;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQNotifyListener
 *
 * @author yakir on 2023/12/13 10:32.
 */
@Slf4j
public class RabbitMQNotifyListener extends AbstractNotifyListener<SimpleMessageListenerContainer> {

    private final RabbitProperties properties;

    private RabbitAdmin rabbitAdmin;

    public RabbitMQNotifyListener(NotifyListenerWrapper notifyListener, NotifyConsumer<? extends Serializable> notifyConsumer,
                                  RabbitProperties properties) {
        super(notifyListener, notifyConsumer);
        this.properties = properties;
    }

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    public RabbitMQNotifyListener(NotifyListenerWrapper notifyListener, NotifyEventListener eventListener,
                                  RabbitProperties properties) {
        super(notifyListener, null);
        this.properties = properties;
        this.eventListener = eventListener;
    }

    @Override
    public void start() {
        this.rabbitAdmin = BeanDefinitionUtils.getBean(RabbitAdmin.class);
        initExchange();
        initQueue();
        initRouting();
        initRabbitMQConsumer();
        this.consumer.start();
        log.info("注册RabbitMQ消息消费者成功 Exchange:[{}] Routing:[{}] Queue: [{}]", topic, topic, topic);
    }

    @Override
    public void destroy() {
        this.consumer.stop();
    }

    private void initExchange() {
        this.rabbitAdmin.declareExchange(new DirectExchange(this.topic, true, false, null));
    }

    private void initQueue() {
        this.rabbitAdmin.declareQueue(new Queue(this.topic, true, false, false, null));
    }

    private void initRouting() {
        this.rabbitAdmin.declareBinding(new Binding(this.topic, Binding.DestinationType.QUEUE, this.topic, this.topic, null));
    }

    private void initRabbitMQConsumer() {

        CachingConnectionFactory connectionFactory = BeanDefinitionUtils.getBean(CachingConnectionFactory.class);
        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        listenerContainer.setDefaultRequeueRejected(true);
        listenerContainer.setQueueNames(this.topic);
        listenerContainer.setAmqpAdmin(rabbitAdmin);
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new PamirsRabbitMessageBridge(notifyConsumer, eventListener));
        listenerAdapter.setMessageConverter(new PamirsMessageJsonConverter());
        listenerContainer.setMessageListener(listenerAdapter);
        this.consumer = listenerContainer;
    }

    public static class PamirsRabbitMessageBridge {

        private final NotifyConsumer<? extends Serializable> notifyConsumer;
        private final NotifyEventListener eventListener;

        public PamirsRabbitMessageBridge(NotifyConsumer<? extends Serializable> notifyConsumer, NotifyEventListener eventListener) {
            this.notifyConsumer = notifyConsumer;
            this.eventListener = eventListener;
        }

        @SuppressWarnings({"rawtypes"})
        public void handleMessage(org.springframework.messaging.Message message) {
            try {
                if (null != notifyConsumer) {
                    for (NotifyConsumeBefore consumeBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeBefore.class)) {
                        message = consumeBefore.consumeBefore(message);
                    }
                    notifyConsumer.consume(message);
                    for (NotifyConsumeAfter consumeAfter : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeAfter.class)) {
                        consumeAfter.consumeAfter(message);
                    }
                }
                if (null != eventListener) {
                    NotifyEvent notifyEvent = new NotifyEvent(message.getHeaders().get("consumerQueue", String.class), null, message.getPayload());
                    Map<String, String> props = new HashMap<>();
                    for (Map.Entry<String, Object> entry : message.getHeaders().entrySet()) {
                        props.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    notifyEvent.setProperties(props);
                    notifyEvent.setExtend(null);
                    for (NotifyConsumeBefore consumeBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeBefore.class)) {
                        consumeBefore.consumeBefore(message);
                    }
                    eventListener.consumer(notifyEvent);
                    for (NotifyConsumeAfter consumeAfter : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeAfter.class)) {
                        consumeAfter.consumeAfter(message);
                    }
                }
            } finally {
                PamirsSession.clear();
            }
        }
    }
}
