package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * 用户行为的服务：比如 修改密码
 *
 * @author shier
 * date  2022/5/26 下午10:23
 */
public interface UserBehaviorService {


    /**
     * 用户登录
     *
     * @param user
     * @param redirect 是否重定向到登录后的首页
     */
    PamirsUserTransient loginBySmsVerificationCodeWithToken(PamirsUserTransient user, Boolean redirect);

    /**
     * 用户登录
     *
     * @param user
     * @param redirect 是否重定向到登录后的首页
     */
    PamirsUserTransient loginBySmsVerificationCodeWithCookie(PamirsUserTransient user, Boolean redirect);

    /**
     * 用户登录
     *
     * @param user
     * @param redirect 是否重定向到登录后的首页
     */
    PamirsUserTransient loginByEmailVerificationCodeWithCookie(PamirsUserTransient user, Boolean redirect);


    /**
     * 发送短信验证码
     *
     * @param user 手机号，手机验证码
     */
    PamirsUserTransient sendLoginSmsVerificationCode(PamirsUserTransient user);

    /**
     * 发送注册的短信验证码
     *
     * @param user 手机号，手机验证码
     */
    PamirsUserTransient sendSignUpSmsVerificationCode(PamirsUserTransient user);

    /**
     * 注册
     *
     * @param user
     */
    PamirsUserTransient signUp(PamirsUserTransient user);

    /**
     * 移动端注册(手机号,邮箱)
     *
     * @param user
     * @return
     */
    PamirsUserTransient signUpMobile(PamirsUserTransient user);

    /**
     * 用户登录
     *
     * @param user 手机号，手机验证码
     */
    PamirsUserTransient loginByToken(PamirsUserTransient user);

    /**
     * 微信小程序登录
     *
     * @param user
     * @return
     */
    PamirsUserTransient loginByMA(PamirsUserTransient user);

    PamirsUserTransient loginMAPhone(PamirsUserTransient user);

    /**
     * 用户登录
     *
     * @param user
     */
    PamirsUserTransient loginByCookie(PamirsUserTransient user);

    PamirsUserTransient loginByCookie(PamirsUser user);

    /**
     * 用户登录，不校验跳转
     *
     * @param user
     * @return
     */
    PamirsUserTransient loginWithoutRedirect(PamirsUserTransient user);

    PamirsUserTransient loginWithoutRedirect(PamirsUser user);

    /**
     * 退出登录
     *
     * @param user 手机号，手机验证码
     */
    PamirsUserTransient logoutByToken(PamirsUserTransient user, Boolean redirect);

    /**
     * 退出登录
     *
     * @param user
     */
    PamirsUserTransient logoutByCookie(PamirsUserTransient user, Boolean redirect);

    /**
     * 忘记密码
     *
     * @param user
     * @return
     */
    PamirsUserTransient forgetPassword(PamirsUserTransient user);

    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    PamirsUserTransient modifyCurrentUserPassword(PamirsUserTransient user);

    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    PamirsUserTransient modifyMobilePassword(PamirsUserTransient user);

    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    PamirsUserTransient firstResetPassword(PamirsUserTransient user);

    /**
     * 发送手机验证码
     *
     * @param user 手机号 msgType：SIGN_IN：登录确认 CHANGE_PWD：修改密码 CHANGE_PHONE：修改手机号 NEW_PHONE：新手机号
     * @return
     */
    PamirsUserTransient sendSmsVerificationCode(PamirsUserTransient user);


    /**
     * 校验验证码
     *
     * @param user
     * @return
     */
    PamirsUserTransient checkVerificationCode(PamirsUserTransient user);

    /**
     * 修改用户手机
     *
     * @param user
     * @return
     */
    PamirsUserTransient modifyPhone(PamirsUserTransient user);

    /**
     * 发送确认邮件
     *
     * @param user
     * @return
     */
    PamirsUserTransient sendConfirmEmail(PamirsUserTransient user);

    /**
     * 修改用户邮箱
     *
     * @param user
     * @return
     */
    PamirsUserTransient modifyEmail(PamirsUserTransient user);

    void clearSessionByUid(Long userId);

}
