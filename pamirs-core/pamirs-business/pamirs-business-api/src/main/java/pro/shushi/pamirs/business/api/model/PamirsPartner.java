package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerOptionBitEnum;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.AuditStatusEnum;
import pro.shushi.pamirs.resource.api.model.OutResourceRelation;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Model.model(PamirsPartner.MODEL_MODEL)
@Model.Advanced(unique = "code", index = {"name", "phone"})
@Model(displayName = "合作伙伴", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "PT", size = 8)
public class PamirsPartner extends BizCodeModel implements IDataStatus {

    private static final long serialVersionUID = 4485384402774009344L;

    public static final String MODEL_MODEL = "business.PamirsPartner";

    @Field.String
    @Field(displayName = "名称", required = true)
    private String name;

    @Field.String(size = 255)
    @Field(displayName = "英文名称", summary = "英文名称")
    private String englishName;

    @Field.Enum
    @Field(displayName = "合作伙伴类型", required = true)
    private BusinessPartnerTypeEnum partnerType;

    @Field.Enum(size = 128)
    @Field(displayName = "合作伙伴标记")
    private List<BusinessPartnerOptionBitEnum> partnerOption;

    @Field.String
    @Field(displayName = "联系手机")
    private String phone;

    @Field.String
    @Field(displayName = "联系邮箱")
    private String email;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"originPartnerCode"})
    private List<PamirsPartnerRelation> partnerRelationList;

    @Field.Enum
    @Field(displayName = "数据状态", required = true, defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    @Field.Enum
    @Field(displayName = "审核状态", defaultValue = "PENDING_AUDIT")
    private AuditStatusEnum auditStatus;

    @Field.Date
    @Field(displayName = "审核通过时间/入驻时间")
    private Date approvedDate;

    @Field(displayName = "扩展字段", serialize = Field.serialize.JSON, store = NullableBoolEnum.TRUE, invisible = true)
    @Field.Advanced(columnDefinition = "varchar(1024)")
    private Map<String, Object> features;

    @Field.one2many
    @Field(displayName = "外部资源关联列表", invisible = true)
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + PamirsPartner.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE},
            referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

}
