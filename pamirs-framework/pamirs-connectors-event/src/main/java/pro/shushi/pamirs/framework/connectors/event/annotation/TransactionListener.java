package pro.shushi.pamirs.framework.connectors.event.annotation;

import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyType;

import java.lang.annotation.*;

/**
 * @author Adamancy Zhang on 2021-03-17 23:14
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransactionListener {

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    NotifyType notifyType() default NotifyType.ROCKET_MQ;

    String value();
}
