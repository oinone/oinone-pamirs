package pro.shushi.pamirs.business.api.entity;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.business.api.enumeration.StaffSizeEnum;
import pro.shushi.pamirs.business.api.enumeration.TeamAuthEnum;
import pro.shushi.pamirs.business.api.enumeration.TeamType;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.resource.api.enmu.CompanyNatureEnum;
import pro.shushi.pamirs.resource.api.model.ResourceAddress;

import java.util.Date;
import java.util.List;

/**
 * FIXME: zbh 20220401 partner多表继承
 */
@Model.model(PamirsCompany.MODEL_MODEL)
@Model.Advanced(unique = {"code"})
@Model(displayName = "公司", summary = "公司", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "C", size = 8)
public class PamirsCompany extends PamirsPartner implements IDataStatus {

    private static final long serialVersionUID = -4241435982340928124L;

    public static final String MODEL_MODEL = "business.PamirsCompany";

    @Field.many2one
    @Field.Relation(relationFields = {"parentCode"}, referenceFields = {"code"})
    @Field(displayName = "上级公司", summary = "上级公司")
    private PamirsCompany parent;

    @Field.String
    @Field(displayName = "上级公司编码", summary = "上级公司编码", invisible = true)
    private String parentCode;

    @Field.Enum
    @Field(displayName = "团队类型", defaultValue = "OTHERS")
    private TeamType teamType;

    @Field.Enum
    @Field(displayName = "认证状态", defaultValue = "UNAUTH")
    private TeamAuthEnum teamAuth;

    /**
     * 企业 {@link pro.shushi.pamirs.business.api.enumeration.EntType}
     * 政府 {@link pro.shushi.pamirs.business.api.enumeration.GovType}
     * 其他 {@link pro.shushi.pamirs.business.api.enumeration.OrgType}
     */
    @Field.String
    @Field(displayName = "类型", summary = "团队类型->类型:   企业->行业,政府->单位,其他->组织类型")
    private String companyType;

    @Field.Enum
    @Field(displayName = "公司性质（组织性质）")
    private CompanyNatureEnum companyNature;

    @Field.Enum
    @Field(displayName = "人员规模")
    private StaffSizeEnum staffSize;

    @Field.Boolean
    @Field(displayName = "加入审核", summary = "加入企业是否需要管理员审核")
    private Boolean joinAudit;

    @Field.Integer
    @Field(displayName = "优先级", defaultValue = MetaDefaultConstants.PRIORITY_VALUE_STRING)
    private Long priority;

    @Field(displayName = "是否支持搜索")
    @Field.Boolean
    private Boolean searchable;

    @Field(displayName = "个性化域名")
    @Field.String
    private String domainName;

    @Field.String
    @Field(displayName = "统一社会信用代码")
    private String creditCode;

    @Field.Integer
    @Field(summary = "Used to order Companies in the company switcher", displayName = "公司顺序", defaultValue = "10")
    private Integer sequence;

    @Field.String(size = 512)
    @Field(displayName = "logoUrl")
    private String logoUrl;

    @Field.many2one
    @Field(displayName = "公司logo")
    private PamirsFile logo;

    @Field.String
    @Field(displayName = "营业执照号")
    private String licenseNo;

    @Field.String
    @Field(displayName = "税务登记号")
    private String taxNo;

    @Field.String
    @Field(displayName = "组织机构代码")
    private String organizationNo;

    @Field.Date
    @Field(displayName = "营业执照注册时间")
    private Date licenseRegisterTime;

    @Field.Date
    @Field(displayName = "营业执照开始时间")
    private Date licenseStartTime;

    @Field.Date
    @Field(displayName = "营业执照结束时间")
    private Date licenseEndTime;

    @Field.String
    @Field(displayName = "营业执照注册资金")
    private String licenseFund;

    @Field.String
    @Field(displayName = "营业执照电话")
    private String licensePhone;

    @Field.String
    @Field(displayName = "法人")
    private String legalPerson;

    @Field.String
    @Field(displayName = "法人身份证号")
    private String legalIdcardNo;

    @Field.many2one
    @Field(displayName = "法人身份证扫描件")
    private PamirsFile legalIdcardFile;

    @Field.many2one
    @Field(displayName = "注册地址")
    private ResourceAddress registerAddress;

    @Field(displayName = "省市县短地址")
    @Field.String
    private String shortAddress;

    @Field.Text
    @Field(displayName = "备注")
    private String remark;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"companyCode"})
    @Field(displayName = "部门列表")
    private List<PamirsDepartment> departmentList;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"companyCode"})
    @Field(displayName = "所属员工列表")
    private List<PamirsEmployee> employeeList;

    @Field.many2one
    @Field.Relation(relationFields = {"responsiblePersonCode"}, referenceFields = {"code"})
    @Field(displayName = "负责人", summary = "公司负责人")
    private PamirsEmployee responsiblePerson;

    @Field.String
    @Field(displayName = "负责人编码")
    private String responsiblePersonCode;
}
