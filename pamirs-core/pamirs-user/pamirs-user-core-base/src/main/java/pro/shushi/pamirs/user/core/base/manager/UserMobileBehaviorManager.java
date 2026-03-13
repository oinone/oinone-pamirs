package pro.shushi.pamirs.user.core.base.manager;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.international.util.InternationalInfo;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.resource.api.enmu.TimeZoneTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.sys.setting.api.SysSettingsService;
import pro.shushi.pamirs.sys.setting.enmu.LoginTypeEnum;
import pro.shushi.pamirs.sys.setting.model.SysSettings;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.login.UserCookieLogin;
import pro.shushi.pamirs.user.api.login.UserLoginFactory;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.PamirsUserThirdParty;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserLoginSequence;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.service.UserThirdPartyService;

import java.util.Optional;

import static pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum.LOGIN_BY_WECHAT_MA;
import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.enmu.UserThirdPartyTypeEnum.WEIXIN_MINI_PROGRAM;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;

/**
 * UserMobileBehaviorManager
 *
 * @author yakir on 2022/10/26 14:44.
 */
@Slf4j
@Component
public class UserMobileBehaviorManager {

    @Autowired(required = false)
    private SysSettingsService bcSysSettingsService;
    @Autowired
    private UserThirdPartyService userThirdPartyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserLoginSequence userLoginSequence;

    public UserMobileBehaviorManager() {}

    public PamirsUserTransient loginByMA(PamirsUserTransient user) {

        if (null == user) {
            return user;
        }

        if (StringUtils.isBlank(user.getToken())) {
            log.error("{}", USER_FIRST_LOGIN_PE_ERROR.msg());
            broken(user.setErrorMsg(USER_FIRST_LOGIN_PE_ERROR.msg())
                    .setErrorCode(USER_FIRST_LOGIN_PE_ERROR.code())
                    .setErrorField("login"));
            return user;
        }

        UserBehaviorEventEnum behaviorEvent = user.getUserBehaviorEvent();
        log.info("Login method: [{}]", behaviorEvent);
        if (!LOGIN_BY_WECHAT_MA.equals(behaviorEvent)) {
            return user;
        }

        WxMaJscode2SessionResult session = BeanDefinitionUtils.getBean(WeChatManager.class).wxSession(user.getToken());

        String sessionKey = session.getSessionKey();
        String unionId = session.getUnionid();
        String openId = session.getOpenid();

        log.info("SessionKey:[{}] UnionId:[{}] OpenId:[{}]", sessionKey, unionId, openId);

        PamirsUserThirdParty thirdPartyUser = userThirdPartyService.queryByOpenId(openId, WEIXIN_MINI_PROGRAM);

        PamirsUser rUser = null;
        if (null == thirdPartyUser || null == thirdPartyUser.getUserId() || thirdPartyUser.getUserId() < 0) {
            SysSettings sysSettings = bcSysSettingsService.sysSettings();
            LoginTypeEnum loginType = sysSettings.getLoginType();
            if (!LoginTypeEnum.NAME_EMAIL.equals(loginType)) { // 支持手机登录
                if (StringUtils.isBlank(user.getPhone())) {
                    log.error("{}", USER_FIRST_LOGIN_PE_ERROR.msg());
                    broken(user.setErrorMsg(USER_FIRST_LOGIN_PE_ERROR.msg())
                            .setErrorCode(USER_FIRST_LOGIN_PE_ERROR.code())
                            .setErrorField("login"));
                    return user;
                }
                // 查询是否存在手机号
                IWrapper<PamirsUser> qw = Pops.<PamirsUser>lambdaQuery()
                        .from(PamirsUser.MODEL_MODEL)
                        .eq(PamirsUser::getPhone, user.getPhone());
                rUser = userService.queryOneByWrapper(qw);
                if (null == rUser) {
                    boolean isOpenReg = Optional.ofNullable(bcSysSettingsService.sysSettings())
                            .map(SysSettings::getOpenReg)
                            .orElse(false);
                    if (isOpenReg) {
                        String login = userLoginSequence.loginSequence();
                        PamirsUser cUser = new PamirsUser();
                        cUser.setCurrency(InternationalInfo.getDefaultCurrency());
                        cUser.setTimeZoneType(TimeZoneTypeEnum.getEnumByValue(InternationalInfo.getDefaultTimeZone()));
                        cUser.setLang(InternationalInfo.getDefaultLang());
                        cUser.setLogin(login);
                        cUser.setNickname(null == user.getNickname() ? user.getLogin() : user.getNickname());
                        cUser.setName(null == user.getName() ? user.getLogin() : user.getName());
                        cUser.setPassword(user.getPassword());//加密
                        cUser.setActive(Boolean.TRUE);
                        cUser.setEmail(user.getEmail());
                        cUser.setPhone(user.getPhone());
                        cUser.setRealname(user.getRealname());
                        cUser.setAvatarUrl(user.getAvatarUrl());
                        cUser.setGender(user.getGender());
                        cUser.setBirthday(user.getBirthday());
                        cUser.setSignUpType(UserSignUpType.BYSELF);
                        rUser = cUser.create();
                    } else {
                        log.error("{}", USER_LOGIN_CANNOT_REG.msg());
                        broken(user.setErrorMsg(USER_LOGIN_CANNOT_REG.msg())
                                .setErrorCode(USER_LOGIN_CANNOT_REG.code())
                                .setErrorField("login"));
                        return user;

                    }
                }
                userThirdPartyService.addThirdParty(rUser.getId(), openId, unionId, WEIXIN_MINI_PROGRAM);
            } else {
                log.error("{}", USER_LOGIN_EMAIL_REG.msg());
                user.setOpenid(openId);// 邮箱登录|注册传递openid
                user.setUnionId(unionId);// 邮箱登录|注册传递unionId
                broken(user.setErrorMsg(USER_LOGIN_EMAIL_REG.msg())
                        .setErrorCode(USER_LOGIN_EMAIL_REG.code())
                        .setErrorField("login"));
                return user;
            }
        } else {
            // 已存在第三方绑定
            Long userId = thirdPartyUser.getUserId();
            rUser = new PamirsUser().queryById(userId);
        }

        //session处理
        //登录
        UserCookieLogin userCookieLogin = (UserCookieLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.COOKIE.value());
        userCookieLogin.login(rUser);
        return user;

    }
}
