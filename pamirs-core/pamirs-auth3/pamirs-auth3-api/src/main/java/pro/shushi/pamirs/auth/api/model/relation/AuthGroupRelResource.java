package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.auth.api.behavior.AuthGroupRelationModel;
import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 权限组与资源关联表（系统权限）
 *
 * @author Adamancy Zhang at 16:02 on 2024-08-22
 */
@Base
@Model.model(AuthGroupRelResource.MODEL_MODEL)
@Model(displayName = "权限组与资源关联表")
public class AuthGroupRelResource extends BaseRelation implements AuthGroupRelationModel {

    private static final long serialVersionUID = -910873396578485905L;

    public static final String MODEL_MODEL = "auth.AuthGroupRelResource";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "权限组ID")
    private Long groupId;

    @Field.String(size = 256)
    @Field(displayName = "权限组名称")
    private String groupName;

    @Field.PrimaryKey
    @Field.Enum
    @Field(displayName = "权限组类型", defaultValue = "RUNTIME")
    private AuthGroupTypeEnum groupType;

    @Field.PrimaryKey
    @Field.String(size = 256)
    @Field(displayName = "资源编码")
    private String resourceCode;

    @Field.Integer
    @Field(displayName = "资源权限项ID")
    private Long permissionId;

    @Field.many2one
    @Field.Relation(relationFields = {"permissionId"}, referenceFields = {"id"})
    @Field(displayName = "资源权限项")
    private AuthResourcePermission permission;

    @Field.PrimaryKey
    @Field.Enum
    @Field(displayName = "资源子类型", required = true)
    private ResourcePermissionSubtypeEnum nodeType;

    @Override
    public Long getAuthorizedValue() {
        AuthGroupTypeEnum groupType = getGroupType();
        if (groupType == null) {
            return null;
        }
        switch (groupType) {
            case RUNTIME:
                return ResourceAuthorizedValueEnum.ACCESS.value();
            case MANAGEMENT:
                return ResourceAuthorizedValueEnum.MANAGEMENT.value();
        }
        return null;
    }
}
