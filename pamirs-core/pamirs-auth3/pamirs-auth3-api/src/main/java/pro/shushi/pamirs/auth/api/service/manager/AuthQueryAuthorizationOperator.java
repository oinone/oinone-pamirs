package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleModelPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限授权查询操作
 *
 * @author Adamancy Zhang at 17:21 on 2024-01-22
 */
public interface AuthQueryAuthorizationOperator {

    /**
     * <h>查询指定角色所有用户授权集合</h>
     * <p>
     * userId -> roleIds
     * </p>
     *
     * @param roleIds 角色ID集合
     * @return 用户-角色授权集合
     */
    Map<Long, Set<Long>> queryUserRoleAuthorizations(Set<Long> roleIds);

    /**
     * <h>查询指定角色及指定用户的授权集合</h>
     * <p>
     * userId -> roleIds
     * </p>
     *
     * @param roleIds 角色ID集合
     * @param userIds 用户ID集合
     * @return 用户-角色授权集合
     */
    Map<Long, Set<Long>> queryUserRoleAuthorizations(Set<Long> roleIds, Set<Long> userIds);

    /**
     * <h>查询指定用户的所有角色授权集合</h>
     * <p>
     * userId -> roleIds
     * </p>
     *
     * @param userIds 用户ID集合
     * @return 用户-角色授权集合
     */
    Map<Long, Set<Long>> queryUserRoleAuthorizationsByUserIds(Set<Long> userIds);

    /**
     * <h>查询指定角色所有资源权限授权集合</h>
     * <p>
     * roleId -> resourcePermissions
     * </p>
     *
     * @param roleIds 角色ID集合
     * @return 角色-资源权限授权集合
     */
    Map<Long, List<AuthResourceAuthorization>> queryRoleResourceAuthorizations(Set<Long> roleIds);

    /**
     * <h>查询指定角色及指定资源权限的授权集合</h>
     * <p>
     * roleId -> resourcePermissions
     * </p>
     *
     * @param roleIds       角色ID集合
     * @param permissionIds 权限项ID集合
     * @return 角色-资源权限授权集合
     */
    Map<Long, List<AuthResourceAuthorization>> queryRoleResourceAuthorizations(Set<Long> roleIds, Set<Long> permissionIds);

    /**
     * 填充资源权限项
     *
     * @param roleResourcePermissions 角色资源权限项集合
     * @return 填充完成的资源权限项
     */
    List<AuthRoleResourcePermission> fillResourcePermissions(List<AuthRoleResourcePermission> roleResourcePermissions);

    /**
     * <h>查询指定角色所有模型权限授权集合</h>
     * <p>
     * roleId -> modelPermissions
     * </p>
     *
     * @param roleIds 角色ID集合
     * @return 角色-模型权限授权集合
     */
    Map<Long, List<AuthModelAuthorization>> queryRoleModelAuthorizations(Set<Long> roleIds);

    /**
     * <h>查询指定角色及指定模型权限的授权集合</h>
     * <p>
     * roleId -> modelPermissions
     * </p>
     *
     * @param roleIds       角色ID集合
     * @param permissionIds 权限项ID集合
     * @return 角色-模型权限授权集合
     */
    Map<Long, List<AuthModelAuthorization>> queryRoleModelAuthorizations(Set<Long> roleIds, Set<Long> permissionIds);

    /**
     * 填充模型权限项
     *
     * @param roleModelPermissions 角色模型权限项集合
     * @return 填充完成的模型权限项
     */
    List<AuthRoleModelPermission> fillModelPermissions(List<AuthRoleModelPermission> roleModelPermissions);

    /**
     * <h>查询指定角色所有字段权限授权集合</h>
     * <p>
     * roleId -> fieldPermissions
     * </p>
     *
     * @param roleIds 角色ID集合
     * @return 角色-字段权限授权集合
     */
    Map<Long, List<AuthFieldAuthorization>> queryRoleFieldAuthorizations(Set<Long> roleIds);

    /**
     * <h>查询指定角色及指定字段权限的授权集合</h>
     * <p>
     * roleId -> fieldPermissions
     * </p>
     *
     * @param roleIds       角色ID集合
     * @param permissionIds 权限项ID集合
     * @return 角色-字段权限授权集合
     */
    Map<Long, List<AuthFieldAuthorization>> queryRoleFieldAuthorizations(Set<Long> roleIds, Set<Long> permissionIds);

    /**
     * 填充字段权限项
     *
     * @param roleFieldPermissions 角色字段权限项集合
     * @return 填充完成的字段权限项
     */
    List<AuthRoleFieldPermission> fillFieldPermissions(List<AuthRoleFieldPermission> roleFieldPermissions);

    /**
     * <h>查询指定角色所有行权限授权集合</h>
     * <p>
     * roleId -> rowPermissions
     * </p>
     *
     * @param roleIds 角色ID集合
     * @return 角色-行权限授权集合
     */
    Map<Long, List<AuthRowAuthorization>> queryRoleRowAuthorizations(Set<Long> roleIds);

    /**
     * <h>查询指定角色及指定行权限的授权集合</h>
     * <p>
     * roleId -> rowPermissions
     * </p>
     *
     * @param roleIds       角色ID集合
     * @param permissionIds 权限项ID集合
     * @return 角色-行权限授权集合
     */
    Map<Long, List<AuthRowAuthorization>> queryRoleRowAuthorizations(Set<Long> roleIds, Set<Long> permissionIds);

    /**
     * 填充行权限项
     *
     * @param roleRowPermissions 角色行权限项集合
     * @return 填充完成的行权限项
     */
    List<AuthRoleRowPermission> fillRowPermissions(List<AuthRoleRowPermission> roleRowPermissions);
}
