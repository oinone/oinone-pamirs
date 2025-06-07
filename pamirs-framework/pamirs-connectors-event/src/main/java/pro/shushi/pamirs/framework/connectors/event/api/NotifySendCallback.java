package pro.shushi.pamirs.framework.connectors.event.api;

import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;

@FunctionalInterface
public interface NotifySendCallback {

    void callback(NotifySendResult sendResult);
}
