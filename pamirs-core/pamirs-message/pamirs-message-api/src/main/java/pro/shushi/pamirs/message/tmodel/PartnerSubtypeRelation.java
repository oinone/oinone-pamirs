package pro.shushi.pamirs.message.tmodel;
import pro.shushi.pamirs.message.model.MessageSubtype;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model(displayName = "用户和模型消息子类型关系")
@Model.model(PartnerSubtypeRelation.MODEL_MODEL)
public class PartnerSubtypeRelation extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.message.PartnerSubtypeRelation";

    @Field.String
    @Field( displayName = "模型")
    private String resModel;

    @Field.Integer
    @Field( displayName = "模型记录ID")
    private Long resId;

    @Field.many2one
    @Field(  displayName = "消息子类型")
    private MessageSubtype subtype;

    @Field.Integer
    @Field( displayName = "关注者ID")
    private Long partnerId;

    @Field.Boolean
    @Field( displayName = "选择状态")
    private Boolean selectState;

    @Field.String
    @Field(displayName = "当前通讯的模型")
    private String activeModel;

    @Field.Integer
    @Field(displayName = "当前活动id")
    private Long activeId;

}
