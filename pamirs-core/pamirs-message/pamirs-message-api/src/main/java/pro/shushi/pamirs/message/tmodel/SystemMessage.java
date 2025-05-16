package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.message.enmu.MessageGroupTypeEnum;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

@Model(displayName = "站内信")
@Model.model(SystemMessage.MODEL_MODEL)
public class SystemMessage extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.message.SystemMessage";

    @Field.one2many
    @Field(displayName = "站内信")
    private List<PamirsMessage> messages;

    @Field.Enum
    @Field(displayName = "消息类型")
    private MessageGroupTypeEnum type;

    @Field.one2many
    @Field(displayName = "站内信接收人")
    private List<PamirsUser> partners;

}

