package pro.shushi.pamirs.framework.connectors.event.annotation;

import pro.shushi.pamirs.framework.connectors.event.api.NotifyQueueSelector;
import pro.shushi.pamirs.framework.connectors.event.api.NotifySendCallback;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyTagsGenerator;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyBizType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Notify {

    /**
     * {@link NotifyBizType}
     * {@link NotifyBizType#biz}
     */
    String notifyBizType() default "biz";

    String topic();

    String tags() default "";

    Class<? extends NotifySendCallback> sendCallback() default NotifySendCallback.class;

    Class<? extends NotifyQueueSelector> querySelector() default NotifyQueueSelector.class;

    Class<? extends NotifyTagsGenerator> tagsGenerator() default NotifyTagsGenerator.class;
}
