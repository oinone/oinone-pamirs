package pro.shushi.pamirs.auth.core.service.manager;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.extpoint.AuthRoleManagerExtPoint;
import pro.shushi.pamirs.auth.api.helper.AuthRoleHelper;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.model.relation.*;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.manager.AuthRoleManager;
import pro.shushi.pamirs.auth.api.service.relation.*;
import pro.shushi.pamirs.auth.api.spi.AuthRoleManagerExtendApi;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 角色管理服务实现
 *
 * @author Adamancy Zhang at 14:40 on 2024-01-09
 */
@Service
@Fun(AuthRoleManager.FUN_NAMESPACE)
public class AuthRoleManagerImpl implements AuthRoleManager {

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthUserRoleService authUserRoleService;

    @Autowired
    private AuthRoleResourcePermissionService authRoleResourcePermissionService;

    @Autowired
    private AuthRoleModelPermissionService authRoleModelPermissionService;

    @Autowired
    private AuthRoleFieldPermissionService authRoleFieldPermissionService;

    @Autowired
    private AuthRoleRowPermissionService authRoleRowPermissionService;

    @Transactional(rollbackFor = Throwable.class)
    @Function
    @Override
    public Boolean delete(List<AuthRole> roles) {
        int initialCapacity = roles.size();
        Set<Long> roleIds = new HashSet<>(initialCapacity);
        List<AuthRole> allowOperationRoles = new ArrayList<>(initialCapacity);
        for (AuthRole role : roles) {
            if (AuthRoleHelper.isAllowOperationRole(role)) {
                allowOperationRoles.add(role);
                roleIds.add(role.getId());
            }
        }
        if (roleIds.isEmpty()) {
            return Boolean.FALSE;
        }
        List<AuthUserRoleRel> userRoleRelList = authUserRoleService.queryValidListByRoleIds(roleIds);
        if (Boolean.FALSE.equals(Ext.run(AuthRoleManagerExtPoint::deleteBefore, allowOperationRoles, userRoleRelList))) {
            return Boolean.FALSE;
        }
        Integer effectRow = authRoleService.deleteByWrapper(Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .in(AuthRole::getId, roleIds)
                .ne(AuthRole::getSource, AuthorizationSourceEnum.BUILD_IN));
        if (effectRow >= 1) {
            deleteRoles(roleIds);
            extendExecute((extendApi) -> extendApi.delete(allowOperationRoles, userRoleRelList));
            Ext.run(AuthRoleManagerExtPoint::deleteAfter, allowOperationRoles, userRoleRelList);
            revokeRefreshUserRoleCache(roleIds, userRoleRelList);
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Function
    @Override
    public Boolean active(AuthRole role) {
        Long roleId = role.getId();
        if (authRoleService.active(roleId)) {
            Set<Long> roleIds = Sets.newHashSet(roleId);
            List<AuthUserRoleRel> userRoleRelList = authUserRoleService.queryValidListByRoleIds(roleIds);
            updateUserRoleToActive(roleId);
            extendExecute((extendApi) -> extendApi.active(role, userRoleRelList));
            Ext.run(AuthRoleManagerExtPoint::activeAfter, role, userRoleRelList);
            authorizeRefreshUserRoleCache(roleIds, userRoleRelList);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Function
    @Override
    public Boolean disable(AuthRole role) {
        Long roleId = role.getId();
        if (authRoleService.disable(roleId)) {
            Set<Long> roleIds = Sets.newHashSet(roleId);
            List<AuthUserRoleRel> userRoleRelList = authUserRoleService.queryValidListByRoleIds(roleIds);
            updateUserRoleToInactive(roleId);
            extendExecute((extendApi) -> extendApi.disable(role, userRoleRelList));
            Ext.run(AuthRoleManagerExtPoint::disableAfter, role, userRoleRelList);
            revokeRefreshUserRoleCache(roleIds, userRoleRelList);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void deleteRoles(Set<Long> roleIds) {
        authUserRoleService.deleteByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .in(AuthUserRoleRel::getRoleId, roleIds)
                .ne(AuthUserRoleRel::getSource, AuthorizationSourceEnum.BUILD_IN));
        authRoleResourcePermissionService.deleteByWrapper(Pops.<AuthRoleResourcePermission>lambdaQuery()
                .from(AuthRoleResourcePermission.MODEL_MODEL)
                .in(AuthRoleResourcePermission::getRoleId, roleIds)
                .ne(AuthRoleResourcePermission::getSource, AuthorizationSourceEnum.BUILD_IN));
        authRoleModelPermissionService.deleteByWrapper(Pops.<AuthRoleModelPermission>lambdaQuery()
                .from(AuthRoleModelPermission.MODEL_MODEL)
                .in(AuthRoleModelPermission::getRoleId, roleIds)
                .ne(AuthRoleModelPermission::getSource, AuthorizationSourceEnum.BUILD_IN));
        authRoleFieldPermissionService.deleteByWrapper(Pops.<AuthRoleFieldPermission>lambdaQuery()
                .from(AuthRoleFieldPermission.MODEL_MODEL)
                .in(AuthRoleFieldPermission::getRoleId, roleIds)
                .ne(AuthRoleFieldPermission::getSource, AuthorizationSourceEnum.BUILD_IN));
        authRoleRowPermissionService.deleteByWrapper(Pops.<AuthRoleRowPermission>lambdaQuery()
                .from(AuthRoleRowPermission.MODEL_MODEL)
                .in(AuthRoleRowPermission::getRoleId, roleIds)
                .ne(AuthRoleRowPermission::getSource, AuthorizationSourceEnum.BUILD_IN));
        Models.origin().deleteByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                .from(AuthGroupRole.MODEL_MODEL)
                .in(AuthGroupRole::getRoleId, roleIds));
    }

    private void updateUserRoleToActive(Long roleId) {
        updateUserRoleActive(roleId, false, true);
    }

    private void updateUserRoleToInactive(Long roleId) {
        updateUserRoleActive(roleId, true, false);
    }

    private void updateUserRoleActive(Long roleId, Boolean from, Boolean to) {
        authUserRoleService.updateByWrapper(new AuthUserRoleRel().setActive(to), Pops.<AuthUserRoleRel>lambdaUpdate()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .eq(AuthUserRoleRel::getRoleId, roleId)
                .ne(AuthUserRoleRel::getSource, AuthorizationSourceEnum.BUILD_IN)
                .eq(AuthUserRoleRel::getActive, from));
    }

    private void authorizeRefreshUserRoleCache(Set<Long> roleIds, List<AuthUserRoleRel> userRoleRelList) {
        RefreshCacheEntity refreshCacheEntity = collectionRefreshCacheEntity(roleIds, userRoleRelList);
        if (CollectionUtils.isEmpty(userRoleRelList)) {
            return;
        }
        AuthApiHolder.getAuthUserRoleCacheService().add(refreshCacheEntity.getUserIds(), refreshCacheEntity.getRoleIdsCollection());
    }

    private void revokeRefreshUserRoleCache(Set<Long> roleIds, List<AuthUserRoleRel> userRoleRelList) {
        RefreshCacheEntity refreshCacheEntity = collectionRefreshCacheEntity(roleIds, userRoleRelList);
        if (CollectionUtils.isEmpty(userRoleRelList)) {
            AuthApiHolder.getAuthUserRoleCacheService().delete(refreshCacheEntity.getUserIds());
            return;
        }
        AuthApiHolder.getAuthUserRoleCacheService().remove(refreshCacheEntity.getUserIds(), refreshCacheEntity.getRoleIdsCollection());
    }

    private void extendExecute(Consumer<AuthRoleManagerExtendApi> consumer) {
        for (AuthRoleManagerExtendApi extendApi : Spider.getLoader(AuthRoleManagerExtendApi.class).getOrderedExtensions()) {
            consumer.accept(extendApi);
        }
    }

    private RefreshCacheEntity collectionRefreshCacheEntity(Set<Long> roleIds, List<AuthUserRoleRel> userRoleRelList) {
        Set<Long> userIds = new HashSet<>(userRoleRelList.size());
        List<Set<Long>> roleIdsCollection = new ArrayList<>(userRoleRelList.size());
        for (AuthUserRoleRel userRoleRel : userRoleRelList) {
            if (AuthorizationSourceEnum.BUILD_IN.equals(userRoleRel.getSource())) {
                continue;
            }
            if (ObjectHelper.isNotRepeat(userIds, userRoleRel.getUserId())) {
                roleIdsCollection.add(roleIds);
            }
        }
        return new RefreshCacheEntity(userIds, roleIdsCollection);
    }

    private static class RefreshCacheEntity {

        private final Set<Long> userIds;

        private final List<Set<Long>> roleIdsCollection;

        public RefreshCacheEntity(Set<Long> userIds, List<Set<Long>> roleIdsCollection) {
            this.userIds = userIds;
            this.roleIdsCollection = roleIdsCollection;
        }

        public Set<Long> getUserIds() {
            return userIds;
        }

        public List<Set<Long>> getRoleIdsCollection() {
            return roleIdsCollection;
        }
    }
}
