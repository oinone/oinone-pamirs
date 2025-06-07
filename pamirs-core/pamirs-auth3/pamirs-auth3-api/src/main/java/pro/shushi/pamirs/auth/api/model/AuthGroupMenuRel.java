package pro.shushi.pamirs.auth.api.model;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 权限组与资源(菜单/模块的)的关系
 *
 * @author wnagxian
 * date  2023/02/15
 */
@Deprecated
@Base
@Model(displayName = "权限组与资源(菜单/模块的)的关系")
@Model.model(AuthGroupMenuRel.MODEL_MODEL)
@Model.Advanced(name = "authGroupMenuRel")
public class AuthGroupMenuRel extends BaseRelation {

    private static final long serialVersionUID = 2707560653549770348L;

    public final static String MODEL_MODEL = "auth.AuthGroupMenuRel";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "权限组ID", index = true, required = true)
    private Long authGroupId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "资源ID", index = true, required = true)
    private Long resourceId;

    @Field.Integer
    @Field(displayName = "资源权限项ID")
    private Long permissionId;

    @Field.many2one
    @Field.Relation(relationFields = {"permissionId"}, referenceFields = {"id"})
    @Field(displayName = "资源权限项")
    private AuthResourcePermission permission;

    @Field.PrimaryKey
    @Field.Enum
    @Field(displayName = "元数据类型", summary = "仅包括MODULE,HOMEPAGE,MENU")
    private PermissionMateDataEnum nodeType;

    @Field.PrimaryKey
    @Field.Enum
    @Field(displayName = "权限组类型", defaultValue = "RUNTIME")
    private AuthGroupTypeEnum authGroupType;

    @Deprecated
    @Field.Boolean
    @Field(displayName = "是否是批量方式", invisible = true, defaultValue = "false")
    private Boolean batch;
}
