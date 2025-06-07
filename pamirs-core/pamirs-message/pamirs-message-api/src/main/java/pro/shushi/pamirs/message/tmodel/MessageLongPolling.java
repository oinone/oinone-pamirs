package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Slf4j
@Model(displayName = "消息long polling请求", summary = "long polling数据统一返回")
@Model.model(MessageLongPolling.MODEL_MODEL)
public class MessageLongPolling extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageLongPolling";

    @Field.Integer
    @Field(displayName = "未读消息数")
    private Long count;

    @Field.Integer
    @Field(displayName = "最后消息id")
    private Long lastMessageId;

    @Field.String
    @Field(displayName = "多个频道")
    private String channelIds;

    @Field.one2many
    @Field(displayName = "频道消息")
    private List<PamirsMessage> channelMessages;

    @Field.one2many
    @Field(displayName = "模型消息")
    private List<PamirsMessage> modelMessages;

    @Field.String
    @Field(displayName = "当前通讯的模型")
    private String activeModel;

    @Field.Integer
    @Field(displayName = "当前活动id")
    private Long activeId;

}
