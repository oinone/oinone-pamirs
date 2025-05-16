package pro.shushi.pamirs.auth.api.service.authorize;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Map;
import java.util.Set;

/**
 * 用户权限授权服务
 *
 * @author Adamancy Zhang at 14:32 on 2024-01-04
 */
@Fun(AuthUserAuthorizeService.FUN_NAMESPACE)
public interface AuthUserAuthorizeService {

    String FUN_NAMESPACE = "auth.AuthUserAuthorizeService";

    /**
     * 查询用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Function
    Set<Long> queryRoleIds(Long userId);

    /**
     * 查询用户的有效角色ID列表
     *
     * @param userId 用户ID
     * @return 有效角色ID列表
     */
    @Function
    Set<Long> queryValidRoleIds(Long userId);

    /**
     * 批量查询用户的角色ID列表
     *
     * @param userIds 用户ID列表
     * @return 多用户角色ID列表
     */
    @Function
    Map<Long, Set<Long>> queryRoleIdsBatch(Set<Long> userIds);

    /**
     * 批量查询用户的有效角色ID列表
     *
     * @param userIds 用户ID列表
     * @return 多用户有效角色ID列表
     */
    @Function
    Map<Long, Set<Long>> queryValidRoleIdsBatch(Set<Long> userIds);

    /**
     * 查询角色的用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Function
    Set<Long> queryUserIds(Long roleId);

    /**
     * 批量查询角色的用户ID列表
     *
     * @param roleIds 角色ID列表
     * @return 用户ID列表
     */
    @Function
    Map<Long, Set<Long>> queryUserIdsBatch(Set<Long> roleIds);

    /**
     * 授权
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @param source 授权来源
     * @return 授权是否成功
     */
    @Function
    Boolean authorize(Long userId, Long roleId, AuthorizationSourceEnum source);

    /**
     * 批量授权（增量授权）
     *
     * @param userIds 用户ID列表
     * @param roleIds 角色ID列表
     * @param source  授权来源
     * @return 授权是否成功
     */
    @Function
    Boolean authorizes(Set<Long> userIds, Set<Long> roleIds, AuthorizationSourceEnum source);

    /**
     * 取消授权
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 取消授权是否成功
     */
    @Function
    Boolean revoke(Long userId, Long roleId);

    /**
     * 批量取消授权
     *
     * @param userIds 用户ID列表
     * @param roleIds 角色ID列表
     * @return 取消授权是否成功
     */
    @Function
    Boolean revokes(Set<Long> userIds, Set<Long> roleIds);

    /**
     * 全量授权（移除不在指定角色ID列表的授权）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @param source  授权来源
     * @return 授权是否成功
     */
    @Function
    Boolean fullAuthorize(Long userId, Set<Long> roleIds, AuthorizationSourceEnum source);

    /**
     * 全量批量授权（移除不在指定角色ID列表的授权）
     *
     * @param userIds 用户ID列表
     * @param roleIds 角色ID列表
     * @param source  授权来源
     * @return 授权是否成功
     */
    @Function
    Boolean fullAuthorizes(Set<Long> userIds, Set<Long> roleIds, AuthorizationSourceEnum source);
}
