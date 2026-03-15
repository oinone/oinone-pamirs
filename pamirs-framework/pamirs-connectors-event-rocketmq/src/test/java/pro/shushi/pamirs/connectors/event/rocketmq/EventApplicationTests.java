package pro.shushi.pamirs.connectors.event.rocketmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;
import pro.shushi.pamirs.framework.connectors.event.rocketmq.RocketMQNotifyProducer;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EventApplication.class})
public class EventApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(EventApplicationTests.class);

    private TestModel testModel1;
    private TestModel testModel2;

    public EventApplicationTests() {
        testModel1 = new TestModel();
        testModel2 = new TestModel();
        testModel1.setTestModel(testModel2);
        testModel2.setTestModel(testModel1);
        for (int i = 0; i < 1; i++) {
            TestModel temp = new TestModel();
            temp.setTestInteger(i);
            testModel1.getTestModelList().add(temp);
        }
        for (int i = 0; i < 1; i++) {
            testModel1.getTestMap().put(String.valueOf(i), i);
        }
    }

    @Autowired
    private RocketMQNotifyProducer rocketMQNotifyProducer;

    @Test
    public void contextLoad() throws InterruptedException {
//        Iterator<TestModel> testModelIterator = testModel1.getTestModelList().iterator();
//        sendOrdered(testModelIterator);
        for (TestModel item : testModel1.getTestModelList()) {
            NotifySendResult notifySendResult = rocketMQNotifyProducer.send("topic2", "test", item);
//                    .setGroup("testTransaction").setIsTransaction(true);// todo 事务消息
            if (notifySendResult.isSuccess()) {
                logger.info("Send success time: {}", item.getTestInteger());
            } else
                logger.info("Send failed time: {}", item.getTestInteger());
        }
        TimeUnit.MINUTES.sleep(5);
    }

    public void sendOrdered(Iterator<TestModel> testModelIterator) {
        if (testModelIterator.hasNext()) {
            TestModel item = testModelIterator.next();
            rocketMQNotifyProducer.sendOrderly("topic1", "test", item, TestModel::getTestLong);
        }
    }
}
