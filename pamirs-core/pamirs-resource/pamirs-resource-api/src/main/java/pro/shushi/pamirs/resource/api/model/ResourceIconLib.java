package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.IconLibTypeEnum;

import java.util.List;

@Model(displayName = "图标库")
@Model.Advanced(unique = {"outId,type"})
@Model.model(ResourceIconLib.MODEL_MODEL)
public class ResourceIconLib extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceIconLib";

    @Field.String
    @Field(displayName = "图标库id")
    private String outId;

    @Field.Enum
    @Field(displayName = "图标库类型")
    private IconLibTypeEnum type;

    @Field.Integer
    @Field(displayName = "分组id")
    private Long groupId;

    @Field.one2one
    @Field(displayName = "分组")
    @Field.Relation(relationFields = {"groupId"}, referenceFields = {"id"})
    private ResourceIconGroup group;

    @Field.String
    @Field(displayName = "图标库名称")
    private String name;

    @Field.one2many
    @Field(displayName = "图标库全部图标")
    @Field.Relation(relationFields = {"id"}, referenceFields = {"libId"})
    private List<ResourceIcon> iconList;

    @Field.String
    @Field(displayName = "图标库前缀")
    private String fontClassPrefix;

    @Field.String
    @Field(displayName = "css文件链接")
    private List<String> cssUrls;

    @Field.String
    @Field(displayName = "js文件链接")
    private List<String> jsUrls;

    @Field(displayName = "字体文件链接", store = NullableBoolEnum.TRUE, multi = true)
    @Field.Advanced(columnDefinition = "varchar(1024)")
    private List<String> fontUrls;

    @Field.Text
    @Field(displayName = "描述")
    private String description;
}
