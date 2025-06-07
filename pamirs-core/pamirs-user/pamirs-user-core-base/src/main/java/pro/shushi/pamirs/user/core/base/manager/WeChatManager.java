package pro.shushi.pamirs.user.core.base.manager;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.USER_WA_PHONE;
import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.USER_WX_MINI_APP_LOGIN_ERROR;
import static pro.shushi.pamirs.user.core.base.conf.WxConfig.WECHAT_MP_APP_ID;
import static pro.shushi.pamirs.user.core.base.conf.WxConfig.WECHAT_MP_PREFIX;

/**
 * WeChatManager
 *
 * @author yakir on 2022/10/20 15:39.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = WECHAT_MP_PREFIX, name = WECHAT_MP_APP_ID)
public class WeChatManager {

    @Autowired
    private WxMaService userWxMaService;

    public String wechatPhoneNo(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        try {
            WxMaUserService maUserService = userWxMaService.getUserService();
            WxMaPhoneNumberInfo phoneInfo = maUserService.getNewPhoneNoInfo(code);
            return phoneInfo.getPhoneNumber();
        } catch (WxErrorException ex) {
            throw PamirsException.construct(USER_WA_PHONE)
                    .errThrow();
        }
    }

    public WxMaJscode2SessionResult wxSession(String token) {
        WxMaJscode2SessionResult session = null;
        try {
            WxMaUserService maUserService = userWxMaService.getUserService();
            session = maUserService.getSessionInfo(token);
        } catch (WxErrorException ex) {
            throw PamirsException.construct(USER_WX_MINI_APP_LOGIN_ERROR, ex.getMessage())
                    .errThrow();
        }

        return session;
    }

}
