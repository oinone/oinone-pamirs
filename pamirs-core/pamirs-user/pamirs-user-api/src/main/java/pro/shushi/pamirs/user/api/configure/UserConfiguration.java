package pro.shushi.pamirs.user.api.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/**
 * 用户配置
 *
 * @author Adamancy Zhang at 18:01 on 2024-06-15
 */
@Configuration
@ConfigurationProperties(prefix = UserConfiguration.PREFIX)
@Validated
@RefreshScope
public class UserConfiguration {

    public static final String PREFIX = "pamirs.user";

    private UserConfig admin;

    private SessionConfig session;

    public UserConfig getAdmin() {
        return admin;
    }

    public void setAdmin(UserConfig admin) {
        this.admin = admin;
    }

    public SessionConfig getSession() {
        return session;
    }

    public void setSession(SessionConfig session) {
        this.session = session;
    }

    public static class UserConfig {

        public UserConfig() {
        }

        public UserConfig(String login, String password, String name) {
            this.login = login;
            this.password = password;
            this.name = name;
        }

        private String login;

        private String password;

        private String name;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SessionConfig {

        private String mode;

        private Integer expire;

        private Integer renewedExpire;

        private Set<String> renewedFilterUrls;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Integer getExpire() {
            return expire;
        }

        public void setExpire(Integer expire) {
            this.expire = expire;
        }

        public Integer getRenewedExpire() {
            return renewedExpire;
        }

        public void setRenewedExpire(Integer renewedExpire) {
            this.renewedExpire = renewedExpire;
        }

        public Set<String> getRenewedFilterUrls() {
            return renewedFilterUrls;
        }

        public void setRenewedFilterUrls(Set<String> renewedFilterUrls) {
            this.renewedFilterUrls = renewedFilterUrls;
        }
    }
}
