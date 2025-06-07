package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.model(OutResourceRelation.MODEL_MODEL)
@Model.Advanced(name = "OutResourceRelation", unique = {"model,sourceType,relationCode,outCode"})
@Model(displayName = "外部资源关系", labelFields = "name")
public class OutResourceRelation extends IdModel {

    public static final String MODEL_MODEL = "resource.OutResourceRelation";

    @Field.String
    @Field(displayName = "关联模型", required = true)
    private String model;

    @Field.String
    @Field(displayName = "来源类型", required = true, summary = "用于标记该资源存储的外部字段属性存在于哪个外部系统")
    private String sourceType;

    @Field.String
    @Field(displayName = "关联编码", required = true, summary = "推荐使用关联编码作为唯一值进行关联")
    private String relationCode;

    @Field.String
    @Field(displayName = "外部编码", required = true, summary = "推荐使用外部编码进行关联")
    private String outCode;

    @Field.Text
    @Field(displayName = "拓展字段", summary = "对于必要存储的数据内容，建议使用JSON存储")
    private String extra;

}
