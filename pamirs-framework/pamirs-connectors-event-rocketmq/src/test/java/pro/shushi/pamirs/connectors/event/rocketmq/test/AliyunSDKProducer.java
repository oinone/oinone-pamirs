package pro.shushi.pamirs.connectors.event.rocketmq.test;

/**
 * @author Adamancy Zhang on 2021-05-22 21:25
 */
public class AliyunSDKProducer extends AbstractAliyunTestCase implements AliyunProperties {

//    public static void main(String[] args) {
//        Properties producerProperties = new Properties();
//        producerProperties.setProperty(PropertyKeyConst.GROUP_ID, groupId);
//        producerProperties.setProperty(PropertyKeyConst.AccessKey, accessKey);
//        producerProperties.setProperty(PropertyKeyConst.SecretKey, secretKey);
//        producerProperties.setProperty(PropertyKeyConst.INSTANCE_ID, instanceId);
//        producerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, namesrvAddr);
//        Producer producer = ONSFactory.createProducer(producerProperties);
//        producer.start();
//        System.out.println("Producer Started");
//
//        System.out.println("send message...");
//
//        producer.send(new Message(topic, "test", "Hello World".getBytes(StandardCharsets.UTF_8)));
//
//        System.out.println("send message finished");
//
//        producer.shutdown();
//    }
}
