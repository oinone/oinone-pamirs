package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.ModelTtypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;

@Model(displayName = "消息追踪字段信息")
@Model.model(MessageTrackingValue.MODEL_MODEL)
public class MessageTrackingValue extends IdModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "pamirs.message.MessageTrackingValue";

    @Validation(check = checkModelModel)
    @Field.String
    @Field(displayName = "源模型")
    private String resModel;

    @Field.Integer
    @Field(displayName = "源模型行记录")
    private Long resId;

    @Validation(check = checkFieldName)
    @Field.String
    @Field(displayName = "字段名")
    private String fieldName;

    @Field.Enum
    @Field(displayName = "字段类型")
    private ModelTtypeEnum fieldType;

    @Field.Enum
    @Field(displayName = "字段值类型")
    private ModelTtypeEnum valueType;

    @Field.Text
    @Field(displayName = "原有值")
    private String oldValue;

    @Field.Text
    @Field(displayName = "更新值")
    private String newValue;

    @Field.many2one
    @Field(displayName = "消息")
    private PamirsMessage message;
}
