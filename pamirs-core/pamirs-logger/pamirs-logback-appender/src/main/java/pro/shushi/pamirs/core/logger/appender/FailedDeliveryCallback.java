package pro.shushi.pamirs.core.logger.appender;

/**
 * FailedDeliveryCallback
 *
 * @author yakir on 2023/12/27 16:15.
 */
public interface FailedDeliveryCallback<E> {

    void onFailedDelivery(E evt, Throwable throwable);

}
