package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author wuer
 * date  2021/5/8 5:20 下午
 */
@Fun(DefaultLoginService.FUN_NAMESPACE)
public interface DefaultLoginService {
    String FUN_NAMESPACE = "user.DefaultLoginService";

    /**
     * 填充Cookie
     * @param user
     * @return
     */
    @Function
    PamirsUserTransient fillSessionByPhone(PamirsUserTransient user);

    /**
     * 登录发送验证码
     * @param user
     * @return
     */
    @Function
    PamirsUserTransient loginVerificationCode(PamirsUserTransient user);

    /**
     * 手机验证码登录
     * @param user
     * @return
     */
    @Function
    PamirsUserTransient loginByVerificationCode(PamirsUserTransient user);

    /**
     * 登出，清除cookie
     * @param user
     * @return
     */
    @Function
    PamirsUserTransient logout(PamirsUserTransient user);

    /**
     * 发送注册验证码
     * @param user 需要合法的手机号
     * @return
     */
    @Function
    PamirsUserTransient registerVerificationCode(PamirsUserTransient user);

    /**
     * 忘记密码 | 修改密码
     * @param user 需要合法的手机号
     * @return
     */
    @Function
    PamirsUserTransient forgetPassword(PamirsUserTransient user);
    /**
     * 忘记密码发送验证码
     * @param user 需要合法的手机号
     * @return
     */
    @Function
    PamirsUserTransient forgetPasswordVerificationCode(PamirsUserTransient user);
    /**
     * 发送验证码
     * @param user 需要合法的手机号
     * @param msgTypeValue msgTypeValue 取自'pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum'的枚举值
     * @return
     */
    @Function
    PamirsUserTransient sendVerificationCode(PamirsUserTransient user, String msgTypeValue);


    /**
     * 校验验证码
     * @param user 需要合法的手机号和验证码
     * @param msgTypeValue 取自'pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum'的枚举值
     * @return
     */
    @Function
    PamirsUserTransient ensureVerificationCode(PamirsUserTransient user, String msgTypeValue);


}
