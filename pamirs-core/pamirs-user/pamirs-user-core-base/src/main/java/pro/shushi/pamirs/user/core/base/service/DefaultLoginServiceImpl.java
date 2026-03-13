package pro.shushi.pamirs.user.core.base.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.*;
import pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker;

import static pro.shushi.pamirs.user.core.base.util.UserLoginHelper.success;

/**
 * @author wuer
 * date  2021/5/8 5:20 下午
 */

@Slf4j
@Component
@Fun(DefaultLoginService.FUN_NAMESPACE)
public class DefaultLoginServiceImpl implements DefaultLoginService {

    @Autowired
    private UserBehaviorService userBehaviorService;
    @Autowired
    private UserService userService;
    @Autowired
    private IUserDataChecker dataChecker;
    @Autowired
    UserSmsVerificationCodeService smsVerificationCodeService;

    @Autowired
    private PasswordService passwordService;

    @Override
    @Function
    public PamirsUserTransient fillSessionByPhone(PamirsUserTransient user) {
        if (null == user || StringUtils.isBlank(user.getPhone())) {
            log.error("Session injection error, user info is null");
            return user;
        }
        PamirsUser login = userService.queryByPhone(user.getPhone());
        if (null == login) {
            log.error("Session injection error, user not found by phone [{}]", user.getPhone());
            return user;
        }
        //登录
        userBehaviorService.loginByCookie(login);
        return user;
    }

    @Override
    @Function
    public PamirsUserTransient loginVerificationCode(PamirsUserTransient user) {
        return userBehaviorService.sendLoginSmsVerificationCode(user);
    }

    @Override
    @Function
    public PamirsUserTransient loginByVerificationCode(PamirsUserTransient user) {
        return userBehaviorService.loginBySmsVerificationCodeWithCookie(user, Boolean.FALSE);
    }

    @Override
    @Function
    public PamirsUserTransient logout(PamirsUserTransient user) {
        return userBehaviorService.logoutByCookie(user, Boolean.FALSE);
    }

    @Override
    @Function
    public PamirsUserTransient registerVerificationCode(PamirsUserTransient user) {
        PamirsUserDataChecker.checkPhoneFormat(user);
        smsVerificationCodeService.pushPhoneVerificationCode(user, SMSTemplateTypeEnum.SIGN_UP.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    @Function
    @Override
    public PamirsUserTransient forgetPassword(PamirsUserTransient user) {
        PamirsUserDataChecker.checkPhoneFormat(user);
        PamirsUser rUser = dataChecker.checkPhoneExist(user);
        PamirsUserDataChecker.checkConfirmPasswordAndPassword(user);
        ensureVerificationCode(user, SMSTemplateTypeEnum.CHANGE_PWD.value());
        if (user.getBroken() || null == rUser) {
            return user;
        }
        passwordService.unsafeChangePassword(rUser.getId(), user.getPassword());
        rUser.updateById();
        return success(user);
    }

    @Override
    @Function
    public PamirsUserTransient forgetPasswordVerificationCode(PamirsUserTransient user) {
        PamirsUserDataChecker.checkPhoneFormat(user);
        dataChecker.checkPhoneExist(user);
        smsVerificationCodeService.pushPhoneVerificationCode(user, SMSTemplateTypeEnum.CHANGE_PWD.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    @Override
    @Function
    public PamirsUserTransient sendVerificationCode(PamirsUserTransient user, String msgTypeValue) {
        SMSTemplateTypeEnum type = SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.class, msgTypeValue);
        if (null == type) {
            throw PamirsException.construct(UserExpEnumerate.USER_SMS_SMS_TEMPLATE_ISNOTEXISTED).errThrow();
        }
        PamirsUserDataChecker.checkPhoneFormat(user);
        smsVerificationCodeService.pushPhoneVerificationCode(user, type.value());
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    @Override
    @Function
    public PamirsUserTransient ensureVerificationCode(PamirsUserTransient user, String msgTypeValue) {
        smsVerificationCodeService.ensureVerificationCode(user, msgTypeValue);
        return user;
    }

}
