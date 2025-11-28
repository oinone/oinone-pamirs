package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限扩展服务
 *
 * @author Adamancy Zhang at 09:27 on 2024-01-12
 */
public interface AuthAccessService extends AuthApi {

    String FUN_NAMESPACE = "auth.AuthAccessService";

    @Override
    Result<Void> canAccessModule(String module);

    @Override
    Result<Void> canAccessHomepage(String module);

    @Override
    Result<Void> canAccessMenu(String module, String name);

    @Override
    Result<Void> canAccessAction(String model, String name);

    @Override
    Result<Void> canAccessAction(String path);

    @Override
    Result<Void> canAccessFunction(String namespace, String fun);

    @Override
    Result<String> canReadableData(String model);

    @Override
    Result<String> canWritableData(String model);

    @Override
    Result<String> canDeletableData(String model);

    @Override
    Result<Set<String>> canReadableFields(String model);

    @Override
    Result<Set<String>> canWritableFields(String model);

    @Override
    String getDataFilter(String namespace, String fun);

    @Override
    Result<Set<String>> canAccessModules();

    @Override
    Result<Set<String>> canAccessHomepages();

    @Override
    Result<Set<String>> canAccessMenus(String module);

    @Override
    Result<Set<String>> canAccessActions();

    @Override
    Result<Set<String>> canAccessActions(String model);

    @Override
    default Boolean checkModuleAccess(String module) {
        return AuthApi.super.checkModuleAccess(module);
    }

    @Override
    default Result<String> canReadAccessData(String model) {
        return AuthApi.super.canReadAccessData(model);
    }

    @Override
    default Result<List<String>> canReadAccessField(String model) {
        return AuthApi.super.canReadAccessField(model);
    }

    @Override
    default Result<List<String>> canUpdateAccessField(String model) {
        return AuthApi.super.canUpdateAccessField(model);
    }

    Result<Void> canManagementModule(String module);

    Result<Void> canManagementHomepage(String module);

    Result<Void> canManagementMenu(String module, String name);

    Result<Set<String>> canManagementModules();

    Result<Set<String>> canManagementHomepages();

    Result<Set<String>> canManagementMenus(String module);

    Result<Map<String, Set<String>>> canAccessMenus(Set<String> modules);

    Result<Map<String, Set<String>>> canManagementMenus(Set<String> modules);

    Result<Void> canManagementAction(String model, String name);

    Result<Void> canManagementAction(String path);
}
