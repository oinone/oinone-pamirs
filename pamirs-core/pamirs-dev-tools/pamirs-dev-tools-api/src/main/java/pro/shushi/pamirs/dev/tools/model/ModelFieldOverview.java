package pro.shushi.pamirs.dev.tools.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(ModelFieldOverview.MODEL_MODEL)
@Model(displayName = "字段对比概况")
public class ModelFieldOverview extends TransientModel {

    private static final long serialVersionUID = 4246858238262336231L;

    public static final String MODEL_MODEL = "tools.ModelFieldOverview";

    @Field.String
    @Field(displayName = "模型编码")
    private String model;

    @Field.String
    @Field(displayName = "字段")
    private String field;

    @Field.Html
    @Field(displayName = "redis字段信息")
    private String redisContext;

    @Field.Html
    @Field(displayName = "db字段信息")
    private String dbContext;

    @Field.Text
    @Field(displayName = "差异内容")
    private String diff;
}
