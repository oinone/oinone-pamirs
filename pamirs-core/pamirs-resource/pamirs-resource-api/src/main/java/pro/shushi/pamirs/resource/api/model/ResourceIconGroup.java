package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

@Model(displayName = "图标分组", labelFields="name")
@Model.Advanced(unique = "name")
@Model.model(ResourceIconGroup.MODEL_MODEL)
public class ResourceIconGroup extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceIconGroup";

    @Field.String
    @Field(displayName = "分组名称")
    private String name;

    @Field.one2many
    @Field(displayName = "全部图标")
    @Field.Relation(relationFields = {"id"}, referenceFields = {"groupId"})
    private List<ResourceIcon> iconList;

    @Field.Boolean
    @Field(displayName = "是否系统分组")
    private Boolean sys;

    @Field.Integer
    @Field(displayName = "更新批次")
    private Long batchCode;

    @Field.Integer
    @Field(displayName = "图标数量",store = NullableBoolEnum.FALSE)
    private Long iconNum;
}
