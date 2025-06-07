package pro.shushi.pamirs.user.core.service;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.msg.ErrorExtension;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.user.api.checker.PamirsUserBehaviorChecker;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.login.IUserDataChecker;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.PasswordBehaviorService;
import pro.shushi.pamirs.user.api.service.PasswordService;
import pro.shushi.pamirs.user.api.service.UserEmailConfirmationService;
import pro.shushi.pamirs.user.api.service.UserSmsVerificationCodeService;

import java.util.ArrayList;
import java.util.Objects;

import static pro.shushi.pamirs.user.api.utils.PamirsUserDataChecker.*;

/**
 * 用户行为服务的实现类
 *
 * @author shier
 * date  2022/5/26 下午10:24
 */
@SPI.Service(PasswordBehaviorService.DEFAULT_SERVICE)
@Order
@Slf4j
public class DefaultPasswordBehaviorService implements PasswordBehaviorService {

    private PamirsUserBehaviorChecker userChecker;
    UserSmsVerificationCodeService smsVerificationCodeService = BeanDefinitionUtils.getBean(UserSmsVerificationCodeService.class);
    UserEmailConfirmationService userEmailConfirmationService = BeanDefinitionUtils.getBean(UserEmailConfirmationService.class);

    private IUserDataChecker dataChecker;

    /**
     * 页面调用修改密码服务
     *
     * @param user 需要传递的参数：旧密码/新密码/二次确定密码
     * @return
     */
    @Override
    public void modifyPassword(PamirsUserTransient user) {
        if (PamirsSession.getUserId() == null) {
            //用户未登录
            throw PamirsException.construct(UserExpEnumerate.USER_CHANGE_PWD_NO_USER_ERROR).errThrow();
        }

        PamirsUser existUserData = new PamirsUser().setId(PamirsSession.getUserId()).queryById();
        if (Objects.isNull(existUserData)) {
            throw PamirsException.construct(UserExpEnumerate.USER_NOT_EXIST_ERROR).errThrow();
        }

        String login = existUserData.getLogin();
        if (userChecker == null) {
            userChecker = BeanDefinitionUtils.getBean(PamirsUserBehaviorChecker.class);
        }
        if (dataChecker == null) {
            dataChecker = BeanDefinitionUtils.getBean(IUserDataChecker.class);
        }

        try {
            checkPasswordFormat(user.getPassword(), login);
        } catch (PamirsException e) {
            throw error(PamirsUserTransient::getPassword, e);
        }
        if (UserBehaviorEventEnum.MODIFY_PASSWORD_BY_PHONE.equals(user.getUserBehaviorEvent())) {
            try {
                checkPhoneFormat(user);
                PamirsUser rUser = dataChecker.checkPhoneExist(user);
                smsVerificationCodeService.ensureVerificationCode(user, SMSTemplateTypeEnum.CHANGE_PWD.value());
                if (user.getBroken()) {
                    throw PamirsException.construct(UserExpEnumerate.USER_VERIFICATION_CODE_NOT_MATCH_ERROR, user.getErrorMsg()).errThrow();
                }
            } catch (PamirsException e) {
                throw error(PamirsUserTransient::getPhone, e);
            }
        } else if (UserBehaviorEventEnum.MODIFY_PASSWORD_BY_EMAIL.equals(user.getUserBehaviorEvent())) {
            try {
                checkEmailFormat(user);
                PamirsUser rUser = dataChecker.checkEmailIsExist(user);
                userEmailConfirmationService.ensureEmailConfirmationBoth(user, SMSTemplateTypeEnum.CHANGE_PWD.value(), false, false);
                if (user.getBroken()) {
                    throw PamirsException.construct(UserExpEnumerate.USER_VERIFICATION_CODE_NOT_MATCH_ERROR, user.getErrorMsg()).errThrow();
                }
            } catch (PamirsException e) {
                throw error(PamirsUserTransient::getPhone, e);
            }
        } else {
            try {
                userChecker.verifyPicCodeRight(user.getPicCode(), login);
            } catch (PamirsException e) {
                throw error(PamirsUserTransient::getPicCode, e);
            }

            try {
                userChecker.verifyOldPasswordAndPassword(user.getRawPassword(), existUserData);
            } catch (PamirsException e) {
                throw error(PamirsUserTransient::getRawPassword, e);
            }
        }

        try {
            userChecker.verifyConfirmPasswordAndPassword(user.getPassword(), user.getConfirmPassword());
        } catch (PamirsException e) {
            throw error(PamirsUserTransient::getConfirmPassword, e);
        }

        BeanDefinitionUtils.getBean(PasswordService.class).changePassword(existUserData.getId(), user.getRawPassword(), user.getPassword());
    }

    @Override
    public void firstResetPassword(PamirsUserTransient user) {
        if (PamirsSession.getUserId() == null) {
            //用户未登录
            throw PamirsException.construct(UserExpEnumerate.USER_CHANGE_PWD_NO_USER_ERROR).errThrow();
        }

        PamirsUser existUserData = new PamirsUser().setId(PamirsSession.getUserId()).queryById();
        if (Objects.isNull(existUserData)) {
            throw PamirsException.construct(UserExpEnumerate.USER_NOT_EXIST_ERROR).errThrow();
        }

        String login = existUserData.getLogin();
        if (userChecker == null) {
            userChecker = BeanDefinitionUtils.getBean(PamirsUserBehaviorChecker.class);
        }
        if (dataChecker == null) {
            dataChecker = BeanDefinitionUtils.getBean(IUserDataChecker.class);
        }

        try {
            checkPasswordFormat(user.getPassword(), login);
        } catch (PamirsException e) {
            throw error(PamirsUserTransient::getPassword, e);
        }
//        try {
//            userChecker.verifyPicCodeRight(user.getPicCode(), login);
//        } catch (PamirsException e) {
//            throw error(PamirsUserTransient::getPicCode, e);
//        }

//        try {
//            userChecker.verifyOldPasswordAndPassword(user.getRawPassword(), existUserData);
//        } catch (PamirsException e) {
//            throw error(PamirsUserTransient::getRawPassword, e);
//        }

        try {
            userChecker.verifyConfirmPasswordAndPassword(user.getPassword(), user.getConfirmPassword());
        } catch (PamirsException e) {
            throw error(PamirsUserTransient::getConfirmPassword, e);
        }

        BeanDefinitionUtils.getBean(PasswordService.class).unsafeChangePassword(existUserData.getId(), user.getPassword());
    }

    <I extends D, R> PamirsException error(Getter<I, R> getter, PamirsException error) {
        initErrorExtension();
        PamirsSession.getMessageHub().error();
        PamirsSession.getMessageHub().getErrorExtension().getMessages().add(Message.init().setMessage(error.getMsg()).setCode(error.getCode() + "").setLevel(InformationLevelEnum.ERROR).setErrorType(ErrorTypeEnum.valueOf(error.getType())));
        PamirsSession.getMessageHub().setPath(PamirsSession.getMessageHub().getPath().segment(LambdaUtil.fetchFieldName(getter)));
        return error;
    }

    private void initErrorExtension() {
        if (null == PamirsSession.getMessageHub().getErrorExtension()) {
            PamirsSession.getMessageHub().setErrorExtension(new ErrorExtension());
        }

        if (null == PamirsSession.getMessageHub().getErrorExtension().getMessages()) {
            PamirsSession.getMessageHub().getErrorExtension().setMessages(new ArrayList());
        }
    }
}
