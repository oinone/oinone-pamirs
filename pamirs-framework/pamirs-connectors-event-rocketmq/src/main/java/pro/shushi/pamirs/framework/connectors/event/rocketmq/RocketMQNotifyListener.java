package pro.shushi.pamirs.framework.connectors.event.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumeAfter;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumeBefore;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyEventListener;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.manager.AbstractNotifyListener;
import pro.shushi.pamirs.framework.connectors.event.manager.NotifyListenerWrapper;
import pro.shushi.pamirs.framework.connectors.event.manager.TopicClassCacheManager;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.config.RocketMQProperties;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.util.RocketMQUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * RocketMQNotifyListener
 *
 * @author yakir on 2023/12/11 16:02.
 */
@Slf4j
public class RocketMQNotifyListener extends AbstractNotifyListener<DefaultMQPushConsumer> {

    private final RocketMQProperties properties;

    private boolean running;

    private String group;

    public RocketMQNotifyListener(NotifyListenerWrapper notifyListener, NotifyConsumer<? extends Serializable> notifyConsumer,
                                  RocketMQProperties properties) {
        super(notifyListener, notifyConsumer);
        this.properties = properties;
    }

    public RocketMQNotifyListener(NotifyListenerWrapper notifyListener, NotifyEventListener eventListener,
                                  RocketMQProperties properties) {
        super(notifyListener, null);
        this.properties = properties;
        this.eventListener = eventListener;
    }

    @Override
    public void start() {
        if (this.isRunning()) {
            throw new IllegalStateException("Consumer 运行中 " + this.toString());
        }

        try {
            initRocketMQPushConsumer();
            this.consumer.start();
            log.info("Register RocketMQ message consumer successfully group:[{}] consume orderly: [{}] topic: [{}] tags: [{}]",
                    group, this.notifyListener.orderly(), topic, this.notifyListener.selectorExpression());
        } catch (MQClientException e) {
            log.error("Create RocketMQ Pull Consumer exception", e);
        }
        this.setRunning(true);
    }

    @Override
    public void destroy() {
        this.setRunning(false);
        if (Objects.nonNull(consumer)) {
            consumer.shutdown();
        }
        log.info("RocketMQ consumer container destroyed {}", group);
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {

        private final int delayLevelWhenNextConsume;

        public DefaultMessageListenerConcurrently(int delayLevelWhenNextConsume) {
            this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
        }

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt messageExt : msgs) {
                log.debug("received msg: {}", messageExt);
                try {
                    long now = System.currentTimeMillis();
                    handleMessage(messageExt);
                    long costTime = System.currentTimeMillis() - now;
                    log.debug("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (Exception e) {
                    log.warn("consume message failed. messageId:{}, topic:{}, reconsumeTimes:{}",
                            messageExt.getMsgId(), messageExt.getTopic(), messageExt.getReconsumeTimes(), e);
                    context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                } finally {
                    log.info("Consume message: topic: {} keys:{} tags: {}", topic, messageExt.getKeys(), messageExt.getTags());
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    public class DefaultMessageListenerOrderly implements MessageListenerOrderly {

        private final int suspendCurrentQueueTimeMillis;

        public DefaultMessageListenerOrderly(int suspendCurrentQueueTimeMillis) {
            this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
        }

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            for (MessageExt messageExt : msgs) {
                log.debug("received msg: {}", messageExt);
                try {
                    long now = System.currentTimeMillis();
                    handleMessage(messageExt);
                    long costTime = System.currentTimeMillis() - now;
                    log.debug("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (Exception e) {
                    log.warn("consume message failed. messageId:{}, topic:{}, reconsumeTimes:{}",
                            messageExt.getMsgId(), messageExt.getTopic(), messageExt.getReconsumeTimes(), e);
                    context.setSuspendCurrentQueueTimeMillis(suspendCurrentQueueTimeMillis);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                } finally {
                    log.info("Consume message: topic: {} keys:{} tags: {}", topic, messageExt.getKeys(), messageExt.getTags());
                }
            }

            return ConsumeOrderlyStatus.SUCCESS;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void handleMessage(MessageExt messageExt) {
        try {
            if (null != notifyConsumer) {
                Message message = doConvertMessage(messageExt);
                for (NotifyConsumeBefore consumeBefore : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeBefore.class)) {
                    message = consumeBefore.consumeBefore(message);
                }
                notifyConsumer.consume(message);
                for (NotifyConsumeAfter consumeAfter : BeanDefinitionUtils.getBeansOfTypeByOrdered(NotifyConsumeAfter.class)) {
                    consumeAfter.consumeAfter(message);
                }
            }
            if (null != eventListener) {
                Message message = doConvertMessage(messageExt);
                Object payload = message.getPayload();
                MessageHeaders headers = message.getHeaders();
                NotifyEvent notifyEvent = new NotifyEvent(messageExt.getTopic(), messageExt.getTopic(), payload);
                Map<String, String> props = new HashMap<>();
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    props.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                notifyEvent.setProperties(props);
                notifyEvent.setExtend(messageExt);
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

    private Message<? extends Serializable> doConvertMessage(MessageExt messageExt) {
        String json = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        Serializable payload = JsonUtils.parseObject(json, TopicClassCacheManager.get(messageExt.getTopic()));
        if (null == payload) {
            return null;
        }
        Map<String, Object> headers = new HashMap<>();
        headers.put("origin", messageExt);
        headers.put("msgId", messageExt.getMsgId());
        headers.putAll(messageExt.getProperties());
        MessageHeaders msgHeader = new MessageHeaders(headers);
        return MessageBuilder.createMessage(payload, msgHeader);
    }

    private void initRocketMQPushConsumer() throws MQClientException {
        this.group = RocketMQUtil.getCloudConsumerGroupByBeanName(group(null), properties.getAccessChannel());
        RocketMQProperties.Consumer consumerConfig = properties.getConsumer();
        boolean enableMsgTrace = Boolean.TRUE.equals(this.notifyListener.enableMsgTrace()) ? this.notifyListener.enableMsgTrace()
                : (Boolean.TRUE.equals(consumerConfig.isEnableMsgTrace()) ? consumerConfig.isEnableMsgTrace() : properties.isEnableMsgTrace());
        String customizedTraceTopic = consumerConfig.getCustomizedTraceTopic();
        DefaultMQPushConsumer consumer = RocketMQUtil.createDefaultPushConsumer(
                this.notifyListener.namespace(),
                properties.getNameServer(),
                properties.getAccessChannel(),
                this.group,
                topic,
                MessageModel.CLUSTERING,
                this.notifyListener.selectorType(),
                this.notifyListener.selectorExpression(),
                properties.getAccessKey(),
                properties.getSecretKey(),
                10,
                properties.isTlsEnable(),
                enableMsgTrace,
                customizedTraceTopic
        );
        consumer.setUseTLS(Boolean.parseBoolean(this.notifyListener.tlsEnable()));
        consumer.setConsumeThreadMin(consumerConfig.getConsumeThreadMin());
        consumer.setConsumeThreadMax(consumerConfig.getConsumeThreadMax());
        consumer.setPullBatchSize(consumerConfig.getPullBatchSize());
        consumer.setConsumeMessageBatchMaxSize(consumerConfig.getConsumeMessageBatchMaxSize());
        consumer.setConsumeConcurrentlyMaxSpan(consumerConfig.getConsumeConcurrentlyMaxSpan());
        consumer.setMaxReconsumeTimes(consumerConfig.getMaxReconsumeTimes());
        consumer.setPersistConsumerOffsetInterval(consumerConfig.getPersistConsumerOffsetInterval());
        consumer.setInstanceName(consumer.getInstanceName());

        if (this.notifyListener.orderly()) {
            consumer.setMessageListener(new DefaultMessageListenerOrderly(this.notifyListener.suspendCurrentQueueTimeMillis()));
        } else {
            consumer.setMessageListener(new DefaultMessageListenerConcurrently(this.notifyListener.delayLevelWhenNextConsume()));
        }

        this.consumer = consumer;
    }
}
