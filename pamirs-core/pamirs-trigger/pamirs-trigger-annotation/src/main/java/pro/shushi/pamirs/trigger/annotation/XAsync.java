package pro.shushi.pamirs.trigger.annotation;

import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;

import java.lang.annotation.*;

/**
 * @author Adamancy Zhang
 * @date 2020-11-10 21:00
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XAsync {

    /**
     * displayName -> {@link ExecuteTaskAction#getDisplayName()}
     *
     * @return 显示名称
     */
    String displayName();

    /**
     * taskType -> {@link ExecuteTaskAction#getTaskType()}
     *
     * @return 任务类型
     */
    String taskType() default "";

    /**
     * delayTime -> {@link ExecuteTaskAction#getDelayTimeValue()}
     *
     * @return 延时执行时间
     */
    int delayTime() default 0;

    /**
     * delayTimeUnit -> {@link ExecuteTaskAction#getDelayTimeUnit()}
     *
     * @return 延时执行时间单位
     */
    TimeUnitEnum delayTimeUnit() default TimeUnitEnum.SECOND;

    /**
     * limitRetryNumber -> {@link ExecuteTaskAction#getLimitRetryNumber()}
     *
     * @return 最大重试次数
     */
    int limitRetryNumber() default -1;

    /**
     * nextRetryTimeValue -> {@link ExecuteTaskAction#getNextRetryTimeValue()}
     *
     * @return 默认重试时间单位
     */
    int nextRetryTimeValue() default 3;

    /**
     * nextRetryTimeUnit -> {@link ExecuteTaskAction#getNextRetryTimeUnit()}
     *
     * @return 默认重试时间单位
     */
    TimeUnitEnum nextRetryTimeUnit() default TimeUnitEnum.SECOND;
}
