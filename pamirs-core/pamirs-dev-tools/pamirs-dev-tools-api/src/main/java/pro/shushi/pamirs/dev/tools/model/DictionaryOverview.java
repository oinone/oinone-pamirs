package pro.shushi.pamirs.dev.tools.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(DictionaryOverview.MODEL_MODEL)
@Model(displayName = "数据字典概况", labelFields = "name")
public class DictionaryOverview extends TransientModel {

    private static final long serialVersionUID = 4246858238262336231L;

    public static final String MODEL_MODEL = "tools.DictionaryOverview";

    @Field.String
    @Field(displayName = "数据字典")
    private String dictionary;

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
