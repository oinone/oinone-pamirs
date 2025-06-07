package pro.shushi.pamirs.sso.api.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;

/**
 * @author Adamancy Zhang
 * @date 2020-11-05 18:20
 */
public class SsoSwitchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(SsoConfigurationConstant.PAMIRS_SSO_PREFIX + ".enabled", Boolean.class, true);
    }
}
