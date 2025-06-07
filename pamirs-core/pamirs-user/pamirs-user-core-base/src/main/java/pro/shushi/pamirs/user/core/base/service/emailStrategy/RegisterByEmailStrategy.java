package pro.shushi.pamirs.user.core.base.service.emailStrategy;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
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
@Slf4j
@Component
@SPI.Service("registerByEmailStrategy")
public class RegisterByEmailStrategy extends SendConfirmEmailAbstract implements SendEmailServiceApi {


    @Override
    public boolean match(UserBehaviorEventEnum userBehaviorEventEnum) {
        return UserBehaviorEventEnum.SIGN_UP_EMAIL.equals(userBehaviorEventEnum);
    }

    @Override
    public PamirsUserTransient execute(PamirsUserTransient user) {
        RegisterByEmailStrategy registerByEmailStrategy = new RegisterByEmailStrategy();
        return registerByEmailStrategy.sendConfirmEmail(user);
    }

    @Override
    public EmailTemplate getEmailTemplate(PamirsUserTransient userTransient, String code) {
        EmailTemplate template = new EmailTemplate().setName("邮箱注册邮件").queryOne();
        userTransient.setVerificationCode(code);
        return template;
    }

    @Override
    public PamirsUserTransient generateConfirmation(PamirsUserTransient user, UserBehaviorEventEnum userBehaviorEvent, String msgType) {
        boolean isNew = true;
        msgType = SMSTemplateTypeEnum.SIGN_UP.value();
        if (user.getNewEmail() == null) user.setNewEmail(user.getEmail());

        dataChecker.checkEmailIsNotExist(user, true);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        userEmailConfirmationService.sendEmailConfirmationBoth(user, msgType, isNew, UserBehaviorEventEnum.SIGN_UP_EMAIL);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }
}
