package pro.shushi.pamirs.middleware.schedule.core.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.middleware.schedule.constant.ScheduleConstant;

/**
 * @author Adamancy Zhang
 * @date 2020-11-11 10:16
 */
public class ScheduleSwitchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        Boolean scheduleSwitchCondition = environment.getProperty(ScheduleConstant.SCHEDULE_SWITCH_CONFIG, Boolean.class);
        if (scheduleSwitchCondition == null) {
            scheduleSwitchCondition = Boolean.TRUE;
        }
        return scheduleSwitchCondition;
    }
}