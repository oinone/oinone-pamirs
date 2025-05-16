package pro.shushi.pamirs.core.logger.delivery;

import org.apache.kafka.clients.producer.BufferExhaustedException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.TimeoutException;
import pro.shushi.pamirs.core.logger.appender.FailedDeliveryCallback;

/**
 * AsynchronousDeliveryStrategy
 *
 * @author yakir on 2023/12/27 17:04.
 */
public class AsynchronousDeliveryStrategy implements DeliveryStrategy {

    @Override
    public <E> boolean send(Producer producer, ProducerRecord record, final E event, final FailedDeliveryCallback<E> failedDeliveryCallback) {
        try {
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        failedDeliveryCallback.onFailedDelivery(event, exception);
                    }
                }
            });
            return true;
        } catch (BufferExhaustedException | TimeoutException e) {
            failedDeliveryCallback.onFailedDelivery(event, e);
            return false;
        }
    }

}