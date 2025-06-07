package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.auth.api.behavior.AuthAuthorizationSource;
import pro.shushi.pamirs.auth.api.behavior.PermissionRelationModel;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 角色关联字段权限项
 *
 * @author Adamancy Zhang at 15:53 on 2024-01-04
 */
@Base
@Model.model(AuthRoleFieldPermission.MODEL_MODEL)
@Model.Advanced(index = {"roleId,source", "permissionId,source"})
@Model(displayName = "角色关联字段权限项")
public class AuthRoleFieldPermission extends BaseRelation implements AuthAuthorizationSource, PermissionRelationModel {

    private static final long serialVersionUID = -2513950876510319004L;

    public static final String MODEL_MODEL = "auth.AuthRoleFieldPermission";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "角色ID")
    private Long roleId;

    @Field.many2one
    @Field.Relation(relationFields = {"roleId"}, referenceFields = {"id"})
    @Field(displayName = "角色")
    private AuthRole role;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "字段权限项ID")
    private Long permissionId;

    @Field.many2one
    @Field.Relation(relationFields = {"permissionId"}, referenceFields = {"id"})
    @Field(displayName = "字段权限项")
    private AuthFieldPermission permission;

    @Field.Enum
    @Field(displayName = "授权来源", invisible = true)
    private AuthorizationSourceEnum source;

    /**
     * @see pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    @Field.Boolean
    @Field(displayName = "子模型有效", defaultValue = "false")
    private Boolean inherit;
}
