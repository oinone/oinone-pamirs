package pro.shushi.pamirs.framework.connectors.event.common;

/**
 * PamirsEventsConstants
 *
 * @author yakir on 2023/12/09 11:28.
 */
public interface PamirsEventsConstants {

    /* rocketmq */
    String ROCKETMQ_CONFIG_PREFIX = "spring.rocketmq";
    String ROCKETMQ_CONFIG_NAME_SRV = "name-server";
    /* trace topic */
    String ROCKETMQ_SYS_TRACE_TOPIC = "RMQ_SYS_TRACE_TOPIC";


    String ROCKETMQ_DEFAULT_PRODUCER_BEAN_NAME = "defaultMQProducer";
    String ROCKETMQ_NOTIFY_PRODUCER_BEAN_NAME = "rocketMQNotifyProducer";
    String ROCKETMQ_PRODUCER_BEAN_NAME = "rocketMQProducer";
    String ROCKETMQ_APPCTXAWARE_BEAN_NAME = "rocketMQAppCtxAware";
    String ROCKETMQ_ALIYUN_GROUP_PREFIX = "GID_";


    /* kafka */
    String KAFKA_TEMPLATE_BEAN_NAME = "kafkaTemplate";
    String KAFKA_NOTIFY_PRODUCER_BEAN_NAME = "kafkaNotifyProducer";
    String KAFKA_APPCTXAWARE_BEAN_NAME = "kafkaAppCtxAware";


    /* rabbit */
    String RABBITMQ_TEMPLATE_BEAN_NAME = "rabbitTemplate";
    String RABBITMQ_NOTIFY_PRODUCER_BEAN_NAME = "rabbitMQNotifyProducer";
    String RABBITMQ_APPCTXAWARE_BEAN_NAME = "rabbitAppCtxAware";
}
