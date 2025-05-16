package pro.shushi.pamirs.trigger.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;

/**
 * pamirs.event.trigger.auto-trigger 开关
 *
 * @author Adamancy Zhang on 2021-05-21 14:38
 */
public class AutoTriggerSwitchCondition implements Condition {

    /**
     * {@link PamirsTriggerConfiguration#getAutoTrigger()}
     */
    public static final String SWITCH_KEY = NotifyConstant.TRIGGER_CONFIGURATION_KEY + ".auto-trigger";

    /**
     * 默认值
     */
    public static final boolean DEFAULT_VALUE = true;

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(SWITCH_KEY, Boolean.class, DEFAULT_VALUE);
    }
}
