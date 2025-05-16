package pro.shushi.pamirs.eip.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = EipConfigurationConstant.PAMIRS_EIP_OPEN_API_PREFIX)
@Validated
@RefreshScope
@Conditional(EipOpenApiSwitchCondition.class)
public class PamirsEipOpenApiProperties {

    @NotNull
    private Boolean enabled = Boolean.TRUE;

    private Boolean test = Boolean.FALSE;

    private RouteConfigurationItem route;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public RouteConfigurationItem getRoute() {
        return route;
    }

    public void setRoute(RouteConfigurationItem route) {
        this.route = route;
    }

    public static class RouteConfigurationItem {

        @NotBlank
        private String host = EipConfigurationConstant.AUTOMATIC_RECOGNITION_HOST;

        @Max(65535)
        @Min(0)
        private Integer port = 8093;

        @NotBlank
        private String aesKey;

        private Long expires;

        private Long delayExpires;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getAesKey() {
            return aesKey;
        }

        public void setAesKey(String aesKey) {
            this.aesKey = aesKey;
        }

        public Long getExpires() {
            return expires;
        }

        public void setExpires(Long expires) {
            this.expires = expires;
        }

        public Long getDelayExpires() {
            return delayExpires;
        }

        public void setDelayExpires(Long delayExpires) {
            this.delayExpires = delayExpires;
        }
    }
}
