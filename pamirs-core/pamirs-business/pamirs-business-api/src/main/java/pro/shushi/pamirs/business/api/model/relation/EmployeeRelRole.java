package pro.shushi.pamirs.business.api.model.relation;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 员工关联角色
 *
 * @author Adamancy Zhang at 22:16 on 2024-01-04
 */
@Model.model(EmployeeRelRole.MODEL_MODEL)
@Model(displayName = "员工关联角色")
public class EmployeeRelRole extends BaseRelation {

    private static final long serialVersionUID = 8850398632362056633L;

    public static final String MODEL_MODEL = "EmployeeRelRole";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "员工ID")
    private Long employeeId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "角色ID")
    private Long authRoleId;

//    @Field.Enum
//    @Field(displayName = "授权来源", invisible = true)
//    private AuthorizationSourceEnum source;
}
