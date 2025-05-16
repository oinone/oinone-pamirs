package pro.shushi.pamirs.framework.connectors.event.api;

import org.springframework.messaging.Message;

import java.io.Serializable;

@FunctionalInterface
public interface NotifyConsumer<T extends Serializable> {

    void consume(Message<T> event);
}
