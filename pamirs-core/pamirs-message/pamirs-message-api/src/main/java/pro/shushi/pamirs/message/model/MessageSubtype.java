package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

@Model(displayName = "消息子类型")
@Model.model(MessageSubtype.MODEL_MODEL)
public class MessageSubtype extends IdModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageSubtype";

    @Field.String
    @Field(displayName = "消息类型")
    private String name;

    @Field.many2one
    @Field(displayName = "模型")
    private ModelDefinition model;

    @Field.String(size = 1024)
    @Field(displayName = "简介")
    private String desc;

    @Field.Boolean
    @Field(displayName = "默认选择状态")
    private Boolean defaultSelect;

    @Field.many2one
    @Field(displayName = "字段")
    private ModelField field;

    @Field.Integer
    @Field(displayName = "序号", summary = "定义模型的订阅类型的排序规则")
    private Integer sequence;

}
