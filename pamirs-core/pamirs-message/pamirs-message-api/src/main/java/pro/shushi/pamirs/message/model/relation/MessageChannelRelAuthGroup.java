package pro.shushi.pamirs.message.model.relation;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 消息频道关联角色
 *
 * @author Adamancy Zhang at 09:28 on 2024-01-05
 */
@Model.model(MessageChannelRelAuthGroup.MODEL_MODEL)
@Model(displayName = "消息频道关联角色")
public class MessageChannelRelAuthGroup extends BaseRelation {

    private static final long serialVersionUID = 2910715838297604713L;

    public static final String MODEL_MODEL = "MessageChannelRelAuthGroup";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "消息频道ID")
    private Long messageChannelId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "角色ID")
    private Long authRoleId;
}
