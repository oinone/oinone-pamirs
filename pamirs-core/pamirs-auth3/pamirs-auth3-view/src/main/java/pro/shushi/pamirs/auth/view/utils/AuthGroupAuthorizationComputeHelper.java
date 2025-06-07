package pro.shushi.pamirs.auth.view.utils;

import pro.shushi.pamirs.auth.api.behavior.AuthGroupRelationModel;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.auth.view.entity.AuthGroupRevokeContext;
import pro.shushi.pamirs.auth.view.model.AuthActionPermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthFieldPermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthRowPermissionItem;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * 权限组权限计算帮助类
 *
 * @author Adamancy Zhang at 14:29 on 2024-01-26
 */
public class AuthGroupAuthorizationComputeHelper {

    private AuthGroupAuthorizationComputeHelper() {
        // reject create object
    }

    public static <T extends AuthGroupRelationModel> Map<Long, Map<Long, Long>> computeChangedValueMap(AuthGroupRevokeContext context, List<T> authorizations) {
        return computeAuthorizedValueTemplate(context, authorizations, AuthGroupAuthorizationComputeHelper::collectionChangedValueMap);
    }

    public static <T extends AuthGroupRelationModel> Map<Long, Map<Long, Long>> computeFinalValueMap(AuthGroupRevokeContext context, List<T> authorizations) {
        return computeAuthorizedValueTemplate(context, authorizations, AuthGroupAuthorizationComputeHelper::collectionFinalValueMap);
    }

    private static <T extends AuthGroupRelationModel> Map<Long, Map<Long, Long>> computeAuthorizedValueTemplate(AuthGroupRevokeContext context, List<T> authorizations,
                                                                                                                BiFunction<Map<Long, Map<Long, Long>>, Map<Long, Map<Long, Long>>, Map<Long, Map<Long, Long>>> function) {
        Long groupId = context.getGroupId();
        Set<Long> roleIds = context.getRoleIds();
        List<AuthGroupRole> allGroupRoles = context.getAllGroupRoles();
        MemoryListSearchCache<String, AuthGroupRole> allGroupRoleCache = new MemoryListSearchCache<>(allGroupRoles, v -> StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE, Object::toString, v.getGroupId(), v.getRoleId()));
        Map<Long, Map<Long, Long>> authorizedValueMap = new HashMap<>(authorizations.size());
        Map<Long, Map<Long, Long>> targetAuthorizedValueMap = new HashMap<>(2);
        for (T resourcePermission : authorizations) {
            Long targetGroupId = resourcePermission.getGroupId();
            for (Long roleId : roleIds) {
                AuthGroupRole groupRole = allGroupRoleCache.get(StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE, Object::toString, targetGroupId, roleId));
                if (groupRole == null && !groupId.equals(targetGroupId)) {
                    continue;
                }
                Map<Long, Map<Long, Long>> targetRoleValueMap;
                if (groupId.equals(targetGroupId)) {
                    targetRoleValueMap = targetAuthorizedValueMap;
                } else {
                    targetRoleValueMap = authorizedValueMap;
                }
                compute(targetRoleValueMap.computeIfAbsent(roleId, k -> new HashMap<>()), resourcePermission);
            }
        }
        return function.apply(authorizedValueMap, targetAuthorizedValueMap);
    }

    private static Map<Long, Map<Long, Long>> collectionChangedValueMap(Map<Long, Map<Long, Long>> roleAuthorizedValueMap, Map<Long, Map<Long, Long>> targetRoleAuthorizedValueMap) {
        Map<Long, Map<Long, Long>> changedRoleValueMap = new HashMap<>(targetRoleAuthorizedValueMap.size());
        for (Map.Entry<Long, Map<Long, Long>> targetAuthorizedRoleValueEntry : targetRoleAuthorizedValueMap.entrySet()) {
            Long roleId = targetAuthorizedRoleValueEntry.getKey();
            Map<Long, Long> authorizedValueMap = roleAuthorizedValueMap.get(roleId);
            if (authorizedValueMap == null) {
                authorizedValueMap = Collections.emptyMap();
            }
            for (Map.Entry<Long, Long> targetAuthorizedValueEntry : targetAuthorizedRoleValueEntry.getValue().entrySet()) {
                Long permissionId = targetAuthorizedValueEntry.getKey();
                Long authorizedValue = authorizedValueMap.get(permissionId);
                Long targetAuthorizedValue = targetAuthorizedValueEntry.getValue();
                if (authorizedValue == null || (authorizedValue & targetAuthorizedValue) != targetAuthorizedValue) {
                    changedRoleValueMap.computeIfAbsent(roleId, k -> new HashMap<>()).put(permissionId, targetAuthorizedValue);
                }
            }
        }
        return changedRoleValueMap;
    }

    private static Map<Long, Map<Long, Long>> collectionFinalValueMap(Map<Long, Map<Long, Long>> roleAuthorizedValueMap, Map<Long, Map<Long, Long>> targetRoleAuthorizedValueMap) {
        Map<Long, Map<Long, Long>> finalValueMap = new HashMap<>(targetRoleAuthorizedValueMap.size());
        for (Map.Entry<Long, Map<Long, Long>> targetAuthorizedRoleValueEntry : targetRoleAuthorizedValueMap.entrySet()) {
            Long roleId = targetAuthorizedRoleValueEntry.getKey();
            Map<Long, Long> authorizedValueMap = roleAuthorizedValueMap.get(roleId);
            if (authorizedValueMap == null) {
                authorizedValueMap = Collections.emptyMap();
            }
            for (Map.Entry<Long, Long> targetAuthorizedValueEntry : targetAuthorizedRoleValueEntry.getValue().entrySet()) {
                Long permissionId = targetAuthorizedValueEntry.getKey();
                Long authorizedValue = authorizedValueMap.get(permissionId);
                Long finalAuthorizedValue = targetAuthorizedValueEntry.getValue();
                if (authorizedValue != null) {
                    finalAuthorizedValue = finalAuthorizedValue | authorizedValue;
                }
                finalValueMap.computeIfAbsent(roleId, k -> new HashMap<>()).put(permissionId, finalAuthorizedValue);
            }
        }
        return finalValueMap;
    }

    public static Map<Long, List<AuthGroupResourcePermission>> filterAndComputeChangedResourcePermissions(List<AuthGroupResourcePermission> authorizations, Map<Long, Map<Long, Long>> changedValueMap) {
        return filterAndComputePermissions(authorizations, changedValueMap, AuthGroupResourcePermission::setAuthorizedValue);
    }

    public static Map<Long, List<AuthGroupFieldPermission>> filterAndComputeChangedFieldPermissions(List<AuthGroupFieldPermission> authorizations, Map<Long, Map<Long, Long>> changedValueMap) {
        return filterAndComputePermissions(authorizations, changedValueMap, AuthGroupFieldPermission::setAuthorizedValue);
    }

    public static Map<Long, List<AuthGroupRowPermission>> filterAndComputeChangedRowPermissions(List<AuthGroupRowPermission> authorizations, Map<Long, Map<Long, Long>> changedValueMap) {
        return filterAndComputePermissions(authorizations, changedValueMap, AuthGroupRowPermission::setAuthorizedValue);
    }

    private static <T extends AuthGroupRelationModel> Map<Long, List<T>> filterAndComputePermissions(List<T> authorizations, Map<Long, Map<Long, Long>> roleChangedValueMap, BiConsumer<T, Long> authorizedValueSetter) {
        Map<Long, List<T>> results = new HashMap<>();
        int initialCapacity = authorizations.size();
        for (T authorization : authorizations) {
            for (Map.Entry<Long, Map<Long, Long>> roleChangedValueEntry : roleChangedValueMap.entrySet()) {
                Long roleId = roleChangedValueEntry.getKey();
                List<T> result = results.computeIfAbsent(roleId, k -> new ArrayList<>(initialCapacity));
                Long authorizedValue = roleChangedValueEntry.getValue().get(authorization.getPermissionId());
                if (authorizedValue == null) {
                    continue;
                }
                authorizedValueSetter.accept(authorization, authorizedValue);
                result.add(authorization);
            }
        }
        return results;
    }

    private static void compute(Map<Long, Long> storageMap, AuthGroupRelationModel groupRelationModel) {
        storageMap.compute(groupRelationModel.getPermissionId(), (k, v) -> {
            if (v == null) {
                return groupRelationModel.getAuthorizedValue();
            }
            return v | groupRelationModel.getAuthorizedValue();
        });
    }

    public static Long getActionAuthorizedValue(AuthActionPermissionItem actionPermissionItem) {
        Long authorizedValue = 0L;
        Boolean canAccess = actionPermissionItem.getCanAccess();
        if (canAccess == null) {
            canAccess = Boolean.TRUE;
        }
        if (canAccess) {
            authorizedValue |= ResourceAuthorizedValueEnum.ACCESS.value();
        }
        return authorizedValue;
    }

    public static Long getFieldAuthorizedValue(AuthFieldPermissionItem fieldPermissionItem) {
        Long authorizedValue = 0L;
        Boolean permRead = fieldPermissionItem.getPermRead();
        if (permRead == null) {
            permRead = Boolean.TRUE;
        }
        if (permRead) {
            authorizedValue |= FieldAuthorizedValueEnum.READ.value();
        }
        Boolean permWrite = fieldPermissionItem.getPermWrite();
        if (permWrite == null) {
            permWrite = Boolean.TRUE;
        }
        if (permWrite) {
            authorizedValue |= FieldAuthorizedValueEnum.WRITE.value();
        }
        return authorizedValue;
    }

    public static Long getRowPermissionAuthorizedValue(AuthRowPermissionItem rowPermissionItem) {
        Long authorizedValue = 0L;
        Boolean permRead = rowPermissionItem.getPermRead();
        if (permRead == null) {
            permRead = Boolean.TRUE;
        }
        if (permRead) {
            authorizedValue |= RowAuthorizedValueEnum.READ.value();
        }
        Boolean permWrite = rowPermissionItem.getPermWrite();
        if (permWrite == null) {
            permWrite = Boolean.TRUE;
        }
        if (permWrite) {
            authorizedValue |= RowAuthorizedValueEnum.WRITE.value();
        }
        Boolean permDelete = rowPermissionItem.getPermDelete();
        if (permDelete == null) {
            permDelete = Boolean.TRUE;
        }
        if (permDelete) {
            authorizedValue |= RowAuthorizedValueEnum.DELETE.value();
        }
        return authorizedValue;
    }
}
