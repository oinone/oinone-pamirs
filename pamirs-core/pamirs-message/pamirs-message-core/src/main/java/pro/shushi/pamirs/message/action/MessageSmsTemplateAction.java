package pro.shushi.pamirs.message.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.model.SmsTemplate;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.constant.ExpConstants;

import jakarta.annotation.Resource;

/**
 * @author shier
 * date  2022/6/28 下午4:59
 */
@Model.model(SmsTemplate.MODEL_MODEL)
@Component
public class MessageSmsTemplateAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.templateCode)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.templateType)", error = "邮件内容不能为空"),
            @Validation.Rule(value = "!(EQUALS(data.templateType,NOTIFY) && IS_BLANK(data.timeInterval))", error = "验证码有效时间不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.channel)", error = "短信通道不能为空")
    })
    @Action(displayName = "创建")
    @Action.Advanced(invisible = ExpConstants.idValueExist)
    public SmsTemplate create(SmsTemplate data) {
        return defaultWriteWithFieldApi.createWithField(data);
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.templateCode)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.templateType)", error = "邮件内容不能为空"),
            @Validation.Rule(value = "!(EQUALS(data.templateType,NOTIFY) && IS_BLANK(data.timeInterval))", error = "验证码有效时间不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.channel)", error = "短信通道不能为空")
    })
    @Action(displayName = "更新")
    @Action.Advanced(invisible = ExpConstants.idValueNotExist)
    public SmsTemplate update(SmsTemplate data) {
        return defaultWriteWithFieldApi.updateWithField(data);
    }

}
