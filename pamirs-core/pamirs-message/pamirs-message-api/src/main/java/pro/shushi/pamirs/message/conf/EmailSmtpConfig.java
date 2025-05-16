package pro.shushi.pamirs.message.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 邮箱配置
 * @author haibo(xf.z@shushi.pro)
 * @date 2022-09-16 18:16:39
 */
@Configuration
@ConfigurationProperties(prefix = EmailSmtpConfig.PROPERTY_PREFIX)
public class EmailSmtpConfig {
    public static final String PROPERTY_PREFIX = "email.smtp";

    private String smtpUser;

    private String smtpPassword;

    private String smtpSecurity;

    private String smtpHost;

    private Integer smtpPort;

    private List<EmailTemplateConfig> templates;

    public static class EmailTemplateConfig {

        private String  name;
        private String  title;
        private String  body;

        public String getName() {
            return name;
        }

        public EmailTemplateConfig setName(String name) {
            this.name = name;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public EmailTemplateConfig setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getBody() {
            return body;
        }

        public EmailTemplateConfig setBody(String body) {
            this.body = body;
            return this;
        }
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    public EmailSmtpConfig setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
        return this;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public EmailSmtpConfig setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
        return this;
    }

    public String getSmtpSecurity() {
        return smtpSecurity;
    }

    public EmailSmtpConfig setSmtpSecurity(String smtpSecurity) {
        this.smtpSecurity = smtpSecurity;
        return this;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public EmailSmtpConfig setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
        return this;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public EmailSmtpConfig setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
        return this;
    }

    public List<EmailTemplateConfig> getTemplates() {
        return templates;
    }

    public EmailSmtpConfig setTemplates(List<EmailTemplateConfig> templates) {
        this.templates = templates;
        return this;
    }
}
