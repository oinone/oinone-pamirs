package pro.shushi.pamirs.connectors.event.rocketmq.test;

/**
 * @author Adamancy Zhang on 2021-05-22 21:25
 */
public class AliyunSDKConsumer extends AbstractAliyunTestCase implements AliyunProperties {

//    public static void main(String[] args) {
//        Properties consumerProperties = new Properties();
//        consumerProperties.setProperty(PropertyKeyConst.GROUP_ID, groupId);
//        consumerProperties.setProperty(PropertyKeyConst.AccessKey, accessKey);
//        consumerProperties.setProperty(PropertyKeyConst.SecretKey, secretKey);
//        consumerProperties.setProperty(PropertyKeyConst.INSTANCE_ID, instanceId);
//        consumerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, namesrvAddr);
//        Consumer consumer = ONSFactory.createConsumer(consumerProperties);
//        consumer.subscribe(topic, "*", new MessageListener() {
//            @Override
//            public Action consume(Message message, ConsumeContext context) {
//                System.out.println(1);
//                return Action.CommitMessage;
//            }
//        });
//        consumer.start();
//
//        System.out.println("start consumer");
//    }
}
