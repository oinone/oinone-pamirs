package pro.shushi.pamirs.trigger.annotation;

import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.trigger.enmu.TriggerConditionEnum;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

import java.lang.annotation.*;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 12:53
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trigger {

    /**
     * name -> {@link TriggerTaskAction#getTechnicalName()}
     *
     * @return 技术名称
     */
    String name();

    /**
     * displayName -> {@link TriggerTaskAction#getDisplayName()}
     *
     * @return 显示名称
     */
    String displayName();

    /**
     * active -> {@link TriggerTaskAction#getActive()}
     *
     * @return 是否启用
     */
    boolean active() default true;

    /**
     * condition -> {@link TriggerTaskAction#getCondition()}
     *
     * @return 触发场景
     */
    TriggerConditionEnum condition();

    /**
     * auto wired context argument name -> {@link TriggerTaskAction#getWiredContext()}
     *
     * @return 装配上下文参数名称
     */
    String wiredContext() default CharacterConstants.SEPARATOR_EMPTY;

    /**
     * event parameter argument name -> {@link TriggerTaskAction#getEventParameter()}
     *
     * @return 事件Id参数名称
     */
    String eventParameter() default CharacterConstants.SEPARATOR_EMPTY;

    /**
     * 任务类型
     * @return
     */
    TaskType taskType() default TaskType.BASE_SCHEDULE_TASK;
}
