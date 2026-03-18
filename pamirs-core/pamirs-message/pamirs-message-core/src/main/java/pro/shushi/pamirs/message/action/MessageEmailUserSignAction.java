package pro.shushi.pamirs.message.action;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.model.EmailUserSign;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.constant.ExpConstants;

import static pro.shushi.pamirs.message.enmu.MessageExpEnumerate.BIZ_ERROR;

/**
 * @author shier
 * date  2022/6/29 上午10:50
 */
@Model.model(EmailUserSign.MODEL_MODEL)
@Component
public class MessageEmailUserSignAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.emailSignature)", error = "邮件签名不能为空"),
    })
    @Action(displayName = "创建")
    @Action.Advanced(invisible = ExpConstants.idValueExist)
    public EmailUserSign create(EmailUserSign data) {
        if (StringUtils.isNotBlank(data.getName()) && Models.origin().count(Pops.<EmailUserSign>lambdaQuery()
                .from(EmailUserSign.MODEL_MODEL)
                .eq(EmailUserSign::getName, data.getName())) > 0) {
            throw PamirsException.construct(BIZ_ERROR).appendMsg(I18nUtils.getMessage("pamirs.message.email.sign.duplicate")).errThrow();
        }
        return defaultWriteWithFieldApi.createWithField(data);
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.emailSignature)", error = "邮件签名不能为空"),
    })
    @Action(displayName = "更新")
    @Action.Advanced(invisible = ExpConstants.idValueNotExist)
    public EmailUserSign update(EmailUserSign data) {
        if (StringUtils.isNotBlank(data.getName()) && Models.origin().count(Pops.<EmailUserSign>lambdaQuery()
                .from(EmailUserSign.MODEL_MODEL)
                .ne(EmailUserSign::getId, data.getId())
                .eq(EmailUserSign::getName, data.getName())) > 0) {
            throw PamirsException.construct(BIZ_ERROR).appendMsg(I18nUtils.getMessage("pamirs.message.email.sign.duplicate")).errThrow();
        }
        return defaultWriteWithFieldApi.updateWithField(data);
    }

}
