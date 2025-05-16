package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.BaseModel;

/**
 * @author shier
 * date  2020/6/19 2:25 下午
 */
@Model.Advanced(name = "MessageChannelUserRel", unique = {"userId,channelId"})
@Model(displayName = "频道用户关系表")
@Model.model(MessageChannelUserRel.MODEL_MODEL)
public class MessageChannelUserRel extends BaseModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageChannelUserRel";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "用户id", summary = "ID字段，唯一自增索引", required = true)
    protected Long userId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "频道id", summary = "ID字段，唯一自增索引", required = true)
    protected Long channelId;
}
