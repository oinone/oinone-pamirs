package pro.shushi.pamirs.connectors.event.rocketmq.test;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

import java.io.IOException;

/**
 * @author Adamancy Zhang
 * @date 2020-12-03 18:13
 */
public class AliyunMQConsumer extends AbstractAliyunTestCase implements AliyunProperties {

    public static void main(String[] args) throws MQClientException, IOException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(null, groupId, getRpcHook());
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(instanceId);
        consumer.setAccessChannel(AccessChannel.CLOUD);

        consumer.subscribe(topic, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(1);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();

        System.out.println("start consumer");
    }
}
