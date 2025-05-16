package pro.shushi.pamirs.auth.api.runtime.cache.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.runtime.cache.AccessPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.cache.fast.AuthL2Cache;
import pro.shushi.pamirs.auth.api.utils.AuthAccessPermissionCacheHelper;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 默认访问权限缓存实现
 *
 * @author Adamancy Zhang at 15:48 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class DefaultAccessPermissionCache implements AccessPermissionCacheApi {

    @Override
    public AuthResult<Set<String>> fetchAccessModules(Set<Long> roleIds) {
        return AuthAccessPermissionCacheHelper.collectionAccessResult(AuthL2Cache.getRoleModulePermissions(roleIds));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessHomepages(Set<Long> roleIds) {
        return AuthAccessPermissionCacheHelper.collectionAccessResult(AuthL2Cache.getRoleHomepagePermissions(roleIds));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessMenus(Set<Long> roleIds, String module) {
        return AuthAccessPermissionCacheHelper.collectionAccessResult(AuthL2Cache.getRoleMenuPermissions(roleIds, module));
    }

    @Override
    public AuthResult<Map<String, Set<String>>> fetchAccessMenus(Set<Long> roleIds, Set<String> modules) {
        return AuthAccessPermissionCacheHelper.collectionMenuPermissionResult(AuthL2Cache.getRoleMenuPermissions(roleIds, modules));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, String model, String actionName) {
        return AuthAccessPermissionCacheHelper.collectionAccessResult(AuthL2Cache.getRoleActionPermissionsByViewAction(roleIds, model, actionName));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, Collection<String> models, Collection<String> actionNames) {
        return AuthAccessPermissionCacheHelper.collectionAccessResult(AuthL2Cache.getRoleActionPermissionsByViewAction(roleIds, models, actionNames));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, String model) {
        return AuthAccessPermissionCacheHelper.collectionAccessResult(AuthL2Cache.getRoleActionPermissionsByModel(roleIds, model));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, Set<String> models) {
        return AuthAccessPermissionCacheHelper.collectionModelActionPermissionResult(AuthL2Cache.getRoleActionPermissionsByModel(roleIds, models));
    }
}
