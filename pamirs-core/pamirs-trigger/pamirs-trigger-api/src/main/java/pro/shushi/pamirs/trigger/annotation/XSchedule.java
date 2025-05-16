package pro.shushi.pamirs.trigger.annotation;

import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.trigger.enmu.TriggerTimeAnchorEnum;
import pro.shushi.pamirs.trigger.model.ExecuteTaskAction;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;

import java.lang.annotation.*;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 11:34
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XSchedule {

    /**
     * cron -> {@link ScheduleTaskAction#getCron()}
     *
     * @return cron表达式
     */
    String cron();

    /**
     * name -> {@link ScheduleTaskAction#getTechnicalName()}
     *
     * @return 技术名称
     */
    String name() default "";

    /**
     * name -> {@link ScheduleTaskAction#getDisplayName()}
     *
     * @return 显示名称
     */
    String displayName() default "";

    /**
     * taskType -> {@link ExecuteTaskAction#getTaskType()}
     *
     * @return 任务类型
     */
    String taskType() default "";

    /**
     * periodTimeAnchor -> {@link ScheduleTaskAction#getPeriodTimeAnchor()}
     *
     * @return 触发时机
     */
    TriggerTimeAnchorEnum periodTimeAnchor() default TriggerTimeAnchorEnum.START;

    /**
     * limitExecuteNumber -> {@link ScheduleTaskAction#getLimitExecuteNumber()}
     *
     * @return 限制执行次数
     */
    int limitExecuteNumber() default -1;

    /**
     * limitRetryNumber -> {@link ScheduleTaskAction#getLimitRetryNumber()}
     *
     * @return 最大重试次数
     */
    int limitRetryNumber() default -1;

    /**
     * nextRetryTimeValue -> {@link ScheduleTaskAction#getNextRetryTimeValue()}
     *
     * @return 下次重试执行时间
     */
    int nextRetryTimeValue() default -1;

    /**
     * nextRetryTimeUnit -> {@link ScheduleTaskAction#getNextRetryTimeUnit()}
     *
     * @return 下次重试时间单位
     */
    TimeUnitEnum nextRetryTimeUnit() default TimeUnitEnum.SECOND;
}
