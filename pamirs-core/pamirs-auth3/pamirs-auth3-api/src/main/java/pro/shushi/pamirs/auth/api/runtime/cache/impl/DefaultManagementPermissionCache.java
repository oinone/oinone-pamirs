package pro.shushi.pamirs.auth.api.runtime.cache.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.runtime.cache.ManagementPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthL2Cache;
import pro.shushi.pamirs.auth.api.utils.AuthManagementPermissionCacheHelper;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 默认管理权限缓存实现
 *
 * @author Adamancy Zhang at 15:49 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class DefaultManagementPermissionCache implements ManagementPermissionCacheApi {

    @Override
    public AuthResult<Set<String>> fetchManagementModules(Set<Long> roleIds) {
        return AuthManagementPermissionCacheHelper.collectionManagementResults(AuthL2Cache.getRoleModulePermissions(roleIds));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementHomepages(Set<Long> roleIds) {
        return AuthManagementPermissionCacheHelper.collectionManagementResults(AuthL2Cache.getRoleHomepagePermissions(roleIds));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementMenus(Set<Long> roleIds, String module) {
        return AuthManagementPermissionCacheHelper.collectionManagementResults(AuthL2Cache.getRoleMenuPermissions(roleIds, module));
    }

    @Override
    public AuthResult<Map<String, Set<String>>> fetchManagementMenus(Set<Long> roleIds, Set<String> modules) {
        return AuthManagementPermissionCacheHelper.collectionMenuPermissionResult(AuthL2Cache.getRoleMenuPermissions(roleIds, modules));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions(Set<Long> roleIds, String model, String actionName) {
        return AuthManagementPermissionCacheHelper.collectionManagementResults(AuthL2Cache.getRoleActionPermissionsByViewAction(roleIds, model, actionName));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions(Set<Long> roleIds, Collection<String> models, Collection<String> actionNames) {
        return AuthManagementPermissionCacheHelper.collectionManagementResults(AuthL2Cache.getRoleActionPermissionsByViewAction(roleIds, models, actionNames));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions(Set<Long> roleIds, String model) {
        return AuthManagementPermissionCacheHelper.collectionManagementResults(AuthL2Cache.getRoleActionPermissionsByModel(roleIds, model));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions(Set<Long> roleIds, Set<String> models) {
        return AuthManagementPermissionCacheHelper.collectionModelActionPermissionResult(AuthL2Cache.getRoleActionPermissionsByModel(roleIds, models));
    }
}
