package pro.shushi.pamirs.message.action;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;

import static pro.shushi.pamirs.message.enmu.MessageExpEnumerate.BIZ_ERROR;

/**
 * @author shier
 * date  2022/6/28 下午4:59
 */
@Model.model(EmailTemplate.MODEL_MODEL)
@Component
public class MessageEmailTemplateAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.emailSenderSourceId)", error = "邮件服务器不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.body)", error = "邮件内容不能为空"),
    })
    @Action(displayName = "创建")
    @Action.Advanced(invisible = ExpConstants.idValueExist)
    public EmailTemplate create(EmailTemplate data) {
        if (StringUtils.isNotBlank(data.getName()) && Models.origin().count(Pops.<EmailTemplate>lambdaQuery()
                .from(EmailTemplate.MODEL_MODEL)
//                .ne(EmailTemplate::getId, data.getId())
                .eq(EmailTemplate::getName, data.getName())) > 0) {
            throw PamirsException.construct(BIZ_ERROR).appendMsg("邮件模板名称已经存在，不能重复").errThrow();
        }
        return defaultWriteWithFieldApi.createWithField(data);
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.emailSenderSourceId)", error = "邮件服务器不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.body)", error = "邮件内容不能为空"),
    })
    @Action(displayName = "更新")
    @Action.Advanced(invisible = ExpConstants.idValueNotExist)
    public EmailTemplate update(EmailTemplate data) {
        if (StringUtils.isNotBlank(data.getName()) && Models.origin().count(Pops.<EmailTemplate>lambdaQuery()
                .from(EmailTemplate.MODEL_MODEL)
                .ne(EmailTemplate::getId, data.getId())
                .eq(EmailTemplate::getName, data.getName())) > 0) {
            throw PamirsException.construct(BIZ_ERROR).appendMsg("邮件模板名称已经存在，不能重复").errThrow();
        }
        return defaultWriteWithFieldApi.updateWithField(data);
    }
}
