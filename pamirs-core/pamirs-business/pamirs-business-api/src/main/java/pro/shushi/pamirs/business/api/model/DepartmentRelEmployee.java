package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * @author shier
 * date  2020/6/19 2:25 下午
 */
@Model.model(DepartmentRelEmployee.MODEL_MODEL)
@Model(displayName = "部门员工关系表")
@Model.Advanced(index = {"departmentCode,departmentType", "employeeCode,employeeType"})
public class DepartmentRelEmployee extends BaseRelation {

    private static final long serialVersionUID = 5716595089649781301L;

    public static final String MODEL_MODEL = "DepartmentRelEmployee";

    @Field.PrimaryKey
    @Field.String(size = 32)
    @Field(displayName = "部门类型")
    private String departmentType;

    @Field.PrimaryKey
    @Field.String(size = 64)
    @Field(displayName = "部门编码")
    private String departmentCode;

    @Field.String(size = 256)
    @Field(displayName = "部门树编码")
    private String departmentTreeCode;

    @Field.PrimaryKey
    @Field.String(size = 64)
    @Field(displayName = "员工编码")
    private String employeeCode;

    @Field.PrimaryKey
    @Field.String(size = 32)
    @Field(displayName = "员工类型")
    private String employeeType;

    public static DepartmentRelEmployee newInstance(PamirsEmployee employee, PamirsDepartment department) {
        return new DepartmentRelEmployee()
                .setDepartmentType(department.getDepartmentType())
                .setDepartmentCode(department.getCode())
                .setDepartmentTreeCode(department.getTreeCode())
                .setEmployeeCode(employee.getCode())
                .setEmployeeType(employee.getEmployeeType());
    }

}
