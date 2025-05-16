package pro.shushi.pamirs.user.core.base.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

import static pro.shushi.pamirs.user.core.base.conf.WxConfig.WECHAT_MP_APP_ID;
import static pro.shushi.pamirs.user.core.base.conf.WxConfig.WECHAT_MP_PREFIX;

/**
 * WxConfig
 *
 * @author yakir on 2022/09/16 17:10.
 */
@Configuration
@ConfigurationProperties(prefix = WxConfig.WECHAT_MP_PREFIX)
@ConditionalOnProperty(prefix = WECHAT_MP_PREFIX, name = WECHAT_MP_APP_ID)
public class WxConfig implements Serializable {

    public static final String WECHAT_MP_PREFIX = "pamirs.user.wechat";
    public static final String WECHAT_MP_APP_ID = "app-id";

    private static final long serialVersionUID = -2786180457833673437L;

    private String appId;
    private String appSecret;

    public String getAppId() {
        return appId;
    }

    public WxConfig setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public WxConfig setAppSecret(String appSecret) {
        this.appSecret = appSecret;
        return this;
    }

    @Override
    public String toString() {
        return "WxConfig{" +
                "appId='" + appId + '\'' +
                ", appSecret='" + appSecret + '\'' +
                '}';
    }
}
