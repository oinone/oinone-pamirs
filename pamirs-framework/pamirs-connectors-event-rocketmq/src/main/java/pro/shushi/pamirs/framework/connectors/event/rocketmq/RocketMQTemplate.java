package pro.shushi.pamirs.framework.connectors.event.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.common.RocketMQLocalRequestCallback;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.util.RocketMQUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * destination formats: `topicName:tags`
 */
@Slf4j
@SuppressWarnings({"WeakerAccess", "unused", "rawtypes", "unchecked"})
public class RocketMQTemplate extends AbstractMessageSendingTemplate<String> implements InitializingBean, DisposableBean {

    private DefaultMQProducer producer;

    private final Charset charset = StandardCharsets.UTF_8;

    private MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    public MessageQueueSelector getMessageQueueSelector() {
        return messageQueueSelector;
    }

    public void setMessageQueueSelector(MessageQueueSelector messageQueueSelector) {
        this.messageQueueSelector = messageQueueSelector;
    }

    public <T> T sendAndReceive(String destination, Message<?> message, Type type) {
        return sendAndReceive(destination, message, type, null, producer.getSendMsgTimeout(), 0);
    }

    public <T> T sendAndReceive(String destination, Object payload, Type type) {
        return sendAndReceive(destination, payload, type, null, producer.getSendMsgTimeout(), 0, null);
    }

    public <T> T sendAndReceive(String destination, Message<?> message, Type type, long timeout) {
        return sendAndReceive(destination, message, type, null, timeout, 0);
    }

    public <T> T sendAndReceive(String destination, Object payload, Type type, long timeout) {
        return sendAndReceive(destination, payload, type, null, timeout, 0, null);
    }

    public <T> T sendAndReceive(String destination, Message<?> message, Type type, long timeout, int delayLevel) {
        return sendAndReceive(destination, message, type, null, timeout, delayLevel);
    }

    public <T> T sendAndReceive(String destination, Object payload, Type type, long timeout, int delayLevel) {
        return sendAndReceive(destination, payload, type, null, timeout, delayLevel, null);
    }

    public <T> T sendAndReceive(String destination, Message<?> message, Type type, String hashKey) {
        return sendAndReceive(destination, message, type, hashKey, producer.getSendMsgTimeout(), 0);
    }

    public <T> T sendAndReceive(String destination, Object payload, Type type, String hashKey) {
        return sendAndReceive(destination, payload, type, hashKey, producer.getSendMsgTimeout(), 0, null);
    }

    public <T> T sendAndReceive(String destination, Message<?> message, Type type, String hashKey, long timeout) {
        return sendAndReceive(destination, message, type, hashKey, timeout, 0);
    }

    public <T> T sendAndReceive(String destination, Object payload, Type type, String hashKey, long timeout) {
        return sendAndReceive(destination, payload, type, hashKey, timeout, 0, null);
    }

    public <T> T sendAndReceive(String destination, Message<?> message, Type type, String hashKey, long timeout, int delayLevel) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("send request message failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            if (delayLevel > 0) {
                rocketMsg.setDelayTimeLevel(delayLevel);
            }
            MessageExt replyMessage;

            if (Objects.isNull(hashKey) || hashKey.isEmpty()) {
                replyMessage = (MessageExt) producer.request(rocketMsg, timeout);
            } else {
                replyMessage = (MessageExt) producer.request(rocketMsg, messageQueueSelector, hashKey, timeout);
            }
            return replyMessage != null ? (T) doConvertMessage(replyMessage, type) : null;
        } catch (Exception e) {
            log.error("send request message failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public <T> T sendAndReceive(String destination, Object payload, Type type, String hashKey,
                                long timeout, int delayLevel, Map<String, Object> headers) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        if (null != headers && !headers.isEmpty()) {
            message.getHeaders().putAll(headers);
        }
        return sendAndReceive(destination, message, type, hashKey, timeout, delayLevel);
    }

    public void sendAndReceive(String destination, Message<?> message, RocketMQLocalRequestCallback rocketMQLocalRequestCallback) {
        sendAndReceive(destination, message, rocketMQLocalRequestCallback, null, producer.getSendMsgTimeout(), 0);
    }

    public void sendAndReceive(String destination, Object payload, RocketMQLocalRequestCallback rocketMQLocalRequestCallback) {
        sendAndReceive(destination, payload, rocketMQLocalRequestCallback, null, producer.getSendMsgTimeout(), 0);
    }

    public void sendAndReceive(String destination, Message<?> message, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, long timeout) {
        sendAndReceive(destination, message, rocketMQLocalRequestCallback, null, timeout, 0);
    }

    public void sendAndReceive(String destination, Object payload, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, long timeout) {
        sendAndReceive(destination, payload, rocketMQLocalRequestCallback, null, timeout, 0);
    }

    public void sendAndReceive(String destination, Message<?> message, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, long timeout, int delayLevel) {
        sendAndReceive(destination, message, rocketMQLocalRequestCallback, null, timeout, delayLevel);
    }

    public void sendAndReceive(String destination, Object payload, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, String hashKey) {
        sendAndReceive(destination, payload, rocketMQLocalRequestCallback, hashKey, producer.getSendMsgTimeout(), 0);
    }

    public void sendAndReceive(String destination, Message<?> message, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, String hashKey, long timeout) {
        sendAndReceive(destination, message, rocketMQLocalRequestCallback, hashKey, timeout, 0);
    }

    public void sendAndReceive(String destination, Object payload, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, String hashKey, long timeout) {
        sendAndReceive(destination, payload, rocketMQLocalRequestCallback, hashKey, timeout, 0);
    }

    public void sendAndReceive(String destination, Message<?> message, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, String hashKey) {
        sendAndReceive(destination, message, rocketMQLocalRequestCallback, hashKey, producer.getSendMsgTimeout(), 0);
    }

    public void sendAndReceive(String destination, Object payload, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, long timeout, int delayLevel) {
        sendAndReceive(destination, payload, rocketMQLocalRequestCallback, null, timeout, delayLevel);
    }

    public void sendAndReceive(String destination, Object payload, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, String hashKey, long timeout, int delayLevel) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        sendAndReceive(destination, message, rocketMQLocalRequestCallback, hashKey, timeout, delayLevel);
    }

    public void sendAndReceive(String destination, Message<?> message, RocketMQLocalRequestCallback rocketMQLocalRequestCallback, String hashKey, long timeout, int delayLevel) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("send request message failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            if (delayLevel > 0) {
                rocketMsg.setDelayTimeLevel(delayLevel);
            }
            if (timeout <= 0) {
                timeout = producer.getSendMsgTimeout();
            }
            RequestCallback requestCallback = null;
            if (rocketMQLocalRequestCallback != null) {
                requestCallback = new RequestCallback() {
                    @Override
                    public void onSuccess(org.apache.rocketmq.common.message.Message message) {
                        rocketMQLocalRequestCallback.onSuccess(doConvertMessage((MessageExt) message, getMessageType(rocketMQLocalRequestCallback)));
                    }

                    @Override
                    public void onException(Throwable e) {
                        rocketMQLocalRequestCallback.onException(e);
                    }
                };
            }
            if (Objects.isNull(hashKey) || hashKey.isEmpty()) {
                producer.request(rocketMsg, requestCallback, timeout);
            } else {
                producer.request(rocketMsg, messageQueueSelector, hashKey, requestCallback, timeout);
            }
        } catch (
                Exception e) {
            log.error("send request message failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }

    }

    public SendResult syncSend(String destination, Message<?> message) {
        return syncSend(destination, message, producer.getSendMsgTimeout());
    }

    public SendResult syncSend(String destination, Message<?> message, long timeout) {
        return syncSend(destination, message, timeout, 0);
    }

    public <T extends Message> SendResult syncSend(String destination, Collection<T> messages, long timeout) {
        if (Objects.isNull(messages) || messages.isEmpty()) {
            log.error("syncSend with batch failed. destination:{}, messages is empty ", destination);
            throw new IllegalArgumentException("`messages` can not be empty");
        }

        try {
            long now = System.currentTimeMillis();
            Collection<org.apache.rocketmq.common.message.Message> rmqMsgs = new ArrayList<>();
            for (Message msg : messages) {
                if (Objects.isNull(msg) || Objects.isNull(msg.getPayload())) {
                    log.warn("Found a message empty in the batch, skip it");
                    continue;
                }
                rmqMsgs.add(this.createRocketMqMessage(destination, msg));
            }

            SendResult sendResult = producer.send(rmqMsgs, timeout);
            long costTime = System.currentTimeMillis() - now;
            if (log.isDebugEnabled()) {
                log.debug("send messages cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            }
            return sendResult;
        } catch (Exception e) {
            log.error("syncSend with batch failed. destination:{}, messages.size:{} ", destination, messages.size());
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public SendResult syncSend(String destination, Message<?> message, long timeout, int delayLevel) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("syncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }
        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            if (delayLevel > 0) {
                rocketMsg.setDelayTimeLevel(delayLevel);
            }
            SendResult sendResult = producer.send(rocketMsg, timeout);
            long costTime = System.currentTimeMillis() - now;
            if (log.isDebugEnabled()) {
                log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            }
            return sendResult;
        } catch (Exception e) {
            log.error("syncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public SendResult syncSend(String destination, Object payload) {
        return syncSend(destination, payload, producer.getSendMsgTimeout());
    }

    public SendResult syncSend(String destination, Object payload, Map<String, Object> headers) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        if (null != headers && !headers.isEmpty()) {
            message.getHeaders().putAll(headers);
        }
        return syncSend(destination, payload, producer.getSendMsgTimeout());
    }

    public SendResult syncSend(String destination, Object payload, long timeout) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        return syncSend(destination, message, timeout);
    }

    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey) {
        return syncSendOrderly(destination, message, hashKey, producer.getSendMsgTimeout());
    }

    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("syncSendOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }
        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            SendResult sendResult = producer.send(rocketMsg, messageQueueSelector, hashKey, timeout);
            long costTime = System.currentTimeMillis() - now;
            if (log.isDebugEnabled()) {
                log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            }
            return sendResult;
        } catch (Exception e) {
            log.error("syncSendOrderly failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public SendResult syncSendOrderly(String destination, Object payload, String hashKey) {
        return syncSendOrderly(destination, payload, hashKey, producer.getSendMsgTimeout());
    }

    public SendResult syncSendOrderly(String destination, Object payload, String hashKey, long timeout) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        return syncSendOrderly(destination, message, hashKey, timeout);
    }

    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback, long timeout,
                          int delayLevel) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("asyncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }
        try {
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            if (delayLevel > 0) {
                rocketMsg.setDelayTimeLevel(delayLevel);
            }
            producer.send(rocketMsg, sendCallback, timeout);
        } catch (Exception e) {
            log.info("asyncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback, long timeout) {
        asyncSend(destination, message, sendCallback, timeout, 0);
    }

    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback) {
        asyncSend(destination, message, sendCallback, producer.getSendMsgTimeout());
    }

    public void asyncSend(String destination, Object payload, SendCallback sendCallback, long timeout) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        asyncSend(destination, message, sendCallback, timeout);
    }

    public void asyncSend(String destination, Object payload, SendCallback sendCallback) {
        asyncSend(destination, payload, sendCallback, producer.getSendMsgTimeout());
    }

    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback,
                                 long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("asyncSendOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }
        try {
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            producer.send(rocketMsg, messageQueueSelector, hashKey, sendCallback, timeout);
        } catch (Exception e) {
            log.error("asyncSendOrderly failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback) {
        asyncSendOrderly(destination, message, hashKey, sendCallback, producer.getSendMsgTimeout());
    }

    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback) {
        asyncSendOrderly(destination, payload, hashKey, sendCallback, producer.getSendMsgTimeout());
    }

    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback,
                                 long timeout) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        asyncSendOrderly(destination, message, hashKey, sendCallback, timeout);
    }

    public void sendOneWay(String destination, Message<?> message) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("sendOneWay failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }
        try {
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            producer.sendOneway(rocketMsg);
        } catch (Exception e) {
            log.error("sendOneWay failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public void sendOneWay(String destination, Object payload) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        sendOneWay(destination, message);
    }

    public void sendOneWayOrderly(String destination, Message<?> message, String hashKey) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("sendOneWayOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }
        try {
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            producer.sendOneway(rocketMsg, messageQueueSelector, hashKey);
        } catch (Exception e) {
            log.error("sendOneWayOrderly failed. destination:{}, message:{}", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    public void sendOneWayOrderly(String destination, Object payload, String hashKey) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        sendOneWayOrderly(destination, message, hashKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (producer != null) {
            producer.start();
        }
    }

    @Override
    protected void doSend(String destination, Message<?> message) {
        SendResult sendResult = syncSend(destination, message);
        if (log.isDebugEnabled()) {
            log.debug("send message to `{}` finished. result:{}", destination, sendResult);
        }
    }

    @Override
    protected Message<?> doConvert(Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor) {
        Message<?> message = super.doConvert(payload, headers, postProcessor);
        MessageBuilder<?> builder = MessageBuilder.fromMessage(message);
        builder.setHeaderIfAbsent(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN);
        return builder.build();
    }

    @Override
    public void destroy() {
        if (Objects.nonNull(producer)) {
            producer.shutdown();
        }
    }

    public TransactionSendResult sendMessageInTransaction(final String destination, final Message<?> message, final Object arg) throws MessagingException {
        try {
            if (((TransactionMQProducer) producer).getTransactionListener() == null) {
                throw new IllegalStateException("The rocketMQTemplate does not exist TransactionListener");
            }
            org.apache.rocketmq.common.message.Message rocketMsg = this.createRocketMqMessage(destination, message);
            return producer.sendMessageInTransaction(rocketMsg, arg);
        } catch (MQClientException e) {
            throw RocketMQUtil.convert(e);
        }
    }

    private org.apache.rocketmq.common.message.Message createRocketMqMessage(
            String destination, Message<?> message) {
        Message<?> msg = this.doConvert(message.getPayload(), message.getHeaders(), null);
        return RocketMQUtil.convertToRocketMessage(getMessageConverter(), charset,
                destination, msg);
    }

    private Object doConvertMessage(MessageExt messageExt, Type type) {
        if (Objects.equals(type, MessageExt.class)) {
            return messageExt;
        } else if (Objects.equals(type, byte[].class)) {
            return messageExt.getBody();
        } else {
            String str = new String(messageExt.getBody(), charset);
            if (Objects.equals(type, String.class)) {
                return str;
            } else {
                try {
                    if (type instanceof Class) {
                        return this.getMessageConverter().fromMessage(MessageBuilder.withPayload(str).build(), (Class<?>) type);
                    } else {
                        return ((SmartMessageConverter) this.getMessageConverter()).fromMessage(MessageBuilder.withPayload(str).build(), (Class<?>) ((ParameterizedType) type).getRawType(), null);
                    }
                } catch (Exception e) {
                    log.error("convert failed. str:{}, msgType:{}", str, type);
                    throw new RuntimeException("cannot convert message to " + type, e);
                }
            }
        }
    }

    private Type getMessageType(RocketMQLocalRequestCallback rocketMQLocalRequestCallback) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(rocketMQLocalRequestCallback);
        Type matchedGenericInterface = null;
        while (Objects.nonNull(targetClass)) {
            Type[] interfaces = targetClass.getGenericInterfaces();
            if (Objects.nonNull(interfaces)) {
                for (Type type : interfaces) {
                    if (type instanceof ParameterizedType && (Objects.equals(((ParameterizedType) type).getRawType(), RocketMQLocalRequestCallback.class))) {
                        matchedGenericInterface = type;
                        break;
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        if (Objects.isNull(matchedGenericInterface)) {
            return Object.class;
        }

        Type[] actualTypeArguments = ((ParameterizedType) matchedGenericInterface).getActualTypeArguments();
        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
            return actualTypeArguments[0];
        }
        return Object.class;
    }
}
