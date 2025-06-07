package pro.shushi.pamirs.auth.api.service.authorize;

import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizeModel;
import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizedValue;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;

import java.util.List;
import java.util.Set;

/**
 * 权限项授权服务
 *
 * @author Adamancy Zhang at 18:32 on 2024-01-08
 */
public interface AuthPermissionAuthorizeService<E extends Enum<E> & PermissionAuthorizedValue, T extends PermissionAuthorizeModel<E>> {

    /**
     * 授权
     *
     * @param roleId     角色ID
     * @param permission 权限项
     * @param source     授权来源
     * @return 授权是否成功
     */
    T authorize(Long roleId, T permission, AuthorizationSourceEnum source);

    /**
     * 批量授权（增量授权）
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @param source      授权来源
     * @return 授权是否成功
     */
    List<T> authorizes(Set<Long> roleIds, List<T> permissions, AuthorizationSourceEnum source);

    /**
     * 取消授权
     *
     * @param roleId     角色ID
     * @param permission 权限项
     * @return 取消授权是否成功
     */
    T revoke(Long roleId, T permission);

    /**
     * 批量取消授权
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @return 取消授权是否成功
     */
    List<T> revokes(Set<Long> roleIds, List<T> permissions);

    /**
     * 更新授权
     *
     * @param roleId     角色ID
     * @param permission 权限项
     * @param source     授权来源
     * @return 更新授权是否成功
     */
    T update(Long roleId, T permission, AuthorizationSourceEnum source);

    /**
     * 批量更新授权
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @param source      授权来源
     * @return 更新授权是否成功
     */
    List<T> updates(Set<Long> roleIds, List<T> permissions, AuthorizationSourceEnum source);
}
