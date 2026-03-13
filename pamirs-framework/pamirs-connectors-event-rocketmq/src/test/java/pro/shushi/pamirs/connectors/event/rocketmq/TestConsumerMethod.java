package pro.shushi.pamirs.connectors.event.rocketmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.enumeration.ConsumerType;

@Component
public class TestConsumerMethod {

    private static final Logger logger = LoggerFactory.getLogger(TestConsumerMethod.class);

    @Bean
    @NotifyListener(consumerType = ConsumerType.ORDERLY, topic = "topic1", tags = "test")
    public NotifyConsumer<TestModel> eventListener() {
        return event -> {
            TestModel testModel = event.getPayload();
            logger.error("Consume time: {}", testModel.getTestInteger());
        };
    }
}
