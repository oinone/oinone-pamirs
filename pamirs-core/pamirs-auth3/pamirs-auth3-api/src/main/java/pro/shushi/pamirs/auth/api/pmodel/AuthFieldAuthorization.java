package pro.shushi.pamirs.auth.api.pmodel;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizeModel;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 字段权限项授权
 *
 * @author Adamancy Zhang at 20:30 on 2024-01-08
 */
@Base
@Model.model(AuthFieldAuthorization.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "字段权限项授权")
public class AuthFieldAuthorization extends AuthFieldPermission implements PermissionAuthorizeModel<FieldAuthorizedValueEnum> {

    private static final long serialVersionUID = 3332879543175121491L;

    public static final String MODEL_MODEL = "auth.AuthFieldAuthorization";

    @Field.Enum
    @Field(displayName = "权限枚举值")
    private List<FieldAuthorizedValueEnum> authorizedEnumList;

    /**
     * @see FieldAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    @Field.Integer
    @Field(displayName = "角色ID")
    private Long roleId;

    public static AuthFieldAuthorization from(AuthRoleFieldPermission permission) {
        AuthFieldAuthorization authorization = new AuthFieldAuthorization();
        AuthFieldPermission.transfer(permission.getPermission(), authorization);
        authorization.setAuthorizedValue(permission.getAuthorizedValue());
        authorization.setRoleId(permission.getRoleId());
        return authorization;
    }

    public static AuthFieldAuthorization from(AuthGroupFieldPermission permission) {
        AuthFieldAuthorization authorization = new AuthFieldAuthorization();
        AuthFieldPermission.transfer(permission.getPermission(), authorization);
        authorization.setAuthorizedValue(permission.getAuthorizedValue());
        return authorization;
    }
}
