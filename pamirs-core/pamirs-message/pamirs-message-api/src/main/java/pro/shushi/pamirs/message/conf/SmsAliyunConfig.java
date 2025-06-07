package pro.shushi.pamirs.message.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 阿里云短信配置
 *
 * @author syj@shushi.pro
 * @version 1.0.0
 * date 2021-04-28 21:22
 */
@Configuration
@ConfigurationProperties(prefix = SmsAliyunConfig.PROPERTY_PREFIX)
public class SmsAliyunConfig {
    public static final String PROPERTY_PREFIX = "sms.aliyun";

    // 短信签名名称
    private String signName; // 数式科技
    // 主账号AccessKey的ID
    private String accessKeyId;
    // 主账号AccessKey的密钥
    private String accessKeySecret;
    // 发送渠道Endpoint
    private String endpoint; // http[s]://dysmsapi.aliyuncs.com
    // API支持的RegionID
    private String regionId; // cn-hangzhou
    // 时区
    private String timeZone; // GMT
    // 签名方式
    private String signatureMethod; // HMAC-SHA1
    // 签名算法版本
    private String signatureVersion; // 1.0
    // API的版本号
    private String version; // 2017-05-25

    private List<SmsAliyunTplConfig> templates;

    public static class SmsAliyunTplConfig {

        private String channel;
        private String templateType;
        private String templateCode;
        private String templateContent;
        private Integer timeInterval;
        private Boolean hasVerifyCode;
        private String name;

        public String getName() {
            return name;
        }

        public SmsAliyunTplConfig setName(String name) {
            this.name = name;
            return this;
        }

        public String getChannel() {
            return channel;
        }

        public SmsAliyunTplConfig setChannel(String channel) {
            this.channel = channel;
            return this;
        }

        public String getTemplateType() {
            return templateType;
        }

        public SmsAliyunTplConfig setTemplateType(String templateType) {
            this.templateType = templateType;
            return this;
        }

        public String getTemplateCode() {
            return templateCode;
        }

        public SmsAliyunTplConfig setTemplateCode(String templateCode) {
            this.templateCode = templateCode;
            return this;
        }

        public String getTemplateContent() {
            return templateContent;
        }

        public SmsAliyunTplConfig setTemplateContent(String templateContent) {
            this.templateContent = templateContent;
            return this;
        }

        public Integer getTimeInterval() {
            return timeInterval;
        }

        public SmsAliyunTplConfig setTimeInterval(Integer timeInterval) {
            this.timeInterval = timeInterval;
            return this;
        }

        public Boolean getHasVerifyCode() {
            return hasVerifyCode;
        }

        public SmsAliyunTplConfig setHasVerifyCode(Boolean hasVerifyCode) {
            this.hasVerifyCode = hasVerifyCode;
            return this;
        }
    }

    public String getSignName() {
        return signName;
    }

    public SmsAliyunConfig setSignName(String signName) {
        this.signName = signName;
        return this;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public SmsAliyunConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public SmsAliyunConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public SmsAliyunConfig setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getRegionId() {
        return regionId;
    }

    public SmsAliyunConfig setRegionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public SmsAliyunConfig setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String getSignatureMethod() {
        return signatureMethod;
    }

    public SmsAliyunConfig setSignatureMethod(String signatureMethod) {
        this.signatureMethod = signatureMethod;
        return this;
    }

    public String getSignatureVersion() {
        return signatureVersion;
    }

    public SmsAliyunConfig setSignatureVersion(String signatureVersion) {
        this.signatureVersion = signatureVersion;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public SmsAliyunConfig setVersion(String version) {
        this.version = version;
        return this;
    }

    public List<SmsAliyunTplConfig> getTemplates() {
        return templates;
    }

    public SmsAliyunConfig setTemplates(List<SmsAliyunTplConfig> templates) {
        this.templates = templates;
        return this;
    }
}
