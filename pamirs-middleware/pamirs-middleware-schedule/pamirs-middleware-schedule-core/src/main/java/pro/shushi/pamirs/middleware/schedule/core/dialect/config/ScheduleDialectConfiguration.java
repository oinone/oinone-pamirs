package pro.shushi.pamirs.middleware.schedule.core.dialect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.middleware.schedule.constant.ScheduleConstant;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;

/**
 * schedule 方言配置
 *
 * @author Adamancy Zhang at 21:35 on 2023-06-27
 */
@Configuration
@ConfigurationProperties(prefix = ScheduleConstant.PAMIRS_SCHEDULE_DIALECT_CONFIG_PREFIX)
@Validated
public class ScheduleDialectConfiguration {

    private String type;

    private String version;

    private String majorVersion;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(String majorVersion) {
        this.majorVersion = majorVersion;
    }

    public DialectVersion getDialectVersion() {
        return new DialectVersion(this.getType(), this.getVersion(), this.getMajorVersion());
    }
}
