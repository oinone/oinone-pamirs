package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

@Model.model(PamirsOrganization.MODEL_MODEL)
@Model.Advanced(name = "PamirsOrganization", unique = {"code"}, index = {"name"})
@Model(displayName = "组织", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "O", size = 8)
public class PamirsOrganization extends BizCodeModel implements IDataStatus {

    private static final long serialVersionUID = -6061027818056251648L;

    public static final String MODEL_MODEL = "business.PamirsOrganization";

    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "组织类型", required = true)
    private String organizationType;

    @Field.String(size = 255)
    @Field(displayName = "组织名称", required = true)
    private String name;

    @Field.Enum
    @Field(displayName = "数据状态", required = true, defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    @Field.many2many(through = "OrganizationRelEmployee", relationFields = {"organizationType", "organizationCode"}, referenceFields = {"employeeType", "employeeCode"})
    @Field.Relation(relationFields = {"organizationType", "code"}, referenceFields = {"employeeType", "code"})
    @Field(displayName = "员工列表")
    private List<PamirsEmployee> relationEmployeeList;

}
