package pro.shushi.pamirs.framework.connectors.event.rocketmq.common;

/**
 * RocketMQHeaders
 *
 * @author yakir on 2023/12/09 13:37.
 */
public interface RocketMQHeaders {

    String PREFIX = "rocketmq_";
    String KEYS = "KEYS";
    String TAGS = "TAGS";
    String TOPIC = "TOPIC";
    String MESSAGE_ID = "MESSAGE_ID";
    String BORN_TIMESTAMP = "BORN_TIMESTAMP";
    String BORN_HOST = "BORN_HOST";
    String FLAG = "FLAG";
    String QUEUE_ID = "QUEUE_ID";
    String SYS_FLAG = "SYS_FLAG";
    String TRANSACTION_ID = "TRANSACTION_ID";
    String DELAY = "DELAY";
    String WAIT = "WAIT";

}
