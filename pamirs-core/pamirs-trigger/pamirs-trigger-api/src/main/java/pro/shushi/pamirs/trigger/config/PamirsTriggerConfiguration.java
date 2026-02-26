package pro.shushi.pamirs.trigger.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;

/**
 * Pamirs Trigger 配置
 *
 * @author Adamancy Zhang on 2021-05-21 14:30
 */
@Configuration
@ConfigurationProperties(prefix = NotifyConstant.TRIGGER_CONFIGURATION_KEY)
@Validated
@RefreshScope
@Conditional(NotifySwitchCondition.class)
public class PamirsTriggerConfiguration {

    private boolean autoTrigger = AutoTriggerSwitchCondition.DEFAULT_VALUE;

    private boolean defaultListener = DefaultListenerSwitchCondition.DEFAULT_VALUE;

    private CanalConfiguration canal = new CanalConfiguration();

    public boolean getAutoTrigger() {
        return autoTrigger;
    }

    public void setAutoTrigger(boolean autoTrigger) {
        this.autoTrigger = autoTrigger;
    }

    public boolean getDefaultListener() {
        return defaultListener;
    }

    public void setDefaultListener(boolean defaultListener) {
        this.defaultListener = defaultListener;
    }

    public CanalConfiguration getCanal() {
        return canal;
    }

    public void setCanal(CanalConfiguration canal) {
        this.canal = canal;
    }

    public static class CanalConfiguration {

        @NotBlank
        private String destination;

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }
    }
}
