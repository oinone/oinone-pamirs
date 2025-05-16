package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.auth.api.behavior.AuthGroupRelationModel;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 权限组关联行权限项
 *
 * @author Adamancy Zhang at 21:12 on 2024-01-17
 */
@Base
@Model.model(AuthGroupRowPermission.MODEL_MODEL)
@Model(displayName = "权限组关联行权限项")
public class AuthGroupRowPermission extends BaseRelation implements AuthGroupRelationModel {

    private static final long serialVersionUID = -8202206186896029441L;

    public static final String MODEL_MODEL = "auth.AuthGroupRowPermission";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "权限组ID")
    private Long groupId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "行权限项ID")
    private Long permissionId;

    @Field.String
    @Field(displayName = "行权限项编码", store = NullableBoolEnum.FALSE)
    private String permissionCode;

    @Field.many2one
    @Field.Relation(relationFields = {"permissionId"}, referenceFields = {"id"})
    @Field(displayName = "行权限项")
    private AuthRowPermission permission;

    /**
     * @see pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum
     */
    @Field.Integer
    @Field(displayName = "权限值")
    private Long authorizedValue;

    public static <T extends AuthGroupRowPermission> T transfer(AuthGroupRowPermission origin, T target) {
        target.setGroupId(origin.getGroupId());
        target.setPermissionId(origin.getPermissionId());
        target.setPermissionCode(origin.getPermissionCode());
        target.setPermission(origin.getPermission());
        target.setAuthorizedValue(origin.getAuthorizedValue());
        return target;
    }
}
