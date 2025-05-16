package pro.shushi.pamirs.user.api.model.tmodel;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

@Component
@Model.model(PamirsUserRoleTransient.MODEL_MODEL)
@Model(displayName = "用户角色授权临时模型")
public class PamirsUserRoleTransient extends TransientModel {

    public static final String MODEL_MODEL = "user.PamirsUserRoleTransient";

    @Field.one2many
    @Field(displayName = "用户列表")
    private List<PamirsUser> userList;

    @Field.one2many
    @Field(displayName = "角色列表")
    private List<AuthRole> roleList;
}
