package pro.shushi.pamirs.eip.api.strategy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

import jakarta.validation.constraints.NotNull;


/**
 * @author yeshenyue on 2024/9/26 16:54.
 */
@Configuration
@ConfigurationProperties(prefix = EipConfigurationConstant.PAMIRS_EIP_LOG_PREFIX)
public class PamirsEipLogProperties {

    public static final double DEFAULT_FREQUENCY = 0.01;

    // 日志记录频率默认：千分之一
    @NotNull
    private Double frequency = DEFAULT_FREQUENCY;

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }
}