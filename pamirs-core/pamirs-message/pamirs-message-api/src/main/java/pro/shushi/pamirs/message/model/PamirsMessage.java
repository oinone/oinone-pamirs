package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.message.enmu.MessageMasterEnum;
import pro.shushi.pamirs.message.enmu.MessageTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

//todo zl 这里很多字段会有defaultValue定义，定义前端设默认值的schema
@Base
@Model(displayName = "Pamirs消息")
@Model.model(PamirsMessage.MODEL_MODEL)
public class PamirsMessage extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.PamirsMessage";

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "应用模块")
    private ModuleDefinition appModule;

    @Field.String
    @Field(displayName = "模块编码",defaultValue = "message")
    private String module;

    @Field.String(size = 2048)
    @Field(displayName = "主题")
    private String subject;

    @Field.String
    @Field(displayName = "名称")
    private String name;

    @Field.Html
    @Field(displayName = "内容")
    private String body;

    @Field.one2many
    @Field.Relation(relationFields = {"id"}, referenceFields = {"resId"})
    @Field(displayName = "附件列表")
    private List<PamirsFile> file;

    @Field.many2one
    @Field(displayName = "父节点")
    private PamirsMessage parent;

    @Field.one2many
    @Field(displayName = "子列表")
    @Field.Relation(relationFields = "id",referenceFields = "parentId")
    private List<PamirsMessage> children;

    // todo compute = "fetchActiveModel(resModel)" 前端传
    @Field.String
    @Field(displayName = "模型", required = true)
    private String resModel;

    @Field.String
    @Field(displayName = "业务跳转窗口动作名称")
    private String resViewActionName;

    @Field.many2one
    @Field.Relation(relationFields = {"resModel", "resViewActionName"}, referenceFields = {"model", "name"})
    @Field(displayName = "业务跳转窗口动作")
    private ViewAction resViewAction;

    // todo compute = "fetchActiveId(resId)"  前端传
    @Field.Integer
    @Field(displayName = "模型行记录id")
    private Long resId;

    //resName需要根据activeModel计算得到，todo depends 不需要
    @Field.String
    @Field(displayName = "模型行记录name")
    private String resName;

    @Field.Enum
    @Field(displayName = "消息类型", required = true)
    private MessageTypeEnum messageType;

    @Field.many2one
    @Field(displayName = "消息子类型")
    private MessageSubtype subtype;

    // todo compute = "fetchSendPartner(messageType,resModel)"
    @Field.many2one
    @Field( displayName = "消息发送者")
    private PamirsUser sendPartner;

    @Field.one2many
    @Field(displayName = "消息通知对象")
    @Field.Relation(relationFields = "id",referenceFields = "messageId")
    private List<UnreadMessage> partnerNeedAction;

    @Field.many2many(through = "MessageChannelRelPamirsMessage")
    @Field(displayName = "消息接收频道")
    private List<MessageChannel> channels;

    @Field.Enum
    @Field(store = NullableBoolEnum.FALSE, summary = "消息在聊天窗口中的位置")
    private MessageMasterEnum mailMaster;

    @Field.String
    @Field(displayName = "消息头像")
    private String iconUrl;

    @Field.String
    @Field(displayName = "额外图表",summary = "工作流催办时，增加一个催")
    private String extendIcon;

    // todo compute = "fetchCurrentUserName()"
    @Field.String
    @Field(summary = "当前登录用户名")
    private String currentUserName;

    @Field.String
    @Field(displayName = "任务类型",summary = "工作流类型")
    private String workFlowTaskType;

//    @Function
//    public String fetchCurrentUserName() {
//        //系统发的模型消息也不要发送者
//        if (!MailChannel.class.getName().equalsIgnoreCase(resModel)
//                && StringUtils.isBlank(PamirsEnvironment.getThreadLocal().getActiveModel())) {
//            return null;
//        }
//        return MailUtils.getLoginUser().getName();
//    }
//
//    @Function
//    public String fetchActiveModel(String resModel) {
//        if (StringUtils.isEmpty(resModel)) {
//            return PamirsEnvironment.getThreadLocal().getActiveModel();
//        } else {
//            return resModel;
//        }
//    }
//
//    @Function
//    public Long fetchActiveId(Long resId) {
//        if (resId == null) {
//            return PamirsEnvironment.getThreadLocal().getActiveId();
//        } else {
//            return resId;
//        }
//    }
//
//    @Function
//    public Map fetchSendPartner(String messageType, String resModel) {
//        //系统通知不要发送者
//        if (MailMessageTypeEnum.NOTIFICATION.getValue().equalsIgnoreCase(messageType)) {
//            return null;
//        }
//        //系统发的模型消息也不要发送者
//        if (!MailChannel.class.getName().equalsIgnoreCase(resModel)
//                && StringUtils.isBlank(PamirsEnvironment.getThreadLocal().getActiveModel())) {
//            return null;
//        }
//        PamirsPartner loginUser = MailUtils.getLoginUser();
////        partnerMap.put("sendPartnerId",sendPartner.getId());
//        Map<String, Long> partnerMap = new HashMap<>();
//        partnerMap.put("id", loginUser.getId());
//        return partnerMap;
//    }
}
