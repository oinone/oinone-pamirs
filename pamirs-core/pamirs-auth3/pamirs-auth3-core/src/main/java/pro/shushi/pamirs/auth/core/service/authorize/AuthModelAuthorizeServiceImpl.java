package pro.shushi.pamirs.auth.core.service.authorize;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.behavior.AuthorizedValueComputer;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ModelAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleModelPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.service.authorize.AuthModelAuthorizeService;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleModelPermissionService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.*;

/**
 * 模型权限授权服务实现
 *
 * @author Adamancy Zhang at 12:07 on 2024-01-08
 */
@Slf4j
@Service
@Fun(AuthModelAuthorizeService.FUN_NAMESPACE)
public class AuthModelAuthorizeServiceImpl extends AbstractAuthPermissionAuthorizeService<ModelAuthorizedValueEnum, AuthRoleModelPermission, AuthModelAuthorization> implements AuthModelAuthorizeService {

    @Autowired
    private AuthRoleModelPermissionService authRoleModelPermissionService;

    @Function
    @Override
    public AuthModelAuthorization authorize(Long roleId, AuthModelAuthorization permission, AuthorizationSourceEnum source) {
        return update(roleId, permission, source, AuthorizedValueComputer.AUTHORIZE);
    }

    @Function
    @Override
    public List<AuthModelAuthorization> authorizes(Set<Long> roleIds, List<AuthModelAuthorization> permissions, AuthorizationSourceEnum source) {
        return updates(roleIds, permissions, source, AuthorizedValueComputer.AUTHORIZE);
    }

    @Function
    @Override
    public AuthModelAuthorization revoke(Long roleId, AuthModelAuthorization permission) {
        return update(roleId, permission, null, AuthorizedValueComputer.REVOKE);
    }

    @Function
    @Override
    public List<AuthModelAuthorization> revokes(Set<Long> roleIds, List<AuthModelAuthorization> permissions) {
        return updates(roleIds, permissions, null, AuthorizedValueComputer.REVOKE);
    }

    @Function
    @Override
    public AuthModelAuthorization update(Long roleId, AuthModelAuthorization permission, AuthorizationSourceEnum source) {
        return update(roleId, permission, source, AuthorizedValueComputer.UPDATE);
    }

    @Function
    @Override
    public List<AuthModelAuthorization> updates(Set<Long> roleIds, List<AuthModelAuthorization> permissions, AuthorizationSourceEnum source) {
        return updates(roleIds, permissions, source, AuthorizedValueComputer.UPDATE);
    }

    private AuthModelAuthorization update(Long roleId, AuthModelAuthorization permission, AuthorizationSourceEnum source, AuthorizedValueComputer computer) {
        Long permissionId = permission.getId();
        assertRoleId(roleId);
        assertPermissionId(permissionId);
        Long authorizedValue = getAuthorizedValue(permission);
        AuthRoleModelPermission currentPermission = authRoleModelPermissionService.queryOneByWrapper(Pops.<AuthRoleModelPermission>lambdaQuery()
                .from(AuthRoleModelPermission.MODEL_MODEL)
                .eq(AuthRoleModelPermission::getRoleId, roleId)
                .eq(AuthRoleModelPermission::getPermissionId, permissionId));
        if (currentPermission == null) {
            if (source == null) {
                return null;
            }
            authRoleModelPermissionService.create(generatorRoleModelPermission(roleId, permission, source));
        } else {
            if (AuthorizationSourceEnum.BUILD_IN.equals(currentPermission.getSource())) {
                logModifyBuildInWarn(currentPermission);
                return null;
            }
            Long currentAuthorizedValue = currentPermission.getAuthorizedValue();
            authorizedValue = computer.compute(currentAuthorizedValue, authorizedValue);
            if (currentAuthorizedValue.equals(authorizedValue)) {
                return null;
            }
            permission.setAuthorizedValue(authorizedValue);
            authRoleModelPermissionService.update(generatorRoleModelPermission(roleId, permission, currentPermission.getSource()));
        }
        return permission;
    }

    private List<AuthModelAuthorization> updates(Set<Long> roleIds, List<AuthModelAuthorization> permissions, AuthorizationSourceEnum source, AuthorizedValueComputer computer) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(permissions)) {
            return null;
        }
        Set<Long> permissionIds = new HashSet<>(permissions.size());
        for (AuthModelAuthorization permission : permissions) {
            Long permissionId = permission.getId();
            assertPermissionId(permissionId);
            permission.setAuthorizedValue(getAuthorizedValue(permission));
            permissionIds.add(permissionId);
        }
        Map<Long, List<AuthRoleModelPermission>> permissionsMap = queryPermissionsBatch(roleIds, permissionIds);
        List<AuthRoleModelPermission> createAuthorizedList = new ArrayList<>(8);
        List<AuthRoleModelPermission> updateAuthorizedList = new ArrayList<>(16);
        List<AuthModelAuthorization> changedAuthorizedList = new ArrayList<>(roleIds.size() * permissions.size());
        if (source != null) {
            Set<Long> authorizeRoleIds = Sets.difference(roleIds, permissionsMap.keySet());
            for (Long authorizeRoleId : authorizeRoleIds) {
                for (AuthModelAuthorization authorizePermission : permissions) {
                    createAuthorizedList.add(generatorRoleModelPermission(authorizeRoleId, authorizePermission, source));
                    changedAuthorizedList.add(generatorChangedModelPermission(authorizeRoleId, authorizePermission, source, authorizePermission.getAuthorizedValue()));
                }
            }
        }
        for (Map.Entry<Long, List<AuthRoleModelPermission>> permissionEntry : permissionsMap.entrySet()) {
            Long authorizeRoleId = permissionEntry.getKey();
            List<AuthRoleModelPermission> currentPermissions = permissionEntry.getValue();
            MemoryListSearchCache<Long, AuthRoleModelPermission> currentPermissionCache = new MemoryListSearchCache<>(currentPermissions, AuthRoleModelPermission::getPermissionId);
            for (AuthModelAuthorization authorizePermission : permissions) {
                AuthRoleModelPermission currentPermission = currentPermissionCache.get(authorizePermission.getId());
                if (currentPermission == null) {
                    if (source != null) {
                        createAuthorizedList.add(generatorRoleModelPermission(authorizeRoleId, authorizePermission, source));
                        changedAuthorizedList.add(generatorChangedModelPermission(authorizeRoleId, authorizePermission, source, authorizePermission.getAuthorizedValue()));
                    }
                } else {
                    if (AuthorizationSourceEnum.BUILD_IN.equals(currentPermission.getSource())) {
                        logModifyBuildInWarn(currentPermission);
                        continue;
                    }
                    Long currentAuthorizedValue = currentPermission.getAuthorizedValue();
                    Long authorizedValue = computer.compute(currentAuthorizedValue, authorizePermission.getAuthorizedValue());
                    if (!currentAuthorizedValue.equals(authorizedValue)) {
                        currentPermission.setAuthorizedValue(authorizedValue);
                        updateAuthorizedList.add(currentPermission);
                        changedAuthorizedList.add(generatorChangedModelPermission(authorizeRoleId, authorizePermission, currentPermission.getSource(), authorizedValue));
                    }
                }
            }
        }
        if (!createAuthorizedList.isEmpty()) {
            authRoleModelPermissionService.createBatch(createAuthorizedList);
        }
        if (!updateAuthorizedList.isEmpty()) {
            authRoleModelPermissionService.updateBatch(updateAuthorizedList);
        }
        return changedAuthorizedList;
    }

    protected Map<Long, List<AuthRoleModelPermission>> queryPermissionsBatch(Set<Long> roleIds, Set<Long> permissionIds) {
        Map<Long, List<AuthRoleModelPermission>> permissionIdsMap = new LinkedHashMap<>(roleIds.size());
        List<AuthRoleModelPermission> rolePermissionList = DataShardingHelper.build().collectionSharding(permissionIds,
                (sublist) -> authRoleModelPermissionService.queryListByWrapper(Pops.<AuthRoleModelPermission>lambdaQuery()
                        .from(AuthRoleModelPermission.MODEL_MODEL)
                        .in(AuthRoleModelPermission::getRoleId, roleIds)
                        .in(AuthRoleModelPermission::getPermissionId, sublist)));
        for (AuthRoleModelPermission rolePermission : rolePermissionList) {
            permissionIdsMap.computeIfAbsent(rolePermission.getRoleId(), v -> new ArrayList<>()).add(rolePermission);
        }
        List<AuthRoleModelPermission> permissionsByAllFlag = authRoleModelPermissionService.queryPermissionIdsByAllFlag();
        if (CollectionUtils.isNotEmpty(permissionsByAllFlag)) {
            for (Long roleId : roleIds) {
                permissionIdsMap.computeIfAbsent(roleId, v -> new ArrayList<>()).addAll(permissionsByAllFlag);
            }
        }
        return permissionIdsMap;
    }

    private AuthRoleModelPermission generatorRoleModelPermission(Long roleId, AuthModelAuthorization authorizePermission, AuthorizationSourceEnum source) {
        AuthRoleModelPermission authorizedPermission = new AuthRoleModelPermission();
        authorizedPermission.setRoleId(roleId);
        authorizedPermission.setPermissionId(authorizePermission.getId());
        authorizedPermission.setAuthorizedValue(authorizePermission.getAuthorizedValue());
        authorizedPermission.setSource(source);
        return authorizedPermission;
    }

    private AuthModelAuthorization generatorChangedModelPermission(Long roleId, AuthModelAuthorization authorizePermission, AuthorizationSourceEnum source, Long authorizedValue) {
        AuthModelAuthorization changedTarget = AuthModelAuthorization.transfer(authorizePermission, new AuthModelAuthorization());
        changedTarget.setSource(source);
        changedTarget.setAuthorizedValue(authorizedValue);
        changedTarget.setRoleId(roleId);
        return changedTarget;
    }
}
