package pro.shushi.pamirs.auth.api.pmodel;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizeModel;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 行权限项授权
 *
 * @author Adamancy Zhang at 20:35 on 2024-01-08
 */
@Base
@Model.model(AuthRowAuthorization.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "行权限项授权")
public class AuthRowAuthorization extends AuthRowPermission implements PermissionAuthorizeModel<RowAuthorizedValueEnum> {

    private static final long serialVersionUID = 7839537018170125442L;

    public static final String MODEL_MODEL = "auth.AuthRowAuthorization";

    @Field.Enum
    @Field(displayName = "权限枚举值")
    private List<RowAuthorizedValueEnum> authorizedEnumList;

    /**
     * @see RowAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    @Field.Integer
    @Field(displayName = "角色ID")
    private Long roleId;

    public static AuthRowAuthorization from(AuthRoleRowPermission permission) {
        AuthRowAuthorization authorization = new AuthRowAuthorization();
        AuthRowPermission.transfer(permission.getPermission(), authorization);
        authorization.setAuthorizedValue(permission.getAuthorizedValue());
        authorization.setRoleId(permission.getRoleId());
        return authorization;
    }

    public static AuthRowAuthorization from(AuthGroupRowPermission permission) {
        AuthRowAuthorization authorization = new AuthRowAuthorization();
        AuthRowPermission.transfer(permission.getPermission(), authorization);
        authorization.setAuthorizedValue(permission.getAuthorizedValue());
        return authorization;
    }
}
