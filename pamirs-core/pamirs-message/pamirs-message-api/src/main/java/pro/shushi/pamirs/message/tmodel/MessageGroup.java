package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.message.enmu.MessageGroupTypeEnum;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.Date;
import java.util.List;

@Model(displayName = "消息组")
@Model.model(MessageGroup.MODEL_MODEL)
public class MessageGroup extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageGroup";

    @Field.Integer
    @Field(displayName = "个人或频道id", summary = "根据groupType来识别是哪个的id")
    private Long id;

    @Field.String
    @Field(displayName = "模型")
    private String resModel;

    @Field.Integer
    @Field(displayName = "模型记录id")
    private Long resId;

    @Field.String
    @Field(displayName = "模型记录name")
    private String resName;

    @Field.String(size = 2048)
    @Field(displayName = "输入消息内容")
    private String messageInput;

    @Field.String
    @Field(displayName = "组标题")
    private String title;

    @Field.Integer
    @Field(displayName = "未读消息数")
    private Integer unreadCount;

    @Field.Integer
    @Field(displayName = "最近一条消息ID")
    private Long lastMessageId;

    @Field.Html
    @Field(displayName = "最近一条消息内容")
    private String lastMessageBody;

    @Field.Date
    @Field(displayName = "最近一条消息时间")
    private Date lastMessageTime;

//    @Field.Date
//    @Field( displayName = "已读最大消息id")
//    private Long maxMessageIdLimit;

    @Field.String
    @Field(displayName = "消息组logo")
    private String iconUrl;

    //todo 用户信息name
    @Field.String
    @Field(displayName = "当前用户名")
    private String currentUserName;

    @Field.Enum
    @Field(displayName = "消息组类型")
    private MessageGroupTypeEnum groupType;

    @Field.many2one
    @Field(displayName = "接收对象")
    private PamirsUser chatPartner;

    @Field.many2one
    @Field(displayName = "接收渠道")
    private MessageChannel channel;

    @Field.one2many
    @Field(displayName = "消息列表")
    private List<PamirsMessage> messages;
}
