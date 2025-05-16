package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.AddressTypeEnum;
import pro.shushi.pamirs.resource.api.service.ResourceRegionService;

import java.util.List;

@Model.MultiTableInherited(type = "RESOURCE_DISTRICT")
@Model.model(ResourceDistrict.MODEL_MODEL)
@Model.Advanced(name = "resourceDistrict", unique = {"code"})
@Model(displayName = "区/县", labelFields = "name")
public class ResourceDistrict extends BaseResourceModel {

    public static final String MODEL_MODEL = "resource.ResourceDistrict";

    public static final String defaultSourceType = "GD";

    @Field.String
    @Field(displayName = "来源类型", required = true, defaultValue = defaultSourceType)
    private String sourceType;

    @Field.String
    @Field(displayName = "区县名称")
    private String name;

    @Field.String
    @Field(displayName = "城市地区邮政编码")
    private String zipCode;

    @Field.many2one
    @Field.Relation(relationFields = {"countryCode"}, referenceFields = {"code"})
    @Field(displayName = "国家")
    private ResourceCountry country;

    @Field.String
    @Field(displayName = "国家编码")
    private String countryCode;

    @Field.many2one
    @Field.Relation(relationFields = {"provinceCode"}, referenceFields = {"code"})
    @Field(displayName = "省")
    private ResourceProvince province;

    @Field.String
    @Field(displayName = "省编码")
    private String provinceCode;

    @Field.many2one
    @Field.Relation(relationFields = {"cityCode"}, referenceFields = {"code"})
    @Field(displayName = "市")
    private ResourceCity city;

    @Field.String
    @Field(displayName = "市编码")
    private String cityCode;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"districtCode"})
    @Field(displayName = "街道")
    private List<ResourceStreet> streetList;

    @Field.one2many
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceDistrict.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    @Field(displayName = "关键字映射列表")
    private List<ResourceRegionMapping> mappingList;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceDistrict.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

    public static ResourceRegion fetchCurrentRegion(ResourceDistrict district) {
        ResourceRegion parentRegion = CommonApiFactory.getApi(ResourceRegionService.class).queryByCode(district.getCityCode());
        if (null == parentRegion) {
            return null;
        }
        return (ResourceRegion) new ResourceRegion()
                .setSourceType(DefaultResourceConstants.SYSTEM_SOURCE_TYPE)
                .setName(district.getName())
                .setType(AddressTypeEnum.District)
                .setLevel(DefaultResourceConstants.REGION_LEVEL_DISTRICT)
                .setPid(parentRegion.getId())
                .setPCode(parentRegion.getCode())
                .setCountryCode(district.getCountryCode())
                .setHasChildren(Boolean.TRUE)
                .setCode(district.getCode());
    }
}
