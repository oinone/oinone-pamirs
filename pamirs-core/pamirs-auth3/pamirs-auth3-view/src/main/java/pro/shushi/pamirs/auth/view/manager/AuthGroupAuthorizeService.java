package pro.shushi.pamirs.auth.view.manager;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.core.common.diff.DiffList;

import java.util.List;
import java.util.Set;

/**
 * 权限组授权服务
 *
 * @author Adamancy Zhang at 21:01 on 2024-01-18
 */
public interface AuthGroupAuthorizeService {

    /**
     * 增量授权角色权限
     */
    void authorizeRolePermissions(Set<Long> roleIds,
                                  List<AuthGroupResourcePermission> resourcePermissions,
                                  List<AuthGroupFieldPermission> fieldPermissions,
                                  List<AuthGroupRowPermission> rowPermissions);

    void authorizeRolePermissions(List<AuthRole> roles,
                                  List<AuthGroupResourcePermission> resourcePermissions,
                                  List<AuthGroupFieldPermission> fieldPermissions,
                                  List<AuthGroupRowPermission> rowPermissions);

    /**
     * 增量取消授权角色权限
     */
    void revokeRolePermissions(Set<Long> roleIds,
                               List<AuthGroupResourcePermission> resourcePermissions,
                               List<AuthGroupFieldPermission> fieldPermissions,
                               List<AuthGroupRowPermission> rowPermissions);

    void revokeRolePermissions(List<AuthRole> roles,
                               List<AuthGroupResourcePermission> resourcePermissions,
                               List<AuthGroupFieldPermission> fieldPermissions,
                               List<AuthGroupRowPermission> rowPermissions);

    /**
     * 对指定角色权限进行覆盖
     */
    void refreshRolePermissions(Set<Long> roleIds,
                                List<AuthGroupResourcePermission> resourcePermissions,
                                List<AuthGroupFieldPermission> fieldPermissions,
                                DiffList<AuthGroupRowPermission> rowPermissions);

    void refreshRolePermissions(List<AuthRole> roles,
                                List<AuthGroupResourcePermission> resourcePermissions,
                                List<AuthGroupFieldPermission> fieldPermissions,
                                DiffList<AuthGroupRowPermission> rowPermissions);

    /**
     * 差量更新角色授权
     */
    void updateRolePermissions(Set<Long> roleIds,
                               DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions,
                               DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions,
                               DiffList<AuthGroupRowPermission> diffRowPermissions);

    void updateRolePermissions(List<AuthRole> roles,
                               DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions,
                               DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions,
                               DiffList<AuthGroupRowPermission> diffRowPermissions);

    /**
     * 差量刷新角色授权
     */
    void refreshRolePermissions(DiffList<AuthRole> diffRoles,
                                DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions,
                                DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions,
                                DiffList<AuthGroupRowPermission> diffRowPermissions);
}
