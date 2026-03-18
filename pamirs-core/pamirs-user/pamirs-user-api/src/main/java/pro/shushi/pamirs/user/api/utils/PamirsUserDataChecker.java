package pro.shushi.pamirs.user.api.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.PasswordService;
import pro.shushi.pamirs.user.api.spi.UserPatternCheckApi;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;

/**
 * 用户数据格式校验
 *
 * @author shier
 * date  2022/5/26 下午12:26
 */
@Slf4j
public class PamirsUserDataChecker {

    /**
     * 校验登录账号
     * 校验规则： 只能包含数字/字母/下划线，范围为1-20
     *
     * @param login
     * @return
     */
    public static boolean checkLogin(String login) {
        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        return checkApi.checkLogin(login);
    }

    /**
     * 校验初始化密码
     * 校验规则： 初始化密码长度>1
     *
     * @param initPassword 初始化密码
     * @return
     */
    public static boolean checkInitPassword(String initPassword) {
        return StringUtils.isNotBlank(initPassword) && initPassword.length() > 0;
    }

    /**
     * 检查手机号格式是否正确
     *
     * @param userTransient
     */
    public static void checkPhoneFormat(PamirsUserTransient userTransient) {
        if (userTransient.getBroken()) return;
        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        String phone = userTransient.getPhone();
        String phoneCode = UserServiceUtils.getPhoneCode(userTransient);
        if (null == phone || !checkApi.checkPhone(phone, phoneCode)) {
            log.error("{}", UserExpEnumerate.USER_PHONE_CODE_NOT_RIGHT_ERROR.msg());
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_PHONE_OR_VERIFICATION_CODE_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_PHONE_CODE_NOT_RIGHT_ERROR.code())
                    .setErrorField("phone"));
        }
    }

    /**
     * 检查密码格式
     *
     * @param userTransient
     * @returncd ggit statusgit logq
     */
    public static void checkPasswordFormat(PamirsUserTransient userTransient) {
        if (userTransient.getBroken()) return;
        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        if (null == userTransient.getPassword() || !checkApi.checkPassword(userTransient.getPassword())) {
            log.error(USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR.msg())
                    .setErrorCode(USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR.code())
                    .setErrorField("password"));

        }
    }

    /**
     * 检查密码格式
     *
     * @param pwd
     * @param login
     * @return
     */
    public static void checkPasswordFormat(String pwd, String login) {
        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        if (null == pwd || !checkApi.checkPassword(pwd)) {
            //密码 验证码错误
            log.info("{}, user input data is {}, user account is {}", USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR.msg(), pwd, login);
            throw PamirsException.construct(USER_PASSWORD_SIMPLE_OR_SIZE_NOT_MATCH_ERROR).errThrow();
        }
    }

    /**
     * 检查 密码以及确认密码
     *
     * @param userTransient
     */
    public static void checkConfirmPasswordAndPassword(PamirsUserTransient userTransient) {
        if (userTransient.getBroken()) return;
        String password = userTransient.getPassword();
        String confirmPassword = userTransient.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            log.error("{}", USER_DO_NOT_MATCH_PASSWORD_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_DO_NOT_MATCH_PASSWORD_ERROR.msg())
                    .setErrorCode(USER_DO_NOT_MATCH_PASSWORD_ERROR.code())
                    .setErrorField("confirmPassword"));
        }
    }

    public static void checkOldPasswordAndPassword(PamirsUserTransient userTransient, PamirsUser rUser) {
        if (userTransient.getBroken() || null == rUser) return;
        //如果是初始化密码，用初始化密码比较
        if (!PasswordEncoder.matches(userTransient.getRawPassword(), null == rUser.getPassword() ? rUser.getInitialPassword() : rUser.getPassword())) {
            log.error("{}", USER_OLD_NEW_PASSWORD_RAW_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_OLD_NEW_PASSWORD_RAW_ERROR.msg())
                    .setErrorCode(USER_OLD_NEW_PASSWORD_RAW_ERROR.code())
                    .setErrorField("rawPassword"));
        }
    }

    /**
     * 检查邮箱格式是否正确
     *
     * @param
     */
    public static void checkEmailFormat(PamirsUserTransient userTransient) {
        if (userTransient.getBroken()) return;
        if (!UserBehaviorEventEnum.SIGN_UP_PHONE.equals(userTransient.getUserBehaviorEvent())) return;
        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        if (StringUtils.isBlank(userTransient.getEmail()) || !checkApi.checkEmail(userTransient.getEmail())) {
            log.error("{}", USER_EMAIL_NOT_EXISTED_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_EMAIL_NOT_EXISTED_ERROR.msg())
                    .setErrorCode(USER_EMAIL_NOT_EXISTED_ERROR.code())
                    .setErrorField("email"));
        }
    }

    /**
     * 密码登录时检查
     *
     * @param userTransient
     */
    public static void checkPwdWithDbPwd(PamirsUserTransient userTransient, PamirsUser resourceUser) {
        if (userTransient.getBroken() || null == resourceUser) {
            return;
        }
        if (!BeanDefinitionUtils.getBean(PasswordService.class).checkUserPassword(resourceUser.getId(), userTransient.getPassword())) {
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.code())
                    .setErrorField(UserConstant.FIELD_PASSWORD));
        }
    }

}
