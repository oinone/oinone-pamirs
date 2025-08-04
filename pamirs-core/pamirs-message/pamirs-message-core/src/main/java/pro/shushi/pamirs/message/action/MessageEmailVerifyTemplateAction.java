package pro.shushi.pamirs.message.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.model.EmailVerifyTemplate;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;

import jakarta.annotation.Resource;

/**
 * @author shier
 * date  2022/6/28 下午4:59
 */
@Model.model(EmailVerifyTemplate.MODEL_MODEL)
@Component
public class MessageEmailVerifyTemplateAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.body)", error = "邮件内容不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.templateType)", error = "邮件服务器不能为空")
    })
    @Action(displayName = "创建")
    @Action.Advanced(invisible = ExpConstants.idValueExist)
    public EmailVerifyTemplate create(EmailVerifyTemplate data) {
        if (null != data.getTemplateType() && exist(new EmailVerifyTemplate().setTemplateType(data.getTemplateType()))) {
            throw PamirsException.construct(MessageExpEnumerate.EMAIL_VERIFY_TEMPLATE_TEMPLATE_TYPE_REPEAT_ERROR).errThrow();
        }
        if (null != data.getTitle() && exist(new EmailVerifyTemplate().setTitle(data.getTitle()))) {
            throw PamirsException.construct(MessageExpEnumerate.EMAIL_VERIFY_TEMPLATE_TITLE_REPEAT_ERROR).errThrow();
        }
        return defaultWriteWithFieldApi.createWithField(data);
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.body)", error = "邮件内容不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.templateType)", error = "邮件服务器不能为空")
    })
    @Action(displayName = "更新")
    @Action.Advanced(invisible = ExpConstants.idValueNotExist)
    public EmailVerifyTemplate update(EmailVerifyTemplate data) {
        EmailVerifyTemplate originData = data.queryById();
        if (null != data.getTemplateType() && !originData.getTemplateType().equals(data.getTemplateType()) && exist(new EmailVerifyTemplate().setTemplateType(data.getTemplateType()))) {
            throw PamirsException.construct(MessageExpEnumerate.EMAIL_VERIFY_TEMPLATE_TEMPLATE_TYPE_REPEAT_ERROR).errThrow();
        }
        if (null != data.getTitle() && !originData.getTitle().equals(data.getTitle()) && exist(new EmailVerifyTemplate().setTitle(data.getTitle()))) {
            throw PamirsException.construct(MessageExpEnumerate.EMAIL_VERIFY_TEMPLATE_TITLE_REPEAT_ERROR).errThrow();
        }

        return defaultWriteWithFieldApi.updateWithField(data);
    }

    Boolean exist(EmailVerifyTemplate data) {
        Long count = Models.origin().count(data);
        return count > 0;
    }

}
