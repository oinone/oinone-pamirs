package pro.shushi.pamirs.message.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.constant.ExpConstants;

import jakarta.annotation.Resource;

/**
 * @author shier
 * date  2022/6/28 下午1:48
 */
@Model.model(MessageChannel.MODEL_MODEL)
@Component
public class MessageChannelAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(channel.name)", error = "消息频道的名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(channel.channelType)", error = "消息频道的类型不能为空"),
            @Validation.Rule(value = "!IS_NULL(channel.iconUrl)", error = "消息频道的图标不能为空"),
            @Validation.Rule(value = "!IS_NULL(channel.openType)", error = "消息频道的开放类型不能为空"),
    })
    @Action(displayName = "创建")
    @Action.Advanced(invisible = ExpConstants.idValueExist)
    public MessageChannel create(MessageChannel channel) {
        return defaultWriteWithFieldApi.createWithField(channel);
    }


    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(channel.name)", error = "消息频道的名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(channel.channelType)", error = "消息频道的类型不能为空"),
            @Validation.Rule(value = "!IS_NULL(channel.iconUrl)", error = "消息频道的图标不能为空"),
            @Validation.Rule(value = "!IS_NULL(channel.openType)", error = "消息频道的开放类型不能为空"),
    })
    @Action(displayName = "更新")
    @Action.Advanced(invisible = ExpConstants.idValueNotExist)
    public MessageChannel update(MessageChannel channel) {
        return defaultWriteWithFieldApi.updateWithField(channel);
    }

}
