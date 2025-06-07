package pro.shushi.pamirs.connectors.event.rocketmq.test.order;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import pro.shushi.pamirs.connectors.event.rocketmq.test.AbstractAliyunTestCase;
import pro.shushi.pamirs.connectors.event.rocketmq.test.AliyunProperties;
import pro.shushi.pamirs.connectors.event.rocketmq.test.RocketMQMessageQueueSelector;

import java.nio.charset.StandardCharsets;

/**
 * @author Adamancy Zhang on 2021-05-21 10:33
 */
public class AliyunOrderedProducer extends AbstractAliyunTestCase implements AliyunProperties {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(groupId, getRpcHook());
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(instanceId);
        producer.setAccessChannel(AccessChannel.CLOUD);

        producer.start();

        System.out.println("start producer");

        System.out.println("send message...");

        producer.send(new Message(topic, "test", "Hello World".getBytes(StandardCharsets.UTF_8)), new RocketMQMessageQueueSelector(), null);

        System.out.println("send message finished");

        producer.shutdown();
    }
}
