package pro.shushi.pamirs.business.api.tmodel;

import pro.shushi.pamirs.business.api.enumeration.EntType;
import pro.shushi.pamirs.business.api.enumeration.GovType;
import pro.shushi.pamirs.business.api.enumeration.OrgType;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * CompanyType
 *
 * @author yakir on 2022/09/13 18:07.
 */
@Model(displayName = "企业/政府/其他类型")
@Model.model(CompanyType.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class CompanyType extends TransientModel {

    private static final long serialVersionUID = -1081070043543537696L;

    public static final String MODEL_MODEL = "business.CompanyType";

    @Field.String
    @Field(displayName = "类型Code")
    private String type;

    @Field.String
    @Field(displayName = "类型显示名称")
    private String displayName;

    @Field.String
    @Field(displayName = "父Code")
    private String parentType;

    @Field.many2one
    @Field(displayName = "父类型")
    @Field.Relation(relationFields = {"parentCode"}, referenceFields = {"code"})
    private CompanyType parent;

    @Field.Integer
    @Field(displayName = "层级")
    private Integer dept;

    public static CompanyType instance(EntType entType) {
        CompanyType companyType = new CompanyType();
        companyType.setType(entType.getValue());
        companyType.setDisplayName(entType.getDisplayName());
        return companyType;
    }

    public static CompanyType instance(OrgType entType) {
        CompanyType companyType = new CompanyType();
        companyType.setType(entType.getValue());
        companyType.setDisplayName(entType.getDisplayName());
        return companyType;
    }

    public static CompanyType instance(GovType entType) {
        CompanyType companyType = new CompanyType();
        companyType.setType(entType.getValue());
        companyType.setDisplayName(entType.getDisplayName());
        return companyType;
    }
}
