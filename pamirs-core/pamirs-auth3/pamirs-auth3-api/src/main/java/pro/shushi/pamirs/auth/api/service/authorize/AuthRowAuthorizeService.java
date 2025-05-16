package pro.shushi.pamirs.auth.api.service.authorize;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;
import java.util.Set;

/**
 * 行权限授权服务
 *
 * @author Adamancy Zhang at 12:03 on 2024-01-08
 */
@Fun(AuthRowAuthorizeService.FUN_NAMESPACE)
public interface AuthRowAuthorizeService extends AuthPermissionAuthorizeService<RowAuthorizedValueEnum, AuthRowAuthorization> {

    String FUN_NAMESPACE = "auth.AuthRowAuthorizeService";

    /**
     * 授权
     *
     * @param roleId     角色ID
     * @param permission 权限项
     * @param source     授权来源
     * @return 授权是否成功
     */
    @Function
    @Override
    AuthRowAuthorization authorize(Long roleId, AuthRowAuthorization permission, AuthorizationSourceEnum source);

    /**
     * 批量授权（增量授权）
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @param source      授权来源
     * @return 授权是否成功
     */
    @Function
    @Override
    List<AuthRowAuthorization> authorizes(Set<Long> roleIds, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source);

    /**
     * 取消授权
     *
     * @param roleId     角色ID
     * @param permission 权限项
     * @return 取消授权是否成功
     */
    @Function
    @Override
    AuthRowAuthorization revoke(Long roleId, AuthRowAuthorization permission);

    /**
     * 批量取消授权
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @return 取消授权是否成功
     */
    @Function
    @Override
    List<AuthRowAuthorization> revokes(Set<Long> roleIds, List<AuthRowAuthorization> permissions);

    /**
     * 更新授权
     *
     * @param roleId     角色ID
     * @param permission 权限项
     * @param source     授权来源
     * @return 更新授权是否成功
     */
    @Function
    @Override
    AuthRowAuthorization update(Long roleId, AuthRowAuthorization permission, AuthorizationSourceEnum source);

    /**
     * 批量更新授权
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @param source      授权来源
     * @return 更新授权是否成功
     */
    @Function
    @Override
    List<AuthRowAuthorization> updates(Set<Long> roleIds, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source);

    /**
     * 全量授权（移除不在指定权限项中的其他权限项）
     *
     * @param roleId      角色ID
     * @param permissions 权限项列表
     * @param source      授权来源
     * @return 更新授权是否成功
     */
    @Function
    List<AuthRowAuthorization> fullUpdate(Long roleId, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source);

    /**
     * 全量批量授权（移除不在指定权限项中的其他权限项）
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @param source      授权来源
     * @return 更新授权是否成功
     */
    @Function
    List<AuthRowAuthorization> fullUpdates(Set<Long> roleIds, List<AuthRowAuthorization> permissions, AuthorizationSourceEnum source);
}
