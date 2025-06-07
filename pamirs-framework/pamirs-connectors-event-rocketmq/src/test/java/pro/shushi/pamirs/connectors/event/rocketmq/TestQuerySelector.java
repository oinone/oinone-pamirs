package pro.shushi.pamirs.connectors.event.rocketmq;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyQueueSelector;

@Component
public class TestQuerySelector implements NotifyQueueSelector {

    @Override
    public String hashing(Object event) {
        return String.valueOf(event.hashCode());
    }
}
