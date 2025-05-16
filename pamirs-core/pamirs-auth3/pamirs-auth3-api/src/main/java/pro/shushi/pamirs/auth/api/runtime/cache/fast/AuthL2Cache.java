package pro.shushi.pamirs.auth.api.runtime.cache.fast;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.cache.AccessPermissionCacheApi;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionApi;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionPrepareApi;
import pro.shushi.pamirs.auth.api.utils.AuthAccessPermissionCacheHelper;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.AuthorizationPath;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.core.common.directive.Directive;
import pro.shushi.pamirs.core.common.directive.DirectiveHelper;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限二级缓存
 *
 * @author Adamancy Zhang at 20:46 on 2024-01-20
 */
@Order(88)
@Component
@SPI.Service("AuthL2Cache")
public class AuthL2Cache implements AccessPermissionPrepareApi, SessionClearApi {

    private static final TransmittableThreadLocal<AuthSafeCache<String>> CACHE = new TransmittableThreadLocal<>();

    public static Map<Long, Map<String, Long>> getRoleModulePermissions(Set<Long> roleIds) {
        return init().computeIfAbsent("getRoleModulePermissions:" + roleIds,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleModulePermissions(roleIds));
    }

    public static Map<Long, Map<String, Long>> getRoleHomepagePermissions(Set<Long> roleIds) {
        return init().computeIfAbsent("getRoleHomepagePermissions:" + roleIds,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleHomepagePermissions(roleIds));
    }

    public static Map<Long, Map<String, Long>> getRoleMenuPermissions(Set<Long> roleIds, String module) {
        return init().computeIfAbsent("getRoleMenuPermissions:" + roleIds + CharacterConstants.SEPARATOR_COLON + module,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleMenuPermissions(roleIds, module));
    }

    public static Map<Long, Map<String, Map<String, Long>>> getRoleMenuPermissions(Set<Long> roleIds, Set<String> modules) {
        return init().computeIfAbsent("getRoleMenuPermissions:" + roleIds + CharacterConstants.SEPARATOR_COLON + modules,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleMenuPermissions(roleIds, modules));
    }

    public static Map<Long, Long> getRoleModelPermissions(Set<Long> roleIds, String model) {
        return init().computeIfAbsent("getRoleModelPermissions:" + roleIds + CharacterConstants.SEPARATOR_COLON + model,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleModelPermissions(roleIds, model));
    }

    public static Map<Long, Map<String, Long>> getRoleFieldPermissions(Set<Long> roleIds, String model) {
        return init().computeIfAbsent("getRoleFieldPermissions:" + roleIds + CharacterConstants.SEPARATOR_COLON + model,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleFieldPermissions(roleIds, model));
    }

    public static Map<Long, Set<String>> getRoleRowPermissions(Set<Long> roleIds, String model, RowAuthorizedValueEnum authorizedType) {
        return init().computeIfAbsent("getRoleRowPermissions:" + roleIds + CharacterConstants.SEPARATOR_COLON + authorizedType.name() + CharacterConstants.SEPARATOR_COLON + model,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleRowPermissions(roleIds, model, authorizedType));
    }

    public static Map<Long, Map<String, Long>> getRoleActionPermissionsByViewAction(Set<Long> roleIds, String model, String actionName) {
        return init().computeIfAbsent("getRoleActionPermissionsByViewAction:" + roleIds + CharacterConstants.SEPARATOR_COLON + model + CharacterConstants.SEPARATOR_COLON + actionName,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleActionPermissionsByViewAction(roleIds, model, actionName));
    }

    public static Map<Long, Map<String, Long>> getRoleActionPermissionsByViewAction(Set<Long> roleIds, Collection<String> models, Collection<String> actionNames) {
        return init().computeIfAbsent("getRoleActionPermissionsByViewAction:" + roleIds + CharacterConstants.SEPARATOR_COLON + models + CharacterConstants.SEPARATOR_COLON + actionNames,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleActionPermissionsByViewAction(roleIds, models, actionNames));
    }

    public static Map<Long, Map<String, Long>> getRoleActionPermissionsByModel(Set<Long> roleIds, String model) {
        return init().computeIfAbsent("getRoleActionPermissionsByModel:" + roleIds + CharacterConstants.SEPARATOR_COLON + model,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleActionPermissionsByModel(roleIds, model));
    }

    public static Map<Long, Map<String, Map<String, Long>>> getRoleActionPermissionsByModel(Set<Long> roleIds, Set<String> models) {
        return init().computeIfAbsent("getRoleActionPermissionsByModel:" + roleIds + CharacterConstants.SEPARATOR_COLON + models,
                () -> AuthAccessPermissionCacheHelper.rawGetRoleActionPermissionsByModel(roleIds, models));
    }

    public static Set<String> getPathMappings(String path) {
        return init().computeIfAbsent("getPathMappings:" + path,
                () -> AuthAccessPermissionCacheHelper.rawGetPathMappings(path));
    }

    private static AuthSafeCache<String> init() {
        AuthSafeCache<String> data = CACHE.get();
        if (data == null) {
            data = new AuthSafeCache<>();
            CACHE.set(data);
        }
        return data;
    }

    public static Map<String, Object> getAllCache() {
        return Optional.ofNullable(CACHE.get())
                .map(AuthSafeCache::entrySet)
                .map(v -> v.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .map(Collections::unmodifiableMap)
                .orElse(Collections.emptyMap());
    }

    @Override
    public void clear() {
        CACHE.remove();
    }

    @Override
    public void prepareAccessPermission(Function function, Object... args) {
        if (PamirsSession.getUserId() == null) {
            return;
        }
        AccessPermissionApi accessPermissionApi = AuthApiHolder.getAccessPermissionApi();
        String namespace = function.getNamespace();
        String fun = function.getFun();
        if (accessPermissionApi.isFilterFunction(namespace, fun) ||
                accessPermissionApi.isFilterFunctionOnlyLogin(namespace, fun)) {
            return;
        }
        Set<Long> roleIds = AuthRoleSession.getCurrentRoles();
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        if (info == null) {
            return;
        }
        if (info.getAuthorizationPath() != null) {
            return;
        }

        info.setAuthorizationPath(new AuthorizationPath());

        boolean isHomepage = StringUtils.isNotBlank(info.getHomepage());

        int directive = getLoadDirective(namespace, fun, isHomepage);

        if (DirectiveHelper.isEnabled(directive, PrepareDirective.LOAD_MODULE)) {
            loadModulePermissions(info, roleIds, isHomepage);
        }
        if (DirectiveHelper.isEnabled(directive, PrepareDirective.LOAD_HOMEPAGE)) {
            loadHomepagePermissions(info, roleIds);
        }
        if (DirectiveHelper.isEnabled(directive, PrepareDirective.LOAD_MENU)) {
            loadMenuPermissions(info, roleIds);
        }
        if (DirectiveHelper.isEnabled(directive, PrepareDirective.LOAD_ACTION)) {
            loadActionPermissions(info, roleIds);
        }
    }

    private int getLoadDirective(String namespace, String fun, boolean isHomepage) {
        int directive;
        if (UeModule.MODEL_MODEL.equals(namespace) && FunctionConstants.load.equals(fun)) {
            directive = PrepareDirective.LOAD_ALL;
        } else if (isHomepage) {
            directive = PrepareDirective.LOAD_BY_HOMEPAGE;
        } else {
            directive = PrepareDirective.LOAD_BY_MENU;
        }
        return directive;
    }

    private void loadModulePermissions(AccessResourceInfo info, Set<Long> roleIds, boolean isHomepage) {
        Set<String> accessModules = AuthApiHolder.getAccessPermissionCacheApi().fetchAccessModules(roleIds).getData();
        if (CollectionUtils.isEmpty(accessModules)) {
            return;
        }
        AuthorizationPath authorizationPath = info.getAuthorizationPath();
        Set<String> modulePaths = authorizationPath.getModulePaths();
        Set<String> homepagePaths = authorizationPath.getHomepagePaths();
        Set<String> menuPaths = authorizationPath.getMenuPaths();
        Set<String> actionPaths = authorizationPath.getActionPaths();
        for (String accessModule : accessModules) {
            modulePaths.add(accessModule);
            if (accessModule.endsWith(AuthConstants.ALL_FLAG_PATH_SUFFIX)) {
                if (isHomepage) {
                    homepagePaths.add(accessModule);
                } else {
                    menuPaths.add(accessModule);
                }
                actionPaths.add(accessModule);
            }
        }
    }

    private void loadHomepagePermissions(AccessResourceInfo info, Set<Long> roleIds) {
        Set<String> accessHomepages = AuthApiHolder.getAccessPermissionCacheApi().fetchAccessHomepages(roleIds).getData();
        if (CollectionUtils.isEmpty(accessHomepages)) {
            return;
        }
        AuthorizationPath authorizationPath = info.getAuthorizationPath();
        Set<String> homepagePaths = authorizationPath.getHomepagePaths();
        Set<String> actionPaths = authorizationPath.getActionPaths();
        for (String accessHomepage : accessHomepages) {
            homepagePaths.add(accessHomepage);
            if (accessHomepage.endsWith(AuthConstants.ALL_FLAG_PATH_SUFFIX)) {
                actionPaths.add(accessHomepage);
            }
        }
    }

    private void loadMenuPermissions(AccessResourceInfo info, Set<Long> roleIds) {
        Set<String> accessMenus = AuthApiHolder.getAccessPermissionCacheApi().fetchAccessMenus(roleIds, info.getModule()).getData();
        if (CollectionUtils.isEmpty(accessMenus)) {
            return;
        }
        AuthorizationPath authorizationPath = info.getAuthorizationPath();
        Set<String> menuPaths = authorizationPath.getMenuPaths();
        Set<String> actionPaths = authorizationPath.getActionPaths();
        for (String accessMenu : accessMenus) {
            menuPaths.add(accessMenu);
            if (accessMenu.endsWith(AuthConstants.ALL_FLAG_PATH_SUFFIX)) {
                actionPaths.add(accessMenu);
            }
        }
    }

    private void loadActionPermissions(AccessResourceInfo info, Set<Long> roleIds) {
        AccessPermissionCacheApi cacheApi = AuthApiHolder.getAccessPermissionCacheApi();
        String model = info.getModel();
        Set<String> accessModelActions = cacheApi.fetchAccessActions(roleIds, model, info.getActionName()).getData();
        if (CollectionUtils.isNotEmpty(accessModelActions)) {
            info.getAuthorizationPath().getActionPaths().addAll(accessModelActions);
        }
        Set<String> accessActions = cacheApi.fetchAccessActions(roleIds, model).getData();
        if (CollectionUtils.isNotEmpty(accessActions)) {
            info.getAuthorizationPath().getActionPaths().addAll(accessActions);
        }
    }

    private enum PrepareDirective implements Directive {

        LOAD_MODULE(1),
        LOAD_HOMEPAGE(2),
        LOAD_MENU(4),
        LOAD_ACTION(8),
        ;

        private final int value;

        private static final int LOAD_ALL = DirectiveHelper.enable(0, Arrays.asList(
                PrepareDirective.LOAD_MODULE,
                PrepareDirective.LOAD_HOMEPAGE,
                PrepareDirective.LOAD_MENU,
                PrepareDirective.LOAD_ACTION
        ));

        private static final int LOAD_BY_HOMEPAGE = DirectiveHelper.enable(0, Arrays.asList(
                PrepareDirective.LOAD_MODULE,
                PrepareDirective.LOAD_HOMEPAGE,
                PrepareDirective.LOAD_ACTION
        ));

        private static final int LOAD_BY_MENU = DirectiveHelper.enable(0, Arrays.asList(
                PrepareDirective.LOAD_MODULE,
                PrepareDirective.LOAD_MENU,
                PrepareDirective.LOAD_ACTION
        ));

        PrepareDirective(int value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            return value;
        }
    }
}
