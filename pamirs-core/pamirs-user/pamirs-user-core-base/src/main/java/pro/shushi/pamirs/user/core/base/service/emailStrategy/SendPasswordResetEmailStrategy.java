package pro.shushi.pamirs.user.core.base.service.emailStrategy;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.core.base.service.SendConfirmEmailAbstract;
import pro.shushi.pamirs.user.core.base.spi.SendEmailServiceApi;

import static pro.shushi.pamirs.user.core.base.util.UserLoginHelper.success;

/**
 * @author Wuxin
 * @Date 2024/7/12
 * @since 1.0
 */
@Order
@Component
@SPI.Service("sendEmailVerificationCodeStrategy")
@Slf4j
public class SendPasswordResetEmailStrategy extends SendConfirmEmailAbstract implements SendEmailServiceApi {

    @Override
    public boolean match(UserBehaviorEventEnum userBehaviorEventEnum) {
        return UserBehaviorEventEnum.MODIFY_PASSWORD_SEND_RESET_EMAIL.equals(userBehaviorEventEnum);
    }

    @Override
    public PamirsUserTransient execute(PamirsUserTransient user) {
        SendPasswordResetEmailStrategy sendPasswordResetEmailStrategy = new SendPasswordResetEmailStrategy();
        return sendPasswordResetEmailStrategy.sendConfirmEmail(user);
    }

    @Override
    public PamirsUserTransient generateConfirmation(PamirsUserTransient user, UserBehaviorEventEnum userBehaviorEvent, String msgType) {
        boolean isNew = false;
        msgType = SMSTemplateTypeEnum.CHANGE_PWD.value();

        PamirsUser rUser = dataChecker.checkEmailIsExist(user, isNew);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        user.setNickname(rUser.getNickname());
        user.setRealname(rUser.getRealname());

        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        userEmailConfirmationService.sendEmailConfirmationBoth(user, msgType, isNew, UserBehaviorEventEnum.MODIFY_PASSWORD_SEND_RESET_EMAIL);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    @Override
    public EmailTemplate getEmailTemplate(PamirsUserTransient userTransient, String code) {
        EmailTemplate template = new EmailTemplate().setName("修改密码验证码邮件").queryOne();
        userTransient.setVerificationCode(code);
        return template;
    }
}
