package pro.shushi.pamirs.business.api.pmodel;

import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

/**
 * @author yeshenyue on 2025/6/6 11:42.
 */
@Model(displayName = "部门员工关系代理")
@Model.model(DepartmentRelEmployeeProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class DepartmentRelEmployeeProxy extends DepartmentRelEmployee {

    public static final String MODEL_MODEL = "business.DepartmentRelEmployeeProxy";
    private static final long serialVersionUID = 396608526638149256L;

    public static final String DEFAULT_CODE = "0";

    @Field.String
    @Field(displayName = "公司编码")
    private String companyCode;

    @Field.many2one
    @Field.Relation(relationFields = {"employeeCode"}, referenceFields = {"code"})
    @Field(displayName = "员工")
    private PamirsEmployee employee;

    @Field.Related({"employee", "department"})
    @Field(displayName = "所属主部门")
    private String employeeDepartment;

    @Field.Related({"employee", "id"})
    @Field(displayName = "员工id")
    private Long employeeId;

    @Field.Related({"employee", "name"})
    @Field(displayName = "员工名称", store = NullableBoolEnum.FALSE)
    private String employeeName;

    @Field.Related({"employee", "defaultBindingUser"})
    @Field(displayName = "绑定用户")
    private PamirsUser employeeDefaultBindingUser;

    @Field.many2one
    @Field.Relation(relationFields = {"departmentCode"}, referenceFields = {"code"})
    @Field(displayName = "部门")
    private PamirsDepartment department;

    @Field.Related({"department", "id"})
    @Field(displayName = "部门id")
    private Long departmentId;

    @Field.Related({"department", "name"})
    @Field(displayName = "部门名称")
    private String departmentName;

    @Field.Related({"department", "parent"})
    @Field(displayName = "上级部门")
    private PamirsDepartment parentDepartment;
}
