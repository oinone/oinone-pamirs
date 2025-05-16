package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.auth.api.behavior.AuthGroupRelationModel;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 自定义权限组关联资源权限项
 *
 * @author Adamancy Zhang at 15:34 on 2024-08-21
 */
@Base
@Model.model(AuthCustomGroupResourcePermission.MODEL_MODEL)
@Model(displayName = "自定义权限组关联资源权限项")
public class AuthCustomGroupResourcePermission extends BaseRelation implements AuthGroupRelationModel {

    private static final long serialVersionUID = -1542154150990974114L;

    public static final String MODEL_MODEL = "auth.AuthCustomGroupResourcePermission";

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
}
