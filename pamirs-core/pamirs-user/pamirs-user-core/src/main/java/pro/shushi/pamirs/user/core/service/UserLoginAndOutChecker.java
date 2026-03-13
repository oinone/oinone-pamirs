package pro.shushi.pamirs.user.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.sys.setting.api.SysSettingsService;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

import java.util.List;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.*;
import static pro.shushi.pamirs.user.api.utils.UserServiceUtils.broken;

/**
 * 用户登录的检查器
 */
@Component
@Slf4j
@Order(999)
public class UserLoginAndOutChecker implements IUserDataChecker {

    @Autowired(required = false)
    private SysSettingsService sysSettingsService;

    /**
     * 检查手机号是否有效
     *
     * @param userTransient
     * @return
     */
    @Override
    public PamirsUser checkPhoneExist(PamirsUserTransient userTransient) {
        return checkPhoneExist(userTransient, false);
    }

    @Override
    public PamirsUser checkPhoneOrLoginExist(PamirsUserTransient userTransient) {
        if (userTransient.getBroken()) {
            return null;
        }

        PamirsUser user = new PamirsUser().setPhone(userTransient.getPhone()).queryOne();
        if (null == user) {
            user = new PamirsUser().setLogin(userTransient.getLogin()).queryOne();
        }
        if (null == user) {
            log.error("{}", USER_PHONE_NO_SIGN_UP_ERROR.msg());
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_PHONE_OR_VERIFICATION_CODE_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_PHONE_OR_VERIFICATION_CODE_ERROR.code())
                    .setErrorField(UserConstant.FIELD_VERIFICATION_CODE));
            return null;
        }
        return checkActive(user, userTransient, UserExpEnumerate.USER_PHONE_OR_VERIFICATION_CODE_ERROR, UserConstant.FIELD_VERIFICATION_CODE);
    }

    @Override
    public void checkInviteCode(PamirsUserTransient userTransient) {
        /**
         if (Optional.ofNullable(sysSettingsService).map(SysSettingsService::sysSettings).map(SysSettings::getRegInvite).orElse(true)) {
         BeanDefinitionUtils.getBean(InvitationCodeManager.class).checkInvitationCode(userTransient.getInviteCode());
         }**/
        throw new UnsupportedOperationException("不支持");
    }

    @Override
    public PamirsUser checkPhoneExist(PamirsUserTransient userTransient, Boolean isNew) {
        String phone = isNew == null || !isNew ? userTransient.getPhone() : userTransient.getNewPhone();

        if (userTransient.getBroken()) return null;
        PamirsUser user = new PamirsUser().setPhone(phone).queryOne();
        if (null == user) {
            log.error("{}", USER_PHONE_NO_SIGN_UP_ERROR.msg());
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_PHONE_OR_VERIFICATION_CODE_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_PHONE_OR_VERIFICATION_CODE_ERROR.code())
                    .setErrorField(UserConstant.FIELD_VERIFICATION_CODE));
            return null;
        }
        return checkActive(user, userTransient, UserExpEnumerate.USER_PHONE_OR_VERIFICATION_CODE_ERROR, UserConstant.FIELD_VERIFICATION_CODE);
    }

    @Override
    public void checkLoginNameIsExist(PamirsUserTransient userTransient) {
        if (userTransient.getBroken()) return;
        String login = userTransient.getLogin();
        if (StringUtils.isBlank(login)) {
            log.error("{}", UserExpEnumerate.USER_NAME_NULL_ERROR.msg());
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.code())
                    .setErrorField(UserConstant.FIELD_PASSWORD));
            return;
        }
        List<PamirsUser> list = new PamirsUser().setLogin(login).queryList();
        if (CollectionUtils.isNotEmpty(list)) {
            log.error("{}", UserExpEnumerate.USER_NAME_EXIST_ERROR.msg());
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.code())
                    .setErrorField(UserConstant.FIELD_PASSWORD));
        }
    }

    @Override
    public PamirsUser checkLoginNameNotExist(PamirsUserTransient userTransient) {
        if (userTransient.getBroken()) return null;
        String login = userTransient.getLogin();
        if (StringUtils.isBlank(login)) {
            log.error("{}", UserExpEnumerate.USER_NAME_NULL_ERROR.msg());
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.code())
                    .setErrorField(UserConstant.FIELD_PASSWORD));
            return null;
        }
        PamirsUser user = new PamirsUser().setLogin(login).queryOne();
        if (null == user) {
            log.error("{}", UserExpEnumerate.USER_NAME_NOT_EXIST_ERROR.msg());
            broken(userTransient.setErrorMsg(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.msg())
                    .setErrorCode(UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR.code())
                    .setErrorField(UserConstant.FIELD_PASSWORD));
            return null;
        }
        return checkActive(user, userTransient, UserExpEnumerate.USER_USERNAME_OR_PASSWORD_ERROR, UserConstant.FIELD_PASSWORD);
    }

    protected PamirsUser checkActive(PamirsUser user, PamirsUserTransient userTransient, UserExpEnumerate expEnumerate, String errorField) {
        if (!Boolean.TRUE.equals(user.getActive())) {
            log.error("{}", UserExpEnumerate.USER_NOT_ACTIVE_ERROR.msg());
            broken(userTransient.setErrorMsg(expEnumerate.msg())
                    .setErrorCode(expEnumerate.code())
                    .setErrorField(errorField));
            return null;
        }
        return user;
    }

    @Override
    public PamirsUser checkEmailIsExist(PamirsUserTransient userTransient) {
        return checkEmailIsExist(userTransient, false);
    }

    @Override
    public PamirsUser checkEmailIsExist(PamirsUserTransient userTransient, Boolean isNew) {
        String email = isNew == null || !isNew ? userTransient.getEmail() : userTransient.getNewEmail();
        if (userTransient.getBroken()) {
            return null;
        }
        PamirsUser rUser = new PamirsUser().setEmail(email).queryOne();
        if (null == rUser) {
            broken(userTransient.setErrorMsg(USER_EMAIL_OR_VERIFICATION_CODE_ERROR.msg())
                    .setErrorCode(USER_EMAIL_OR_VERIFICATION_CODE_ERROR.code())
                    .setErrorField(UserConstant.FIELD_VERIFICATION_CODE));
            log.error("{}, Email login failed, user does not exist for current email, email is {}", userTransient.getEmail(), USER_EMAIL_NOT_EXISTED_ERROR.msg());
            return null;
        }
        return checkActive(rUser, userTransient, USER_EMAIL_OR_VERIFICATION_CODE_ERROR, UserConstant.FIELD_VERIFICATION_CODE);
    }

    /**
     * 检查邮箱是否已注册
     *
     * @param userTransient
     */
    @Override
    public void checkEmailExist(PamirsUserTransient userTransient) {
        checkEmailExist(userTransient, false);
    }

    /**
     * 检查邮箱是否已注册
     *
     * @param userTransient
     */
    @Override
    public void checkEmailExist(PamirsUserTransient userTransient, Boolean isNew) {
        String email = isNew == null || !isNew ? userTransient.getEmail() : userTransient.getNewEmail();
        if (userTransient.getBroken()) {
            return;
        }
        PamirsUser rUser = new PamirsUser().setEmail(email).queryOne();
        if (null != rUser) {
            log.error("{}", USER_EMAIL_EXIST_ERROR.msg());
            broken(userTransient.setErrorMsg(USER_EMAIL_EXIST_ERROR.msg())
                    .setErrorCode(USER_EMAIL_EXIST_ERROR.code())
                    .setErrorField("email"));
        }
    }

    /**
     * 检查手机号是否没被注册
     *
     * @param userTransient
     * @return
     */
    @Override
    public void checkPhoneIsNotExist(PamirsUserTransient userTransient, Boolean isNew) {
        String phone = isNew == null || !isNew ? userTransient.getPhone() : userTransient.getNewPhone();
        if (userTransient.getBroken()) {
            return;
        }
        if (new PamirsUser().setPhone(phone).count() > 0) {
            broken(userTransient.setErrorMsg(USER_PHONE_EXIST_ERROR.msg())
                    .setErrorCode(USER_PHONE_EXIST_ERROR.code())
                    .setErrorField(UserConstant.FIELD_LOGIN));
        }
    }

    /**
     * 检查邮箱是否没被注册
     *
     * @param userTransient
     * @return
     */
    @Override
    public void checkEmailIsNotExist(PamirsUserTransient userTransient, Boolean isNew) {
        String email = isNew == null || !isNew ? userTransient.getEmail() : userTransient.getNewEmail();
        if (userTransient.getBroken()) {
            return;
        }
        if (new PamirsUser().setPhone(email).count() > 0) {
            broken(userTransient.setErrorMsg(USER_EMAIL_EXIST_ERROR.msg())
                    .setErrorCode(USER_EMAIL_EXIST_ERROR.code())
                    .setErrorField(UserConstant.FIELD_EMAIL));
        }
    }
}
