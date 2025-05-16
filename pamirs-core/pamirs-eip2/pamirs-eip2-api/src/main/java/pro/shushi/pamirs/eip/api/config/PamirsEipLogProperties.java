package pro.shushi.pamirs.eip.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;


/**
 * @author yeshenyue on 2024/9/26 16:54.
 */
@Configuration
@ConfigurationProperties(prefix = EipConfigurationConstant.PAMIRS_EIP_LOG_PREFIX)
public class PamirsEipLogProperties {

    // 日志记录频率默认：千分之一
    private Double frequency = 0.01;

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }
}