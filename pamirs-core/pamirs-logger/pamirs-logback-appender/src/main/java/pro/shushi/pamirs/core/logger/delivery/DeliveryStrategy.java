package pro.shushi.pamirs.core.logger.delivery;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import pro.shushi.pamirs.core.logger.appender.FailedDeliveryCallback;

/**
 * DeliveryStrategy
 *
 * @author yakir on 2023/12/27 17:05.
 */
public interface DeliveryStrategy {

    <E> boolean send(Producer producer, ProducerRecord record, E event, FailedDeliveryCallback<E> failedDeliveryCallback);

}
