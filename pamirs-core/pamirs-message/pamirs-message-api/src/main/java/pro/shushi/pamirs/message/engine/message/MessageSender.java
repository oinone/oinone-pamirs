package pro.shushi.pamirs.message.engine.message;

import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.tmodel.MessageGroup;
import pro.shushi.pamirs.message.tmodel.SystemMessage;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

public interface MessageSender {

    /**
     * 发系统消息-点对点
     * 其中消息指定body即可
     * @param systemTransient 系统消息临时模型
     * @return
     */
    Boolean sendSystemMail(SystemMessage systemTransient);


    /**
     * 广播消息 多个接收人
     * @param systemTransient
     * @return
     */
    Boolean sendSystemMailBroadcast(SystemMessage systemTransient);

    /**
     * 发频道消息
     * @param groupTransient
     * @return
     */
    Boolean sendChannelMail(MessageGroup groupTransient);


    /**
     * 发模型消息
     * @param messageList
     * @return
     */
    Boolean sendModelMail(List<PamirsMessage> messageList, List<PamirsUser> partnerList);

}
