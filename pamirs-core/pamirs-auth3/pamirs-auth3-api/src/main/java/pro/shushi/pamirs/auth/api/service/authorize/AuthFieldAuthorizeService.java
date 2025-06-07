package pro.shushi.pamirs.auth.api.service.authorize;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;
import java.util.Set;

/**
 * 字段权限授权服务
 *
 * @author Adamancy Zhang at 12:02 on 2024-01-08
 */
@Fun(AuthFieldAuthorizeService.FUN_NAMESPACE)
public interface AuthFieldAuthorizeService extends AuthPermissionAuthorizeService<FieldAuthorizedValueEnum, AuthFieldAuthorization> {

    String FUN_NAMESPACE = "auth.AuthFieldAuthorizeService";

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
    AuthFieldAuthorization authorize(Long roleId, AuthFieldAuthorization permission, AuthorizationSourceEnum source);

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
    List<AuthFieldAuthorization> authorizes(Set<Long> roleIds, List<AuthFieldAuthorization> permissions, AuthorizationSourceEnum source);

    /**
     * 取消授权
     *
     * @param roleId     角色ID
     * @param permission 权限项
     * @return 取消授权是否成功
     */
    @Function
    @Override
    AuthFieldAuthorization revoke(Long roleId, AuthFieldAuthorization permission);

    /**
     * 批量取消授权
     *
     * @param roleIds     角色ID列表
     * @param permissions 权限项列表
     * @return 取消授权是否成功
     */
    @Function
    @Override
    List<AuthFieldAuthorization> revokes(Set<Long> roleIds, List<AuthFieldAuthorization> permissions);

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
    AuthFieldAuthorization update(Long roleId, AuthFieldAuthorization permission, AuthorizationSourceEnum source);

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
    List<AuthFieldAuthorization> updates(Set<Long> roleIds, List<AuthFieldAuthorization> permissions, AuthorizationSourceEnum source);
}
