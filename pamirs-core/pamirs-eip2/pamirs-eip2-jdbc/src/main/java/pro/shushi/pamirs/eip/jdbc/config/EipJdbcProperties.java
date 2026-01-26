package pro.shushi.pamirs.eip.jdbc.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

import java.util.Map;

/**
 * EIP Jdbc 配置
 *
 * @author Adamancy Zhang at 21:22 on 2025-08-12
 */
@Configuration
@ConfigurationProperties(prefix = EipJdbcProperties.PAMIRS_EIP_JDBC_PREFIX)
@Validated
@RefreshScope
@Conditional(EipJdbcSwitchCondition.class)
public class EipJdbcProperties {

    public static final String PAMIRS_EIP_JDBC_PREFIX = EipConfigurationConstant.PAMIRS_EIP_PREFIX + ".jdbc";

    @NotNull
    private Boolean enabled = Boolean.TRUE;

    private Map<String, String> druid;

    private Map<String, Map<String, String>> dataSource;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getDruid() {
        return druid;
    }

    public void setDruid(Map<String, String> druid) {
        this.druid = druid;
    }

    public Map<String, Map<String, String>> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, Map<String, String>> dataSource) {
        this.dataSource = dataSource;
    }
}
