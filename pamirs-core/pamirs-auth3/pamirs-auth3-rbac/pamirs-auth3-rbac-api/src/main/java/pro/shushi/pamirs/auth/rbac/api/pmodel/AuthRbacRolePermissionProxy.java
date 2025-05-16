package pro.shushi.pamirs.auth.rbac.api.pmodel;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.auth.rbac.api.model.*;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 角色权限
 *
 * @author Adamancy Zhang at 11:17 on 2024-08-09
 */
@Base
@Model.model(AuthRbacRolePermissionProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY, unInheritedFunctions = {FunctionConstants.create, FunctionConstants.deleteWithFieldBatch})
@Model(displayName = "角色权限")
public class AuthRbacRolePermissionProxy extends AuthRole {

    private static final long serialVersionUID = 5422216917969430069L;

    public static final String MODEL_MODEL = "auth.AuthRbacRolePermissionProxy";

    @Field.String
    @Field(displayName = "JSON格式数据")
    private String nodesJson;

    @Field(displayName = "版本标记")
    @Field.Boolean
    private Boolean enterpriseEdition;

    @Field.many2many(through = AuthRoleResourcePermission.MODEL_MODEL, relationFields = {"roleId"}, referenceFields = {"permissionId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "资源访问权限")
    private List<AuthRbacResourcePermissionItem> resourcePermissions;

    @Field.many2many(through = AuthRoleResourcePermission.MODEL_MODEL, relationFields = {"roleId"}, referenceFields = {"permissionId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "资源管理权限")
    private List<AuthRbacResourcePermissionItem> managementPermissions;

    @Field.many2many(through = AuthRoleResourcePermission.MODEL_MODEL, relationFields = {"roleId"}, referenceFields = {"permissionId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "自定义资源权限")
    private List<AuthRbacCustomResourcePermissionItem> customResourcePermissions;

    @Field.many2one
    @Field(displayName = "字段权限选择模型")
    private AuthRbacFieldPermissionModelSelect fieldPermissionModelSelect;

    @Field.many2many(through = AuthRoleFieldPermission.MODEL_MODEL, relationFields = {"roleId"}, referenceFields = {"permissionId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "字段权限")
    private List<AuthRbacFieldPermissionItem> fieldPermissions;

    @Field.many2many(through = AuthRoleRowPermission.MODEL_MODEL, relationFields = {"roleId"}, referenceFields = {"permissionId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "行权限")
    private List<AuthRbacRowPermissionItem> rowPermissions;
}
