package pro.shushi.pamirs.user.core.base.conf;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import static pro.shushi.pamirs.user.core.base.conf.WxConfig.WECHAT_MP_APP_ID;
import static pro.shushi.pamirs.user.core.base.conf.WxConfig.WECHAT_MP_PREFIX;

/**
 * WxServiceConfiguration
 *
 * @author yakir on 2022/09/16 17:23.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = WECHAT_MP_PREFIX, name = WECHAT_MP_APP_ID)
public class WxServiceConfiguration {

    public static final String USER_WX_SERVICE_BEAN = "userWxMaService";

    @Bean(name = USER_WX_SERVICE_BEAN)
    public WxMaService userWxMaService(@Autowired WxConfig wxConfig) {
        if (null == wxConfig || StringUtils.isAnyBlank(wxConfig.getAppId())) {
            log.error("WeChat configuration exception: [{}]", wxConfig);
            System.exit(-1);
        }

        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(wxConfig.getAppId());
        config.setSecret(wxConfig.getAppSecret());
        config.setMsgDataFormat("JSON");
        WxMaServiceImpl wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(config);
        return wxMaService;
    }
}
