package pro.shushi.pamirs.connectors.event.rocketmq.test.transaction;

import pro.shushi.pamirs.connectors.event.rocketmq.test.AbstractAliyunTestCase;
import pro.shushi.pamirs.connectors.event.rocketmq.test.AliyunProperties;

/**
 * @author Adamancy Zhang on 2021-05-21 10:33
 */
public class AliyunSDKTransactionProducer extends AbstractAliyunTestCase implements AliyunProperties {

//    private static final AtomicInteger transactionIndex = new AtomicInteger(0);
//
//    private static final Map<String, Integer> localTransactionStore = new ConcurrentHashMap<>(2);
//
//    public static void main(String[] args) throws InterruptedException {
//        Properties producerProperties = new Properties();
//        producerProperties.setProperty(PropertyKeyConst.GROUP_ID, groupId);
//        producerProperties.setProperty(PropertyKeyConst.AccessKey, accessKey);
//        producerProperties.setProperty(PropertyKeyConst.SecretKey, secretKey);
//        producerProperties.setProperty(PropertyKeyConst.INSTANCE_ID, instanceId);
//        producerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, namesrvAddr);
//
//        TestSDKTransactionListener listener = new TestSDKTransactionListener();
//
//        TransactionProducer producer = ONSFactory.createTransactionProducer(producerProperties, listener);
//        producer.start();
//
//        System.out.println("start producer");
//
//        System.out.println("send message...");
//
//        SendResult sendResult = producer.send(new Message(topic, "test", "Hello World".getBytes(StandardCharsets.UTF_8)), listener, null);
//
//        System.out.println("send message finished");
//
//        localTransactionStore.put(sendResult.getMessageId(), 1);
//
//        System.out.println("commit message");
//
//        TimeUnit.SECONDS.sleep(10);
//    }
//
//    public static class TestSDKTransactionListener implements LocalTransactionChecker, LocalTransactionExecuter {
//
//        @Override
//        public TransactionStatus execute(Message message, Object o) {
//            int value = transactionIndex.getAndIncrement();
//            int status = value % 3;
//            localTransactionStore.put(message.getMsgID(), status);
//            return TransactionStatus.Unknow;
//        }
//
//        @Override
//        public TransactionStatus check(Message message) {
//            String uniqKey = message.getUserProperties("UNIQ_KEY");
//            if (StringUtils.isBlank(uniqKey)) {
//                return TransactionStatus.CommitTransaction;
//            }
//            Integer status = localTransactionStore.get(uniqKey);
//            if (status != null) {
//                switch (status) {
//                    case 0:
//                        return TransactionStatus.Unknow;
//                    case 1:
//                        return TransactionStatus.CommitTransaction;
//                    case 2:
//                        return TransactionStatus.RollbackTransaction;
//                    default:
//                        throw new RuntimeException();
//                }
//            }
//            return TransactionStatus.CommitTransaction;
//        }
//    }
}
