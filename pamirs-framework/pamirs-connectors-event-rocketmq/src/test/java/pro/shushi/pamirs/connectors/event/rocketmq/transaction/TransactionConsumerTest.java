package pro.shushi.pamirs.connectors.event.rocketmq.transaction;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

import java.io.IOException;

/**
 * @author Adamancy Zhang on 2021-03-17 16:53
 */
public class TransactionConsumerTest extends AbstractProperties {

    public static void main(String[] args) throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(null, groupId);
        consumer.setNamesrvAddr(namesrvAddr);

        consumer.subscribe(topic, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(1);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();

        System.out.println("start consumer");
    }
}
