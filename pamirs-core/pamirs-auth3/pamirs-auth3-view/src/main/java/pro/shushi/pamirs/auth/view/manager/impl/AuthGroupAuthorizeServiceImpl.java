package pro.shushi.pamirs.auth.view.manager.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthAuthorizationSceneApi;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthFieldAuthorizationExtendApi;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthResourceAuthorizationExtendApi;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthRowAuthorizationExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthAuthorizationExtendExecutor;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.authorize.AuthFieldAuthorizeService;
import pro.shushi.pamirs.auth.api.service.authorize.AuthResourceAuthorizeService;
import pro.shushi.pamirs.auth.api.service.authorize.AuthRowAuthorizeService;
import pro.shushi.pamirs.auth.api.service.manager.AuthPermissionCacheManager;
import pro.shushi.pamirs.auth.view.manager.AuthGroupAuthorizeService;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.core.common.diff.DiffList;
import pro.shushi.pamirs.core.common.path.ResourcePathParser;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限组授权服务实现
 *
 * @author Adamancy Zhang at 09:49 on 2024-01-19
 */
@Slf4j
@Service
public class AuthGroupAuthorizeServiceImpl implements AuthGroupAuthorizeService {

    @Autowired
    private AuthResourceAuthorizeService authResourceAuthorizeService;

    @Autowired
    private AuthFieldAuthorizeService authFieldAuthorizeService;

    @Autowired
    private AuthRowAuthorizeService authRowAuthorizeService;

    @Autowired
    private ResourcePathParser resourcePathParser;

    @Autowired
    private AuthPermissionCacheManager authPermissionCacheManager;

    @Override
    public void authorizeRolePermissions(Set<Long> roleIds,
                                         List<AuthGroupResourcePermission> resourcePermissions,
                                         List<AuthGroupFieldPermission> fieldPermissions,
                                         List<AuthGroupRowPermission> rowPermissions) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        authorizeRolePermissions0(roleIds, resourcePermissions, fieldPermissions, rowPermissions);
    }

    @Override
    public void authorizeRolePermissions(List<AuthRole> roles, List<AuthGroupResourcePermission> resourcePermissions, List<AuthGroupFieldPermission> fieldPermissions, List<AuthGroupRowPermission> rowPermissions) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        Set<Long> roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        authorizeRolePermissions0(roleIds, resourcePermissions, fieldPermissions, rowPermissions);
    }

    private void authorizeRolePermissions0(Set<Long> roleIds,
                                           List<AuthGroupResourcePermission> resourcePermissions,
                                           List<AuthGroupFieldPermission> fieldPermissions,
                                           List<AuthGroupRowPermission> rowPermissions) {
        List<AuthResourceAuthorization> resourceAuthorizations = null;
        List<AuthFieldAuthorization> fieldAuthorizations = null;
        List<AuthRowAuthorization> rowAuthorizations = null;
        boolean isRefresh = false;
        if (CollectionUtils.isNotEmpty(resourcePermissions)) {
            resourceAuthorizations = collectionResourcePermissions(resourcePermissions);
            resourceAuthorizations = authResourceAuthorizeService.authorizes(roleIds, resourceAuthorizations, AuthorizationSourceEnum.MANUAL);
            if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
                isRefresh = true;
            }
        }
        if (CollectionUtils.isNotEmpty(fieldPermissions)) {
            fieldAuthorizations = collectionFieldPermissions(fieldPermissions);
            fieldAuthorizations = authFieldAuthorizeService.authorizes(roleIds, fieldAuthorizations, AuthorizationSourceEnum.MANUAL);
            if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
                isRefresh = true;
            }
        }
        if (CollectionUtils.isNotEmpty(rowPermissions)) {
            rowAuthorizations = collectionRowPermissions(rowPermissions);
            authRowAuthorizeService.authorizes(roleIds, rowAuthorizations, AuthorizationSourceEnum.MANUAL);
            isRefresh = true;
        }
        if (isRefresh) {
            authPermissionCacheManager.authorizeRefreshPermissions(resourceAuthorizations, null, fieldAuthorizations, null);
            authPermissionCacheManager.authorizeRefreshPermissions(roleIds, null, null, null, rowAuthorizations);
        }
    }

    @Override
    public void revokeRolePermissions(Set<Long> roleIds,
                                      List<AuthGroupResourcePermission> resourcePermissions,
                                      List<AuthGroupFieldPermission> fieldPermissions,
                                      List<AuthGroupRowPermission> rowPermissions) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        revokeRolePermissions0(roleIds, resourcePermissions, fieldPermissions, rowPermissions);
    }

    @Override
    public void revokeRolePermissions(List<AuthRole> roles, List<AuthGroupResourcePermission> resourcePermissions, List<AuthGroupFieldPermission> fieldPermissions, List<AuthGroupRowPermission> rowPermissions) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        Set<Long> roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        revokeRolePermissions0(roleIds, resourcePermissions, fieldPermissions, rowPermissions);
    }

    private void revokeRolePermissions0(Set<Long> roleIds,
                                        List<AuthGroupResourcePermission> resourcePermissions,
                                        List<AuthGroupFieldPermission> fieldPermissions,
                                        List<AuthGroupRowPermission> rowPermissions) {
        List<AuthResourceAuthorization> resourceAuthorizations = null;
        List<AuthFieldAuthorization> fieldAuthorizations = null;
        List<AuthRowAuthorization> rowAuthorizations = null;
        boolean isRefresh = false;
        if (CollectionUtils.isNotEmpty(resourcePermissions)) {
            resourceAuthorizations = collectionResourcePermissions(resourcePermissions);
            resourceAuthorizations = authResourceAuthorizeService.revokes(roleIds, resourceAuthorizations);
            if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
                isRefresh = true;
            }
        }
        if (CollectionUtils.isNotEmpty(fieldPermissions)) {
            fieldAuthorizations = collectionFieldPermissions(fieldPermissions);
            fieldAuthorizations = authFieldAuthorizeService.revokes(roleIds, fieldAuthorizations);
            if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
                isRefresh = true;
            }
        }
        if (CollectionUtils.isNotEmpty(rowPermissions)) {
            rowAuthorizations = collectionRowPermissions(rowPermissions);
            authRowAuthorizeService.revokes(roleIds, rowAuthorizations);
            isRefresh = true;
        }
        if (isRefresh) {
            authPermissionCacheManager.revokeRefreshPermissions(roleIds, resourceAuthorizations, null, fieldAuthorizations, rowAuthorizations);
        }
    }

    @Override
    public void refreshRolePermissions(Set<Long> roleIds,
                                       List<AuthGroupResourcePermission> resourcePermissions,
                                       List<AuthGroupFieldPermission> fieldPermissions,
                                       DiffList<AuthGroupRowPermission> rowPermissions) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        refreshRolePermissions0(roleIds, resourcePermissions, fieldPermissions, rowPermissions);
    }

    @Override
    public void refreshRolePermissions(List<AuthRole> roles, List<AuthGroupResourcePermission> resourcePermissions, List<AuthGroupFieldPermission> fieldPermissions, DiffList<AuthGroupRowPermission> rowPermissions) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        Set<Long> roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        refreshRolePermissions0(roleIds, resourcePermissions, fieldPermissions, rowPermissions);
    }

    private void refreshRolePermissions0(Set<Long> roleIds,
                                         List<AuthGroupResourcePermission> resourcePermissions,
                                         List<AuthGroupFieldPermission> fieldPermissions,
                                         DiffList<AuthGroupRowPermission> rowPermissions) {
        List<AuthResourceAuthorization> resourceAuthorizations = null;
        List<AuthFieldAuthorization> fieldAuthorizations = null;
        boolean isRefresh = false;
        if (CollectionUtils.isNotEmpty(resourcePermissions)) {
            resourceAuthorizations = collectionResourcePermissions(resourcePermissions);
            resourceAuthorizations = authResourceAuthorizeService.updates(roleIds, resourceAuthorizations, AuthorizationSourceEnum.MANUAL);
            if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
                isRefresh = true;
            }
        }
        if (CollectionUtils.isNotEmpty(fieldPermissions)) {
            fieldAuthorizations = collectionFieldPermissions(fieldPermissions);
            fieldAuthorizations = authFieldAuthorizeService.updates(roleIds, fieldAuthorizations, AuthorizationSourceEnum.MANUAL);
            if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
                isRefresh = true;
            }
        }

        if (isRefresh) {
            authPermissionCacheManager.authorizeRefreshPermissions(roleIds, resourceAuthorizations, null, fieldAuthorizations, null);
            if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
                final List<AuthResourceAuthorization> finalResourceAuthorizations = resourceAuthorizations;
                AuthAuthorizationExtendExecutor.execute(AuthResourceAuthorizationExtendApi.class,
                        AuthAuthorizationSceneApi.GROUP_SCENE,
                        api -> api.updates(roleIds, finalResourceAuthorizations));
            }
            if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
                final List<AuthFieldAuthorization> finalFieldAuthorizations = fieldAuthorizations;
                AuthAuthorizationExtendExecutor.execute(AuthFieldAuthorizationExtendApi.class,
                        AuthAuthorizationSceneApi.GROUP_SCENE,
                        api -> api.updates(roleIds, finalFieldAuthorizations));
            }
        }

        if (rowPermissions != null) {
            updateRolePermissions(roleIds, null, null, rowPermissions);
            List<AuthGroupRowPermission> groupRowAuthorizations = rowPermissions.getAll();
            if (CollectionUtils.isNotEmpty(groupRowAuthorizations)) {
                List<AuthRowAuthorization> rowAuthorizations = collectionRowPermissions(groupRowAuthorizations);
                AuthAuthorizationExtendExecutor.execute(AuthRowAuthorizationExtendApi.class,
                        AuthAuthorizationSceneApi.GROUP_SCENE,
                        api -> api.updates(roleIds, rowAuthorizations));
            }
        }
    }

    @Override
    public void updateRolePermissions(Set<Long> roleIds,
                                      DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions,
                                      DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions,
                                      DiffList<AuthGroupRowPermission> diffRowPermissions) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        updateRolePermissions0(roleIds, diffGroupResourcePermissions, diffGroupFieldPermissions, diffRowPermissions);
    }

    @Override
    public void updateRolePermissions(List<AuthRole> roles, DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions, DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions, DiffList<AuthGroupRowPermission> diffRowPermissions) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        Set<Long> roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        updateRolePermissions0(roleIds, diffGroupResourcePermissions, diffGroupFieldPermissions, diffRowPermissions);
    }

    private void updateRolePermissions0(Set<Long> roleIds,
                                        DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions,
                                        DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions,
                                        DiffList<AuthGroupRowPermission> diffRowPermissions) {
        List<AuthGroupResourcePermission> deleteResourcePermissions = Optional.ofNullable(diffGroupResourcePermissions).map(DiffCollection::getDelete).orElse(null);
        List<AuthGroupFieldPermission> deleteFieldPermissions = Optional.ofNullable(diffGroupFieldPermissions).map(DiffCollection::getDelete).orElse(null);
        List<AuthGroupRowPermission> deleteRowPermissions = Optional.ofNullable(diffRowPermissions).map(DiffCollection::getDelete).orElse(null);
        revokeRolePermissions(roleIds, deleteResourcePermissions, deleteFieldPermissions, deleteRowPermissions);

        List<AuthGroupResourcePermission> createResourcePermissions = Optional.ofNullable(diffGroupResourcePermissions).map(DiffCollection::getCreate).orElse(null);
        List<AuthGroupFieldPermission> createFieldPermissions = Optional.ofNullable(diffGroupFieldPermissions).map(DiffCollection::getCreate).orElse(null);
        List<AuthGroupRowPermission> createRowPermissions = Optional.ofNullable(diffRowPermissions).map(DiffCollection::getCreate).orElse(null);
        authorizeRolePermissions(roleIds, createResourcePermissions, createFieldPermissions, createRowPermissions);

        List<AuthGroupResourcePermission> updateResourcePermissions = Optional.ofNullable(diffGroupResourcePermissions).map(DiffCollection::getUpdate).orElse(null);
        List<AuthGroupFieldPermission> updateFieldPermissions = Optional.ofNullable(diffGroupFieldPermissions).map(DiffCollection::getUpdate).orElse(null);
        List<AuthGroupRowPermission> updateRowPermissions = Optional.ofNullable(diffRowPermissions).map(DiffCollection::getUpdate).orElse(null);
        authorizeRolePermissions(roleIds, updateResourcePermissions, updateFieldPermissions, updateRowPermissions);
    }

    @Override
    public void refreshRolePermissions(DiffList<AuthRole> diffRoles,
                                       DiffList<AuthGroupResourcePermission> diffGroupResourcePermissions,
                                       DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions,
                                       DiffList<AuthGroupRowPermission> diffRowPermissions) {
        List<AuthGroupResourcePermission> allResourcePermissions = Optional.ofNullable(diffGroupResourcePermissions).map(DiffCollection::getAll).orElse(null);
        List<AuthGroupFieldPermission> allFieldPermissions = Optional.ofNullable(diffGroupFieldPermissions).map(DiffCollection::getAll).orElse(null);
        List<AuthGroupRowPermission> allRowPermissions = Optional.ofNullable(diffRowPermissions).map(DiffCollection::getAll).orElse(null);

        if (allResourcePermissions != null || allFieldPermissions != null || allRowPermissions != null) {
            authorizeRolePermissions(diffRoles.getCreate(), allResourcePermissions, allFieldPermissions, allRowPermissions);
            revokeRolePermissions(diffRoles.getDelete(), allResourcePermissions, allFieldPermissions, allRowPermissions);

            List<AuthGroupResourcePermission> deleteResourcePermissions = Optional.ofNullable(diffGroupResourcePermissions).map(DiffCollection::getDelete).orElse(null);
            List<AuthGroupFieldPermission> deleteFieldPermissions = Optional.ofNullable(diffGroupFieldPermissions).map(DiffCollection::getDelete).orElse(null);
            List<AuthGroupRowPermission> deleteRowPermissions = Optional.ofNullable(diffRowPermissions).map(DiffCollection::getDelete).orElse(null);
            revokeRolePermissions(diffRoles.getDelete(), deleteResourcePermissions, deleteFieldPermissions, deleteRowPermissions);
        }

        updateRolePermissions(diffRoles.getUpdate(), diffGroupResourcePermissions, diffGroupFieldPermissions, diffRowPermissions);
    }

    public static List<AuthResourceAuthorization> collectionResourcePermissions(List<AuthGroupResourcePermission> actionPermissions) {
        List<AuthResourceAuthorization> permissions = new ArrayList<>(actionPermissions.size());
        for (AuthGroupResourcePermission actionPermission : actionPermissions) {
            AuthResourceAuthorization permission = new AuthResourceAuthorization();
            AuthResourcePermission.transfer(actionPermission.getPermission(), permission);
            permission.setAuthorizedValue(actionPermission.getAuthorizedValue());
            permissions.add(permission);
        }
        return permissions;
    }

    public static List<AuthFieldAuthorization> collectionFieldPermissions(List<AuthGroupFieldPermission> fieldPermissions) {
        List<AuthFieldAuthorization> permissions = new ArrayList<>(fieldPermissions.size());
        for (AuthGroupFieldPermission fieldPermission : fieldPermissions) {
            AuthFieldAuthorization permission = new AuthFieldAuthorization();
            AuthFieldPermission.transfer(fieldPermission.getPermission(), permission);
            permission.setAuthorizedValue(fieldPermission.getAuthorizedValue());
            permissions.add(permission);
        }
        return permissions;
    }

    public static List<AuthRowAuthorization> collectionRowPermissions(List<AuthGroupRowPermission> rowPermissions) {
        List<AuthRowAuthorization> permissions = new ArrayList<>(rowPermissions.size());
        for (AuthGroupRowPermission rowPermission : rowPermissions) {
            AuthRowAuthorization permission = new AuthRowAuthorization();
            AuthRowPermission.transfer(rowPermission.getPermission(), permission);
            permission.setAuthorizedValue(rowPermission.getAuthorizedValue());
            permissions.add(permission);
        }
        return permissions;
    }
}
