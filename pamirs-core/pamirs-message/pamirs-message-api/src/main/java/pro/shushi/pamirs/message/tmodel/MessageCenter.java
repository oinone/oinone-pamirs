package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Slf4j
@Model(displayName = "消息中心")
@Model.model(MessageCenter.MODEL_MODEL)
public class MessageCenter extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageCenter";

    // todo defaultValue = "fetchUnreadCount()"
    @Field.Integer
    @Field(displayName = "未读消息数")
    private Long count;

    // todo defaultValue = "fetchLastId()"
    @Field.Integer
    @Field(displayName = "最新消息Id")
    private Long lastMessageId;

    // todo defaultValue = "fetchMessageGroups()"
    @Field.one2many
    @Field(displayName = "消息组")
    private List<MessageGroup> messageGroups;


}
