package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.message.enmu.MessageTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

@Base
@Model.model(UnreadMessage.MODEL_MODEL)
@Model(displayName = "消息")
@Model.Advanced(index = {"userId,messageType,readDone"})
public class UnreadMessage extends IdModel implements MetaCheckConstants {

    private static final long serialVersionUID = -514108194894331813L;

    public static final String MODEL_MODEL = "pamirs.message.UnreadMessage";

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "应用模块")
    private UeModule appModule;

    @Field.String
    @Field(displayName = "模块编码", defaultValue = "message")
    private String module;

    // todo compute = "fetchActiveModel(resModel)"
    @Validation(check = checkModelModel)
    @Field.String
    @Field(displayName = "模型")
    private String resModel;

    @Field.String
    @Field(displayName = "业务跳转窗口动作名称")
    private String resViewActionName;

    @Field.many2one
    @Field.Relation(relationFields = {"resModel", "resViewActionName"}, referenceFields = {"model", "name"})
    @Field(displayName = "业务跳转窗口动作")
    private ViewAction resViewAction;

    // todo compute = "fetchActiveId(resId)"
    @Field.Integer
    @Field(displayName = "模型行记录id")
    private Long resId;

    // todo compute = "fetchActiveName()"
    @Field.String
    @Field(displayName = "模型行记录name")
    private String resName;

    @Field.many2one
    @Field(displayName = "消息")
    @Field.Relation(relationFields = {"messageId"}, referenceFields = {"id"})
    private PamirsMessage message;

    @Field.Integer
    @Field(displayName = "消息Id")
    private Long messageId;

    @Field.Enum
    @Field(displayName = "消息类型")
    private MessageTypeEnum messageType;

    @Field.String(size = 2048)
    @Field(displayName = "主题", summary = "主题")
    private String subject;

    @Field.Html
    @Field.Related(related = {"message", "body"})
    @Field(displayName = "消息具体内容")
    private String body;

    @Field.many2one
    @Field(displayName = "消息接收者")
    @Field.Relation(relationFields = {"userId"}, referenceFields = {"id"})
    private PamirsUser user;

    @Field.Integer
    @Field(displayName = "用户Id")
    private Long userId;

    // todo compute = "false",
    @Field.Boolean
    @Field(displayName = "是否已读", defaultValue = "false")
    private Boolean readDone;

    //TODO zl 消息失败重发，是否也走这张表来完成
    @Field.String
    @Field(displayName = "消息摘要", store = NullableBoolEnum.FALSE)
    private String messageSummary;

//    @Field.String
//    @Field(displayName = "主题", store = NullableBoolEnum.FALSE)
//    private String subject;

//    @Function
//    public String fetchActiveModel(String resModel) {
//        return Optional.ofNullable(resModel).orElse(PamirsEnvironment.getThreadLocal().getActiveModel());
//    }
//
//    @Function
//    public Long fetchActiveId(Long resId) {
//        return Optional.ofNullable(resId).orElse(PamirsEnvironment.getThreadLocal().getActiveId());
//    }
//
//    @Function
//    public String fetchActiveName() {
////        return PamirsEnvironment.getThreadLocal().getActiveName();
//        return null;
//    }
}
