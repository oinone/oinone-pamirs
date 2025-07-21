package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;

/**
 * ModelAsProperty switch condition
 *
 * @author Adamancy Zhang at 12:07 on 2025-07-14
 */
public class ModelAsPropertySwitchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(ConfigureConstants.MY_BATIS_PLUS_ENHANCE_CONFIG_PREFIX + ".configuration.using-model-as-property", Boolean.class, false);
    }
}