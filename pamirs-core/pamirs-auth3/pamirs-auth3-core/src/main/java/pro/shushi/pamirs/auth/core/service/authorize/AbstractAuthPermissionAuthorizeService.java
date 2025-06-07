package pro.shushi.pamirs.auth.core.service.authorize;

import pro.shushi.pamirs.auth.api.behavior.AuthAuthorizationSource;
import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizeModel;
import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizedValue;
import pro.shushi.pamirs.auth.api.behavior.PermissionRelationModel;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.service.authorize.AuthPermissionAuthorizeService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;

/**
 * 权限项授权服务实现
 *
 * @author Adamancy Zhang at 18:32 on 2024-01-08
 */
@Slf4j
public abstract class AbstractAuthPermissionAuthorizeService<
        E extends Enum<E> & PermissionAuthorizedValue,
        PR extends AuthAuthorizationSource & PermissionRelationModel,
        PA extends PermissionAuthorizeModel<E>
        > implements AuthPermissionAuthorizeService<E, PA> {

//    protected abstract PR queryOneAuthorization(Long roleId, Long permissionId);
//
//    protected abstract List<PR> queryListAuthorization(Set<Long> roleIds, List<Long> permissionIds);
//
//    protected abstract List<PR> queryPermissionIdsByAllFlag();
//
//    protected abstract void createOneAuthorization(Long roleId, PA permission, AuthorizationSourceEnum source);
//
//    protected abstract void updateOneAuthorization(Long roleId, PA permission, AuthorizationSourceEnum source);
//
//    protected abstract void createBatchAuthorization(List<PR> permissions);
//
//    protected abstract void updateBatchAuthorization(List<PR> permissions);
//
//    protected abstract PR generatorRolePermission(Long roleId, PA authorizePermission, AuthorizationSourceEnum source);
//
//    protected abstract PA generatorChangedRolePermission(Long roleId, PA authorizePermission, AuthorizationSourceEnum source, Long authorizedValue);

    protected void assertRoleId(Long roleId) {
        if (roleId == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROLE_ID_NULL_ERROR).errThrow();
        }
    }

    protected void assertPermissionId(Long permissionId) {
        if (permissionId == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_PERMISSION_ID_NULL_ERROR).errThrow();
        }
    }

    protected Long getAuthorizedValue(PA data) {
        Long authorizedValue = data.getAuthorizedValue();
        if (authorizedValue != null) {
            return authorizedValue;
        }
        List<E> authorizedValueEnumList = data.getAuthorizedEnumList();
        if (authorizedValueEnumList != null) {
            authorizedValue = 0L;
            for (E authorizedValueEnum : authorizedValueEnumList) {
                authorizedValue = authorizedValue | authorizedValueEnum.value();
            }
            return authorizedValue;
        }
        throw new IllegalArgumentException("Invalid authorized value.");
    }

//    protected abstract void setAuthorizedValue(PR permission, Long authorizedValue);
//
//    protected abstract void setAuthorizedValue(PA permission, Long authorizedValue);

    protected void logModifyBuildInWarn(PermissionRelationModel currentPermission) {
        if (log.isWarnEnabled()) {
            log.warn("Modify build-in permission error. currentUserId: {}, roleId = {}, permissionId = {}", PamirsSession.getUserId(), currentPermission.getRoleId(), currentPermission.getPermissionId());
        }
    }

//    protected PA update(Long roleId, PA permission, AuthorizationSourceEnum source, AuthorizedValueComputer computer) {
//        Long permissionId = permission.getId();
//        assertRoleId(roleId);
//        assertPermissionId(permissionId);
//        Long authorizedValue = getAuthorizedValue(permission);
//        PR currentPermission = queryOneAuthorization(roleId, permissionId);
//        if (currentPermission == null) {
//            if (source == null) {
//                return null;
//            }
//            createOneAuthorization(roleId, permission, source);
//        } else {
//            if (AuthorizationSourceEnum.BUILD_IN.equals(currentPermission.getSource())) {
//                logModifyBuildInWarn(currentPermission);
//                return null;
//            }
//            Long currentAuthorizedValue = currentPermission.getAuthorizedValue();
//            authorizedValue = computer.compute(currentAuthorizedValue, authorizedValue);
//            if (currentAuthorizedValue.equals(authorizedValue)) {
//                return null;
//            }
//            setAuthorizedValue(permission, authorizedValue);
//            updateOneAuthorization(roleId, permission, currentPermission.getSource());
//        }
//        return permission;
//    }
//
//    protected List<PA> updates(Set<Long> roleIds, List<PA> permissions, AuthorizationSourceEnum source, AuthorizedValueComputer computer) {
//        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(permissions)) {
//            return null;
//        }
//        Set<Long> permissionIds = new HashSet<>(permissions.size());
//        for (PA permission : permissions) {
//            Long permissionId = permission.getId();
//            assertPermissionId(permissionId);
//            setAuthorizedValue(permission, getAuthorizedValue(permission));
//            permissionIds.add(permissionId);
//        }
//        Map<Long, List<PR>> permissionsMap = queryPermissionsBatch(roleIds, permissionIds);
//        List<PR> createAuthorizedList = new ArrayList<>(8);
//        List<PR> updateAuthorizedList = new ArrayList<>(16);
//        List<PA> changedAuthorizedList = new ArrayList<>(roleIds.size() * permissions.size());
//        if (source != null) {
//            Set<Long> authorizeRoleIds = Sets.difference(roleIds, permissionsMap.keySet());
//            for (Long authorizeRoleId : authorizeRoleIds) {
//                for (PA authorizePermission : permissions) {
//                    createAuthorizedList.add(generatorRolePermission(authorizeRoleId, authorizePermission, source));
//                    changedAuthorizedList.add(generatorChangedRolePermission(authorizeRoleId, authorizePermission, source, authorizePermission.getAuthorizedValue()));
//                }
//            }
//        }
//        for (Map.Entry<Long, List<PR>> permissionEntry : permissionsMap.entrySet()) {
//            Long authorizeRoleId = permissionEntry.getKey();
//            List<PR> currentPermissions = permissionEntry.getValue();
//            MemoryListSearchCache<Long, PR> currentPermissionCache = new MemoryListSearchCache<>(currentPermissions, PR::getPermissionId);
//            for (PA authorizePermission : permissions) {
//                PR currentPermission = currentPermissionCache.get(authorizePermission.getId());
//                if (currentPermission == null) {
//                    if (source != null) {
//                        createAuthorizedList.add(generatorRolePermission(authorizeRoleId, authorizePermission, source));
//                        changedAuthorizedList.add(generatorChangedRolePermission(authorizeRoleId, authorizePermission, source, authorizePermission.getAuthorizedValue()));
//                    }
//                } else {
//                    if (AuthorizationSourceEnum.BUILD_IN.equals(currentPermission.getSource())) {
//                        logModifyBuildInWarn(currentPermission);
//                        continue;
//                    }
//                    Long currentAuthorizedValue = currentPermission.getAuthorizedValue();
//                    Long authorizedValue = computer.compute(currentAuthorizedValue, authorizePermission.getAuthorizedValue());
//                    if (!currentAuthorizedValue.equals(authorizedValue)) {
//                        setAuthorizedValue(currentPermission, authorizedValue);
//                        updateAuthorizedList.add(currentPermission);
//                        changedAuthorizedList.add(generatorChangedRolePermission(authorizeRoleId, authorizePermission, currentPermission.getSource(), authorizedValue));
//                    }
//                }
//            }
//        }
//        if (!createAuthorizedList.isEmpty()) {
//            createBatchAuthorization(createAuthorizedList);
//        }
//        if (!updateAuthorizedList.isEmpty()) {
//            updateBatchAuthorization(updateAuthorizedList);
//        }
//        return changedAuthorizedList;
//    }
//
//    protected Map<Long, List<PR>> queryPermissionsBatch(Set<Long> roleIds, Set<Long> permissionIds) {
//        Map<Long, List<PR>> permissionIdsMap = new LinkedHashMap<>(roleIds.size());
//        List<PR> rolePermissionList = DataShardingHelper.build().collectionSharding(permissionIds, (sublist) -> queryListAuthorization(roleIds, sublist));
//        for (PR rolePermission : rolePermissionList) {
//            permissionIdsMap.computeIfAbsent(rolePermission.getRoleId(), v -> new ArrayList<>()).add(rolePermission);
//        }
//        List<PR> permissionsByAllFlag = queryPermissionIdsByAllFlag();
//        if (CollectionUtils.isNotEmpty(permissionsByAllFlag)) {
//            for (Long roleId : roleIds) {
//                permissionIdsMap.computeIfAbsent(roleId, v -> new ArrayList<>()).addAll(permissionsByAllFlag);
//            }
//        }
//        return permissionIdsMap;
//    }
}
