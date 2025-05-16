package pro.shushi.pamirs.resource.api.model;

import com.google.common.base.Joiner;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.AddressSourceTypeEnum;

@Model.model(ResourceAddress.MODEL_MODEL)
@Model(displayName = "地址", labelFields = "fullAddress")
public class ResourceAddress extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceAddress";

    @Field.Enum
    @Field(displayName = "地址源类型")
    private AddressSourceTypeEnum sourceType;

    /**
     * @deprecated 2.3.0
     */
    @Deprecated
    @Field.Integer
    @Field(displayName = "国家/地区Id")
    private Long country;

    /**
     * @deprecated 2.3.0
     */
    @Deprecated
    @Field.Integer
    @Field(displayName = "省/州Id")
    private Long province;

    /**
     * @deprecated 2.3.0
     */
    @Deprecated
    @Field.Integer
    @Field(displayName = "城市Id")
    private Long city;

    /**
     * @deprecated 2.3.0
     */
    @Deprecated
    @Field.Integer
    @Field(displayName = "区/县Id")
    private Long district;

    /**
     * @deprecated 2.3.0
     */
    @Deprecated
    @Field.Integer
    @Field(displayName = "街道Id")
    private Long street;

    @Field.many2one
    @Field.Relation(relationFields = {"countryCode"}, referenceFields = {"code"})
    @Field(displayName = "国家/地区")
    private ResourceCountry originCountry;

    @Field.many2one
    @Field.Relation(relationFields = {"provinceCode"}, referenceFields = {"code"})
    @Field(displayName = "省/州")
    private ResourceProvince originProvince;

    @Field.many2one
    @Field.Relation(relationFields = {"cityCode"}, referenceFields = {"code"})
    @Field(displayName = "城市")
    private ResourceCity originCity;

    @Field.many2one
    @Field.Relation(relationFields = {"districtCode"}, referenceFields = {"code"})
    @Field(displayName = "区/县")
    private ResourceDistrict originDistrict;

    @Field.many2one
    @Field.Relation(relationFields = {"streetCode"}, referenceFields = {"code"})
    @Field(displayName = "街道")
    private ResourceStreet originStreet;

    @Field.many2one
    @Field.Relation(relationFields = {"countryCode"}, referenceFields = {"code"})
    @Field(displayName = "国家/地区-区域")
    private ResourceRegion originRegionCountry;

    @Field.many2one
    @Field.Relation(relationFields = {"provinceCode"}, referenceFields = {"code"})
    @Field(displayName = "省/州-区域")
    private ResourceRegion originRegionProvince;

    @Field.many2one
    @Field.Relation(relationFields = {"cityCode"}, referenceFields = {"code"})
    @Field(displayName = "城市-区域")
    private ResourceRegion originRegionCity;

    @Field.many2one
    @Field.Relation(relationFields = {"districtCode"}, referenceFields = {"code"})
    @Field(displayName = "区/县-区域")
    private ResourceRegion originRegionDistrict;

    @Field.many2one
    @Field.Relation(relationFields = {"streetCode"}, referenceFields = {"code"})
    @Field(displayName = "街道-区域")
    private ResourceRegion originRegionStreet;

    @Field.String
    @Field(displayName = "国家/地区编码")
    private String countryCode;

    @Field.String
    @Field(displayName = "国家/地区名称", store = NullableBoolEnum.FALSE)
    @Field.Related(related = {"originCountry","name"})
    private String countryName;

    @Field.String
    @Field(displayName = "省/州编码")
    private String provinceCode;

    @Field.String
    @Field(displayName = "省/州名称", store = NullableBoolEnum.FALSE)
    @Field.Related(related = {"originProvince","name"})
    private String provinceName;

    @Field.String
    @Field(displayName = "城市编码")
    private String cityCode;

    @Field.String
    @Field(displayName = "城市名称", store = NullableBoolEnum.FALSE)
    @Field.Related(related = {"originCity","name"})
    private String cityName;

    @Field.String
    @Field(displayName = "区/县编码")
    private String districtCode;

    @Field.String
    @Field(displayName = "区/县名称", store = NullableBoolEnum.FALSE)
    @Field.Related(related = {"originDistrict","name"})
    private String districtName;

    @Field.String
    @Field(displayName = "街道编码")
    private String streetCode;

    @Field.String
    @Field(displayName = "街道名称", store = NullableBoolEnum.FALSE)
    @Field.Related(related = {"originStreet","name"})
    private String streetName;

    @Field.Text
    @Field(displayName = "地址")
    private String street2;

    @Field.Text
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "完整地址")
    private String fullAddress;

    public String createArea() {
        return Joiner.on("").skipNulls().join(getCountryName(), getProvinceName(), getCityName(), getDistrictName(), getStreetName());
    }
}
