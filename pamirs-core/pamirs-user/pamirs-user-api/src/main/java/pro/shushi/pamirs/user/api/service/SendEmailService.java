package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author Wuxin
 * @Date 2024/7/12
 * @since 1.0
 */
public interface SendEmailService {

    /**
     * 发送邮件验证码, 该验证码用于邮箱登录
     *
     * @param user user
     */
    PamirsUserTransient sendEmailVerificationCodeForLogin(PamirsUserTransient user);

    /**
     * 发送邮件验证码,发送重置密码邮件
     *
     * @param user user
     * @return
     */
    PamirsUserTransient sendPasswordResetEmail(PamirsUserTransient user);

    /**
     * 发送邮件验证码,修改邮箱-发送原邮箱确认邮件
     *
     * @param user user
     */
    PamirsUserTransient sendOriginalEmailConfirmationEmail(PamirsUserTransient user);

    /**
     * 发送邮件验证码,修改邮箱-发送新邮箱确认邮件
     *
     * @param user user
     */
    PamirsUserTransient sendNewEmailConfirmationEmail(PamirsUserTransient user);

    /**
     * 发送邮件验证码,发送修改手机号-邮箱验证码
     *
     * @param user user
     */
    PamirsUserTransient sendPhoneNumberEmailVerificationCodeForChange(PamirsUserTransient user);

    /**
     * 发送邮件验证码,邮箱注册
     *
     * @param user user
     */
    PamirsUserTransient registerByEmail(PamirsUserTransient user);

    /**
     * 发送邮件验证码,邮箱注册
     *
     * @param user user
     */
    PamirsUserTransient sendTeamJoinEmailVerificationCode(PamirsUserTransient user);
}
