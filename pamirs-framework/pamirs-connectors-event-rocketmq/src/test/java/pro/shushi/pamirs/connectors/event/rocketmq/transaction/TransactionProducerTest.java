package pro.shushi.pamirs.connectors.event.rocketmq.transaction;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Adamancy Zhang on 2021-03-17 16:51
 */
public class TransactionProducerTest extends AbstractProperties {

    private static final AtomicInteger transactionIndex = new AtomicInteger(0);

    private static final Map<String, Integer> localTransactionStore = new ConcurrentHashMap<>(2);

    public static void main(String[] args) throws MQClientException, IOException, RemotingException, InterruptedException, MQBrokerException {
        TransactionMQProducer producer = new TransactionMQProducer(groupId);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setTransactionListener(new TransactionListener() {
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
        });

        producer.start();

        System.out.println("start producer");

        System.out.println("send message...");

        TransactionSendResult sendResult = producer.sendMessageInTransaction(new Message(topic, "test", "Hello World".getBytes(StandardCharsets.UTF_8)), null);

        System.out.println("send message finished");

        localTransactionStore.put(sendResult.getMsgId(), 1);

        System.out.println("commit message");

//        producer.shutdown();

//        System.out.println("shutdown producer");
    }
}
