package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;

import java.util.List;
import java.util.Set;

/**
 * 权限授权操作
 *
 * @author Adamancy Zhang at 11:51 on 2024-01-20
 */
public interface AuthAuthorizationOperator {

    List<AuthResourceAuthorization> fillResourcePermissionIds(List<AuthResourceAuthorization> resourceAuthorizations);

    AuthResourceAuthorization createAndAuthorizeResourcePermission(Long roleId, AuthResourceAuthorization resourceAuthorizations);

    List<AuthResourceAuthorization> createAndAuthorizeResourcePermissions(Long roleId, List<AuthResourceAuthorization> resourceAuthorizations);

    List<AuthResourceAuthorization> createAndAuthorizeResourcePermissions(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations);

    List<AuthModelAuthorization> fillModelPermissionIds(List<AuthModelAuthorization> modelAuthorizations);

    AuthModelAuthorization createAndAuthorizeModelPermission(Long roleId, AuthModelAuthorization modelAuthorization);

    List<AuthModelAuthorization> createAndAuthorizeModelPermissions(Long roleId, List<AuthModelAuthorization> modelAuthorizations);

    List<AuthModelAuthorization> createAndAuthorizeModelPermissions(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations);

    List<AuthFieldAuthorization> fillFieldPermissionIds(List<AuthFieldAuthorization> fieldAuthorizations);

    AuthFieldAuthorization createAndAuthorizeFieldPermission(Long roleId, AuthFieldAuthorization fieldAuthorization);

    List<AuthFieldAuthorization> createAndAuthorizeFieldPermissions(Long roleId, List<AuthFieldAuthorization> fieldAuthorizations);

    List<AuthFieldAuthorization> createAndAuthorizeFieldPermissions(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations);

    List<AuthRowAuthorization> fillRowPermissionIds(List<AuthRowAuthorization> rowAuthorizations);

    AuthRowAuthorization createAndAuthorizeRowPermission(Long roleId, AuthRowAuthorization rowAuthorization);

    List<AuthRowAuthorization> createAndAuthorizeRowPermissions(Long roleId, List<AuthRowAuthorization> rowAuthorizations);

    List<AuthRowAuthorization> createAndAuthorizeRowPermissions(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations);
}
