package pro.shushi.pamirs.trigger.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;

/**
 * pamirs.event.schedule.enabled 开关
 *
 * @author Adamancy Zhang at 16:19 on 2025-03-24
 * @deprecated cause: remote schedule unsupported
 */
@Deprecated
public class ScheduleSwitchCondition implements Condition {

    public static final String SWITCH_KEY = ConfigureConstants.PAMIRS_EVENT_CONFIG_PREFIX + ".schedule.enabled";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(SWITCH_KEY, Boolean.class, true);
    }
}
