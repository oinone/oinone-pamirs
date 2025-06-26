package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.AddressTypeEnum;

import java.util.List;

@Model.MultiTableInherited(type = "RESOURCE_REGION")
@Model.model(ResourceRegion.MODEL_MODEL)
@Model.Advanced(name = "ResourceRegion", unique = {"code"}, index = {"pCode"})
@Model(displayName = "地区", labelFields = "name")
public class ResourceRegion extends BaseResourceModel {

    public static final String MODEL_MODEL = "resource.ResourceRegion";

    public static final String defaultSourceType = "GD";

    @Field.String
    @Field(displayName = "来源类型", required = true, defaultValue = defaultSourceType)
    private String sourceType;

    @Field.String
    @Field(translate = true, required = true, displayName = "名称")
    private String name;

    @Field.Enum
    @Field(displayName = "地址类型")
    private AddressTypeEnum type;

    @Field.Integer
    @Field(displayName = "层级")
    private Integer level;

    @Deprecated
    @Field.Integer
    @Field(displayName = "父节点ID")
    private Long pid;

    @Field.String
    @Field(displayName = "父节点Code")
    private String pCode;

    @Field.many2one
    @Field.Relation(relationFields = {"countryCode"}, referenceFields = {"code"})
    @Field(displayName = "所属国家")
    private ResourceCountry country;

    @Field.String
    @Field(displayName = "所属国家编码")
    private String countryCode;

    @Field.many2one
    @Field.Relation(relationFields = "pCode", referenceFields = {"code"})
    @Field(displayName = "父节点")
    private ResourceRegion parent;

    @Field.one2many
    @Field.Relation(relationFields = "id", referenceFields = "pid")
    @Field(displayName = "子节点")
    private List<ResourceRegion> children;

    @Field.Boolean
    @Field(displayName = "是否有子节点")
    private Boolean hasChildren;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceRegion.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

    public ResourceRegion cloneSelf() {
        return (ResourceRegion) new ResourceRegion()
                .setSourceType(this.getSourceType())
                .setName(this.getName())
                .setType(this.getType())
                .setLevel(DefaultResourceConstants.REGION_LEVEL_COUNTRY)
                .setCountry(this.getCountry())
                .setHasChildren(Boolean.FALSE)
                .setCode(this.getCode());
    }
}
