package pro.shushi.pamirs.framework.connectors.event.api;

@FunctionalInterface
public interface NotifyQueueSelector {

    String hashing(Object /* msg payload */ event);
}
