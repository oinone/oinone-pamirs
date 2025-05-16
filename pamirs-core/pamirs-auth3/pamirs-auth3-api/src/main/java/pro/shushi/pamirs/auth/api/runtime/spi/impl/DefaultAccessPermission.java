package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.configure.AuthConfiguration;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionApi;
import pro.shushi.pamirs.auth.api.runtime.utils.AuthFilterHelper;
import pro.shushi.pamirs.auth.api.utils.AuthFetchPermissionHelper;
import pro.shushi.pamirs.auth.api.utils.AuthVerificationHelper;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 默认访问权限验证实现
 *
 * @author Adamancy Zhang at 17:11 on 2024-01-06
 */
@Order
@Component
@SPI.Service
public class DefaultAccessPermission implements AccessPermissionApi {

    @Autowired
    private AuthConfiguration authConfiguration;

    @Override
    public AuthResult<Set<String>> fetchAccessModules() {
        return AuthApiHolder.getFetchPermissionApi().fetchByRole((roleIds) -> AuthApiHolder.getAccessPermissionCacheApi().fetchAccessModules(roleIds));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessHomepages() {
        return AuthApiHolder.getFetchPermissionApi().fetchByRole((roleIds) -> AuthApiHolder.getAccessPermissionCacheApi().fetchAccessHomepages(roleIds));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessMenus(String module) {
        return AuthApiHolder.getFetchPermissionApi().fetchByRole((roleIds) -> AuthApiHolder.getAccessPermissionCacheApi().fetchAccessMenus(roleIds, module));
    }

    @Override
    public AuthResult<Map<String, Set<String>>> fetchAccessMenus(Set<String> modules) {
        return AuthApiHolder.getFetchPermissionApi().fetchByRole((roleIds) -> AuthApiHolder.getAccessPermissionCacheApi().fetchAccessMenus(roleIds, modules));
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions() {
        return AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) -> {
            ViewAction viewAction = accessInfo.getViewAction();
            if (viewAction == null) {
                return AuthResult.error();
            }
            return AuthApiHolder.getAccessPermissionCacheApi().fetchAccessActions(roleIds, viewAction.getModel(), viewAction.getName());
        });
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions(String model) {
        return AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) -> AuthApiHolder.getAccessPermissionCacheApi().fetchAccessActions(roleIds, model));
    }

    @Override
    public AuthResult<Boolean> isAccessModule(String module) {
        return AuthFilterHelper.executeBooleanFilter((api) -> api.isAccessModule(module),
                () -> AuthFetchPermissionHelper.fetchModulePermissions().transfer((accessModules) -> AuthVerificationHelper.isAccessModule(accessModules, module)));
    }

    @Override
    public AuthResult<Boolean> isAccessHomepage(String module) {
        return AuthFilterHelper.executeBooleanFilter((api) -> api.isAccessHomepage(module),
                () -> AuthFetchPermissionHelper.fetchHomepagePermissions().transfer((accessHomepages) -> AuthVerificationHelper.isAccessHomepage(accessHomepages, module)));
    }

    @Override
    public AuthResult<Boolean> isAccessMenu(String module, String name) {
        return AuthFilterHelper.executeBooleanFilter((api) -> api.isAccessMenu(module, name),
                () -> AuthFetchPermissionHelper.fetchMenuPermissions(module).transfer((accessMenus) -> AuthVerificationHelper.isAccessMenu(accessMenus, module, name)));
    }

    @Override
    public Boolean isFilterFunction(String namespace, String fun) {
        if (AuthHelper.isFunctionInWhite(namespace, fun)) {
            return Boolean.TRUE;
        }
        return Optional.ofNullable(authConfiguration.getFunFilter())
                .filter(CollectionUtils::isNotEmpty)
                .map(v -> v.stream().anyMatch(vv -> vv.getNamespace().equals(namespace) && vv.getFun().equals(fun)))
                .orElse(Boolean.FALSE);
    }

    @Override
    public Boolean isFilterFunctionOnlyLogin(String namespace, String fun) {
        if (AuthHelper.isFunctionInWhiteOnlyLogin(namespace, fun)) {
            return Boolean.TRUE;
        }
        return Optional.ofNullable(authConfiguration.getFunFilterOnlyLogin())
                .filter(CollectionUtils::isNotEmpty)
                .map(v -> v.stream().anyMatch(vv -> vv.getNamespace().equals(namespace) && vv.getFun().equals(fun)))
                .orElse(Boolean.FALSE);
    }

    @Override
    public AuthResult<Boolean> isAccessFunction(String namespace, String fun) {
        if (isFilterFunction(namespace, fun)) {
            return AuthResult.success(Boolean.TRUE);
        }
        AuthResult<Boolean> result = AuthFilterHelper.executeBooleanFilter((api) -> api.isAccessFunction(namespace, fun));
        if (result != null) {
            return result;
        }
        AuthVerificationHelper.checkLogin();
        if (isFilterFunctionOnlyLogin(namespace, fun)) {
            return AuthResult.success(Boolean.TRUE);
        }
        return AuthFetchPermissionHelper.fetchActionPermissions().transfer((accessActions) -> AuthVerificationHelper.isAccessFunction(namespace, fun));
    }

    @Override
    public AuthResult<Boolean> isAccessAction(String model, String name) {
        if (isFilterFunction(model, name)) {
            return AuthResult.success(Boolean.TRUE);
        }
        AuthResult<Boolean> result = AuthFilterHelper.executeBooleanFilter((api) -> api.isAccessAction(model, name));
        if (result != null) {
            return result;
        }
        AuthVerificationHelper.checkLogin();
        if (isFilterFunctionOnlyLogin(model, name)) {
            return AuthResult.success(Boolean.TRUE);
        }
        return AuthFetchPermissionHelper.fetchActionPermissions().transfer((accessActions) -> AuthVerificationHelper.isAccessAction(accessActions, model, name));
    }

    @Override
    public AuthResult<Boolean> isAccessAction(String path) {
        return AuthFilterHelper.executeBooleanFilter(
                (api) -> api.isAccessAction(path),
                () -> AuthFetchPermissionHelper.fetchActionPermissions().transfer((accessActions) -> AuthVerificationHelper.isAccessAction(accessActions, path))
        );
    }
}
