package pro.shushi.pamirs.connectors.event.rocketmq.test.transaction;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.NamespaceUtil;
import pro.shushi.pamirs.connectors.event.rocketmq.test.AbstractAliyunTestCase;
import pro.shushi.pamirs.connectors.event.rocketmq.test.AliyunProperties;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Adamancy Zhang on 2021-05-21 10:33
 */
public class AliyunTransactionProducer extends AbstractAliyunTestCase implements AliyunProperties {

    private static final AtomicInteger transactionIndex = new AtomicInteger(0);

    private static final Map<String, Integer> localTransactionStore = new ConcurrentHashMap<>(2);

    public static void main(String[] args) throws Exception {
        TransactionMQProducer producer = new TransactionMQProducer(groupId, getRpcHook());
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(instanceId);
        producer.setAccessChannel(AccessChannel.CLOUD);

        producer.setTransactionListener(new TestTransactionListener());

        producer.start();

        System.out.println("start producer");

        System.out.println("send message...");

        String topic = NamespaceUtil.wrapNamespace(instanceId, AbstractAliyunTestCase.topic);

        Message message = new Message(topic, "test", "Hello World".getBytes(StandardCharsets.UTF_8));

        TransactionSendResult sendResult = producer.sendMessageInTransaction(message, null);

        System.out.println("send message finished");

        localTransactionStore.put(sendResult.getMsgId(), 1);

        System.out.println("commit message");

        TimeUnit.SECONDS.sleep(10);
    }

    public static class TestTransactionListener implements TransactionListener {

        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            int value = transactionIndex.getAndIncrement();
            int status = value % 3;
            localTransactionStore.put(msg.getTransactionId(), status);
            return LocalTransactionState.UNKNOW;
        }

        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            Integer status = localTransactionStore.get(msg.getTransactionId());
            if (status != null) {
                switch (status) {
                    case 0:
                        return LocalTransactionState.UNKNOW;
                    case 1:
                        return LocalTransactionState.COMMIT_MESSAGE;
                    case 2:
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                    default:
                        throw new RuntimeException();
                }
            }
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }
}
