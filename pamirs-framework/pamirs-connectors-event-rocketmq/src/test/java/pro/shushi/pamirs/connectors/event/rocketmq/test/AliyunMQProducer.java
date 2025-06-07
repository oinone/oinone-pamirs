package pro.shushi.pamirs.connectors.event.rocketmq.test;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author Adamancy Zhang
 * @date 2020-12-03 18:27
 */
public class AliyunMQProducer extends AbstractAliyunTestCase implements AliyunProperties {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(groupId, getRpcHook());
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(instanceId);
        producer.setAccessChannel(AccessChannel.CLOUD);

        producer.start();

        System.out.println("start producer");

        System.out.println("send message...");

        producer.send(new Message(topic, "test", "Hello World".getBytes(StandardCharsets.UTF_8)));

        System.out.println("send message finished");

        producer.shutdown();
    }
}
