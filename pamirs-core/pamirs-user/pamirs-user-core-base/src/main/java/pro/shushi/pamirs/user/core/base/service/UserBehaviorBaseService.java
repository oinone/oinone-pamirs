package pro.shushi.pamirs.user.core.base.service;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.auth.api.cache.PermissionCache;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.international.util.InternationalInfo;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.resource.api.enmu.TimeZoneTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.sys.setting.api.SysSettingsService;
import pro.shushi.pamirs.sys.setting.model.SysSettings;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.login.*;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsTenantTransient;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.*;
import pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker;
import pro.shushi.pamirs.user.api.utils.UserServiceUtils;
import pro.shushi.pamirs.user.core.base.util.UserLoginHelper;

import java.util.Arrays;

import static pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum.*;
import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.enmu.UserThirdPartyTypeEnum.WEIXIN_MINI_PROGRAM;
import static pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker.checkEmailFormat;
import static pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker.checkPhoneFormat;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;
import static pro.shushi.pamirs.user.core.base.util.UserLoginHelper.success;

/**
 * 用户行为服务
 *
 * @author shier
 * date 2020/4/11
 */
@Slf4j
public abstract class UserBehaviorBaseService {

    private IUserDataChecker dataChecker = BeanDefinitionUtils.getBean(IUserDataChecker.class);

    private IUserLoginChecker loginChecker = BeanDefinitionUtils.getBean(IUserLoginChecker.class);

    UserSmsVerificationCodeService smsVerificationCodeService = BeanDefinitionUtils.getBean(UserSmsVerificationCodeService.class);

    UserEmailConfirmationService userEmailConfirmationService = BeanDefinitionUtils.getBean(UserEmailConfirmationService.class);

    UserThirdPartyService userThirdPartyService = BeanDefinitionUtils.getBean(UserThirdPartyService.class);

    SysSettingsService sysSettingsService = BeanDefinitionUtils.getBean(SysSettingsService.class);

    public PamirsUserTransient loginBySmsVerificationCodeWithToken(PamirsUserTransient user, Boolean redirect) {
        checkPhoneFormat(user);
        PamirsUser rUser = dataChecker.checkPhoneExist(user);
        smsVerificationCodeService.ensureVerificationCode(user, SMSTemplateTypeEnum.SIGN_IN.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        //登录
        UserTokenLogin userTokenLogin = (UserTokenLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.TOKEN.value());
        String token = userTokenLogin.login(rUser);
        user.setToken(token);
        user.setRedirect(UserLoginHelper.getRedirectMenu());
        return success(user);
    }

    public PamirsUserTransient loginByToken(PamirsUserTransient user) {
        PamirsUser rUser = loginChecker.check4login(user);
        if (rUser == null || user.getBroken()) {
            return user;
        }
        //根据request当中的请求参数选择登录方式
        UserTokenLogin userTokenLogin = (UserTokenLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.TOKEN.value());
        String token = userTokenLogin.login(rUser);
        user.setToken(token);
        user.setRedirect(UserLoginHelper.getRedirectMenu());
        return success(user);
    }

    public PamirsUserTransient loginByCookie(PamirsUserTransient user) {
        CommonApiFactory.getApi(PermissionCache.class).clear();
        UserCookieLogin userCookieLogin = (UserCookieLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.COOKIE.value());
        PamirsUser rUser = (PamirsUser) userCookieLogin.resolveAndVerification(user);
        //登录之前
        if (rUser == null || user.getBroken()) {
            return user;
        }

        if (LOGIN_BY_WECHAT_MA.equals(user.getUserBehaviorEvent())) {
            userThirdPartyService.addThirdParty(rUser.getId(), user.getOpenid(), user.getUnionId(), WEIXIN_MINI_PROGRAM);
        }
        //从缓存中拿取当前用户登录使用的语言
        String lang = PamirsSession.getLang();
        if (StringUtils.isNotBlank(lang)) {
            ResourceLang resourceLang = new ResourceLang().setCode(lang).queryOne();
            Long langId = rUser.getLangId();
            if (resourceLang != null && langId != null && !langId.equals(resourceLang.getId())) {
                PamirsUser pamirsUser = new PamirsUser();
                pamirsUser.setLangId(resourceLang.getId());
                pamirsUser.setId(rUser.getId());
                pamirsUser.updateById();
                rUser.setLangId(resourceLang.getId());
                UserInfoCache.clearUserById(rUser.getId());
            }
        }
        //登录
        userCookieLogin.login(rUser);
        String tenant = PamirsTenantSession.getTenant();
        if (StringUtils.isNotBlank(tenant)) {
            //todo tenant的租户信息
            PamirsTenantTransient tenantInfo = new PamirsTenantTransient();
            tenantInfo.setTenant(tenant).setDisplayName(tenant);
            user.setTenants(Arrays.asList(tenantInfo));
            user.setTenant(tenant);
        }

        if (null == user.getNeedRedirect()) {
            user.setNeedRedirect(true);
        }

        SysSettings sysSettings = null;
        try {
            sysSettings = sysSettingsService.sysSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如配置了SysSettings中首次登录必须修改初始密码，为初次登录抛出异常，由前端来处理首次登录
        if (sysSettings != null && sysSettings.getNeedModifyInitialPassword() != null && sysSettings.getNeedModifyInitialPassword()) {
            if (BeanDefinitionUtils.getBean(PasswordService.class).isNeedModifyInitialPassword(rUser.getId())) {
                broken(user.setErrorMsg(UserExpEnumerate.USER_FIRST_LOGIN_ERROR.msg())
                        .setErrorCode(UserExpEnumerate.USER_FIRST_LOGIN_ERROR.code())
                        .setErrorField(UserConstant.FIELD_PASSWORD));
            }
        }
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        if (user.getNeedRedirect()) {
            user.setRedirect(UserLoginHelper.getRedirectMenu());
        }
        return success(user);
    }

    public PamirsUserTransient sendLoginSmsVerificationCode(PamirsUserTransient user) {
        checkPhoneFormat(user);
        dataChecker.checkPhoneExist(user);
        smsVerificationCodeService.pushPhoneVerificationCode(user, SMSTemplateTypeEnum.SIGN_IN.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    public PamirsUserTransient loginBySmsVerificationCodeWithCookie(PamirsUserTransient user, Boolean redirect) {
        CommonApiFactory.getApi(PermissionCache.class).clear();
        checkPhoneFormat(user);
        if (user.getBroken()) {
            return user;
        }

        //登录
        UserCookieLogin userCookieLogin = (UserCookieLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.COOKIE.value());

        PamirsUser rUser = (PamirsUser) userCookieLogin.resolveAndVerification(user);
        //登录之前
        if (rUser == null || user.getBroken()) {
            return user;
        }

        smsVerificationCodeService.ensureVerificationCode(user, SMSTemplateTypeEnum.SIGN_IN.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        userCookieLogin.login(rUser);
        user.setRedirect(UserLoginHelper.getRedirectMenu());
        return success(user);
    }

    public PamirsUserTransient loginByEmailVerificationCodeWithCookie(PamirsUserTransient user, Boolean redirect) {
        CommonApiFactory.getApi(PermissionCache.class).clear();
        checkEmailFormat(user);
        if (user.getBroken()) {
            return user;
        }

        //session处理 登录
        UserCookieLogin userCookieLogin = (UserCookieLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.COOKIE.value());

        PamirsUser rUser = (PamirsUser) userCookieLogin.resolveAndVerification(user);
        //登录之前
        if (rUser == null || user.getBroken()) {
            return user;
        }

        userEmailConfirmationService.ensureEmailConfirmation(user, SMSTemplateTypeEnum.SIGN_IN.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        userCookieLogin.login(rUser);
        user.setRedirect(UserLoginHelper.getRedirectMenu());
        return success(user);
    }

    public PamirsUserTransient logoutByCookie(PamirsUserTransient user, Boolean redirect) {
        //session处理
        UserCache.logout();
        return success(user);
    }

    public PamirsUserTransient sendSignUpSmsVerificationCode(PamirsUserTransient user) {
        checkPhoneFormat(user);
        dataChecker.checkPhoneExist(user);
        smsVerificationCodeService.pushPhoneVerificationCode(user, SMSTemplateTypeEnum.SIGN_UP.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    public PamirsUserTransient beforeSignUp(PamirsUserTransient user) {
        dataChecker.checkLoginNameIsExist(user);
        checkEmailFormat(user);
        PamirsUserDataChecker.checkPhoneFormat(user);
        dataChecker.checkPhoneIsNotExist(user, false);
        dataChecker.checkEmailIsNotExist(user, false);
        PamirsUserDataChecker.checkPasswordFormat(user);
        PamirsUserDataChecker.checkConfirmPasswordAndPassword(user);
        smsVerificationCodeService.ensureVerificationCode(user, SMSTemplateTypeEnum.SIGN_UP.value());
        return user;
    }

    public PamirsUserTransient beforeSignUpMobile(PamirsUserTransient user) {
        dataChecker.checkInviteCode(user);
        if (user.getBroken()) {
            log.error("Register user exception: [{}]", user.getErrorMsg());
            return user;
        }
        dataChecker.checkLoginNameIsExist(user);
        if (StringUtils.isBlank(user.getPhone()) && StringUtils.isBlank(user.getEmail())) {
            broken(user.setErrorMsg(USER_EMAIL_EXIST_ERROR.msg())
                    .setErrorCode(USER_EMAIL_EXIST_ERROR.code())
                    .setErrorField(UserConstant.FIELD_EMAIL));
        }

        if (UserBehaviorEventEnum.SIGN_UP_EMAIL.equals(user.getUserBehaviorEvent())) {
            PamirsUserDataChecker.checkEmailFormat(user);
            dataChecker.checkEmailIsNotExist(user, false);
        }
        if (UserBehaviorEventEnum.SIGN_UP_PHONE.equals(user.getUserBehaviorEvent())) {
            user.unsetEmail();
            PamirsUserDataChecker.checkPhoneFormat(user);
            dataChecker.checkPhoneIsNotExist(user, false);
        }
        PamirsUserDataChecker.checkPasswordFormat(user);
        if (UserBehaviorEventEnum.SIGN_UP_PHONE.equals(user.getUserBehaviorEvent())) {
            smsVerificationCodeService.ensureVerificationCode(user, SMSTemplateTypeEnum.SIGN_UP.value());
        }
        if (UserBehaviorEventEnum.SIGN_UP_EMAIL.equals(user.getUserBehaviorEvent())) {
            userEmailConfirmationService.ensureEmailConfirmation(user, SMSTemplateTypeEnum.SIGN_UP.value());
        }

        if (user.getBroken()) {
            log.error("Register user exception: [{}]", user.getErrorMsg());
        }
        return user;
    }

    public PamirsUserTransient forgetPassword(PamirsUserTransient user) throws PamirsException {
        UserBehaviorEventEnum behaviorEvent = user.getUserBehaviorEvent();
        if (null == behaviorEvent || MODIFY_PASSWORD_BYPHONE_SEND_CODE.equals(behaviorEvent)) {
            checkPhoneFormat(user);
            PamirsUser rUser = dataChecker.checkPhoneExist(user);
            PamirsUserDataChecker.checkPasswordFormat(user);
            PamirsUserDataChecker.checkConfirmPasswordAndPassword(user);
            smsVerificationCodeService.ensureVerificationCode(user, SMSTemplateTypeEnum.CHANGE_PWD.value());
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
            BeanDefinitionUtils.getBean(PasswordService.class).unsafeChangePassword(rUser.getId(), user.getPassword());

        } else if (MODIFY_PASSWORD_SEND_RESET_EMAIL.equals(behaviorEvent)) {
            checkEmailFormat(user);
            PamirsUser rUser = dataChecker.checkEmailIsExist(user);
            PamirsUserDataChecker.checkPasswordFormat(user);
            PamirsUserDataChecker.checkConfirmPasswordAndPassword(user);
            userEmailConfirmationService.ensureEmailConfirmation(user, SMSTemplateTypeEnum.CHANGE_PWD.value());
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
            BeanDefinitionUtils.getBean(PasswordService.class).unsafeChangePassword(rUser.getId(), user.getPassword());
        }
        return success(user);
    }

    public PamirsUserTransient modifyCurrentUserPassword(PamirsUserTransient user) {
        Spider.getLoader(PasswordBehaviorService.class).getOrderedExtensions().get(0).modifyPassword(user);
        logoutByCookie(user, Boolean.TRUE);
        PamirsSession.getMessageHub()
                .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                        .setMessage(I18nUtils.getMessage("pamirs-user-core-base.UserBehaviorBaseService.passwordChangedSuccessfully")));
        return success(user);
    }

    public PamirsUserTransient modifyMobilePassword(PamirsUserTransient user) {
        Spider.getLoader(PasswordBehaviorService.class).getOrderedExtensions().get(0).modifyPassword(user);
        logoutByCookie(user, Boolean.TRUE);
        PamirsSession.getMessageHub()
                .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                        .setMessage(I18nUtils.getMessage("pamirs-user-core-base.UserBehaviorBaseService.passwordChangedSuccessfully")));
        return success(user);
    }

    public PamirsUserTransient firstResetPassword(PamirsUserTransient user) {
        Spider.getLoader(PasswordBehaviorService.class).getOrderedExtensions().get(0).firstResetPassword(user);
        PamirsSession.getMessageHub()
                .msg(Message.init().setLevel(InformationLevelEnum.SUCCESS)
                        .setMessage(I18nUtils.getMessage("pamirs-user-core-base.UserBehaviorBaseService.initialPasswordChangedSuccessf")));
        user.setRedirect(UserLoginHelper.getRedirectMenu());
        return success(user);
    }

    //@Function(openLevel = {LOCAL, REMOTE})
    public PamirsUser createUserExtpoint(PamirsUserTransient user) {
        PamirsUser rUser = initSignUpUser(user);
        rUser.create();
        return rUser;
    }

    protected PamirsUser initSignUpUser(PamirsUserTransient user) {
        PamirsUser rUser = new PamirsUser();
        rUser.setCurrency(InternationalInfo.getDefaultCurrency());
        rUser.setTimeZoneType(TimeZoneTypeEnum.getEnumByValue(InternationalInfo.getDefaultTimeZone()));
        rUser.setLang(InternationalInfo.getDefaultLang());
        rUser.setLogin(user.getLogin());
        rUser.setNickname(null == user.getNickname() ? user.getLogin() : user.getNickname());
        rUser.setName(null == user.getName() ? user.getLogin() : user.getName());
        rUser.setPassword(user.getPassword());//加密
        rUser.setActive(Boolean.TRUE);
        rUser.setEmail(user.getEmail());
        rUser.setPhone(user.getPhone());
        rUser.setRealname(user.getRealname());
        rUser.setGender(user.getGender());
        rUser.setBirthday(user.getBirthday());
        rUser.setSignUpType(UserSignUpType.BYSELF);
        return rUser;
    }

    public PamirsUserDTO currentUser() {
        String sessionId = PamirsSession.getSessionApi().getSessionId();
        if (StringUtils.isNotBlank(sessionId)) {
            PamirsUserDTO user = UserCache.get(sessionId);
            if (!ObjectUtils.isEmpty(user)) {
                return user.setPassword(null);
            }
        }
        return null;
    }

    public PamirsUserTransient logoutWithoutRedirect(PamirsUserTransient user) throws PamirsException {
        //session处理
        UserCache.logout();
        return user;
    }

    public PamirsUserTransient loginWithoutRedirect(PamirsUserTransient user) throws PamirsException {
        user.setNeedRedirect(Boolean.FALSE);
        return loginByCookie(user);
    }

    public PamirsUserTransient logoutByToken(PamirsUserTransient user, Boolean redirect) {
        return null;
    }

    public PamirsUserTransient loginByVerificationCodeWithoutRedirect(PamirsUserTransient user) throws PamirsException {
        checkPhoneFormat(user);
        PamirsUser rUser = dataChecker.checkPhoneExist(user);
        smsVerificationCodeService.ensureVerificationCode(user, SMSTemplateTypeEnum.SIGN_IN.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        //登录
        UserCookieLogin userCookieLogin = (UserCookieLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.COOKIE.value());
        // 7天免登录
        Integer expireTime = UserConstant.USER_EXPIRE_TIME;
        if (Boolean.TRUE.equals(user.getAutoLogin())) {
            expireTime = 3600 * 24 * 7;
        }
        userCookieLogin.login(rUser);
        return success(user);
    }

    private void checkUserInfo(PamirsUserTransient user) {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_SESSION_ID_ISNULL.msg())
                    .setErrorCode(USER_SESSION_ID_ISNULL.code())
                    .setErrorField("login"));
            return;
        }
        PamirsUser currentUser = new PamirsUser().queryById(userId);
        if (currentUser == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_MODIFY_NOT_EXISTED_ERROR.msg())
                    .setErrorCode(USER_MODIFY_NOT_EXISTED_ERROR.code())
                    .setErrorField("login"));
        }
    }

    public PamirsUserTransient sendSmsVerificationCode(PamirsUserTransient user) {
        if (log.isDebugEnabled()) {
            log.debug("SMS send input params: {}", JsonUtils.toJSONString(user));
        }
        String msgType = user.getMsgType();
        UserBehaviorEventEnum userBehaviorEvent = user.getUserBehaviorEvent();
        if (userBehaviorEvent == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_PHONE_MISS_EVENT_IS_NULL_ERROR.msg())
                    .setErrorCode(USER_PHONE_MISS_EVENT_IS_NULL_ERROR.code())
                    .setErrorField("email"));
            return user;
        }
        boolean isNew = false;
        switch (userBehaviorEvent) {
            case MODIFY_PASSWORD_BYPHONE_SEND_CODE:
                msgType = SMSTemplateTypeEnum.CHANGE_PWD.value();
                break;
            case MODIFY_PHONE_OLD_PHONE_CODE:
                checkUserInfo(user);
                msgType = SMSTemplateTypeEnum.CHANGE_PHONE.value();
                break;
            case MODIFY_PHONE_NEW_PHONE_CODE:
                msgType = SMSTemplateTypeEnum.NEW_PHONE.value();
                if (user.getNewPhone() == null) user.setNewPhone(user.getPhone());
                isNew = true;
                break;
            case MODIFY_EMAIL_SEND_PHONE_CODE:
                checkUserInfo(user);
                msgType = SMSTemplateTypeEnum.CHANGE_EMAIL.value();
                break;
            case SEND_LOGIN_BY_PHONE_CODE:
                msgType = SMSTemplateTypeEnum.SIGN_IN.value();
                break;
            case SIGN_UP_PHONE:
                msgType = SMSTemplateTypeEnum.SIGN_UP.value();
                isNew = true;
                if (user.getNewPhone() == null) user.setNewPhone(user.getPhone());
                break;
            case ADD_CORP_SEND_PHONE_CODE:
                isNew = true;
                if (user.getNewPhone() == null) user.setNewPhone(user.getPhone());
                msgType = SMSTemplateTypeEnum.ADD_CORP.value();
                break;
        }

        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        checkPhoneFormat(user);
        if (isNew) {
            dataChecker.checkPhoneIsNotExist(user, true);
        } else {
            dataChecker.checkPhoneExist(user);
        }

        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        smsVerificationCodeService.pushPhoneVerificationCodeBoth(user, msgType, isNew);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    public PamirsUserTransient checkVerificationCode(PamirsUserTransient user) {
        checkUserInfo(user);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        UserBehaviorEventEnum userBehaviorEvent = user.getUserBehaviorEvent();
        if (userBehaviorEvent == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_PHONE_MISS_EVENT_IS_NULL_ERROR.msg())
                    .setErrorCode(USER_PHONE_MISS_EVENT_IS_NULL_ERROR.code())
                    .setErrorField("email"));
            return user;
        }
        switch (userBehaviorEvent) {
            case MODIFY_PASSWORD_BYPHONE_SEND_CODE:
                smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.CHANGE_PWD.value(), false, true);
                break;
            case MODIFY_PHONE_OLD_PHONE_CODE:
                smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.CHANGE_PHONE.value(), false, true);
                break;
            case MODIFY_PHONE_NEW_PHONE_CODE:
                if (user.getNewPhone() == null) user.setNewPhone(user.getPhone());
                smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.NEW_PHONE.value(), true, true);
                break;
            case MODIFY_EMAIL_SEND_PHONE_CODE:
                smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.CHANGE_EMAIL.value(), false, true);
                break;
            case SEND_LOGIN_BY_PHONE_CODE:
                smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.SIGN_IN.value(), false, true);
                break;
            case ADD_CORP_SEND_PHONE_CODE:
                if (user.getNewPhone() == null) user.setNewPhone(user.getPhone());
                smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.ADD_CORP.value(), true, true);
                break;

            case MODIFY_PASSWORD_SEND_RESET_EMAIL:
                userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.CHANGE_PWD.value(), false, true);
                break;
            case MODIFY_EMAIL_SEND_OLD_EMAIL:
                userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.CHANGE_EMAIL.value(), false, true);
                break;
            case MODIFY_EMAIL_SEND_NEW_EMAIL:
                if (user.getNewEmail() == null) user.setNewEmail(user.getEmail());
                userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.NEW_EMAIL.value(), true, true);
                break;
            case MODIFY_PHONE_SEND_EMAIL:
                userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.CHANGE_PHONE.value(), false, true);
                break;
            case SEND_LOGIN_BY_EMAIL_CODE:
                userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.SIGN_IN.value(), false, true);
                break;
            case ADD_CORP_SEND_EMAIL_CODE:
                if (user.getNewEmail() == null) user.setNewEmail(user.getEmail());
                userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.ADD_CORP.value(), true, true);
                break;
        }

        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return user;
    }

    public PamirsUserTransient modifyPhone(PamirsUserTransient user) {
        checkUserInfo(user);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        PamirsUser rUser = new PamirsUser();

        if (StringUtils.isBlank(user.getPhone()) && StringUtils.isNotBlank(user.getEmail())) {
            //原手机不存在、校验邮箱
            checkEmailFormat(user);
            rUser = dataChecker.checkEmailIsExist(user);
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
            userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.CHANGE_PHONE.value(), false, false);
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
        } else if (StringUtils.isNotBlank(user.getPhone())) {
            //原手机存在
            checkPhoneFormat(user);
            rUser = dataChecker.checkPhoneExist(user);
            smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.CHANGE_PHONE.value(), false, false);
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
        } else {
            UserServiceUtils.broken(user.setErrorMsg(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.msg())
                    .setErrorCode(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.code())
                    .setErrorField("phone"));
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
        }

        if (rUser == null) return user;
        if (StringUtils.isBlank(rUser.getPhone()) && StringUtils.isBlank(rUser.getEmail())) {
            UserServiceUtils.broken(user.setErrorMsg(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.msg())
                    .setErrorCode(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.code())
                    .setErrorField("phone"));
        }
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        dataChecker.checkPhoneIsNotExist(user, true);
        smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.NEW_PHONE.value(), true, false);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        rUser.setPhone(user.getNewPhone());
        rUser.updateById();
        return success(user);
    }

    public PamirsUserTransient sendConfirmEmail(PamirsUserTransient user) {
        checkEmailFormat(user);

        String msgType = user.getMsgType();
        UserBehaviorEventEnum userBehaviorEvent = user.getUserBehaviorEvent();
        if (userBehaviorEvent == null) {
            UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                    .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                    .setErrorField("email"));
            return user;
        }
        boolean isNew = false;
        switch (userBehaviorEvent) {
            case MODIFY_PASSWORD_SEND_RESET_EMAIL:
                msgType = SMSTemplateTypeEnum.CHANGE_PWD.value();
                break;
            case MODIFY_EMAIL_SEND_OLD_EMAIL:
                checkUserInfo(user);
                msgType = SMSTemplateTypeEnum.CHANGE_EMAIL.value();
                break;
            case MODIFY_EMAIL_SEND_NEW_EMAIL:
                checkUserInfo(user);
                isNew = true;
                msgType = SMSTemplateTypeEnum.NEW_EMAIL.value();
                if (user.getNewEmail() == null) user.setNewEmail(user.getEmail());
                break;
            case MODIFY_PHONE_SEND_EMAIL:
                msgType = SMSTemplateTypeEnum.CHANGE_PHONE.value();
                break;
            case SEND_LOGIN_BY_EMAIL_CODE:
                msgType = SMSTemplateTypeEnum.SIGN_IN.value();
                break;
            case SIGN_UP_EMAIL:
                isNew = true;
                msgType = SMSTemplateTypeEnum.SIGN_UP.value();
                if (user.getNewEmail() == null) user.setNewEmail(user.getEmail());
                break;
            case ADD_CORP_SEND_EMAIL_CODE:
                isNew = true;
                if (user.getNewEmail() == null) user.setNewEmail(user.getEmail());
                msgType = SMSTemplateTypeEnum.ADD_CORP.value();
                break;
        }
        if (isNew) {
            dataChecker.checkEmailIsNotExist(user, true);
        } else {
            PamirsUser rUser = dataChecker.checkEmailIsExist(user, isNew);
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
            user.setNickname(rUser.getNickname());
            user.setRealname(rUser.getRealname());
        }
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        userEmailConfirmationService.sendEmailConfirmationBoth(user, msgType, isNew);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    public PamirsUserTransient modifyEmail(PamirsUserTransient user) {
        checkUserInfo(user);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        PamirsUser rUser = new PamirsUser().queryById(PamirsSession.getUserId());

        if (StringUtils.isBlank(rUser.getEmail()) && StringUtils.isNotBlank(user.getNewEmail())) {
            //原邮箱不存在、校验手机号
            user.setPhone(rUser.getPhone());
            rUser = dataChecker.checkPhoneExist(user);
            smsVerificationCodeService.ensureVerificationCodeBoth(user, SMSTemplateTypeEnum.NEW_EMAIL.value(), false, false);
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
        } else if (StringUtils.isNotBlank(rUser.getEmail())) {
            //原邮箱存在
            user.setEmail(rUser.getEmail());
            rUser = dataChecker.checkEmailIsExist(user);
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
            userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.CHANGE_EMAIL.value(), false, false);
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
        } else {
            UserServiceUtils.broken(user.setErrorMsg(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.msg())
                    .setErrorCode(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.code())
                    .setErrorField("phone"));
            if (user.getBroken()) {
                log.error("Broken: [{}]", user.getErrorMsg());
                return user;
            }
        }

        if (rUser == null) return user;
        if (StringUtils.isBlank(rUser.getPhone()) && StringUtils.isBlank(rUser.getEmail())) {
            UserServiceUtils.broken(user.setErrorMsg(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.msg())
                    .setErrorCode(USER_MODIFY_PHONE_EMAIL_BOTH_EMPTY.code())
                    .setErrorField("phone"));
        }
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        dataChecker.checkEmailIsNotExist(user, true);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        rUser.setEmail(user.getNewEmail());
        rUser.updateById();
        return success(user);
    }
}

