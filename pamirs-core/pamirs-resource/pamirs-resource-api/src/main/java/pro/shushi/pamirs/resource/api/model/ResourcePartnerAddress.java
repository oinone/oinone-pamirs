package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.PartnerAddressTypeEnum;

/**
 * @Author: haibo
 * @email: xf.z@shushi.pro
 * @Date: 2019-08-28 23:14
 */
@Model.model(ResourcePartnerAddress.MODEL_MODEL)
@Model.Advanced(name = "resourcePartnerAddress")
@Model(displayName = "合作伙伴地址", labelFields = "fullAddress")
public class ResourcePartnerAddress extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourcePartnerAddress";

    @Field.String
    @Field(displayName = "名称")
    private String name;

    @Field.Enum
    @Field(displayName = "收货地址类型")
    private PartnerAddressTypeEnum addressType;

    @Field.String
    @Field(displayName = "联系人")
    private String contactMan;

    @Field.String
    @Field(displayName = "联系人真实姓名")
    private String contactManRealName;

    @Field.String
    @Field(displayName = "联系人座机", summary = "一般给公司联系人使用")
    private String contactLandPhone;

    @Field.String
    @Field(displayName = "联系人身份证号", summary = "海关或者某些快递需要收件人的身份证号")
    private String contactManIdentityNo;

    @Field.String
    @Field(displayName = "联系电话")
    private String contactPhone;

    @Field.String
    @Field(displayName = "联系邮箱")
    private String contactEmail;

    @Field.String
    @Field(displayName = "邮编")
    private String zipCode;

    @Field.many2one
    @Field.Relation(relationFields = {"regionCode"}, referenceFields = {"code"})
    @Field(displayName = "地址-区域")
    private ResourceRegion region;

    /**
     * 如果行政层级是省市县，则改code为县的code
     * 如果行政层级是省市县街道，则改code为街道的code
     */
    @Field.String(size = 32)
    @Field(displayName = "地区码", summary = "最末级的地区码")
    private String regionCode;

    @Field.String(size = 512)
    @Field(displayName = "详细地址")
    private String detailAddress;

    @Deprecated
    @Field.many2one
    @Field(displayName = "地址")
    private ResourceAddress address;

    @Field.Text
    @Field.Related(related = {"address", "street2"})
    @Field(displayName = "地址-详细地址", summary = "设计器4.0的地址组件需要与address的详细地址做映射")
    private String street2;

    @Field.String
    @Field.Related(related = {"address", "fullAddress"})
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "显示地址")
    private String fullAddress;

    @Field.Boolean
    @Field(displayName = "是否默认")
    private Boolean isDefault;

    @Field.String
    @Field(displayName = "岗位/职务")
    private String position;

    @Field.String
    @Field(displayName = "备注")
    private String remark;

    @Field.Integer
    @Field(displayName = "关联对象的唯一键值", required = true, summary = "O2M预留字段，建议手动配置O2M字段的referenceFields为该属性值")
    private Long relationId;

    @Field.String
    @Field(displayName = "相同关联对象的唯一区分标记")
    private String relationTags;

    @Field.String
    @Field(displayName = "所在地区", store = NullableBoolEnum.FALSE)
    private String area;

    @Field.String
    @Field(displayName = "外部编码")
    private String outCode;
}
