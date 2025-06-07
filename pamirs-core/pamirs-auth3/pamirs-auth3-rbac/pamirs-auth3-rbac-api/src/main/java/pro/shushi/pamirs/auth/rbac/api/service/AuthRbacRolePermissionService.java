package pro.shushi.pamirs.auth.rbac.api.service;

import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacFieldPermissionItem;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacRowPermissionItem;
import pro.shushi.pamirs.auth.rbac.api.pmodel.AuthRbacRolePermissionProxy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * RBAC角色权限服务
 *
 * @author Adamancy Zhang at 21:15 on 2024-08-12
 */
@Fun(AuthRbacRolePermissionService.FUN_NAMESPACE)
public interface AuthRbacRolePermissionService {

    String FUN_NAMESPACE = "auth.AuthRbacRolePermissionService";

    @Function
    AuthRbacRolePermissionProxy queryOne(AuthRbacRolePermissionProxy data);

    @Function
    List<AuthRbacFieldPermissionItem> queryFieldPermissions(Long roleId, String model);

    @Function
    List<AuthRbacRowPermissionItem> queryRowPermissions(AuthRbacRolePermissionProxy data);

    @Function
    AuthRbacRolePermissionProxy update(AuthRbacRolePermissionProxy data);

}
