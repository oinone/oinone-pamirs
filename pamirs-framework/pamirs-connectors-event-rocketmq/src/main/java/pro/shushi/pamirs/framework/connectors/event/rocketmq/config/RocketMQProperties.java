package pro.shushi.pamirs.framework.connectors.event.rocketmq.config;

import org.apache.rocketmq.client.AccessChannel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.ROCKETMQ_CONFIG_PREFIX;
import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventsConstants.ROCKETMQ_SYS_TRACE_TOPIC;

/**
 * RocketMQProperties
 *
 * @author yakir on 2023/12/09 10:54.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = ROCKETMQ_CONFIG_PREFIX)
@Conditional(NotifySwitchCondition.class)
public class RocketMQProperties implements Serializable {

    private static final long serialVersionUID = 1707936557663147933L;

    private String nameServer;

    private String accessChannel = AccessChannel.LOCAL.name();

    private Producer producer = new Producer();

    private Consumer consumer = new Consumer();

    private String accessKey;

    private String secretKey;

    private boolean tlsEnable;

    private boolean enableMsgTrace = false;

    @Data
    public static class Producer {

        private String group;

        private String namespace;

        private int sendMessageTimeout = 3000;

        private int compressMessageBodyThreshold = 1024 * 4;

        private int retryTimesWhenSendFailed = 2;

        private int retryTimesWhenSendAsyncFailed = 2;

        private boolean retryNextServer = false;

        private int maxMessageSize = 1024 * 1024 * 4;

        private String instanceName = "DEFAULT";

        private boolean enableMsgTrace = false;

        private String customizedTraceTopic = ROCKETMQ_SYS_TRACE_TOPIC;
    }

    @Data
    public static class Consumer {

        private int consumeThreadMin = 1;

        private int consumeThreadMax = 5;

        private int pullBatchSize = 8;

        private int consumeMessageBatchMaxSize = 1;

        private int consumeConcurrentlyMaxSpan = 1000;

        private int maxReconsumeTimes = 16;

        private int persistConsumerOffsetInterval = 3000;

        private boolean enableMsgTrace = false;

        private String customizedTraceTopic = ROCKETMQ_SYS_TRACE_TOPIC;

        private String instanceName = "DEFAULT";
    }
}
