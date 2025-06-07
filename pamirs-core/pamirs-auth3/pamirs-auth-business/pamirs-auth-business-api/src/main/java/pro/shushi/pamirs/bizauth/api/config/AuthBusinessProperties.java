package pro.shushi.pamirs.bizauth.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.bizauth.api.constant.AuthBusinessConstant;

@Configuration
@ConfigurationProperties(prefix = AuthBusinessConstant.BUSINESS_CODE_SESSION)
@Validated
@RefreshScope
public class AuthBusinessProperties {

    private String businessCodeKey = "companyCode";

    public String getBusinessCodeKey() {
        return businessCodeKey;
    }

    public void setBusinessCodeKey(String businessCodeKey) {
        this.businessCodeKey = businessCodeKey;
    }
}
