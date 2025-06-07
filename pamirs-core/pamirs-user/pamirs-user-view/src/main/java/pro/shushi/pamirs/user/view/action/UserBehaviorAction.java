package pro.shushi.pamirs.user.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.crypto.annotation.NeedDecrypt;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.SendEmailService;
import pro.shushi.pamirs.user.api.service.UserBehaviorService;

import java.util.Optional;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * 用户行为服务
 *
 * @author shier
 * date 2020/4/11
 */
@Base
@Component
@Model.model(PamirsUserTransient.MODEL_MODEL)
public class UserBehaviorAction {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Autowired
    private SendEmailService sendEmailService;


    @Action(displayName = "登录", summary = "手机号和验证码登录,使用TOKEN的方式")
    @Function(openLevel = {LOCAL, REMOTE, API})
    public PamirsUserTransient tokenLoginByVerificationCode(PamirsUserTransient user) {
        userBehaviorService.loginBySmsVerificationCodeWithToken(user, Boolean.TRUE);
        return user;
    }

    @Action(displayName = "登录", summary = "使用token的登录方式")
    @Function(openLevel = {LOCAL, REMOTE, API})
    public PamirsUserTransient tokenLoginSimple(PamirsUserTransient user) {
        return userBehaviorService.loginByToken(user);
    }

    @Action(displayName = "登录", summary = "使用cookie的登录方式", contextType = ActionContextTypeEnum.CONTEXT_FREE)
    @NeedDecrypt
    @Function(openLevel = {LOCAL, REMOTE, API})
    public PamirsUserTransient login(PamirsUserTransient user) {
        return userBehaviorService.loginByCookie(user);
    }

    @Action(displayName = "登录", summary = "使用cookie的登录方式，登录多租户平台，不会自动跳转", contextType = ActionContextTypeEnum.CONTEXT_FREE)
    @NeedDecrypt
    @Function(openLevel = {LOCAL, REMOTE, API})
    public PamirsUserTransient loginPlatform(PamirsUserTransient user) {
        return userBehaviorService.loginWithoutRedirect(user);
    }

    @Action(displayName = "微信小程序手机号", summary = "微信小程序手机号", contextType = ActionContextTypeEnum.CONTEXT_FREE)
    public PamirsUserTransient loginMAPhone(PamirsUserTransient user) {
        return userBehaviorService.loginMAPhone(user);
    }

    @Action(displayName = "小程序登录", summary = "小程序登录", contextType = ActionContextTypeEnum.CONTEXT_FREE)
    public PamirsUserTransient loginByMA(PamirsUserTransient user) {
        return userBehaviorService.loginByMA(user);
    }

    @Action(displayName = "发送短信验证码", summary = "该验证码用于手机号和验证码登录")
    public PamirsUserTransient loginVerificationCode(PamirsUserTransient user) {
        return userBehaviorService.sendLoginSmsVerificationCode(user);
    }

    @Action(displayName = "登录", summary = "手机号和验证码登录,使用Cookie的方式")
    public PamirsUserTransient loginByVerificationCode(PamirsUserTransient user) {
        UserBehaviorEventEnum userBehaviorEvent = Optional.ofNullable(user.getUserBehaviorEvent()).orElse(UserBehaviorEventEnum.LOGIN_BY_PHONE_CODE);
        if (UserBehaviorEventEnum.LOGIN_BY_PHONE_CODE.equals(userBehaviorEvent)) {
            return userBehaviorService.loginBySmsVerificationCodeWithCookie(user, user.getNeedRedirect());
        } else if (UserBehaviorEventEnum.LOGIN_BY_EMAIL_CODE.equals(userBehaviorEvent)) {
            return userBehaviorService.loginByEmailVerificationCodeWithCookie(user, user.getNeedRedirect());
        }
        return userBehaviorService.loginBySmsVerificationCodeWithCookie(user, user.getNeedRedirect());
    }

    @Action(displayName = "退出登录")
    public PamirsUserTransient logout(PamirsUserTransient user) {
        return userBehaviorService.logoutByCookie(user, Boolean.TRUE);
    }

    @Action(displayName = "注册验证码")
    public PamirsUserTransient signUpVerificationCode(PamirsUserTransient user) {
        return userBehaviorService.sendSignUpSmsVerificationCode(user);
    }

    @Action(displayName = "注册")
    public PamirsUserTransient signUp(PamirsUserTransient user) {
        return userBehaviorService.signUp(user);
    }

    @Action(displayName = "注册", summary = "为移动注册使用")
    public PamirsUserTransient signUpMobile(PamirsUserTransient user) {
        return userBehaviorService.signUpMobile(user);
    }

    @Action(displayName = "忘记密码")
    @NeedDecrypt
    public PamirsUserTransient forgetPassword(PamirsUserTransient user) {
        return userBehaviorService.forgetPassword(user);
    }

    @Action(displayName = "修改密码", contextType = ActionContextTypeEnum.SINGLE, bindingType = ViewTypeEnum.FORM)
    @NeedDecrypt
    public PamirsUserTransient modifyCurrentUserPassword(PamirsUserTransient user) {
        return userBehaviorService.modifyCurrentUserPassword(user);
    }

    @Action(displayName = "移动端修改密码", contextType = ActionContextTypeEnum.SINGLE, bindingType = ViewTypeEnum.FORM)
    @NeedDecrypt
    public PamirsUserTransient modifyMobilePassword(PamirsUserTransient user) {
        return userBehaviorService.modifyMobilePassword(user);
    }

    @Action(displayName = "发送短信验证码", summary = "该验证码用手机验证码修改密码验证码")
    public PamirsUserTransient sendSmsVerificationCode(PamirsUserTransient user) {
        return userBehaviorService.sendSmsVerificationCode(user);
    }

    @Action(displayName = "校验验证码", summary = "该验证码用手机邮箱验证码是否正确")
    public PamirsUserTransient checkVerificationCode(PamirsUserTransient user) {
        return userBehaviorService.checkVerificationCode(user);
    }

    @Action(displayName = "修改手机号")
    public PamirsUserTransient modifyPhone(PamirsUserTransient user) {
        return userBehaviorService.modifyPhone(user);
    }

    @Action(displayName = "修改邮箱")
    public PamirsUserTransient modifyEmail(PamirsUserTransient user) {
        return userBehaviorService.modifyEmail(user);
    }

    @Action(displayName = "发送邮件", summary = "修改密码的邮件")
    @Deprecated
    public PamirsUserTransient sendConfirmEmail(PamirsUserTransient user) {
        return userBehaviorService.sendConfirmEmail(user);
    }

    @Action(displayName = "发送邮件验证码", summary = "该验证码用于邮件验证码登录")
    public PamirsUserTransient sendEmailVerificationCodeForLogin(PamirsUserTransient user) {
        return sendEmailService.sendEmailVerificationCodeForLogin(user);
    }

    @Action(displayName = "发送邮件验证码", summary = "发送重置密码邮件")
    public PamirsUserTransient sendPasswordResetEmail(PamirsUserTransient user) {
        return sendEmailService.sendPasswordResetEmail(user);
    }

    @Action(displayName = "发送邮件验证码", summary = "修改邮箱-发送原邮箱确认邮件")
    public PamirsUserTransient sendOriginalEmailConfirmationEmail(PamirsUserTransient user) {
        return sendEmailService.sendOriginalEmailConfirmationEmail(user);
    }

    @Action(displayName = "发送邮件验证码", summary = "修改邮箱-发送新邮箱确认邮件")
    public PamirsUserTransient sendNewEmailConfirmationEmail(PamirsUserTransient user) {
        return sendEmailService.sendNewEmailConfirmationEmail(user);
    }

    @Action(displayName = "发送邮件验证码", summary = "发送修改手机号-邮箱验证码")
    public PamirsUserTransient sendPhoneNumberEmailVerificationCodeForChange(PamirsUserTransient user) {
        return sendEmailService.sendPhoneNumberEmailVerificationCodeForChange(user);
    }

    @Action(displayName = "发送邮件验证码", summary = "邮箱注册")
    public PamirsUserTransient registerByEmail(PamirsUserTransient user) {
        return sendEmailService.registerByEmail(user);
    }

    @Action(displayName = "发送邮件验证码", summary = "加入团队邮件验证码")
    public PamirsUserTransient sendTeamJoinEmailVerificationCode(PamirsUserTransient user) {
        return sendEmailService.sendTeamJoinEmailVerificationCode(user);
    }

    public PamirsUserTransient logoutWithoutRedirect(PamirsUserTransient user) {
        //session处理
        UserCache.logout();
        return user;
    }

    @Action(displayName = "修改初始密码", summary = "首次登录修改初始密码", contextType = ActionContextTypeEnum.CONTEXT_FREE)
    @NeedDecrypt
    public PamirsUserTransient firstResetPassword(PamirsUserTransient user) {
        user = userBehaviorService.firstResetPassword(user);
        return user;
    }

}
