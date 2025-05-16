package pro.shushi.pamirs.auth.api.pmodel;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizeModel;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ModelAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthModelPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleModelPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 模型权限项授权
 *
 * @author Adamancy Zhang at 20:36 on 2024-01-08
 */
@Base
@Model.model(AuthModelAuthorization.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "模型权限项授权")
public class AuthModelAuthorization extends AuthModelPermission implements PermissionAuthorizeModel<ModelAuthorizedValueEnum> {

    private static final long serialVersionUID = 1207140223058655383L;

    public static final String MODEL_MODEL = "auth.AuthModelAuthorization";

    @Field.Enum
    @Field(displayName = "权限枚举值")
    private List<ModelAuthorizedValueEnum> authorizedEnumList;

    /**
     * @see ModelAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    @Field.Integer
    @Field(displayName = "角色ID")
    private Long roleId;

    public static AuthModelAuthorization from(AuthRoleModelPermission permission) {
        AuthModelAuthorization authorization = new AuthModelAuthorization();
        AuthModelPermission.transfer(permission.getPermission(), authorization);
        authorization.setAuthorizedValue(permission.getAuthorizedValue());
        authorization.setRoleId(permission.getRoleId());
        return authorization;
    }
}
