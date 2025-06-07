package pro.shushi.pamirs.management.center.tmodel;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

/**
 * @author WuXin at 10:05 on 2025/1/9
 */
@Model(displayName = "用户绑定模型")
@Model.model(UserBindingManager.MODEL_MODEL)
public class UserBindingManager extends TransientModel {

    private static final long serialVersionUID = -2610880353051972805L;

    public static final String MODEL_MODEL = "managementCenter.UserBindingManager";

    @Field.many2many
    @Field(displayName = "用户列表")
    private List<PamirsUser> bindingUsers;

    @Field.many2many
    @Field(displayName = "角色列表")
    private List<AuthRole> roles;
}
