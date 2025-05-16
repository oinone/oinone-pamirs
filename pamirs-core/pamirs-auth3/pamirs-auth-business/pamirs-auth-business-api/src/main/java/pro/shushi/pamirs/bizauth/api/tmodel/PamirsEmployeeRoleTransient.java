package pro.shushi.pamirs.bizauth.api.tmodel;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Model.model(PamirsEmployeeRoleTransient.MODEL_MODEL)
@Model(displayName = "员工角色授权临时模型")
public class PamirsEmployeeRoleTransient extends TransientModel {

    private static final long serialVersionUID = 2024913606137123425L;

    public static final String MODEL_MODEL = "auth.business.PamirsEmployeeRoleTransient";

    @Field.one2many
    @Field(displayName = "角色列表")
    private List<AuthRole> roleList;

    @Field.one2many
    @Field(displayName = "员工列表")
    private List<PamirsEmployee> employeeList;

}
