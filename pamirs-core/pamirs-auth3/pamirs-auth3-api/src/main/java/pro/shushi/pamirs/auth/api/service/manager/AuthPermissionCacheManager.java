package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;

import java.util.List;
import java.util.Set;

/**
 * 权限缓存管理
 *
 * @author Adamancy Zhang at 16:50 on 2024-01-22
 */
public interface AuthPermissionCacheManager {

    /**
     * 授权刷新指定角色全部权限
     *
     * @param roleIds 指定角色ID列表
     * @return 授权是否刷新成功
     */
    boolean authorizeRefreshPermissions(Set<Long> roleIds);

    /**
     * 授权刷新指定角色的指定资源权限（每个角色授予相同的资源权限）
     *
     * @param roleIds                指定角色ID列表
     * @param resourceAuthorizations 资源权限授权列表
     * @return 授权是否刷新成功
     */
    boolean authorizeRefreshResourcePermissions(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations);

    /**
     * 授权刷新指定角色的指定模型权限（每个角色授予相同的模型权限）
     *
     * @param roleIds             指定角色ID列表
     * @param modelAuthorizations 模型权限授权列表
     * @return 授权是否刷新成功
     */
    boolean authorizeRefreshModelPermissions(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations);

    /**
     * 授权刷新指定角色的指定字段权限（每个角色授予相同的字段权限）
     *
     * @param roleIds             指定角色ID列表
     * @param fieldAuthorizations 字段权限授权列表
     * @return 授权是否刷新成功
     */
    boolean authorizeRefreshFieldPermissions(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations);

    /**
     * 授权刷新指定角色的指定行权限（每个角色授予相同的行权限）
     *
     * @param roleIds           指定角色ID列表
     * @param rowAuthorizations 行权限授权列表
     * @return 授权是否刷新成功
     */
    boolean authorizeRefreshRowPermissions(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations);

    /**
     * 取消授权刷新指定角色全部权限
     *
     * @param roleIds 指定角色ID列表
     * @return 取消授权是否刷新成功
     */
    boolean revokeRefreshPermissions(Set<Long> roleIds);

    /**
     * 取消授权刷新指定角色的指定资源权限（每个角色取消授予相同的资源权限）
     *
     * @param roleIds                指定角色ID列表
     * @param resourceAuthorizations 资源权限授权列表
     * @return 取消授权是否刷新成功
     */
    boolean revokeRefreshResourcePermissions(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations);

    /**
     * 取消授权刷新指定角色的指定模型权限（每个角色取消授予相同的模型权限）
     *
     * @param roleIds             指定角色ID列表
     * @param modelAuthorizations 模型权限授权列表
     * @return 取消授权是否刷新成功
     */
    boolean revokeRefreshModelPermissions(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations);

    /**
     * 取消授权刷新指定角色的指定字段权限（每个角色取消授予相同的字段权限）
     *
     * @param roleIds             指定角色ID列表
     * @param fieldAuthorizations 字段权限授权列表
     * @return 取消授权是否刷新成功
     */
    boolean revokeRefreshFieldPermissions(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations);

    /**
     * 取消授权刷新指定角色的指定行权限（每个角色取消授予相同的行权限）
     *
     * @param roleIds           指定角色ID列表
     * @param rowAuthorizations 行权限授权列表
     * @return 取消授权是否刷新成功
     */
    boolean revokeRefreshRowPermissions(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations);

    /**
     * 授权刷新指定角色的指定权限（每个角色授予相同的指定权限）
     *
     * @param roleIds                指定角色ID列表
     * @param resourceAuthorizations 资源权限授权列表
     * @param modelAuthorizations    模型权限授权列表
     * @param fieldAuthorizations    字段权限授权列表
     * @param rowAuthorizations      行权限授权列表
     * @return 授权是否刷新成功
     */
    boolean authorizeRefreshPermissions(Set<Long> roleIds,
                                        List<AuthResourceAuthorization> resourceAuthorizations,
                                        List<AuthModelAuthorization> modelAuthorizations,
                                        List<AuthFieldAuthorization> fieldAuthorizations,
                                        List<AuthRowAuthorization> rowAuthorizations);

    /**
     * 取消授权刷新指定角色的指定权限（每个角色取消授予相同的指定权限）
     *
     * @param roleIds                指定角色ID列表
     * @param resourceAuthorizations 资源权限授权列表
     * @param modelAuthorizations    模型权限授权列表
     * @param fieldAuthorizations    字段权限授权列表
     * @param rowAuthorizations      行权限授权列表
     * @return 取消授权是否刷新成功
     */
    boolean revokeRefreshPermissions(Set<Long> roleIds,
                                     List<AuthResourceAuthorization> resourceAuthorizations,
                                     List<AuthModelAuthorization> modelAuthorizations,
                                     List<AuthFieldAuthorization> fieldAuthorizations,
                                     List<AuthRowAuthorization> rowAuthorizations);

    /**
     * 更新授权刷新指定角色的指定权限（每个角色更新相同的指定权限）
     *
     * @param roleIds                指定角色ID列表
     * @param resourceAuthorizations 资源权限授权列表
     * @param modelAuthorizations    模型权限授权列表
     * @param fieldAuthorizations    字段权限授权列表
     * @return 更新授权是否刷新成功
     */
    boolean updateRefreshPermissions(Set<Long> roleIds,
                                     List<AuthResourceAuthorization> resourceAuthorizations,
                                     List<AuthModelAuthorization> modelAuthorizations,
                                     List<AuthFieldAuthorization> fieldAuthorizations);

    /**
     * 授权刷新指定权限（根据授权项中指定的角色分别授予指定权限）
     *
     * @param resourceAuthorizations 资源权限授权列表
     * @param modelAuthorizations    模型权限授权列表
     * @param fieldAuthorizations    字段权限授权列表
     * @param rowAuthorizations      行权限授权列表
     * @return 授权是否刷新成功
     */
    boolean authorizeRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations,
                                        List<AuthModelAuthorization> modelAuthorizations,
                                        List<AuthFieldAuthorization> fieldAuthorizations,
                                        List<AuthRowAuthorization> rowAuthorizations);

    /**
     * 取消授权刷新指定权限（根据授权项中指定的角色分别取消授予指定权限）
     *
     * @param resourceAuthorizations 资源权限授权列表
     * @param modelAuthorizations    模型权限授权列表
     * @param fieldAuthorizations    字段权限授权列表
     * @param rowAuthorizations      行权限授权列表
     * @return 取消授权是否刷新成功
     */
    boolean revokeRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations,
                                     List<AuthModelAuthorization> modelAuthorizations,
                                     List<AuthFieldAuthorization> fieldAuthorizations,
                                     List<AuthRowAuthorization> rowAuthorizations);

    /**
     * 更新授权刷新指定权限（根据授权项中指定的角色分别更新授予指定权限）
     *
     * @param resourceAuthorizations 资源权限授权列表
     * @param modelAuthorizations    模型权限授权列表
     * @param fieldAuthorizations    字段权限授权列表
     * @return 更新授权是否刷新成功
     */
    boolean updateRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations,
                                     List<AuthModelAuthorization> modelAuthorizations,
                                     List<AuthFieldAuthorization> fieldAuthorizations);
}
