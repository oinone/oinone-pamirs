package pro.shushi.pamirs.auth.api.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.extend.permission.AuthFetchPermissionService;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.AuthorizationPath;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 权限获取帮助类
 *
 * @author Adamancy Zhang at 20:14 on 2024-07-03
 */
public class AuthFetchPermissionHelper {

    private AuthFetchPermissionHelper() {
        // reject create object
    }

    public static AuthResult<Set<String>> fetchPermissions(Function<AuthorizationPath, Set<String>> getter,
                                                           String cacheKey,
                                                           Supplier<AuthResult<Set<String>>> fetcher) {
        Set<String> authorizationPaths = Optional.ofNullable(AccessResourceInfoSession.getInfo())
                .map(AccessResourceInfo::getAuthorizationPath)
                .map(getter)
                .orElse(null);
        if (CollectionUtils.isNotEmpty(authorizationPaths)) {
            return AuthResult.success(authorizationPaths);
        }
//        return AuthL3Cache.init().computeIfAbsent(cacheKey, fetcher);
        return fetcher.get();
    }

    public static AuthResult<Set<String>> fetchModulePermissions() {
        return AuthApiHolder.getFetchPermissionApi().fetchByRole((roleIds) ->
                fetchPermissions(AuthorizationPath::getModulePaths, "fetchModulePermissions", () -> {
                    return AuthApiHolder.getAccessPermissionCacheApi().fetchAccessModules(roleIds)
                            .transfer(result -> mergeCustomPermissions(result, roleIds, AuthFetchPermissionService::fetchModulePermissions));
                }));
    }

    public static AuthResult<Set<String>> fetchHomepagePermissions() {
        return AuthApiHolder.getFetchPermissionApi().fetchByRole((roleIds) ->
                fetchPermissions(AuthorizationPath::getHomepagePaths, "fetchHomepagePermissions", () -> {
                    return AuthApiHolder.getAccessPermissionCacheApi().fetchAccessHomepages(roleIds)
                            .transfer(result -> mergeModulePermissions(result, roleIds))
                            .transfer(result -> mergeCustomPermissions(result, roleIds, AuthFetchPermissionService::fetchHomepagePermissions));
                }));
    }

    public static AuthResult<Set<String>> fetchMenuPermissions(String module) {
        return AuthApiHolder.getFetchPermissionApi().fetchByRole((roleIds) ->
                fetchPermissions(AuthorizationPath::getMenuPaths, "fetchMenuPermissions", () -> {
                    return AuthApiHolder.getAccessPermissionCacheApi().fetchAccessMenus(roleIds, module)
                            .transfer(result -> mergeModulePermissions(result, roleIds))
                            .transfer(result -> mergeCustomPermissions(result, roleIds, AuthFetchPermissionService::fetchMenuPermissions));
                }));
    }

    public static AuthResult<Set<String>> fetchActionPermissions() {
        return AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) ->
                fetchPermissions(AuthorizationPath::getActionPaths, "fetchActionPermissions", () -> {
                    ViewAction viewAction = accessInfo.getViewAction();
                    if (viewAction == null) {
                        String model = accessInfo.getModel();
                        if (StringUtils.isNotBlank(model)) {
                            return AuthApiHolder.getAccessPermissionCacheApi().fetchAccessActions(roleIds, model);
                        }
                        return AuthResult.error();
                    }
                    return AuthApiHolder.getAccessPermissionCacheApi().fetchAccessActions(roleIds, viewAction.getModel(), viewAction.getName())
                            .transfer(result -> mergeModulePermissions(result, roleIds))
                            .transfer(result -> mergeHomepagePermissions(result, roleIds))
                            .transfer(result -> mergeMenuPermissions(result, roleIds, accessInfo.getModule()))
                            .transfer(result -> mergeModelActionPermissions(result, roleIds, accessInfo.getModel()))
                            .transfer(result -> mergeCustomPermissions(result, roleIds, AuthFetchPermissionService::fetchActionPermissions));
                }));
    }

    public static Set<String> mergeModulePermissions(Set<String> result, Set<Long> roleIds) {
        Set<String> accessModules = AuthApiHolder.getAccessPermissionCacheApi().fetchAccessModules(roleIds).getData();
        if (CollectionUtils.isEmpty(accessModules)) {
            return result;
        }
        for (String accessModule : accessModules) {
            if (accessModule.endsWith(AuthConstants.ALL_FLAG_PATH_SUFFIX)) {
                result.add(accessModule);
            }
        }
        return result;
    }

    public static Set<String> mergeHomepagePermissions(Set<String> result, Set<Long> roleIds) {
        Set<String> accessHomepages = AuthApiHolder.getAccessPermissionCacheApi().fetchAccessHomepages(roleIds).getData();
        if (CollectionUtils.isEmpty(accessHomepages)) {
            return result;
        }
        result.addAll(accessHomepages);
        return result;
    }

    public static Set<String> mergeMenuPermissions(Set<String> result, Set<Long> roleIds, String module) {
        if (StringUtils.isBlank(module)) {
            return result;
        }
        Set<String> accessMenus = AuthApiHolder.getAccessPermissionCacheApi().fetchAccessMenus(roleIds, module).getData();
        if (CollectionUtils.isEmpty(accessMenus)) {
            return result;
        }
        result.addAll(accessMenus);
        return result;
    }

    public static Set<String> mergeModelActionPermissions(Set<String> result, Set<Long> roleIds, String model) {
        if (StringUtils.isBlank(model)) {
            return result;
        }
        Set<String> accessActions = AuthApiHolder.getAccessPermissionCacheApi().fetchAccessActions(roleIds, model).getData();
        if (CollectionUtils.isEmpty(accessActions)) {
            return result;
        }
        result.addAll(accessActions);
        return result;
    }

    public static Set<String> mergeCustomPermissions(Set<String> result, Set<Long> roleIds, FetchCustomPermissionFunction function) {
        List<AuthFetchPermissionService> services = AuthApiHolder.getAuthFetchPermissionServices();
        for (AuthFetchPermissionService service : services) {
            Set<String> accessActions = function.fetch(service, result, roleIds);
            if (CollectionUtils.isEmpty(accessActions)) {
                continue;
            }
            result.addAll(accessActions);
        }
        return result;
    }

    @FunctionalInterface
    public interface FetchCustomPermissionFunction {

        Set<String> fetch(AuthFetchPermissionService service, Set<String> result, Set<Long> roleIds);
    }
}
