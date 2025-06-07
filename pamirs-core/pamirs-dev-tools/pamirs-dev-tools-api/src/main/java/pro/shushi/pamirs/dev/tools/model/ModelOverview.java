package pro.shushi.pamirs.dev.tools.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Model.model(ModelOverview.MODEL_MODEL)
@Model(displayName = "模型对比概况", labelFields = "name")
public class ModelOverview extends TransientModel {

    private static final long serialVersionUID = 4246858238262336231L;

    public static final String MODEL_MODEL = "tools.ModelOverview";

    @Field.String
    @Field(displayName = "模型编码")
    private String model;

    @Field.Html
    @Field(displayName = "Redis信息")
    private String redisContext;

    @Field.Html
    @Field(displayName = "db信息")
    private String dbContext;

    @Field.Text
    @Field(displayName = "差异内容")
    private String diff;

    @Field.many2one
    @Field(displayName = "redis信息")
    private RedisInfo redisInfo;

    @Field.one2many
    @Field(displayName = "字段列表")
    private List<ModelFieldOverview> modelFieldOverviews;


    @Field.one2many
    @Field(displayName = "ServerAction列表")
    private List<ModelServerActionOverview> serverActionOverviews;

    @Field.one2many
    @Field(displayName = "ViewAction列表")
    private List<ModelViewActionOverview> viewActionOverviews;

    @Field.one2many
    @Field(displayName = "urlAction列表")
    private List<ModelUrlActionOverview> urlActionOverviews;


}
