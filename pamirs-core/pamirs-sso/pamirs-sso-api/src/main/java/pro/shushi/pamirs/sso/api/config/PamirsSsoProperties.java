package pro.shushi.pamirs.sso.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.enmu.SsoAuthTypeEnum;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = SsoConfigurationConstant.PAMIRS_SSO_PREFIX)
@Validated
@RefreshScope
@Conditional(SsoSwitchCondition.class)
@Data
public class PamirsSsoProperties {
    @NotNull
    private Boolean enabled = Boolean.TRUE;

    private Client client = new Client();

    private Server server = new Server();

    @Data
    public static class Client {
        private String clientId = "*";
        private String clientSecret = "*";
        private String ssoServerUrl = "*";
        private Expires expires = new Expires();
    }

    @Data
    public static class Server {
        private String loginUrl;
        private SsoAuthTypeEnum authType = SsoAuthTypeEnum.OAUTH2;
        private DefaultExpires defaultExpires = new DefaultExpires();
    }

    @Data
    public static class Expires {
        private Long expiresIn = 7200L;
        private Long refreshTokenExpiresIn = 604800L;
    }

    @Data
    public static class DefaultExpires {
        private Long expiresIn = 7200L;
        private Long refreshTokenExpiresIn = 604800L;
        private Long codeExpiresIn = 600L;
        private Long cacheTokenExpirationTime = 600L;
    }


}

