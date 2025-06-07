package pro.shushi.pamirs.auth.api.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.cache.entity.*;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.core.common.diff.DiffValue;
import pro.shushi.pamirs.core.common.path.ResourcePathParser;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限授权帮助类
 *
 * @author Adamancy Zhang at 20:33 on 2024-01-18
 */
public class AuthAuthorizationHelper {

    private AuthAuthorizationHelper() {
        // reject create object
    }

    public static DiffValue<Map<String, Long>> collectionModule(List<AuthResourceAuthorization> moduleAuthorizations) {
        Map<String, Long> allAuthorizations = new LinkedHashMap<>(moduleAuthorizations.size());
        Map<String, Long> updateAuthorizations = new LinkedHashMap<>(moduleAuthorizations.size());
        Map<String, Long> deleteAuthorizations = new LinkedHashMap<>(moduleAuthorizations.size());
        for (AuthResourceAuthorization moduleAuthorization : moduleAuthorizations) {
            String path = moduleAuthorization.getPath();
            Long authorizedValue = moduleAuthorization.getAuthorizedValue();
            allAuthorizations.put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteAuthorizations.put(path, authorizedValue);
            } else {
                updateAuthorizations.put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allAuthorizations, null, updateAuthorizations, deleteAuthorizations);
    }

    public static DiffValue<Map<MenuCacheKey, Map<String, Long>>> collectionMenu(Long roleId, List<AuthResourceAuthorization> menuAuthorizations) {
        Map<MenuCacheKey, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<MenuCacheKey, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<MenuCacheKey, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        for (AuthResourceAuthorization menuAuthorization : menuAuthorizations) {
            String path = menuAuthorization.getPath();
            Long authorizedValue = menuAuthorization.getAuthorizedValue();
            MenuCacheKey cacheKey = new MenuCacheKey(roleId, menuAuthorization.getModule());
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>())
                    .put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<ModelCacheKey, Long>> collectionModel(Long roleId, List<AuthModelAuthorization> modelAuthorizations) {
        Map<ModelCacheKey, Long> allGroups = new LinkedHashMap<>();
        Map<ModelCacheKey, Long> updateGroups = new LinkedHashMap<>();
        Map<ModelCacheKey, Long> deleteGroups = new LinkedHashMap<>();
        for (AuthModelAuthorization modelAuthorization : modelAuthorizations) {
            Long authorizedValue = modelAuthorization.getAuthorizedValue();
            ModelCacheKey cacheKey = new ModelCacheKey(roleId, modelAuthorization.getModel());
            allGroups.put(cacheKey, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.put(cacheKey, authorizedValue);
            } else {
                updateGroups.put(cacheKey, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<FieldCacheKey, Map<String, Long>>> collectionField(Long roleId, List<AuthFieldAuthorization> fieldAuthorizations) {
        Map<FieldCacheKey, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<FieldCacheKey, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<FieldCacheKey, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        for (AuthFieldAuthorization fieldAuthorization : fieldAuthorizations) {
            String field = fieldAuthorization.getField();
            Long authorizedValue = fieldAuthorization.getAuthorizedValue();
            FieldCacheKey cacheKey = new FieldCacheKey(roleId, fieldAuthorization.getModel());
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(field, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(field, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(field, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static Map<RowCacheKey, Set<String>> collectionRowAuthorizations(Long roleId, List<AuthRowAuthorization> rowAuthorizations) {
        Map<RowCacheKey, Set<String>> groups = new LinkedHashMap<>();
        for (AuthRowAuthorization rowAuthorization : rowAuthorizations) {
            Long authorizedValue = rowAuthorization.getAuthorizedValue();
            String filter = rowAuthorization.getFilter();
            if (StringUtils.isBlank(filter)) {
                continue;
            }
            for (RowAuthorizedValueEnum authorizedType : RowAuthorizedValueEnum.values()) {
                if (authorizedType.enabled(authorizedValue)) {
                    RowCacheKey cacheKey = new RowCacheKey(roleId, rowAuthorization.getModel(), authorizedType);
                    groups.computeIfAbsent(cacheKey, v -> new LinkedHashSet<>()).add(rowAuthorization.getFilter());
                }
            }
        }
        return groups;
    }

    public static DiffValue<Map<ActionCacheKeyByViewAction, Map<String, Long>>> collectionActionByViewAction(Long roleId, List<AuthResourceAuthorization> actionAuthorizations) {
        Map<ActionCacheKeyByViewAction, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByViewAction, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByViewAction, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        ResourcePathParser resourcePathParser = BeanDefinitionUtils.getBean(ResourcePathParser.class);
        for (AuthResourceAuthorization actionAuthorization : actionAuthorizations) {
            String path = actionAuthorization.getPath();
            AccessResourceInfo info = resourcePathParser.parseAccessInfo(path);
            if (info == null) {
                continue;
            }
            String model = info.getModel();
            if (StringUtils.isBlank(model)) {
                continue;
            }
            String actionName = info.getActionName();
            if (StringUtils.isBlank(actionName)) {
                continue;
            }
            ActionCacheKeyByViewAction cacheKey = new ActionCacheKeyByViewAction(roleId, model, actionName);
            Long authorizedValue = actionAuthorization.getAuthorizedValue();
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<ActionCacheKeyByModel, Map<String, Long>>> collectionActionByModel(Long roleId, List<AuthResourceAuthorization> actionAuthorizations) {
        Map<ActionCacheKeyByModel, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByModel, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByModel, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        for (AuthResourceAuthorization actionAuthorization : actionAuthorizations) {
            String model = actionAuthorization.getModel();
            String actionName = actionAuthorization.getName();
            if (StringUtils.isAnyBlank(model, actionName)) {
                continue;
            }
            String path = actionAuthorization.getPath();
            if (!ResourcePath.generatorPath(model, actionName).equals(path)) {
                continue;
            }
            ActionCacheKeyByModel cacheKey = new ActionCacheKeyByModel(roleId, model);
            Long authorizedValue = actionAuthorization.getAuthorizedValue();
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<Long, Map<String, Long>>> collectionModuleAndRoles(List<AuthResourceAuthorization> moduleAuthorizations) {
        Map<Long, Map<String, Long>> allAuthorizations = new LinkedHashMap<>(8);
        Map<Long, Map<String, Long>> updateAuthorizations = new LinkedHashMap<>(8);
        Map<Long, Map<String, Long>> deleteAuthorizations = new LinkedHashMap<>(8);
        for (AuthResourceAuthorization moduleAuthorization : moduleAuthorizations) {
            Long roleId = moduleAuthorization.getRoleId();
            String path = moduleAuthorization.getPath();
            Long authorizedValue = moduleAuthorization.getAuthorizedValue();
            allAuthorizations.computeIfAbsent(roleId, (k) -> new LinkedHashMap<>(16)).put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteAuthorizations.computeIfAbsent(roleId, (k) -> new LinkedHashMap<>(16)).put(path, authorizedValue);
            } else {
                updateAuthorizations.computeIfAbsent(roleId, (k) -> new LinkedHashMap<>(16)).put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allAuthorizations, null, updateAuthorizations, deleteAuthorizations);
    }

    public static DiffValue<Map<MenuCacheKey, Map<String, Long>>> collectionMenuAndRoles(List<AuthResourceAuthorization> menuAuthorizations) {
        Map<MenuCacheKey, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<MenuCacheKey, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<MenuCacheKey, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        for (AuthResourceAuthorization menuAuthorization : menuAuthorizations) {
            String path = menuAuthorization.getPath();
            Long authorizedValue = menuAuthorization.getAuthorizedValue();
            MenuCacheKey cacheKey = new MenuCacheKey(menuAuthorization.getRoleId(), menuAuthorization.getModule());
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<ActionCacheKeyByViewAction, Map<String, Long>>> collectionActionByViewActionAndRoles(List<AuthResourceAuthorization> actionAuthorizations) {
        Map<ActionCacheKeyByViewAction, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByViewAction, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByViewAction, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        ResourcePathParser resourcePathParser = BeanDefinitionUtils.getBean(ResourcePathParser.class);
        for (AuthResourceAuthorization actionAuthorization : actionAuthorizations) {
            String path = actionAuthorization.getPath();
            AccessResourceInfo info = resourcePathParser.parseAccessInfo(path);
            if (info == null) {
                continue;
            }
            String model = info.getModel();
            if (StringUtils.isBlank(model)) {
                continue;
            }
            String actionName = info.getActionName();
            if (StringUtils.isBlank(actionName)) {
                continue;
            }
            ActionCacheKeyByViewAction cacheKey = new ActionCacheKeyByViewAction(actionAuthorization.getRoleId(), model, actionName);
            Long authorizedValue = actionAuthorization.getAuthorizedValue();
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<ActionCacheKeyByModel, Map<String, Long>>> collectionActionByModelAndRoles(List<AuthResourceAuthorization> actionAuthorizations) {
        Map<ActionCacheKeyByModel, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByModel, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<ActionCacheKeyByModel, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        for (AuthResourceAuthorization actionAuthorization : actionAuthorizations) {
            String model = actionAuthorization.getModel();
            String actionName = actionAuthorization.getName();
            if (StringUtils.isAnyBlank(model, actionName)) {
                continue;
            }
            String path = actionAuthorization.getPath();
            if (!ResourcePath.generatorPath(model, actionName).equals(path)) {
                continue;
            }
            ActionCacheKeyByModel cacheKey = new ActionCacheKeyByModel(actionAuthorization.getRoleId(), actionAuthorization.getModel());
            Long authorizedValue = actionAuthorization.getAuthorizedValue();
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(path, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<ModelCacheKey, Long>> collectionModelAndRoles(List<AuthModelAuthorization> modelAuthorizations) {
        Map<ModelCacheKey, Long> allGroups = new LinkedHashMap<>();
        Map<ModelCacheKey, Long> updateGroups = new LinkedHashMap<>();
        Map<ModelCacheKey, Long> deleteGroups = new LinkedHashMap<>();
        for (AuthModelAuthorization modelAuthorization : modelAuthorizations) {
            Long authorizedValue = modelAuthorization.getAuthorizedValue();
            ModelCacheKey cacheKey = new ModelCacheKey(modelAuthorization.getRoleId(), modelAuthorization.getModel());
            allGroups.put(cacheKey, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.put(cacheKey, authorizedValue);
            } else {
                updateGroups.put(cacheKey, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static DiffValue<Map<FieldCacheKey, Map<String, Long>>> collectionFieldAndRoles(List<AuthFieldAuthorization> fieldAuthorizations) {
        Map<FieldCacheKey, Map<String, Long>> allGroups = new LinkedHashMap<>();
        Map<FieldCacheKey, Map<String, Long>> updateGroups = new LinkedHashMap<>();
        Map<FieldCacheKey, Map<String, Long>> deleteGroups = new LinkedHashMap<>();
        for (AuthFieldAuthorization fieldAuthorization : fieldAuthorizations) {
            String field = fieldAuthorization.getField();
            Long authorizedValue = fieldAuthorization.getAuthorizedValue();
            FieldCacheKey cacheKey = new FieldCacheKey(fieldAuthorization.getRoleId(), fieldAuthorization.getModel());
            allGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(field, authorizedValue);
            if (authorizedValue.compareTo(0L) == 0) {
                deleteGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(field, authorizedValue);
            } else {
                updateGroups.computeIfAbsent(cacheKey, v -> new LinkedHashMap<>()).put(field, authorizedValue);
            }
        }
        return DiffCollection.value(allGroups, null, updateGroups, deleteGroups);
    }

    public static Map<RowCacheKey, Set<String>> collectionRowAndRoles(List<AuthRowAuthorization> rowAuthorizations) {
        Map<RowCacheKey, Set<String>> groups = new LinkedHashMap<>();
        for (AuthRowAuthorization rowAuthorization : rowAuthorizations) {
            Long authorizedValue = rowAuthorization.getAuthorizedValue();
            String filter = rowAuthorization.getFilter();
            if (StringUtils.isBlank(filter)) {
                continue;
            }
            for (RowAuthorizedValueEnum authorizedType : RowAuthorizedValueEnum.values()) {
                if (authorizedType.enabled(authorizedValue)) {
                    RowCacheKey cacheKey = new RowCacheKey(rowAuthorization.getRoleId(), rowAuthorization.getModel(), authorizedType);
                    groups.computeIfAbsent(cacheKey, v -> new LinkedHashSet<>()).add(rowAuthorization.getFilter());
                }
            }
        }
        return groups;
    }

    public static Map<String, AuthResourceAuthorization> fetchActionAuthorizationMap(Long groupId) {
        List<AuthGroupResourcePermission> groupActionPermissions = Models.origin().queryListByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
                .from(AuthGroupResourcePermission.MODEL_MODEL)
                .eq(AuthGroupResourcePermission::getGroupId, groupId));
        if (CollectionUtils.isEmpty(groupActionPermissions)) {
            return null;
        }
        Set<Long> permissionIds = groupActionPermissions.stream().map(AuthGroupResourcePermission::getPermissionId).collect(Collectors.toSet());
        List<AuthResourcePermission> actionPermissions = DataShardingHelper.build().collectionSharding(permissionIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                .from(AuthResourcePermission.MODEL_MODEL)
                .eq(AuthResourcePermission::getType, ResourcePermissionTypeEnum.ACTION.value())
                .in(AuthResourcePermission::getId, sublist)));
        MemoryListSearchCache<Long, AuthResourcePermission> actionPermissionCache = new MemoryListSearchCache<>(actionPermissions, AuthResourcePermission::getId);
        Map<String, AuthResourceAuthorization> authorizationMap = new HashMap<>(groupActionPermissions.size());
        for (AuthGroupResourcePermission groupActionPermission : groupActionPermissions) {
            AuthResourcePermission permission = actionPermissionCache.get(groupActionPermission.getPermissionId());
            if (permission == null) {
                continue;
            }
            AuthResourceAuthorization resourceAuthorization = ArgUtils.convert(AuthResourcePermission.MODEL_MODEL, AuthResourceAuthorization.MODEL_MODEL, permission);
            resourceAuthorization.setAuthorizedValue(groupActionPermission.getAuthorizedValue());
            authorizationMap.put(permission.getPath(), resourceAuthorization);
        }
        return authorizationMap;
    }
}
