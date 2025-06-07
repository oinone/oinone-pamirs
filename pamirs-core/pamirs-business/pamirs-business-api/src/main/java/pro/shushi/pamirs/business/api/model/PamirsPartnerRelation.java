package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.resource.api.enmu.AuditStatusEnum;

@Model.MultiTable(typeField = "relationType")
@Model.model(PamirsPartnerRelation.MODEL_MODEL)
@Model.Advanced(unique = {"model,code", "originPartnerCode,targetPartnerCode,relationType"}, index = {"targetPartnerCode"})
@Model(displayName = "合作伙伴关系", labelFields = {"name"})
public class PamirsPartnerRelation extends BizCodeModel implements IDataStatus {

    private static final long serialVersionUID = 1491418323644351238L;

    public static final String MODEL_MODEL = "business.PamirsPartnerRelation";

    public static final String RELATION_TYPE = "NONE";

    @Field.String
    @Field(displayName = "模型编码", required = true, invisible = true)
    private String model;

    @Field.String
    @Field(displayName = "名称", summary = "存储的是targetPartnerName，界面使用时候注意", required = true)
    private String name;

    @Field.String
    @Field(displayName = "主合作伙伴编码", required = true, invisible = true)
    private String originPartnerCode;

    @Field.many2one
    @Field.Relation(relationFields = {"originPartnerCode"}, referenceFields = {"code"})
    @Field(displayName = "主合作伙伴", required = true)
    private PamirsPartner originPartner;

    @Field.String
    @Field(displayName = "目标合作伙伴编码", required = true, invisible = true)
    private String targetPartnerCode;

    @Field.many2one
    @Field.Relation(relationFields = {"targetPartnerCode"}, referenceFields = {"code"})
    @Field(displayName = "目标合作伙伴", required = true)
    private PamirsPartner targetPartner;

    @Field.String
    @Field.Related(related = {"originPartner", "name"})
    @Field(displayName = "主合作伙伴名称")
    private String originPartnerName;

    @Field.String
    @Field.Related(related = {"originPartner", "partnerType"})
    @Field(displayName = "主合作伙伴类型")
    private String originPartnerType;

    @Field.String
    @Field.Related(related = {"originPartner", "phone"})
    @Field(displayName = "主合作伙伴联系手机")
    private String originPartnerPhone;

    @Field.String
    @Field.Related(related = {"originPartner", "email"})
    @Field(displayName = "主合作伙伴联系邮箱")
    private String originPartnerEmail;

    @Field.String
    @Field.Related(related = {"targetPartner", "name"})
    @Field(displayName = "目标合作伙伴名称")
    private String targetPartnerName;

    @Field.String
    @Field.Related(related = {"targetPartner", "partnerType"})
    @Field(displayName = "目标合作伙伴类型")
    private String targetPartnerType;

    @Field.String
    @Field.Related(related = {"targetPartner", "phone"})
    @Field(displayName = "目标合作伙伴联系手机")
    private String targetPartnerPhone;

    @Field.String
    @Field.Related(related = {"targetPartner", "email"})
    @Field(displayName = "目标合作伙伴联系邮箱")
    private String targetPartnerEmail;

    @Field.String
    @Field(displayName = "关系类型", required = true)
    private String relationType;

    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED", required = true)
    private DataStatusEnum dataStatus;

    @Field.Enum
    @Field(displayName = "审核状态", defaultValue = "SUCCESS", required = true)
    private AuditStatusEnum auditStatus;

    @Field.Boolean
    @Field(displayName = "是否默认", defaultValue = "false")
    private Boolean isDefault;

}
