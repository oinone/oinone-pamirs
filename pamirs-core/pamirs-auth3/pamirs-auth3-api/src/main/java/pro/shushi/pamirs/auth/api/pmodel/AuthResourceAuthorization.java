package pro.shushi.pamirs.auth.api.pmodel;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizeModel;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 资源权限项授权
 *
 * @author Adamancy Zhang at 20:36 on 2024-01-08
 */
@Base
@Model.model(AuthResourceAuthorization.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "资源权限项授权")
public class AuthResourceAuthorization extends AuthResourcePermission implements PermissionAuthorizeModel<ResourceAuthorizedValueEnum> {

    private static final long serialVersionUID = -2491626973755827337L;

    public static final String MODEL_MODEL = "auth.AuthResourceAuthorization";

    @Deprecated
    @Field.Integer
    @Field(displayName = "权限组ID")
    private Long groupId;

    @Deprecated
    @Field.Integer
    @Field(displayName = "资源ID")
    private Long resourceId;

    @Field.String
    @Field(displayName = "权限组名称")
    private String groupName;

    @Field.String
    @Field(displayName = "资源编码")
    private String resourceCode;

    @Field.Enum
    @Field(displayName = "权限枚举值")
    private List<ResourceAuthorizedValueEnum> authorizedEnumList;

    /**
     * @see ResourceAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    @Field.Integer
    @Field(displayName = "角色ID")
    private Long roleId;

    public static AuthResourceAuthorization from(AuthRoleResourcePermission permission) {
        AuthResourceAuthorization authorization = new AuthResourceAuthorization();
        AuthResourcePermission.transfer(permission.getPermission(), authorization);
        authorization.setAuthorizedValue(permission.getAuthorizedValue());
        authorization.setRoleId(permission.getRoleId());
        return authorization;
    }

    public static AuthResourceAuthorization from(AuthGroupResourcePermission permission) {
        AuthResourceAuthorization authorization = new AuthResourceAuthorization();
        AuthResourcePermission.transfer(permission.getPermission(), authorization);
        authorization.setAuthorizedValue(permission.getAuthorizedValue());
        return authorization;
    }
}
