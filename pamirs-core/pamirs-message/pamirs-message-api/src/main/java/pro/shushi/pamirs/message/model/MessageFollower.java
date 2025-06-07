package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

@Model(displayName = "模型关注")
@Model.model(MessageFollower.MODEL_MODEL)
public class MessageFollower extends IdModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "pamirs.message.MessageFollower";

    @Validation(check = checkModelModel)
    @Field.String
    @Field(displayName = "模型")
    private String resModel;

    @Field.Integer
    @Field(displayName = "模型行记录id")
    private Long resId;

    @Field.String
    @Field(displayName = "模型行记录name")
    private String resName;

    @Field.many2one
    @Field(displayName = "关注模型的用户")
    private PamirsUser partner;

    @Field.many2one
    @Field(displayName = "关注模型的频道")
    private MessageChannel channel;

    @Field.many2many(through = "MailFollowerRelationMailSubtype")
    @Field(displayName = "消息子类型")
    private List<MessageSubtype> subtypes;

//    @Function
//    public String fetchActiveModel() {
//        return PamirsEnvironment.getThreadLocal().getActiveModel();
//    }
//
//    @Function
//    public Long fetchActiveId() {
//        return PamirsEnvironment.getThreadLocal().getActiveId();
//    }
//
//    @Function
//    public String fetchActiveName() {
////        return PamirsEnvironment.getThreadLocal
//        return null;
//    }
}
