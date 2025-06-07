package pro.shushi.pamirs.connectors.event.rocketmq.test.order;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import pro.shushi.pamirs.connectors.event.rocketmq.test.AbstractAliyunTestCase;
import pro.shushi.pamirs.connectors.event.rocketmq.test.AliyunProperties;

/**
 * @author Adamancy Zhang on 2021-05-21 10:34
 */
public class AliyunOrderedConsumer extends AbstractAliyunTestCase implements AliyunProperties {

    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(null, groupId, getRpcHook());
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(instanceId);
        consumer.setAccessChannel(AccessChannel.CLOUD);

        consumer.subscribe(topic, "*");
        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            System.out.println(1);
            return ConsumeOrderlyStatus.SUCCESS;
        });
        consumer.start();

        System.out.println("start consumer");
    }
}
