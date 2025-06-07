package pro.shushi.pamirs.framework.connectors.event.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.connectors.event.common.PamirsEventProperties;

import static pro.shushi.pamirs.framework.connectors.event.common.PamirsEventProperties.DEFAULT_VALUE;

/**
 * pamirs.event.enabled 开关
 *
 * @author Adamancy Zhang at 14:35 on 2021-05-21
 */
public class NotifySwitchCondition implements Condition {

    /**
     * {@link PamirsEventProperties#isEnabled()}
     */
    public static final String SWITCH_KEY = ConfigureConstants.PAMIRS_EVENT_CONFIG_PREFIX + ".enabled";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(SWITCH_KEY, Boolean.class, DEFAULT_VALUE);
    }
}
