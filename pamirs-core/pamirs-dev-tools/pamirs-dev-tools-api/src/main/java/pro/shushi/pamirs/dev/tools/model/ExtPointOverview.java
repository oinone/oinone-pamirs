package pro.shushi.pamirs.dev.tools.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

@Model.model(ExtPointOverview.MODEL_MODEL)
@Model(displayName = "扩展点概况", labelFields = "name")
public class ExtPointOverview extends TransientModel {

    private static final long serialVersionUID = 4246858238262336231L;

    public static final String MODEL_MODEL = "tools.ExtPointOverview";

    @Field.String
    @Field(displayName = "执行函数命名空间")
    private String executeNamespace;

    @Field.String
    @Field(displayName = "执行函数编码")
    private String executeFun;

    @Field.String
    @Field(displayName = "命名空间")
    private String namespace;

    @Field.String
    @Field(displayName = "函数编码")
    private String name;

    @Field.String
    @Field(displayName = "表达式")
    private String expression;

    @Field.Html
    @Field(displayName = "Redis信息")
    private String redisContext;

    @Field.Html
    @Field(displayName = "db信息")
    private String dbContext;

    @Field.Text
    @Field(displayName = "差异内容")
    private String diff;

    @Field.one2many
    @Field(displayName = "扩展列表")
    private List<ExtPointOverview> extPointOverviews;

}
