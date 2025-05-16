package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.auth.api.behavior.AuthAuthorizationSource;
import pro.shushi.pamirs.auth.api.behavior.PermissionRelationModel;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 角色关联资源权限项
 *
 * @author Adamancy Zhang at 15:00 on 2024-01-04
 */
@Base
@Model.model(AuthRoleResourcePermission.MODEL_MODEL)
@Model.Advanced(index = {"roleId,source", "permissionId,source"})
@Model(displayName = "角色关联资源权限项")
public class AuthRoleResourcePermission extends BaseRelation implements AuthAuthorizationSource, PermissionRelationModel {

    private static final long serialVersionUID = 412770859449880676L;

    public static final String MODEL_MODEL = "auth.AuthRoleResourcePermission";

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
    @Field(displayName = "资源权限项ID")
    private Long permissionId;

    @Field.many2one
    @Field.Relation(relationFields = {"permissionId"}, referenceFields = {"id"})
    @Field(displayName = "资源权限项")
    private AuthResourcePermission permission;

    @Field.Enum
    @Field(displayName = "资源权限项类型")
    private ResourcePermissionTypeEnum permissionType;

    @Field.Enum
    @Field(displayName = "资源权限项子类型")
    private ResourcePermissionSubtypeEnum permissionSubtype;

    @Field.Enum
    @Field(displayName = "授权来源", invisible = true)
    private AuthorizationSourceEnum source;

    /**
     * @see pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;
}
