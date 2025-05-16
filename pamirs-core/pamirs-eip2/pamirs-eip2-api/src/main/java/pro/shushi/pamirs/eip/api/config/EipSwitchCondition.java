package pro.shushi.pamirs.eip.api.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

/**
 * @author Adamancy Zhang
 * @date 2020-11-05 18:20
 */
public class EipSwitchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(EipConfigurationConstant.PAMIRS_EIP_PREFIX + ".enabled", Boolean.class, true);
    }
}
