package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BindingModeEnum;
import pro.shushi.pamirs.business.api.model.relation.EmployeeRelRole;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.cache.UniqueKeyGenerator;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.user.api.crypto.annotation.EncryptField;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

@Model.model(PamirsEmployee.MODEL_MODEL)
@Model.Advanced(name = "PamirsEmployee", unique = {"code", "companyCode,bindingUserId"},
        index = {"name", "bindingUserId", "phone"})
@Model(displayName = "员工", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "E", size = 8)
public class PamirsEmployee extends BizCodeModel implements IDataStatus {

    private static final long serialVersionUID = 8113683057737732596L;

    public static final String MODEL_MODEL = "business.PamirsEmployee";

    public static final UniqueKeyGenerator<PamirsEmployee, String> UNIQUE_KEY_GENERATOR = PamirsEmployee::getCode;

    @Base
    @Field.String(size = 64)
    @Field(displayName = "编码", unique = true, required = true, priority = 90)
    private String code;

    @Field.String(size = 32)
    @Field(displayName = "员工类型", invisible = true)
    private String employeeType;

    @Field.String(size = 255)
    @Field(displayName = "员工名称", required = true)
    private String name;

    @Field.String
    @Field(displayName = "工号")
    private String jobNum;

    @Field.Enum
    @Field(displayName = "数据状态", required = true, defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    @Field.many2one
    @Field.Relation(relationFields = {"companyCode"}, referenceFields = {"code"})
    @Field(displayName = "所属公司")
    private PamirsCompany company;

    @Field.String
    @Field(displayName = "所属公司编码", invisible = true)
    private String companyCode;

    @Field.many2many(through = "CompanyRelEmployee", relationFields = {"employeeType", "employeeCode"}, referenceFields = {"companyType", "companyCode"})
    @Field.Relation(relationFields = {"employeeType", "code"}, referenceFields = {"companyType", "code"})
    @Field(displayName = "公司列表")
    private List<PamirsCompany> companyList;

    @Field.many2one
    @Field(displayName = "所属部门")
    @Field.Relation(relationFields = {"departmentCode"}, referenceFields = {"code"})
    private PamirsDepartment department;

    @Field.String(size = 64)
    @Field(displayName = "所属部门编码", invisible = true)
    private String departmentCode;

    @Field.String(size = 256)
    @Field(displayName = "所属部门树编码")
    private String departmentTreeCode;

    @Field.many2many(through = DepartmentRelEmployee.MODEL_MODEL, relationFields = {"employeeType", "employeeCode"}, referenceFields = {"departmentType", "departmentCode"})
    @Field.Relation(relationFields = {"employeeType", "code"}, referenceFields = {"departmentType", "code"})
    @Field(displayName = "部门列表")
    private List<PamirsDepartment> departmentList;

    @Field.many2many(through = PositionRelEmployee.MODEL_MODEL, relationFields = {"employeeId"}, referenceFields = {"positionId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "岗位列表")
    private List<PamirsPosition> positions;

    @Field.Integer
    @Field(summary = "绑定用户ID", invisible = true)
    private Long bindingUserId;

    // region 废弃字段 since: 6.0.0

    @Deprecated
    @Field.many2one
    @Field(displayName = "绑定用户", required = true, summary = "绑定用户")
    @Field.Relation(relationFields = {"bindingUserId"}, referenceFields = {"id"})
    private PamirsUser defaultBindingUser;

    @Deprecated
    @Field.Boolean
    @Field(displayName = "是否主管理员", defaultValue = "false")
    private Boolean isMaster;

    @Deprecated
    @Field.many2many(through = EmployeeRelRole.MODEL_MODEL, relationFields = {"employeeId"}, referenceFields = {"authRoleId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "角色")
    private List<AuthRole> roles;

    @Deprecated
    @Field.String
    @Field(summary = "邮箱地址", displayName = "邮箱地址", store = NullableBoolEnum.FALSE)
    private String userEmail;

    @Deprecated
    @Field.String
    @Field(required = true, summary = "登录账号", displayName = "登录账号", store = NullableBoolEnum.FALSE)
    private String login;

    //@Deprecated
    @Field.String
    @Field(displayName = "手机号")
    private String phone;

    @Deprecated
    @Field.String
    @Field(summary = "初始密码", displayName = "初始密码", store = NullableBoolEnum.FALSE)
    @EncryptField
    private String initialPassword;

    @Field.Enum
    @Field(displayName = "用户绑定方式", defaultValue = "CREATE_BINDING", required = true, summary = "用户绑定方式枚举", store = NullableBoolEnum.FALSE)
    private BindingModeEnum bindingMode;

    // endregion

    @Field.Boolean
    @Field(displayName = "是否为部门主管", store = NullableBoolEnum.FALSE)
    private Boolean supervisor;
}
