package pro.shushi.pamirs.auth.api.utils;

import pro.shushi.pamirs.auth.api.cache.entity.*;
import pro.shushi.pamirs.auth.api.cache.service.AuthRoleActionByViewActionCacheService;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.model.AuthPathMapping;
import pro.shushi.pamirs.core.common.DataShardingHelper;

import java.util.*;
import java.util.function.Function;

/**
 * 访问权限缓存结果集帮助类
 *
 * @author Adamancy Zhang at 16:40 on 2024-08-24
 */
public class AuthAccessPermissionCacheHelper {

    private AuthAccessPermissionCacheHelper() {
        // reject create object
    }

    public static AuthResult<Set<String>> getRoleModulePermissions(Set<Long> roleIds) {
        return collectionAccessResult(rawGetRoleModulePermissions(roleIds));
    }

    public static Map<Long, Map<String, Long>> rawGetRoleModulePermissions(Set<Long> roleIds) {
        return AuthApiHolder.getAuthRoleModuleCacheService().get(roleIds);
    }

    public static AuthResult<Set<String>> getRoleHomepagePermissions(Set<Long> roleIds) {
        return collectionAccessResult(rawGetRoleHomepagePermissions(roleIds));
    }

    public static Map<Long, Map<String, Long>> rawGetRoleHomepagePermissions(Set<Long> roleIds) {
        return AuthApiHolder.getAuthRoleHomepageCacheService().get(roleIds);
    }

    public static AuthResult<Set<String>> getRoleMenuPermissions(Set<Long> roleIds, String module) {
        return collectionAccessResult(rawGetRoleMenuPermissions(roleIds, module));
    }

    public static AuthResult<Map<String, Set<String>>> getRoleMenuPermissions(Set<Long> roleIds, Set<String> modules) {
        return collectionMenuPermissionResult(rawGetRoleMenuPermissions(roleIds, modules));
    }

    public static Map<Long, Map<String, Long>> rawGetRoleMenuPermissions(Set<Long> roleIds, String module) {
        Set<MenuCacheKey> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            keys.add(new MenuCacheKey(roleId, module));
        }
        Map<MenuCacheKey, Map<String, Long>> cache = AuthApiHolder.getAuthRoleMenuCacheService().get(keys);
        Map<Long, Map<String, Long>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<MenuCacheKey, Map<String, Long>> entry : cache.entrySet()) {
            result.put(entry.getKey().getRoleId(), entry.getValue());
        }
        return result;
    }

    public static Map<Long, Map<String, Map<String, Long>>> rawGetRoleMenuPermissions(Set<Long> roleIds, Set<String> modules) {
        Set<MenuCacheKey> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            for (String module : modules) {
                keys.add(new MenuCacheKey(roleId, module));
            }
        }
        Map<MenuCacheKey, Map<String, Long>> cache = AuthApiHolder.getAuthRoleMenuCacheService().get(keys);
        Map<Long, Map<String, Map<String, Long>>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<MenuCacheKey, Map<String, Long>> entry : cache.entrySet()) {
            MenuCacheKey cacheKey = entry.getKey();
            result.computeIfAbsent(cacheKey.getRoleId(), k -> new HashMap<>())
                    .put(cacheKey.getModule(), entry.getValue());
        }
        return result;
    }

    public static AuthResult<Map<String, Set<String>>> collectionMenuPermissionResult(Map<Long, Map<String, Map<String, Long>>> accessMenusMap) {
        return collectionMenuPermissionResult(accessMenusMap, ResourceAuthorizedValueEnum::isAccess);
    }

    public static AuthResult<Map<String, Set<String>>> collectionMenuPermissionResult(Map<Long, Map<String, Map<String, Long>>> accessMenusMap, Function<Long, Boolean> compute) {
        Map<String, Set<String>> accessMenus = new HashMap<>();
        for (Map<String, Map<String, Long>> accessModuleMenus : accessMenusMap.values()) {
            for (Map.Entry<String, Map<String, Long>> moduleMenusEntry : accessModuleMenus.entrySet()) {
                Map<String, Long> moduleMenuValues = moduleMenusEntry.getValue();
                if (moduleMenuValues.isEmpty()) {
                    continue;
                }
                Set<String> menus = accessMenus.computeIfAbsent(moduleMenusEntry.getKey(), k -> new HashSet<>());
                for (Map.Entry<String, Long> menuEntry : moduleMenuValues.entrySet()) {
                    if (compute.apply(menuEntry.getValue())) {
                        menus.add(menuEntry.getKey());
                    }
                }
            }
        }
        return AuthResult.success(accessMenus);
    }

    public static AuthResult<Long> getRoleModelPermissions(Set<Long> roleIds, String model) {
        return collectionModelPermissionResult(rawGetRoleModelPermissions(roleIds, model));
    }

    public static Map<Long, Long> rawGetRoleModelPermissions(Set<Long> roleIds, String model) {
        Set<ModelCacheKey> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            keys.add(new ModelCacheKey(roleId, model));
        }
        Map<ModelCacheKey, Long> cache = AuthApiHolder.getAuthRoleModelCacheService().get(keys);
        Map<Long, Long> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<ModelCacheKey, Long> entry : cache.entrySet()) {
            Long authorizedValue = entry.getValue();
            if (authorizedValue != null) {
                result.put(entry.getKey().getRoleId(), authorizedValue);
            }
        }
        return result;
    }

    public static AuthResult<Long> collectionModelPermissionResult(Map<Long, Long> accessModelMap) {
        if (accessModelMap.isEmpty()) {
            return AuthResult.success(null);
        }
        Long authorized = 0L;
        for (Long value : accessModelMap.values()) {
            authorized |= value;
        }
        return AuthResult.success(authorized);
    }

    public static AuthResult<Map<String, Long>> fetchFieldPermissions(Set<Long> roleIds, String model) {
        return collectionFieldPermissionResult(rawGetRoleFieldPermissions(roleIds, model));
    }

    public static Map<Long, Map<String, Long>> rawGetRoleFieldPermissions(Set<Long> roleIds, String model) {
        Set<FieldCacheKey> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            keys.add(new FieldCacheKey(roleId, model));
        }
        Map<FieldCacheKey, Map<String, Long>> cache = AuthApiHolder.getAuthRoleFieldCacheService().get(keys);
        Map<Long, Map<String, Long>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<FieldCacheKey, Map<String, Long>> entry : cache.entrySet()) {
            result.put(entry.getKey().getRoleId(), entry.getValue());
        }
        return result;
    }

    public static AuthResult<Map<String, Long>> collectionFieldPermissionResult(Map<Long, Map<String, Long>> accessFieldMap) {
        Map<String, Long> accessFields = new LinkedHashMap<>(16);
        for (Map<String, Long> values : accessFieldMap.values()) {
            for (Map.Entry<String, Long> entry : values.entrySet()) {
                accessFields.compute(entry.getKey(), (k, v) -> {
                    if (v == null) {
                        return entry.getValue();
                    }
                    return v | entry.getValue();
                });
            }
        }
        return AuthResult.success(accessFields);
    }

    public static AuthResult<Set<String>> fetchRowPermissionsForRead(Set<Long> roleIds, String model) {
        return collectionRowPermissionResult(rawGetRoleRowPermissions(roleIds, model, RowAuthorizedValueEnum.READ));
    }

    public static AuthResult<Set<String>> fetchRowPermissionsForWrite(Set<Long> roleIds, String model) {
        return collectionRowPermissionResult(rawGetRoleRowPermissions(roleIds, model, RowAuthorizedValueEnum.WRITE));
    }

    public static AuthResult<Set<String>> fetchRowPermissionsForDelete(Set<Long> roleIds, String model) {
        return collectionRowPermissionResult(rawGetRoleRowPermissions(roleIds, model, RowAuthorizedValueEnum.DELETE));
    }

    public static Map<Long, Set<String>> rawGetRoleRowPermissions(Set<Long> roleIds, String model, RowAuthorizedValueEnum authorizedType) {
        Set<RowCacheKey> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            keys.add(new RowCacheKey(roleId, model, authorizedType));
        }
        Map<RowCacheKey, Set<String>> cache = AuthApiHolder.getAuthRoleRowCacheService().get(keys);
        Map<Long, Set<String>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<RowCacheKey, Set<String>> entry : cache.entrySet()) {
            result.put(entry.getKey().getRoleId(), entry.getValue());
        }
        return result;
    }

    public static AuthResult<Set<String>> collectionRowPermissionResult(Map<Long, Set<String>> accessRowMap) {
        Set<String> accessRows = new LinkedHashSet<>(16);
        for (Set<String> values : accessRowMap.values()) {
            accessRows.addAll(values);
        }
        return AuthResult.success(accessRows);
    }

    public static Map<Long, Map<String, Long>> rawGetRoleActionPermissionsByViewAction(Set<Long> roleIds, String model, String actionName) {
        Set<ActionCacheKeyByViewAction> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            keys.add(new ActionCacheKeyByViewAction(roleId, model, actionName));
        }
        Map<ActionCacheKeyByViewAction, Map<String, Long>> cache = AuthApiHolder.getAuthRoleActionByViewActionCacheService().get(keys);
        Map<Long, Map<String, Long>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<ActionCacheKeyByViewAction, Map<String, Long>> entry : cache.entrySet()) {
            result.put(entry.getKey().getRoleId(), entry.getValue());
        }
        return result;
    }

    public static Map<Long, Map<String, Long>> rawGetRoleActionPermissionsByViewAction(Set<Long> roleIds, Collection<String> models, Collection<String> actionNames) {
        Set<ActionCacheKeyByViewAction> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            Iterator<String> modelIterator = models.iterator();
            Iterator<String> actionNameIterator = actionNames.iterator();
            while (modelIterator.hasNext() && actionNameIterator.hasNext()) {
                String model = modelIterator.next();
                String actionName = actionNameIterator.next();
                keys.add(new ActionCacheKeyByViewAction(roleId, model, actionName));
            }
            if (modelIterator.hasNext() || actionNameIterator.hasNext()) {
                throw new IllegalArgumentException("Keys and cache set list do not match.");
            }
        }
        AuthRoleActionByViewActionCacheService service = AuthApiHolder.getAuthRoleActionByViewActionCacheService();
        Map<ActionCacheKeyByViewAction, Map<String, Long>> cache = new HashMap<>();
        DataShardingHelper.build(100).collectionSharding(keys, sublist -> {
            cache.putAll(service.get(sublist));
            return Collections.emptyList();
        });
        Map<Long, Map<String, Long>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<ActionCacheKeyByViewAction, Map<String, Long>> entry : cache.entrySet()) {
            result.computeIfAbsent(entry.getKey().getRoleId(), k -> new HashMap<>()).putAll(entry.getValue());
        }
        return result;
    }

    public static Map<Long, Map<String, Long>> rawGetRoleActionPermissionsByModel(Set<Long> roleIds, String model) {
        Set<ActionCacheKeyByModel> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            keys.add(new ActionCacheKeyByModel(roleId, model));
        }
        Map<ActionCacheKeyByModel, Map<String, Long>> cache = AuthApiHolder.getAuthRoleActionByModelCacheService().get(keys);
        Map<Long, Map<String, Long>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<ActionCacheKeyByModel, Map<String, Long>> entry : cache.entrySet()) {
            result.put(entry.getKey().getRoleId(), entry.getValue());
        }
        return result;
    }

    public static Map<Long, Map<String, Map<String, Long>>> rawGetRoleActionPermissionsByModel(Set<Long> roleIds, Set<String> models) {
        Set<ActionCacheKeyByModel> keys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            for (String model : models) {
                keys.add(new ActionCacheKeyByModel(roleId, model));
            }
        }
        Map<ActionCacheKeyByModel, Map<String, Long>> cache = AuthApiHolder.getAuthRoleActionByModelCacheService().get(keys);
        Map<Long, Map<String, Map<String, Long>>> result = new LinkedHashMap<>(cache.size());
        for (Map.Entry<ActionCacheKeyByModel, Map<String, Long>> entry : cache.entrySet()) {
            ActionCacheKeyByModel key = entry.getKey();
            result.computeIfAbsent(key.getRoleId(), k -> new HashMap<>()).put(key.getModel(), entry.getValue());
        }
        return result;
    }

    public static AuthResult<Set<String>> collectionModelActionPermissionResult(Map<Long, Map<String, Map<String, Long>>> modelActionPermissions) {
        return collectionModelActionPermissionResult(modelActionPermissions, ResourceAuthorizedValueEnum::isAccess);
    }

    public static AuthResult<Set<String>> collectionModelActionPermissionResult(Map<Long, Map<String, Map<String, Long>>> modelActionPermissions, Function<Long, Boolean> compute) {
        Set<String> accessResults = new HashSet<>(16);
        for (Map<String, Map<String, Long>> modelActionPermission : modelActionPermissions.values()) {
            for (Map<String, Long> accessPermission : modelActionPermission.values()) {
                for (Map.Entry<String, Long> entry : accessPermission.entrySet()) {
                    if (compute.apply(entry.getValue())) {
                        accessResults.add(entry.getKey());
                    }
                }
            }
        }
        return AuthResult.success(accessResults);
    }

    public static Set<String> rawGetPathMappings(String path) {
        return AuthApiHolder.getAuthPathMappingCacheService().get(AuthPathMapping.generatorCode(path));
    }

    public static AuthResult<Set<String>> collectionAccessResult(Map<Long, Map<String, Long>> accessPermissions) {
        return collectionAccessResult(accessPermissions, ResourceAuthorizedValueEnum::isAccess);
    }

    public static AuthResult<Set<String>> collectionAccessResult(Map<Long, Map<String, Long>> accessPermissions, Function<Long, Boolean> compute) {
        Set<String> accessResults = new HashSet<>(16);
        for (Map<String, Long> accessPermission : accessPermissions.values()) {
            for (Map.Entry<String, Long> entry : accessPermission.entrySet()) {
                if (compute.apply(entry.getValue())) {
                    accessResults.add(entry.getKey());
                }
            }
        }
        return AuthResult.success(accessResults);
    }
}
