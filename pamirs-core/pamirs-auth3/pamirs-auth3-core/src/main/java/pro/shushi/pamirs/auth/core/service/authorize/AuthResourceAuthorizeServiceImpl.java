package pro.shushi.pamirs.auth.core.service.authorize;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.behavior.AuthorizedValueComputer;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.authorize.AuthResourceAuthorizeService;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleResourcePermissionService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.*;

/**
 * 资源权限授权服务实现
 *
 * @author Adamancy Zhang at 12:09 on 2024-01-08
 */
@Slf4j
@Service
@Fun(AuthResourceAuthorizeService.FUN_NAMESPACE)
public class AuthResourceAuthorizeServiceImpl extends AbstractAuthPermissionAuthorizeService<ResourceAuthorizedValueEnum, AuthRoleResourcePermission, AuthResourceAuthorization> implements AuthResourceAuthorizeService {

    @Autowired
    private AuthRoleResourcePermissionService authRoleResourcePermissionService;

    @Function
    @Override
    public AuthResourceAuthorization authorize(Long roleId, AuthResourceAuthorization permission, AuthorizationSourceEnum source) {
        return update(roleId, permission, source, AuthorizedValueComputer.AUTHORIZE);
    }

    @Function
    @Override
    public List<AuthResourceAuthorization> authorizes(Set<Long> roleIds, List<AuthResourceAuthorization> permissions, AuthorizationSourceEnum source) {
        return updates(roleIds, permissions, source, AuthorizedValueComputer.AUTHORIZE);
    }

    @Function
    @Override
    public AuthResourceAuthorization revoke(Long roleId, AuthResourceAuthorization permission) {
        return update(roleId, permission, null, AuthorizedValueComputer.REVOKE);
    }

    @Function
    @Override
    public List<AuthResourceAuthorization> revokes(Set<Long> roleIds, List<AuthResourceAuthorization> permissions) {
        return updates(roleIds, permissions, null, AuthorizedValueComputer.REVOKE);
    }

    @Function
    @Override
    public AuthResourceAuthorization update(Long roleId, AuthResourceAuthorization permission, AuthorizationSourceEnum source) {
        return update(roleId, permission, source, AuthorizedValueComputer.UPDATE);
    }

    @Function
    @Override
    public List<AuthResourceAuthorization> updates(Set<Long> roleIds, List<AuthResourceAuthorization> permissions, AuthorizationSourceEnum source) {
        return updates(roleIds, permissions, source, AuthorizedValueComputer.UPDATE);
    }

    private AuthResourceAuthorization update(Long roleId, AuthResourceAuthorization permission, AuthorizationSourceEnum source, AuthorizedValueComputer computer) {
        Long permissionId = permission.getId();
        assertRoleId(roleId);
        assertPermissionId(permissionId);
        Long authorizedValue = getAuthorizedValue(permission);
        AuthRoleResourcePermission currentPermission = authRoleResourcePermissionService.queryOneByWrapper(Pops.<AuthRoleResourcePermission>lambdaQuery()
                .from(AuthRoleResourcePermission.MODEL_MODEL)
                .eq(AuthRoleResourcePermission::getRoleId, roleId)
                .eq(AuthRoleResourcePermission::getPermissionId, permissionId));
        if (currentPermission == null) {
            if (source == null) {
                return null;
            }
            authRoleResourcePermissionService.create(generatorRoleResourcePermission(roleId, permission, source));
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
            authRoleResourcePermissionService.update(generatorRoleResourcePermission(roleId, permission, currentPermission.getSource()));
        }
        return permission;
    }

    private List<AuthResourceAuthorization> updates(Set<Long> roleIds, List<AuthResourceAuthorization> permissions, AuthorizationSourceEnum source, AuthorizedValueComputer computer) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(permissions)) {
            return null;
        }
        Set<Long> permissionIds = new HashSet<>(permissions.size());
        for (AuthResourceAuthorization permission : permissions) {
            Long permissionId = permission.getId();
            assertPermissionId(permissionId);
            permission.setAuthorizedValue(getAuthorizedValue(permission));
            permissionIds.add(permissionId);
        }
        Map<Long, List<AuthRoleResourcePermission>> permissionsMap = queryPermissionsBatch(roleIds, permissionIds);
        List<AuthRoleResourcePermission> createAuthorizedList = new ArrayList<>(16);
        List<AuthRoleResourcePermission> updateAuthorizedList = new ArrayList<>(16);
        List<AuthResourceAuthorization> changedAuthorizedList = new ArrayList<>(roleIds.size() * permissions.size());
        if (source != null) {
            Set<Long> authorizeRoleIds = Sets.difference(roleIds, permissionsMap.keySet());
            for (Long authorizeRoleId : authorizeRoleIds) {
                for (AuthResourceAuthorization authorizePermission : permissions) {
                    createAuthorizedList.add(generatorRoleResourcePermission(authorizeRoleId, authorizePermission, source));
                    changedAuthorizedList.add(generatorChangedResourcePermission(authorizeRoleId, authorizePermission, source, authorizePermission.getAuthorizedValue()));
                }
            }
        }
        for (Map.Entry<Long, List<AuthRoleResourcePermission>> permissionEntry : permissionsMap.entrySet()) {
            Long authorizeRoleId = permissionEntry.getKey();
            List<AuthRoleResourcePermission> currentPermissions = permissionEntry.getValue();
            MemoryListSearchCache<Long, AuthRoleResourcePermission> currentPermissionCache = new MemoryListSearchCache<>(currentPermissions, AuthRoleResourcePermission::getPermissionId);
            for (AuthResourceAuthorization authorizePermission : permissions) {
                AuthRoleResourcePermission currentPermission = currentPermissionCache.get(authorizePermission.getId());
                if (currentPermission == null) {
                    if (source != null) {
                        createAuthorizedList.add(generatorRoleResourcePermission(authorizeRoleId, authorizePermission, source));
                        changedAuthorizedList.add(generatorChangedResourcePermission(authorizeRoleId, authorizePermission, source, authorizePermission.getAuthorizedValue()));
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
                        changedAuthorizedList.add(generatorChangedResourcePermission(authorizeRoleId, authorizePermission, currentPermission.getSource(), authorizedValue));
                    }
                }
            }
        }
        if (!createAuthorizedList.isEmpty()) {
            authRoleResourcePermissionService.createBatch(createAuthorizedList);
        }
        if (!updateAuthorizedList.isEmpty()) {
            authRoleResourcePermissionService.updateBatch(updateAuthorizedList);
        }
        return changedAuthorizedList;
    }

    private Map<Long, List<AuthRoleResourcePermission>> queryPermissionsBatch(Set<Long> roleIds, Set<Long> permissionIds) {
        Map<Long, List<AuthRoleResourcePermission>> permissionIdsMap = new LinkedHashMap<>(roleIds.size());
        List<AuthRoleResourcePermission> rolePermissionList = DataShardingHelper.build().collectionSharding(permissionIds,
                (sublist) -> authRoleResourcePermissionService.queryListByWrapper(Pops.<AuthRoleResourcePermission>lambdaQuery()
                        .from(AuthRoleResourcePermission.MODEL_MODEL)
                        .in(AuthRoleResourcePermission::getRoleId, roleIds)
                        .in(AuthRoleResourcePermission::getPermissionId, sublist)));
        for (AuthRoleResourcePermission rolePermission : rolePermissionList) {
            permissionIdsMap.computeIfAbsent(rolePermission.getRoleId(), v -> new ArrayList<>()).add(rolePermission);
        }
        List<AuthRoleResourcePermission> permissionsByAllFlag = authRoleResourcePermissionService.queryPermissionIdsByAllFlag();
        if (CollectionUtils.isNotEmpty(permissionsByAllFlag)) {
            for (Long roleId : roleIds) {
                permissionIdsMap.computeIfAbsent(roleId, v -> new ArrayList<>()).addAll(permissionsByAllFlag);
            }
        }
        return permissionIdsMap;
    }

    private AuthRoleResourcePermission generatorRoleResourcePermission(Long roleId, AuthResourceAuthorization authorizePermission, AuthorizationSourceEnum source) {
        AuthRoleResourcePermission authorizedPermission = new AuthRoleResourcePermission();
        authorizedPermission.setRoleId(roleId);
        authorizedPermission.setPermissionId(authorizePermission.getId());
        authorizedPermission.setPermissionType(authorizePermission.getType());
        authorizedPermission.setPermissionSubtype(authorizePermission.getSubtype());
        authorizedPermission.setAuthorizedValue(authorizePermission.getAuthorizedValue());
        authorizedPermission.setSource(source);
        return authorizedPermission;
    }

    private AuthResourceAuthorization generatorChangedResourcePermission(Long roleId, AuthResourceAuthorization authorizePermission, AuthorizationSourceEnum source, Long authorizedValue) {
        AuthResourceAuthorization changedTarget = AuthResourceAuthorization.transfer(authorizePermission, new AuthResourceAuthorization());
        changedTarget.setSource(source);
        changedTarget.setResourceId(authorizePermission.getResourceId());
        changedTarget.setAuthorizedValue(authorizedValue);
        changedTarget.setRoleId(roleId);
        return changedTarget;
    }
}
