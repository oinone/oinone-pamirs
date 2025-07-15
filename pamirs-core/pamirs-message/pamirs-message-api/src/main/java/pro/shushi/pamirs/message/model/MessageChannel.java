package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.message.enmu.MessageChannelOpenTypeEnum;
import pro.shushi.pamirs.message.enmu.MessageChannelTypeEnum;
import pro.shushi.pamirs.message.model.relation.MessageChannelRelAuthGroup;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

@Model(displayName = "消息频道")
@Model.model(MessageChannel.MODEL_MODEL)
public class MessageChannel extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageChannel";

    @Field.String
    @Field(required = true, unique = true, displayName = "名称")
    private String name;

    @Field.Enum
    @Field(displayName = "频道类型")
    private MessageChannelTypeEnum channelType;

    @Field.many2many(through = "MessageChannelRelPamirsPartner")
    @Field(displayName = "用户")
    private List<PamirsUser> partners;

    @Field.many2many(through = "MessageChannelRelPamirsMessage")
    @Field(displayName = "频道消息")
    private List<PamirsMessage> messages;

    @Field.Enum
    @Field(displayName = "谁能关注频道？")
    private MessageChannelOpenTypeEnum openType;

    @Field.many2one
    @Field.Relation(relationFields = {"authorRoleId"}, referenceFields = {"id"})
    @Field(displayName = "授权的角色")
    private AuthRole authorRole;

    @Field.Integer
    @Field(displayName = "授权的角色ID")
    private Long authorRoleId;

    @Field.many2many(through = MessageChannelRelAuthGroup.MODEL_MODEL, relationFields = {"messageChannelId"}, referenceFields = {"authRoleId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "自动订阅的角色")
    private List<AuthRole> subscribeRoles;

    @Field.String(size = 4096)
    @Field(displayName = "icon地址")
    @UxForm.FieldWidget(@UxWidget(widget = "UploadImg"))
    private String iconUrl;

}
