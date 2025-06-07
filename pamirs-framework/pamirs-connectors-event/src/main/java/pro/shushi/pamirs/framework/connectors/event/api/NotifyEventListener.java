package pro.shushi.pamirs.framework.connectors.event.api;

import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;

/**
 * @deprecated 即将移除. 使用{@link NotifyConsumer}替代
 */
@Deprecated
@FunctionalInterface
public interface NotifyEventListener {

    void consumer(NotifyEvent event);
}
