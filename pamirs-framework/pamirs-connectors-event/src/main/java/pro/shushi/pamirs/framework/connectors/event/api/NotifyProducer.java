package pro.shushi.pamirs.framework.connectors.event.api;

import pro.shushi.pamirs.framework.connectors.event.engine.NotifySendResult;
import pro.shushi.pamirs.meta.common.lambda.Getter;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

public interface NotifyProducer<TEMPLATE> {

    TEMPLATE getDefaultTemplate();

    TEMPLATE getTemplate(String topic);

    void registerTemplate(String topicOrTxGroup, TEMPLATE template);

    <T> NotifySendResult send(String topic, @Nullable String tag, T msg);

    <T> NotifySendResult sendTx(String topic, String tag, String txGroup, T msg, Object extArg);

    <T> NotifySendResult send(String topic, @Nullable String tag, T msg, Map<String, Object> headers);

    <T, R extends Serializable> NotifySendResult sendOrderly(String topic, String tag, T msg, Getter<T, R> orderlyFieldgetter);

    <T> NotifySendResult sendOrderly(String topic, String tag, T msg, String hashKey);

    <P extends NotifyProducer<TEMPLATE>> P isOk();

    void ok();

    void destroy();
}
