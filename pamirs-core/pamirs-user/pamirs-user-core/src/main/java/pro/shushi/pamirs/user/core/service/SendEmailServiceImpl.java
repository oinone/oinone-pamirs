package pro.shushi.pamirs.user.core.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.SendEmailService;
import pro.shushi.pamirs.user.api.utils.UserServiceUtils;
import pro.shushi.pamirs.user.core.base.spi.SendEmailServiceApi;

import java.util.List;

import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.USER_EMAIL_MISS_EVENT_IS_NULL_ERROR;

/**
 * @author Wuxin
 * @Date 2024/7/12
 * @since 1.0
 */
@Component
public class SendEmailServiceImpl implements SendEmailService {

    @Override
    public PamirsUserTransient sendEmailVerificationCodeForLogin(PamirsUserTransient user) {
        if (UserBehaviorEventEnum.SEND_LOGIN_BY_EMAIL_CODE.equals(user.getUserBehaviorEvent())) {
            List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
            for (SendEmailServiceApi sendMailServiceStrategy : sendEmailServiceApis) {
                boolean match = sendMailServiceStrategy.match(UserBehaviorEventEnum.SEND_LOGIN_BY_EMAIL_CODE);
                if (match) {
                    return sendMailServiceStrategy.execute(user);
                }
            }
        }
        UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                .setErrorField("email"));
        return user;
    }

    @Override
    public PamirsUserTransient sendPasswordResetEmail(PamirsUserTransient user) {
        if (UserBehaviorEventEnum.MODIFY_PASSWORD_SEND_RESET_EMAIL.equals(user.getUserBehaviorEvent())) {
            List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
            for (SendEmailServiceApi emailServiceApi : sendEmailServiceApis) {
                boolean match = emailServiceApi.match(UserBehaviorEventEnum.MODIFY_PASSWORD_SEND_RESET_EMAIL);
                if (match) {
                    return emailServiceApi.execute(user);
                }
            }
        }

        UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                .setErrorField("email"));
        return user;
    }

    @Override
    public PamirsUserTransient sendOriginalEmailConfirmationEmail(PamirsUserTransient user) {
        if (UserBehaviorEventEnum.MODIFY_EMAIL_SEND_OLD_EMAIL.equals(user.getUserBehaviorEvent())) {
            List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
            for (SendEmailServiceApi emailServiceApi : sendEmailServiceApis) {
                boolean match = emailServiceApi.match(UserBehaviorEventEnum.MODIFY_EMAIL_SEND_OLD_EMAIL);
                if (match) {
                    return emailServiceApi.execute(user);
                }
            }
        }

        UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                .setErrorField("email"));
        return user;
    }

    @Override
    public PamirsUserTransient sendNewEmailConfirmationEmail(PamirsUserTransient user) {
        if (UserBehaviorEventEnum.MODIFY_EMAIL_SEND_NEW_EMAIL.equals(user.getUserBehaviorEvent())) {
            List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
            for (SendEmailServiceApi emailServiceApi : sendEmailServiceApis) {
                boolean match = emailServiceApi.match(UserBehaviorEventEnum.MODIFY_EMAIL_SEND_NEW_EMAIL);
                if (match) {
                    return emailServiceApi.execute(user);
                }
            }
        }

        UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                .setErrorField("email"));
        return user;
    }

    @Override
    public PamirsUserTransient sendPhoneNumberEmailVerificationCodeForChange(PamirsUserTransient user) {
        if (UserBehaviorEventEnum.MODIFY_PHONE_SEND_EMAIL.equals(user.getUserBehaviorEvent())) {
            List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
            for (SendEmailServiceApi emailServiceApi : sendEmailServiceApis) {
                boolean match = emailServiceApi.match(UserBehaviorEventEnum.MODIFY_PHONE_SEND_EMAIL);
                if (match) {
                    return emailServiceApi.execute(user);
                }
            }
        }

        UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                .setErrorField("email"));
        return user;
    }

    @Override
    public PamirsUserTransient registerByEmail(PamirsUserTransient user) {
        if (UserBehaviorEventEnum.SIGN_UP_EMAIL.equals(user.getUserBehaviorEvent())) {
            List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
            for (SendEmailServiceApi emailServiceApi : sendEmailServiceApis) {
                boolean match = emailServiceApi.match(UserBehaviorEventEnum.SIGN_UP_EMAIL);
                if (match) {
                    return emailServiceApi.execute(user);
                }
            }
        }

        UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                .setErrorField("email"));
        return user;
    }

    @Override
    public PamirsUserTransient sendTeamJoinEmailVerificationCode(PamirsUserTransient user) {
        if (UserBehaviorEventEnum.ADD_CORP_SEND_EMAIL_CODE.equals(user.getUserBehaviorEvent())) {
            List<SendEmailServiceApi> sendEmailServiceApis = Spider.getLoader(SendEmailServiceApi.class).getOrderedExtensions();
            for (SendEmailServiceApi emailServiceApi : sendEmailServiceApis) {
                boolean match = emailServiceApi.match(UserBehaviorEventEnum.ADD_CORP_SEND_EMAIL_CODE);
                if (match) {
                    return emailServiceApi.execute(user);
                }
            }
        }

        UserServiceUtils.broken(user.setErrorMsg(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.msg())
                .setErrorCode(USER_EMAIL_MISS_EVENT_IS_NULL_ERROR.code())
                .setErrorField("email"));
        return user;
    }
}
