package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.Advanced(name = "MessageSource")
@Model(displayName = "MessageSource")
@Model.model(MessageSource.MODEL_MODEL)
public class MessageSource extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageSource";

    @Field.Enum
    @Field(displayName = "消息通知类型")
    private MessageEngineTypeEnum type;

    @Field.Text
    @Field(displayName = "属性")
    private String attribute;

    @Field.Boolean
    @Field(defaultValue = "true", summary = "是否激活")
    private Boolean active;
}
