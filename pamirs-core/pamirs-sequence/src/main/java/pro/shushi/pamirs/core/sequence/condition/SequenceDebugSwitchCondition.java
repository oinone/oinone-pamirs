package pro.shushi.pamirs.core.sequence.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 序列调试开关
 *
 * @author Adamancy Zhang at 18:10 on 2025-02-27
 */
public class SequenceDebugSwitchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("pamirs.sequence.debug.enabled", Boolean.class, false);
    }
}
