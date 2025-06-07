package pro.shushi.pamirs.connectors.event.rocketmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
import pro.shushi.pamirs.framework.connectors.event.annotation.TransactionListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTransactionListener;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyTransactionState;

import java.io.Serializable;
import java.util.HashMap;

@Component
@TransactionListener(value = "topic2")
@NotifyListener(topic = "topic2", tags = "test")
public class TestConsumerClass implements NotifyConsumer<HashMap<String, Object>>, NotifyTransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TestConsumerClass.class);

    @Override
    public void consume(Message<HashMap<String, Object>> event) {
        logger.error(event.getPayload().getClass().getName());
        logger.error(event.getPayload().toString());
    }

    @Override
    public NotifyTransactionState checkLocalTransaction(Message<? extends Serializable> event) {
        return NotifyTransactionState.ROLLBACK;
    }
}
