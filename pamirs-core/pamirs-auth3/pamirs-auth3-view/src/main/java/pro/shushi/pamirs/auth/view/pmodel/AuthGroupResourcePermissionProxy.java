package pro.shushi.pamirs.auth.view.pmodel;

import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 权限组 - 资源权限
 *
 * @author Adamancy Zhang at 16:59 on 2023-12-13
 */
@Base
@Model.model(AuthGroupResourcePermissionProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY, unInheritedFunctions = {FunctionConstants.create, FunctionConstants.update, FunctionConstants.deleteWithFieldBatch})
@Model(displayName = "资源权限组", labelFields = {"displayName", "name"})
public class AuthGroupResourcePermissionProxy extends AuthGroup {

    private static final long serialVersionUID = 3562436672772684576L;

    public static final String MODEL_MODEL = "auth.AuthGroupResourcePermissionProxy";

    @Field.many2many
    @Field.Relation(store = false)
    @Field(displayName = "资源管理权限")
    private List<AuthManagementResourceItem> managementResourcePermissions;

    @Field.many2many
    @Field.Relation(store = false)
    @Field(displayName = "资源访问权限")
    private List<AuthAccessResourceItem> accessResourcePermissions;

    @Field(displayName = "资源名称")
    private String resourceDisplayName;

    @Field(displayName = "资源类型")
    private PermissionMateDataEnum resourceType;

    @Field(displayName = "是否允许删除")
    private Boolean isAllowDelete;
}
