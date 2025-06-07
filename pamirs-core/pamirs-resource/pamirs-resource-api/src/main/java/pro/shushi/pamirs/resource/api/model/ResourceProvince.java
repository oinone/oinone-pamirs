package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.AddressTypeEnum;
import pro.shushi.pamirs.resource.api.service.ResourceRegionService;

import java.util.List;

@Model.MultiTableInherited(type = "RESOURCE_PROVINCE")
@Model.model(ResourceProvince.MODEL_MODEL)
@Model.Advanced(name = "resourceProvince", unique = {"code"}, index = {"countryCode"})
@Model(displayName = "省/州", labelFields = "name")
public class ResourceProvince extends BaseResourceModel {

    public static final String MODEL_MODEL = "resource.ResourceProvince";

    public static final String DEFAULT_SOURCE_TYPE = "GD";

    @Field.String
    @Field(displayName = "来源类型", required = true, defaultValue = DEFAULT_SOURCE_TYPE)
    private String sourceType;

    @Field.String
    @Field(displayName = "省/州名称")
    private String name;

    @Field.many2one
    @Field.Relation(relationFields = {"countryCode"}, referenceFields = {"code"})
    @Field(displayName = "国家/地区")
    private ResourceCountry country;

    @Field.String
    @Field(displayName = "国家/地区编码")
    private String countryCode;

    @Field.one2many(pageSize = 50)
    @Field.Relation(relationFields = "code", referenceFields = "provinceCode")
    @Field(displayName = "城市")
    private List<ResourceCity> cityList;

    @Field.one2many
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceProvince.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    @Field(displayName = "关键字映射列表")
    private List<ResourceRegionMapping> mappingList;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceProvince.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

    public static ResourceRegion fetchCurrentRegion(ResourceProvince province) {
        ResourceRegion parentRegion = CommonApiFactory.getApi(ResourceRegionService.class).queryByCode(province.getCountryCode());
        if (null == parentRegion) {
            return null;
        }
        return (ResourceRegion) new ResourceRegion()
                .setSourceType(DefaultResourceConstants.SYSTEM_SOURCE_TYPE)
                .setName(province.getName())
                .setType(AddressTypeEnum.Province)
                .setLevel(DefaultResourceConstants.REGION_LEVEL_PROVINCE)
                .setPid(parentRegion.getId())
                .setPCode(parentRegion.getCode())
                .setCountryCode(province.getCountryCode())
                .setHasChildren(Boolean.TRUE)
                .setCode(province.getCode());
    }
}
