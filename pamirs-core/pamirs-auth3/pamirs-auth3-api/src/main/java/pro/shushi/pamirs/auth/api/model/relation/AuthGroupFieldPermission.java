package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.auth.api.behavior.AuthGroupRelationModel;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 权限组关联字段权限项
 *
 * @author Adamancy Zhang at 17:18 on 2024-01-17
 */
@Base
@Model.model(AuthGroupFieldPermission.MODEL_MODEL)
@Model(displayName = "权限组关联字段权限项")
public class AuthGroupFieldPermission extends BaseRelation implements AuthGroupRelationModel {

    private static final long serialVersionUID = -3223758236490814272L;

    public static final String MODEL_MODEL = "auth.AuthGroupFieldPermission";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "权限组ID")
    private Long groupId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "字段权限项ID")
    private Long permissionId;

    @Field.String
    @Field(displayName = "字段权限项编码", store = NullableBoolEnum.FALSE)
    private String permissionCode;

    @Field.many2one
    @Field.Relation(relationFields = {"permissionId"}, referenceFields = {"id"})
    @Field(displayName = "字段权限项")
    private AuthFieldPermission permission;

    /**
     * @see pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    public static <T extends AuthGroupFieldPermission> T transfer(AuthGroupFieldPermission origin, T target) {
        target.setGroupId(origin.getGroupId());
        target.setPermissionId(origin.getPermissionId());
        target.setPermissionCode(origin.getPermissionCode());
        target.setPermission(origin.getPermission());
        target.setAuthorizedValue(origin.getAuthorizedValue());
        return target;
    }
}
