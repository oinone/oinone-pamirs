package pro.shushi.pamirs.dev.tools.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(FunctionOverview.MODEL_MODEL)
@Model(displayName = "函数概况", labelFields = "fun")
public class FunctionOverview extends TransientModel {

    private static final long serialVersionUID = 4246858238262336231L;

    public static final String MODEL_MODEL = "tools.FunctionOverview";

    @Field.String
    @Field(displayName = "命令空间")
    private String namespace;

    @Field.String
    @Field(displayName = "FUN")
    private String fun;

    @Field.Html
    @Field(displayName = "Redis信息")
    private String redisContext;

    @Field.Html
    @Field(displayName = "db信息")
    private String dbContext;

    @Field.Text
    @Field(displayName = "差异内容")
    private String diff;

}
