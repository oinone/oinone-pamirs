package pro.shushi.pamirs.framework.connectors.event.annotation;

import pro.shushi.pamirs.framework.connectors.event.enumeration.ConsumerType;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotifyListener {

    String topic();

    String tags() default "*";

    String group() default "";

    ConsumerType consumerType() default ConsumerType.CONCURRENTLY;

    /**
     * @deprecated 即将移除.
     */
    @Deprecated
    Class<?> bodyClass() default Void.class;

    boolean transactional() default false;

    int consumeThreadMax() default 64;

    int consumeThreadNumber() default 20;

    String messageModel() default "CLUSTERING";

    String selectorType() default "TAG"; /* SQL92 */

    long consumeTimeout() default 15L;

    int maxResumeTimes() default -1;

    String tlsEnable() default "false";

    String namespace() default "";

    int delayLevelWhenNextConsume() default 0;

    int awaitTerminationMillisWhenShutdown() default 1000;

    int suspendCurrentQueueTimeMillis() default 1000;

    String instanceName() default "DEFAULT";

    boolean enableMsgTrace() default false;

    String customizedTraceTopic() default "";

}
