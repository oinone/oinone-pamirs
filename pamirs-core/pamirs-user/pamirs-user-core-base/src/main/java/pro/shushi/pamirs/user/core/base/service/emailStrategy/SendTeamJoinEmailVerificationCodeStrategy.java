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
@SPI.Service("sendTeamJoinEmailVerificationCodeStrategy")
public class SendTeamJoinEmailVerificationCodeStrategy extends SendConfirmEmailAbstract implements SendEmailServiceApi {

    @Override
    public boolean match(UserBehaviorEventEnum userBehaviorEventEnum) {
        return UserBehaviorEventEnum.ADD_CORP_SEND_EMAIL_CODE.equals(userBehaviorEventEnum);
    }

    @Override
    public PamirsUserTransient execute(PamirsUserTransient user) {
        SendTeamJoinEmailVerificationCodeStrategy strategy = new SendTeamJoinEmailVerificationCodeStrategy();
        return strategy.sendConfirmEmail(user);
    }

    @Override
    public PamirsUserTransient generateConfirmation(PamirsUserTransient user, UserBehaviorEventEnum userBehaviorEvent, String msgType) {
        boolean isNew = true;
        if (user.getNewEmail() == null) user.setNewEmail(user.getEmail());
        msgType = SMSTemplateTypeEnum.ADD_CORP.value();
        dataChecker.checkEmailIsNotExist(user, true);

        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }

        userEmailConfirmationService.sendEmailConfirmationBoth(user, msgType, isNew, UserBehaviorEventEnum.ADD_CORP_SEND_EMAIL_CODE);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        return success(user);
    }

    @Override
    public EmailTemplate getEmailTemplate(PamirsUserTransient userTransient, String code) {
        EmailTemplate template = new EmailTemplate().setName("申请加入验证码邮件").queryOne();
        userTransient.setVerificationCode(code);
        return template;
    }
}
