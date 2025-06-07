package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;
import java.util.Set;

/**
 * 权限缓存管理
 *
 * @author Adamancy Zhang at 16:54 on 2024-01-29
 */
@Fun(AuthCacheManager.FUN_NAMESPACE)
public interface AuthCacheManager {

    String FUN_NAMESPACE = "auth.AuthCacheManager";

    /**
     * 刷新指定角色权限
     *
     * @param roles 指定角色
     */
    @Function
    void refresh(List<AuthRole> roles);

    /**
     * <h>刷新指定用户的角色权限</h>
     * <p>
     * 1. 当isRefreshRolePermissionCache为false时，仅刷新用户-角色关系缓存
     * 2. 当isRefreshRolePermissionCache为true时，除了刷新用户-角色关系缓存外，还会刷新与当前用户相关的角色-权限缓存
     * </p>
     *
     * @param userIds                      用户ID列表
     * @param isRefreshRolePermissionCache 是否刷新角色权限缓存，默认为false
     */
    @Function
    void refreshByUserIds(Set<Long> userIds, Boolean isRefreshRolePermissionCache);

    /**
     * 刷新指定角色的全部权限及关联用户
     */
    @Function
    void refreshAll();

    /**
     * 刷新全部用户-角色关系缓存
     */
    @Function
    void refreshAllUserRole();
}
