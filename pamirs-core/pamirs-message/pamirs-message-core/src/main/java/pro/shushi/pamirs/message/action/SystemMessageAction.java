package pro.shushi.pamirs.message.action;

import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.message.MessageSender;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.MessageGroupTypeEnum;
import pro.shushi.pamirs.message.tmodel.SystemMessage;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.model(SystemMessage.MODEL_MODEL)
public class SystemMessageAction {

    /**
     * 直接发送系统消息-点对点
     * @param sys
     * @return
     */
//    @Action(model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.core.model.SystemMailTransient", isExported = true)
    @Action
    public SystemMessage sendSystemMail(SystemMessage sys) {
        if (MessageGroupTypeEnum.SYSTEM_MAIL.getValue().equalsIgnoreCase(sys.getType().getValue())){
            MessageSender sender = MessageEngine.<MessageSender>get(MessageEngineTypeEnum.MAIL_SEND).get(null);
            sender.sendSystemMail(sys);
        }
        return sys;
    }

    /**
     * 直接发送广播消息
     * @param sys
     * @return
     */
//    @Action(model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.core.model.SystemMailTransient", isExported = true)
    @Action
    public SystemMessage sendSystemMailBroadcast(SystemMessage sys) {
        if (MessageGroupTypeEnum.SYSTEM_MAIL_BROADCAST.getValue().equalsIgnoreCase(sys.getType().getValue())){
            MessageSender sender = MessageEngine.<MessageSender>get(MessageEngineTypeEnum.MAIL_SEND).get(null);
            sender.sendSystemMailBroadcast(sys);
        }
        return sys;
    }



}
