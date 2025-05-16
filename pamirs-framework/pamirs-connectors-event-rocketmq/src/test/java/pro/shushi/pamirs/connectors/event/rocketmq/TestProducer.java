package pro.shushi.pamirs.connectors.event.rocketmq;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.annotation.Notify;

import java.util.List;

@Component
public class TestProducer {

    @Notify(topic = "topic1", tags = "test", querySelector = TestQuerySelector.class)
    public TestModel test1(TestModel testModel) {
        return testModel;
    }

    @Notify(topic = "topic2", tags = "test")
    public List<TestModel> test2(List<TestModel> testModelList) {
        return testModelList;
    }
}
