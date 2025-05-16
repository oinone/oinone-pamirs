package pro.shushi.pamirs.framework.connectors.event.rocketmq.common;

/**
 * RocketMQLocalRequestCallback
 *
 * @author yakir on 2023/12/09 13:51.
 */
public interface RocketMQLocalRequestCallback<T> {

    void onSuccess(final T message);

    void onException(final Throwable e);
}
