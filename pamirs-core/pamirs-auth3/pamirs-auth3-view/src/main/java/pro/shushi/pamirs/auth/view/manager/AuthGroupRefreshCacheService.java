package pro.shushi.pamirs.auth.view.manager;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.core.common.diff.DiffList;

/**
 * 权限组刷新缓存服务
 *
 * @author Adamancy Zhang at 22:25 on 2024-01-29
 */
public interface AuthGroupRefreshCacheService {

    /**
     * 更新权限组时刷新缓存
     *
     * @param diffRoles                    差量保存后角色集合
     * @param diffGroupResourcePermissions 差量保存后资源权限授权集合
     * @param diffGroupFieldPermissions    差量保存后字段权限授权集合
     */
    void updateGroupRefresh(Long groupId,
                            DiffList<AuthRole> diffRoles,
                            DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions,
                            DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions);

    void updateRowPermissions(DiffList<AuthRole> diffRoles, DiffList<AuthGroupRowPermission> diffRowPermissions);

}
