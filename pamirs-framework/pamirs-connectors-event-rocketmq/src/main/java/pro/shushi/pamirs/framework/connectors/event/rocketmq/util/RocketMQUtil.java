package pro.shushi.pamirs.framework.connectors.event.rocketmq.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.trace.AsyncTraceDispatcher;
import org.apache.rocketmq.client.trace.hook.SendMessageTraceHookImpl;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTransactionListener;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyTransactionState;
import pro.shushi.pamirs.framework.connectors.event.manager.TopicAndGroupEditorManager;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.common.RocketMQHeaders;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;

import static org.apache.rocketmq.common.filter.ExpressionType.SQL92;
import static org.apache.rocketmq.common.filter.ExpressionType.TAG;
import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.ROCKETMQ_ALIYUN_GROUP_PREFIX;

/**
 * RocketMQUtil
 *
 * @author yakir on 2023/12/09 11:57.
 */
@Slf4j
public class RocketMQUtil {

    public static MessagingException convert(MQClientException e) {
        return new MessagingException(e.getErrorMessage(), e);
    }

    public static TransactionListener convert(NotifyTransactionListener listener) {
        return new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object obj) {
                NotifyTransactionState state = listener.executeLocalTransaction(convertToSpringMessage(message), obj);
                return convertLocalTransactionState(state);
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                NotifyTransactionState state = listener.checkLocalTransaction(convertToSpringMessage(messageExt));
                return convertLocalTransactionState(state);
            }
        };
    }

    private static LocalTransactionState convertLocalTransactionState(NotifyTransactionState state) {
        switch (state) {
            case UNKNOWN:
                return LocalTransactionState.UNKNOW;
            case COMMIT:
                return LocalTransactionState.COMMIT_MESSAGE;
            case ROLLBACK:
                return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        // Never happen
        log.warn("Failed to covert enum type RocketMQLocalTransactionState {}.", state);
        return LocalTransactionState.UNKNOW;
    }

    public static org.springframework.messaging.Message convertToSpringMessage(org.apache.rocketmq.common.message.MessageExt message) {
        MessageBuilder messageBuilder =
                MessageBuilder.withPayload(message.getBody()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.KEYS), message.getKeys()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.TAGS), message.getTags()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.TOPIC), message.getTopic()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.MESSAGE_ID), message.getMsgId()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.BORN_TIMESTAMP), message.getBornTimestamp()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.BORN_HOST), message.getBornHostString()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.FLAG), message.getFlag()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.QUEUE_ID), message.getQueueId()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.SYS_FLAG), message.getSysFlag()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.TRANSACTION_ID), message.getTransactionId()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.DELAY), message.getDelayTimeLevel()).
                        setHeader(toRocketHeaderKey(RocketMQHeaders.WAIT), message.isWaitStoreMsgOK());
        addUserProperties(message.getProperties(), messageBuilder);
        return messageBuilder.build();
    }

    public static String toRocketHeaderKey(String rawKey) {
        return RocketMQHeaders.PREFIX + rawKey;
    }

    private static void addUserProperties(Map<String, String> properties, MessageBuilder messageBuilder) {
        if (!CollectionUtils.isEmpty(properties)) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                if (!MessageConst.STRING_HASH_SET.contains(key) && !MessageHeaders.ID.equals(key)
                        && !MessageHeaders.TIMESTAMP.equals(key) &&
                        (!key.startsWith(RocketMQHeaders.PREFIX) || !MessageConst.STRING_HASH_SET.contains(key.replaceFirst("^" + RocketMQHeaders.PREFIX, "")))) {
                    messageBuilder.setHeader(key, val);
                }
            }
        }
    }

    public static org.springframework.messaging.Message convertToSpringMessage(org.apache.rocketmq.common.message.Message message) {
        MessageBuilder messageBuilder = MessageBuilder.withPayload(message.getBody()).
                setHeader(toRocketHeaderKey(RocketMQHeaders.KEYS), message.getKeys()).
                setHeader(toRocketHeaderKey(RocketMQHeaders.TAGS), message.getTags()).
                setHeader(toRocketHeaderKey(RocketMQHeaders.TOPIC), message.getTopic()).
                setHeader(toRocketHeaderKey(RocketMQHeaders.FLAG), message.getFlag()).
                setHeader(toRocketHeaderKey(RocketMQHeaders.TRANSACTION_ID), message.getTransactionId());
        addUserProperties(message.getProperties(), messageBuilder);
        return messageBuilder.build();
    }

    @Deprecated
    public static org.apache.rocketmq.common.message.Message convertToRocketMessage(ObjectMapper objectMapper, Charset charset,
                                                                                    String destination, org.springframework.messaging.Message message) {
        Object payloadObj = message.getPayload();
        byte[] payloads;

        if (payloadObj instanceof String) {
            payloads = ((String) payloadObj).getBytes(charset);
        } else if (payloadObj instanceof byte[]) {
            payloads = (byte[]) message.getPayload();
        } else {
            try {
                String jsonObj = objectMapper.writeValueAsString(payloadObj);
                payloads = jsonObj.getBytes(charset);
            } catch (Exception e) {
                throw new RuntimeException("convert to RocketMQ message failed.", e);
            }
        }
        return getAndWrapMessage(destination, message.getHeaders(), payloads);
    }

    private static Message getAndWrapMessage(String destination, MessageHeaders headers, byte[] payloads) {
        if (destination == null || destination.length() < 1) {
            return null;
        }
        if (payloads == null || payloads.length < 1) {
            return null;
        }
        String[] tempArr = destination.split(":", 2);
        String topic = tempArr[0];
        String tags = (null != headers && !headers.isEmpty()) ? headers.get("tag", String.class) : null;
        Message rocketMsg = new Message(topic, tags, payloads);
        if (null != headers && !headers.isEmpty()) {
            Object keys = headers.get(RocketMQHeaders.KEYS);
            // if headers not have 'KEYS', try add prefix when getting keys
            if (ObjectUtils.isEmpty(keys)) {
                keys = headers.get(toRocketHeaderKey(RocketMQHeaders.KEYS));
            }
            if (!ObjectUtils.isEmpty(keys)) { // if headers has 'KEYS', set rocketMQ message key
                rocketMsg.setKeys(keys.toString());
            }
            Object flagObj = headers.getOrDefault("FLAG", "0");
            int flag = 0;
            try {
                flag = Integer.parseInt(flagObj.toString());
            } catch (NumberFormatException e) {
                // Ignore it
                if (log.isInfoEnabled()) {
                    log.info("flag must be integer, flagObj:{}", flagObj);
                }
            }
            rocketMsg.setFlag(flag);
            Object waitStoreMsgOkObj = headers.getOrDefault("WAIT_STORE_MSG_OK", "true");
            rocketMsg.setWaitStoreMsgOK(!waitStoreMsgOkObj.equals("false"));
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                if (StringUtils.equalsAny(entry.getKey(), "FLAG", "WAIT_STORE_MSG_OK")) {
                    continue;
                }
                if (!MessageConst.STRING_HASH_SET.contains(entry.getKey())) {
                    rocketMsg.putUserProperty(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }
        return rocketMsg;
    }

    public static org.apache.rocketmq.common.message.Message convertToRocketMessage(
            MessageConverter messageConverter, Charset charset, String destination, org.springframework.messaging.Message<?> message) {
        Object payloadObj = message.getPayload();
        byte[] payloads;
        try {
            if (null == payloadObj) {
                throw new RuntimeException("the message cannot be empty");
            }
            if (payloadObj instanceof String) {
                payloads = ((String) payloadObj).getBytes(charset);
            } else if (payloadObj instanceof byte[]) {
                payloads = (byte[]) message.getPayload();
            } else {
                String jsonObj = (String) messageConverter.fromMessage(message, payloadObj.getClass());
                if (null == jsonObj) {
                    throw new RuntimeException(String.format(
                            "empty after conversion [messageConverter:%s,payloadClass:%s,payloadObj:%s]",
                            messageConverter.getClass(), payloadObj.getClass(), payloadObj));
                }
                payloads = jsonObj.getBytes(charset);
            }
        } catch (Exception e) {
            throw new RuntimeException("convert to RocketMQ message failed.", e);
        }
        return getAndWrapMessage(destination, message.getHeaders(), payloads);
    }

    public static RPCHook getRPCHookByAkSk(String ak, String sk) {
        if (StringUtils.isNotBlank(ak) && StringUtils.isNotBlank(sk)) {
            return new AclClientRPCHook(new SessionCredentials(ak, sk));
        }
        return null;
    }

    public static DefaultMQProducer createDefaultMQProducer(String groupName, String ak, String sk,
                                                            boolean isEnableMsgTrace, String customizedTraceTopic) {

        boolean isEnableAcl = StringUtils.isNotBlank(ak) && StringUtils.isNotBlank(sk);
        DefaultMQProducer producer;
        if (isEnableAcl) {
            producer = new TransactionMQProducer(groupName, new AclClientRPCHook(new SessionCredentials(ak, sk)));
            producer.setVipChannelEnabled(false);
        } else {
            producer = new TransactionMQProducer(groupName);
        }

        if (isEnableMsgTrace) {
            try {
                AsyncTraceDispatcher dispatcher = new AsyncTraceDispatcher(groupName, isEnableAcl ? getRPCHookByAkSk(ak, sk) : null);
                if (StringUtils.isNotBlank(customizedTraceTopic)) {
                    dispatcher.setTraceTopicName(customizedTraceTopic);
                }
                dispatcher.setHostProducer(producer.getDefaultMQProducerImpl());
                Field field = DefaultMQProducer.class.getDeclaredField("traceDispatcher");
                field.setAccessible(true);
                field.set(producer, dispatcher);
                producer.getDefaultMQProducerImpl().registerSendMessageHook(new SendMessageTraceHookImpl(dispatcher));
            } catch (Throwable e) {
                log.error("system trace hook init failed ,maybe can't send msg trace data");
            }
        }

        return producer;
    }

    public static DefaultLitePullConsumer createDefaultLitePullConsumer(String nameServer, String accessChannel,
                                                                        String groupName, String topicName, MessageModel messageModel, String selectorType,
                                                                        String selectorExpression, String ak, String sk, int pullBatchSize, boolean useTLS)
            throws MQClientException {
        DefaultLitePullConsumer litePullConsumer = null;
        if (StringUtils.isNotBlank(ak) && StringUtils.isNotBlank(sk)) {
            litePullConsumer = new DefaultLitePullConsumer(groupName, new AclClientRPCHook(new SessionCredentials(ak, sk)));
            litePullConsumer.setVipChannelEnabled(false);
        } else {
            litePullConsumer = new DefaultLitePullConsumer(groupName);
        }
        litePullConsumer.setNamesrvAddr(nameServer);
        litePullConsumer.setPullBatchSize(pullBatchSize);
        if (accessChannel != null) {
            litePullConsumer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        litePullConsumer.setUseTLS(useTLS);

        switch (messageModel) {
            case BROADCASTING:
                litePullConsumer.setMessageModel(MessageModel.BROADCASTING);
                break;
            case CLUSTERING:
                litePullConsumer.setMessageModel(MessageModel.CLUSTERING);
                break;
            default:
                log.error("Property 'messageModel' was wrong.");
        }

        switch (selectorType) {
            case SQL92:
                litePullConsumer.subscribe(topicName, MessageSelector.bySql(selectorExpression));
                break;
            case TAG:
                litePullConsumer.subscribe(topicName, selectorExpression);
                break;
            default:
                log.error("Property 'selectorType' was wrong.");
        }

        return litePullConsumer;
    }

    public static DefaultMQPushConsumer createDefaultPushConsumer(String namespace, String nameServer, String accessChannel,
                                                                  String groupName, String topic, MessageModel messageModel, String selectorType,
                                                                  String selectorExpression, String ak, String sk, int pullBatchSize, boolean useTLS,
                                                                  boolean enableMsgTrace, String customMsgTopic)
            throws MQClientException {

        RPCHook rpcHook = getRPCHookByAkSk(ak, sk);
        DefaultMQPushConsumer pushConsumer = null;
        if (null != rpcHook) {
            pushConsumer = new DefaultMQPushConsumer(groupName, getRPCHookByAkSk(ak, sk), new AllocateMessageQueueAveragely(), enableMsgTrace, customMsgTopic);
            pushConsumer.setVipChannelEnabled(false);
        } else {
            pushConsumer = new DefaultMQPushConsumer(groupName, enableMsgTrace, customMsgTopic);
        }
        pushConsumer.setNamespace(namespace);
        pushConsumer.setNamesrvAddr(nameServer);
        pushConsumer.setPullBatchSize(pullBatchSize);
        if (accessChannel != null) {
            pushConsumer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        pushConsumer.setUseTLS(useTLS);

        switch (messageModel) {
            case BROADCASTING:
                pushConsumer.setMessageModel(MessageModel.BROADCASTING);
                break;
            case CLUSTERING:
                pushConsumer.setMessageModel(MessageModel.CLUSTERING);
                break;
            default:
                log.error("Property 'messageModel' was wrong.");
        }

        switch (selectorType) {
            case SQL92:
                pushConsumer.subscribe(topic, MessageSelector.bySql(selectorExpression));
                break;
            case TAG:
                pushConsumer.subscribe(topic, selectorExpression);
                break;
            default:
                log.error("Property 'selectorType' was wrong.");
        }

        return pushConsumer;
    }

    public static String getNamespace(String specifiedNamespace, String defaultNamespace) {
        // prefer to use annotation namespace
        // if is empty a default namespace will be used
        return !StringUtils.isNotBlank(specifiedNamespace) && StringUtils.isNotBlank(defaultNamespace) ? defaultNamespace : specifiedNamespace;
    }

    /**
     * 通过Bean名称获取消费者group
     *
     * @param beanName SpringBean名称
     * @return 消费者group
     */
    public static String getCloudConsumerGroupByBeanName(String beanName, String accessChannel) {
        beanName = beanName.replaceAll("\\.", "_");
        beanName = TopicAndGroupEditorManager.editConsumerGroup(beanName);
        if (StringUtils.equalsIgnoreCase(AccessChannel.CLOUD.name(), accessChannel)) {
            beanName = ROCKETMQ_ALIYUN_GROUP_PREFIX + beanName;
        }
        return beanName;
    }
}
