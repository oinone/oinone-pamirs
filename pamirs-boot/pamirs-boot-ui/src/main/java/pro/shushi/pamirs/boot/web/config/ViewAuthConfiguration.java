package pro.shushi.pamirs.boot.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = ViewAuthConfiguration.PAMIRS_VIEW_AUTH_CONFIG_PREFIX
)
@RefreshScope
public class ViewAuthConfiguration {
    public final static String PAMIRS_VIEW_AUTH_CONFIG_PREFIX = "pamirs.view.auth";

    private boolean isEnabledSubViewAuth = false;

    public boolean getIsEnabledSubViewAuth() {
        return isEnabledSubViewAuth;
    }

    public void setIsEnabledSubViewAuth(boolean isEnabledSubViewAuth) {
        this.isEnabledSubViewAuth = isEnabledSubViewAuth;
    }
}
