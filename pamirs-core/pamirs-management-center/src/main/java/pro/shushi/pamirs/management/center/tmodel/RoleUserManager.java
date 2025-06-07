package pro.shushi.pamirs.management.center.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

/**
 * @author WuXin at 16:20 on 2025/1/8
 */
@Model.model(RoleUserManager.MODEL_MODEL)
@Model(displayName = "角色用户管理")
public class RoleUserManager extends TransientModel {
    private static final long serialVersionUID = -7882486366507815391L;

    public static final String MODEL_MODEL = "managementCenter.RoleUserManager";

    @Field.Integer
    @Field(displayName = "角色Id")
    private Long roleId;

    @Field.String
    @Field(displayName = "角色名称", translate = true, required = true)
    private String roleName;

    @Field.one2many
    @Field(displayName = "用户")
    private List<PamirsUser> users;
}
