package pro.shushi.pamirs.message.action;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.model.EmailSenderSource;
import pro.shushi.pamirs.message.utils.MessageEmailUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;

/**
 * @author shier
 * date  2022/6/28 下午4:58
 */
@Model.model(EmailSenderSource.MODEL_MODEL)
@Component
public class MessageEmailSourceAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.smtpUser)", error = "SMTP用户名不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.smtpSecurity)", error = "加密类型不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.smtpHost)", error = "SMTP服务器地址不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.smtpPort)", error = "SMTP服务器端口不能为空,并且必须为整数"),
    })
    @Action(displayName = "创建")
    @Action.Advanced(invisible = ExpConstants.idValueExist)
    public EmailSenderSource create(EmailSenderSource data) {
        Long count = new EmailSenderSource().setSmtpHost(data.getSmtpHost()).setSmtpUser(data.getSmtpUser()).count();
        if (count > 0) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_SENDER_CONFIG_SMTP_HOST_USER_REPEAT_ERROR).errThrow();
        }
        data.setType(MessageEngineTypeEnum.EMAIL_SEND);
        data = defaultWriteWithFieldApi.createWithField(data);
        prompt();
        return data;
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_NULL(data.id)", error = "id不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.smtpUser)", error = "SMTP用户名不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.smtpSecurity)", error = "加密类型不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.smtpHost)", error = "SMTP服务器地址不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.smtpPort)", error = "SMTP服务器端口不能为空,并且必须为整数"),
    })
    @Action(displayName = "更新")
    @Action.Advanced(invisible = ExpConstants.idValueNotExist)
    public EmailSenderSource update(EmailSenderSource data) {
        EmailSenderSource originData = data.queryById();
        Long count = Models.origin().count(Pops.<EmailSenderSource>lambdaQuery()
                .from(EmailSenderSource.MODEL_MODEL)
                .eq(EmailSenderSource::getSmtpHost, data.getSmtpHost())
                .eq(EmailSenderSource::getSmtpUser, data.getSmtpUser())
                .ne(EmailSenderSource::getId, data.getId())
        );
        if (count > 0) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_SENDER_CONFIG_SMTP_HOST_USER_REPEAT_ERROR).errThrow();
        }
        data.setType(MessageEngineTypeEnum.EMAIL_SEND);
        defaultWriteWithFieldApi.updateWithField(data);
        prompt();
        return data;
    }

    private static final String MULTI_MSG_FORMAT = I18nUtils.getMessage("pamirs-message-core.MessageEmailSourceAction.moreThanOneMailServerIsEnabled");
    private static final String NO_CONFIG_MSG = I18nUtils.getMessage("pamirs-message-core.MessageEmailSourceAction.theMailNodeCannotProvideTheNot");
    private static final String SUCCESS_CONFIG_MSG = I18nUtils.getMessage("pamirs-message-core.MessageEmailSourceAction.emailServerConfiguredSuccessfu");

    private void prompt() {
        Long count = new EmailSenderSource().setType(MessageEngineTypeEnum.EMAIL_SEND).setActive(Boolean.TRUE).count();
        if (count > 1) {
            EmailSenderSource emailSenderSourceConfig = MessageEmailUtils.fetchEmailSendConfig();
            PamirsSession.getMessageHub().warn(String.format(MULTI_MSG_FORMAT, emailSenderSourceConfig.getName()));
        } else if (count == 0) {
            PamirsSession.getMessageHub().warn(NO_CONFIG_MSG);
        } else {
            PamirsSession.getMessageHub().success(SUCCESS_CONFIG_MSG);
        }
    }
}
