package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.AddressTypeEnum;
import pro.shushi.pamirs.resource.api.service.ResourceRegionService;

import java.util.List;

@Model.MultiTableInherited(type = "RESOURCE_CITY")
@Model.model(ResourceCity.MODEL_MODEL)
@Model.Advanced(name = "resourceCity", unique = {"code"})
@Model(displayName = "城市", labelFields = "name")
public class ResourceCity extends BaseResourceModel {

    public static final String MODEL_MODEL = "resource.ResourceCity";

    public static final String defaultSourceType = "GD";

    @Field.String
    @Field(displayName = "来源类型", required = true, defaultValue = defaultSourceType)
    private String sourceType;

    @Field.String
    @Field(required = true, displayName = "城市名称")
    private String name;

    @Field.String
    @Field(displayName = "长途区号")
    private String phoneCode;

    @Field.String
    @Field(displayName = "邮政编码")
    private String zipCode;

    @Field.many2one
    @Field.Relation(relationFields = {"countryCode"}, referenceFields = {"code"})
    @Field(displayName = "国家")
    private ResourceCountry country;

    @Field.String
    @Field(displayName = "国家/地区编码")
    private String countryCode;

    @Field.many2one
    @Field.Relation(relationFields = {"provinceCode"}, referenceFields = {"code"})
    @Field(displayName = "省/州")
    private ResourceProvince province;

    @Field.String
    @Field(displayName = "省/州编码")
    private String provinceCode;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = "cityCode")
    @Field(displayName = "区/县")
    private List<ResourceDistrict> districtList;

    @Field.one2many
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceCity.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    @Field(displayName = "关键字映射列表")
    private List<ResourceRegionMapping> mappingList;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceCity.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

    public static ResourceRegion fetchCurrentRegion(ResourceCity city) {
        ResourceRegion parentRegion = CommonApiFactory.getApi(ResourceRegionService.class).queryByCode(city.getProvinceCode());
        if (null == parentRegion) {
            return null;
        }
        return (ResourceRegion) new ResourceRegion()
                .setSourceType(DefaultResourceConstants.SYSTEM_SOURCE_TYPE)
                .setName(city.getName())
                .setType(AddressTypeEnum.City)
                .setLevel(DefaultResourceConstants.REGION_LEVEL_CITY)
                .setPid(parentRegion.getId())
                .setPCode(parentRegion.getCode())
                .setCountryCode(city.getCountryCode())
                .setHasChildren(Boolean.TRUE)
                .setCode(city.getCode());
    }
}
