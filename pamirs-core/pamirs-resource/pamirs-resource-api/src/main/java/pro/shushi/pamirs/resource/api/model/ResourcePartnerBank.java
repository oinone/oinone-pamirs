package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * @Author: haibo
 * @email: xf.z@shushi.pro
 * @Date: 2019/8/6 3:19 PM
 */
@Model.model(ResourcePartnerBank.MODEL_MODEL)
@Model.Advanced(name = "resourcePartnerBank")
@Model(displayName = "合作伙伴银行账号", labelFields = "name")
public class ResourcePartnerBank extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourcePartnerBank";

    @Field.String
    @Field(displayName = "银行账号说明")
    private String name;

    @Field.many2one
    @Field(displayName = "银行地址")
    ResourceAddress address;

    @Field.String
    @Field(displayName = "电话")
    String phone;

    @Field.String
    @Field(displayName = "邮箱")
    String email;

    @Field.String
    @Field(displayName = "开户行账号")
    String bankAccount;

    @Field.many2one
    @Field(displayName = "开户行")
    ResourceBank resourceBank;

    @Field.Boolean
    @Field(displayName = "是否默认")
    Boolean isDefault;

    @Field.many2one
    @Field(displayName = "开户国家")
    private ResourceCountry country;

    @Field.String
    @Field(displayName = "开户银行名称")
    private String resourceBankName;

    @Field.Integer
    @Field(displayName = "关联对象的唯一键值", required = true, summary = "O2M预留字段，建议手动配置O2M字段的referenceFields为该属性值")
    private Long relationId;

    @Field.String
    @Field(displayName = "关联类型", defaultValue = "NORMAL", summary = "区分银行类型,默认的没有业务的是NORMAL")
    private String relationTags;

}
