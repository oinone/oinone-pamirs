package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.spi.ManagementPermissionApi;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;
import java.util.Set;

/**
 * 默认管理权限实现
 *
 * @author Adamancy Zhang at 11:01 on 2024-01-12
 */
@Order
@Component
@SPI.Service
public class DefaultManagementPermission implements ManagementPermissionApi {

    @Override
    public AuthResult<Set<String>> fetchManagementModules() {
        return AuthApiHolder.getFetchPermissionApi().fetch(((accessInfo, roleIds) -> AuthApiHolder.getManagementPermissionCacheApi().fetchManagementModules(roleIds)));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementHomepages() {
        return AuthApiHolder.getFetchPermissionApi().fetch(((accessInfo, roleIds) -> AuthApiHolder.getManagementPermissionCacheApi().fetchManagementHomepages(roleIds)));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementMenus(String module) {
        return AuthApiHolder.getFetchPermissionApi().fetch(((accessInfo, roleIds) -> AuthApiHolder.getManagementPermissionCacheApi().fetchManagementMenus(roleIds, module)));
    }

    @Override
    public AuthResult<Map<String, Set<String>>> fetchManagementMenus(Set<String> modules) {
        return AuthApiHolder.getFetchPermissionApi().fetch(((accessInfo, roleIds) -> AuthApiHolder.getManagementPermissionCacheApi().fetchManagementMenus(roleIds, modules)));
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions() {
        return AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) -> {
            ViewAction viewAction = accessInfo.getViewAction();
            if (viewAction == null) {
                return AuthResult.error();
            }
            return AuthApiHolder.getManagementPermissionCacheApi().fetchManagementActions(roleIds, viewAction.getModel(), viewAction.getName());
        });
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions(String model) {
        return AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) -> AuthApiHolder.getManagementPermissionCacheApi().fetchManagementActions(roleIds, model));
    }

    @Override
    public AuthResult<Boolean> isManagementModule(String module) {
        return fetchManagementModules().transfer((managementModules) -> AuthApiHolder.getVerificationPermissionApi().isManagementModule(managementModules, module));
    }

    @Override
    public AuthResult<Boolean> isManagementHomepage(String module) {
        return fetchManagementHomepages().transfer((managementHomepageModules) -> AuthApiHolder.getVerificationPermissionApi().isManagementHomepage(managementHomepageModules, module));
    }

    @Override
    public AuthResult<Boolean> isManagementMenu(String module, String name) {
        return fetchManagementMenus(module).transfer((managementMenus) -> AuthApiHolder.getVerificationPermissionApi().isManagementMenu(managementMenus, module, name));
    }

    @Override
    public AuthResult<Boolean> isManagementAction(String model, String name) {
        return fetchManagementActions(model).transfer((managementActions) -> AuthApiHolder.getVerificationPermissionApi().isManagementAction(managementActions, model, name));
    }

    @Override
    public AuthResult<Boolean> isManagementAction(String path) {
        return fetchManagementActions().transfer((managementActions) -> AuthApiHolder.getVerificationPermissionApi().isManagementAction(managementActions, path));
    }
}
