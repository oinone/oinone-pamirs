package pro.shushi.pamirs.eip.jdbc.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

/**
 * EIP Jdbc 开关
 *
 * @author Adamancy Zhang at 09:25 on 2025-08-13
 */
public class EipJdbcSwitchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(EipConfigurationConstant.PAMIRS_EIP_PREFIX + ".enabled", Boolean.class, true)
                && context.getEnvironment().getProperty(EipJdbcProperties.PAMIRS_EIP_JDBC_PREFIX + ".enabled", Boolean.class, true);
    }
}
