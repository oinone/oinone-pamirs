package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.auth.api.behavior.AuthGroupRelationModel;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 权限组关联资源权限项
 *
 * @author Adamancy Zhang at 17:57 on 2024-01-17
 */
@Base
@Model.model(AuthGroupResourcePermission.MODEL_MODEL)
@Model(displayName = "权限组关联资源权限项")
public class AuthGroupResourcePermission extends BaseRelation implements AuthGroupRelationModel {

    private static final long serialVersionUID = -4094701513500787325L;

    public static final String MODEL_MODEL = "auth.AuthGroupResourcePermission";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "权限组ID")
    private Long groupId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "资源权限项ID")
    private Long permissionId;

    @Field.String
    @Field(displayName = "资源权限项编码", store = NullableBoolEnum.FALSE)
    private String permissionCode;

    @Field.Enum
    @Field(displayName = "资源类型", required = true)
    private ResourcePermissionTypeEnum permissionType;

    @Field.Enum
    @Field(displayName = "资源子类型", required = true)
    private ResourcePermissionSubtypeEnum permissionSubtype;

    @Field.many2one
    @Field.Relation(relationFields = {"permissionId"}, referenceFields = {"id"})
    @Field(displayName = "资源权限项")
    private AuthResourcePermission permission;

    /**
     * @see pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    public static <T extends AuthGroupResourcePermission> T transfer(AuthGroupResourcePermission origin, T target) {
        target.setGroupId(origin.getGroupId());
        target.setPermissionId(origin.getPermissionId());
        target.setPermissionCode(origin.getPermissionCode());
        target.setPermissionType(origin.getPermissionType());
        target.setPermissionSubtype(origin.getPermissionSubtype());
        target.setPermission(origin.getPermission());
        target.setAuthorizedValue(origin.getAuthorizedValue());
        return target;
    }
}
