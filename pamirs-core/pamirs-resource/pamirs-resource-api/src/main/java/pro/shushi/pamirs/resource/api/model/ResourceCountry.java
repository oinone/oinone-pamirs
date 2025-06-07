package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.AddressTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.VatLabelEnum;

import java.util.List;

@Model.MultiTableInherited(type = "RESOURCE_COUNTRY")
@Model.model(ResourceCountry.MODEL_MODEL)
@Model.Advanced(name = "ResourceCountry", unique = {"code"})
@Model(displayName = "国家", labelFields = "name")
public class ResourceCountry extends BaseResourceModel {

    private static final long serialVersionUID = -460857078219886471L;

    public static final String MODEL_MODEL = "resource.ResourceCountry";

    public static final String DEFAULT_SOURCE_TYPE = "GD";

    @Field.String
    @Field(displayName = "来源类型", required = true, defaultValue = ResourceCountry.DEFAULT_SOURCE_TYPE)
    private String sourceType;

    @Field.String
    @Field(required = true, translate = true, displayName = "国家/地区名称")
    private String name;

    @Field.String(size = 256)
    @Field(displayName = "国家/地区完整名称")
    private String completeName;

    @Field.Enum
    @Field(displayName = "消费税显示名称", summary = "国家的消息税号标签(如大陆/英国叫VAT，加拿大/澳洲叫GST)")
    private VatLabelEnum vatLabel;

    @Field.String
    @Field(required = true, displayName = "长途区号")
    private String phoneCode;

    @Field.many2one
    @Field.Relation(relationFields = {"currencyCode"}, referenceFields = {"code"})
    @Field(displayName = "使用币种")
    private ResourceCurrency currency;

    @Field.String
    @Field(displayName = "使用币种编码")
    private String currencyCode;

    @Field.many2one
    @Field.Relation(relationFields = {"langCode"}, referenceFields = {"code"})
    @Field(displayName = "官方语言")
    private ResourceLang lang;

    @Field.String
    @Field(displayName = "语言编码")
    private String langCode;

    @Field.many2one
    @Field.Relation(relationFields = {"countryGroupCode"}, referenceFields = {"code"})
    @Field(displayName = "洲", summary = "大洲")
    private ResourceCountryGroup countryGroup;

    @Field.String
    @Field(displayName = "洲编码")
    private String countryGroupCode;

    @Field.one2many(pageSize = 50)
    @Field.Relation(relationFields = {"code"}, referenceFields = "countryCode")
    @Field(displayName = "省份")
    private List<ResourceProvince> provinceList;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"countryCode"})
    @Field(displayName = "地区")
    private List<ResourceRegion> regionList;

//    @Field.many2one
//    @Field(  displayName = "地址显示格式")
//    private ResourceAddrFormat addrFormat;

    @Field.Text
    @Field(displayName = "地址显示格式")
    private String addrFormat;

   /* @Field.many2one
    @Field(displayName = "地址输入视图")
    private View addressView;*/ //todo view huidao

    @Field.many2one
    @Field(displayName = "国旗", summary = "国旗")
    private PamirsFile flag;

    @Field.Text
    @Field(displayName = "姓名显示规则", summary = "国家的姓名显示规则(如美国名在前，中国姓在前)")
    private String namePosition;

    @Field.one2many
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceCountry.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    @Field(displayName = "关键字映射列表")
    private List<ResourceRegionMapping> mappingList;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceCountry.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

    public static ResourceRegion fetchCurrentRegion(ResourceCountry country) {
        return (ResourceRegion) new ResourceRegion()
                .setSourceType(DefaultResourceConstants.SYSTEM_SOURCE_TYPE)
                .setName(country.getName())
                .setType(AddressTypeEnum.Country)
                .setLevel(DefaultResourceConstants.REGION_LEVEL_COUNTRY)
                .setCountryCode(country.getCode())
                .setHasChildren(Boolean.TRUE)
                .setCode(country.getCode());
    }
}
