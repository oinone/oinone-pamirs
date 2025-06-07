package pro.shushi.pamirs.auth.api.service.manager.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.cache.entity.*;
import pro.shushi.pamirs.auth.api.cache.service.*;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.extend.cache.AuthPermissionCacheExtendApi;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthPermissionCacheManager;
import pro.shushi.pamirs.auth.api.service.manager.AuthQueryAuthorizationOperator;
import pro.shushi.pamirs.auth.api.utils.AuthAuthorizationHelper;
import pro.shushi.pamirs.core.common.diff.DiffValue;
import pro.shushi.pamirs.core.common.path.ResourcePathParser;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 权限缓存管理
 *
 * @author Adamancy Zhang at 16:56 on 2024-01-22
 */
@Service
public class AuthPermissionCacheManagerImpl implements AuthPermissionCacheManager {

    @Autowired
    private AuthQueryAuthorizationOperator authQueryAuthorizationOperator;

    @Autowired
    private ResourcePathParser resourcePathParser;

    @Override
    public boolean authorizeRefreshPermissions(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        Map<Long, List<AuthResourceAuthorization>> resourceAuthorizations = authQueryAuthorizationOperator.queryRoleResourceAuthorizations(roleIds);
        Map<Long, List<AuthModelAuthorization>> modelAuthorizations = authQueryAuthorizationOperator.queryRoleModelAuthorizations(roleIds);
        Map<Long, List<AuthFieldAuthorization>> fieldAuthorizations = authQueryAuthorizationOperator.queryRoleFieldAuthorizations(roleIds);
        Map<Long, List<AuthRowAuthorization>> rowAuthorizations = authQueryAuthorizationOperator.queryRoleRowAuthorizations(roleIds);
        boolean isSuccess = false;
        for (Long roleId : roleIds) {
            isSuccess = authorizeRefreshPermissions(Collections.singleton(roleId),
                    resourceAuthorizations.get(roleId),
                    modelAuthorizations.get(roleId),
                    fieldAuthorizations.get(roleId),
                    rowAuthorizations.get(roleId),
                    true) || isSuccess;
        }
        return isSuccess;
    }

    @Override
    public boolean authorizeRefreshResourcePermissions(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(resourceAuthorizations)) {
            return false;
        }
        authorizeResourcePermissionRefreshCache(roleIds, resourceAuthorizations, false);
        return true;
    }

    @Override
    public boolean authorizeRefreshModelPermissions(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(modelAuthorizations)) {
            return false;
        }
        authorizeModelPermissionRefreshCache(roleIds, modelAuthorizations);
        return true;
    }

    @Override
    public boolean authorizeRefreshFieldPermissions(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(fieldAuthorizations)) {
            return false;
        }
        authorizeFieldPermissionRefreshCache(roleIds, fieldAuthorizations, false);
        return true;
    }

    @Override
    public boolean authorizeRefreshRowPermissions(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(rowAuthorizations)) {
            return false;
        }
        authorizeRowPermissionRefreshCache(roleIds, rowAuthorizations, false);
        return true;
    }

    @Override
    public boolean revokeRefreshPermissions(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        Map<Long, List<AuthResourceAuthorization>> resourceAuthorizations = authQueryAuthorizationOperator.queryRoleResourceAuthorizations(roleIds);
        Map<Long, List<AuthModelAuthorization>> modelAuthorizations = authQueryAuthorizationOperator.queryRoleModelAuthorizations(roleIds);
        Map<Long, List<AuthFieldAuthorization>> fieldAuthorizations = authQueryAuthorizationOperator.queryRoleFieldAuthorizations(roleIds);
        Map<Long, List<AuthRowAuthorization>> rowAuthorizations = authQueryAuthorizationOperator.queryRoleRowAuthorizations(roleIds);
        boolean isSuccess = false;
        for (Long roleId : roleIds) {
            isSuccess = revokeRefreshPermissions(Collections.singleton(roleId),
                    resourceAuthorizations.get(roleId),
                    modelAuthorizations.get(roleId),
                    fieldAuthorizations.get(roleId),
                    rowAuthorizations.get(roleId),
                    true) || isSuccess;
        }
        return isSuccess;
    }

    @Override
    public boolean revokeRefreshResourcePermissions(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(resourceAuthorizations)) {
            return false;
        }
        revokeResourcePermissionRefreshCache(roleIds, resourceAuthorizations, false);
        return true;
    }

    @Override
    public boolean revokeRefreshModelPermissions(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(modelAuthorizations)) {
            return false;
        }
        revokeModelPermissionRefreshCache(roleIds, modelAuthorizations, false);
        return true;
    }

    @Override
    public boolean revokeRefreshFieldPermissions(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(fieldAuthorizations)) {
            return false;
        }
        revokeFieldPermissionRefreshCache(roleIds, fieldAuthorizations, false);
        return true;
    }

    @Override
    public boolean revokeRefreshRowPermissions(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(rowAuthorizations)) {
            return false;
        }
        revokeRowPermissionRefreshCache(roleIds, rowAuthorizations, false);
        return true;
    }

    @Override
    public boolean authorizeRefreshPermissions(Set<Long> roleIds,
                                               List<AuthResourceAuthorization> resourceAuthorizations,
                                               List<AuthModelAuthorization> modelAuthorizations,
                                               List<AuthFieldAuthorization> fieldAuthorizations,
                                               List<AuthRowAuthorization> rowAuthorizations) {
        return authorizeRefreshPermissions(roleIds, resourceAuthorizations, modelAuthorizations, fieldAuthorizations, rowAuthorizations, false);
    }

    @Override
    public boolean revokeRefreshPermissions(Set<Long> roleIds,
                                            List<AuthResourceAuthorization> resourceAuthorizations,
                                            List<AuthModelAuthorization> modelAuthorizations,
                                            List<AuthFieldAuthorization> fieldAuthorizations,
                                            List<AuthRowAuthorization> rowAuthorizations) {
        return revokeRefreshPermissions(roleIds, resourceAuthorizations, modelAuthorizations, fieldAuthorizations, rowAuthorizations, false);
    }

    @Override
    public boolean updateRefreshPermissions(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations, List<AuthModelAuthorization> modelAuthorizations, List<AuthFieldAuthorization> fieldAuthorizations) {
        return authorizeRefreshPermissions(roleIds, resourceAuthorizations, modelAuthorizations, fieldAuthorizations, null, true);
    }

    @Override
    public boolean authorizeRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations, List<AuthModelAuthorization> modelAuthorizations, List<AuthFieldAuthorization> fieldAuthorizations, List<AuthRowAuthorization> rowAuthorizations) {
        return authorizeRefreshPermissions(resourceAuthorizations, modelAuthorizations, fieldAuthorizations, rowAuthorizations, false);
    }

    @Override
    public boolean revokeRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations, List<AuthModelAuthorization> modelAuthorizations, List<AuthFieldAuthorization> fieldAuthorizations, List<AuthRowAuthorization> rowAuthorizations) {
        return revokeRefreshPermissions(resourceAuthorizations, modelAuthorizations, fieldAuthorizations, rowAuthorizations, false);
    }

    @Override
    public boolean updateRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations, List<AuthModelAuthorization> modelAuthorizations, List<AuthFieldAuthorization> fieldAuthorizations) {
        return authorizeRefreshPermissions(resourceAuthorizations, modelAuthorizations, fieldAuthorizations, null, true);
    }

    // region authorize

    private boolean authorizeRefreshPermissions(Set<Long> roleIds,
                                                List<AuthResourceAuthorization> resourceAuthorizations,
                                                List<AuthModelAuthorization> modelAuthorizations,
                                                List<AuthFieldAuthorization> fieldAuthorizations,
                                                List<AuthRowAuthorization> rowAuthorizations,
                                                boolean override) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        boolean isSuccess = false;
        if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
            authorizeResourcePermissionRefreshCache(roleIds, resourceAuthorizations, override);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(modelAuthorizations)) {
            authorizeModelPermissionRefreshCache(roleIds, modelAuthorizations);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
            authorizeFieldPermissionRefreshCache(roleIds, fieldAuthorizations, override);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
            authorizeRowPermissionRefreshCache(roleIds, rowAuthorizations, override);
            isSuccess = true;
        }
        return isSuccess;
    }

    private void authorizeResourcePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations, boolean override) {
        resourceAuthorizationsConsumer(resourceAuthorizations,
                (moduleAuthorizations) -> authorizeModulePermissionRefreshCache(roleIds, moduleAuthorizations, override),
                (homepageAuthorizations) -> authorizeHomepagePermissionRefreshCache(roleIds, homepageAuthorizations, override),
                (menuAuthorizations) -> authorizeMenuPermissionRefreshCache(roleIds, menuAuthorizations, override),
                (actionAuthorizations) -> authorizeActionPermissionRefreshCache(roleIds, actionAuthorizations, override)
        );
    }

    private void authorizeModulePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> moduleAuthorizations, boolean override) {
        AuthRoleModuleCacheService cacheService = AuthApiHolder.getAuthRoleModuleCacheService();
        Map<String, Long> authorizations = AuthAuthorizationHelper.collectionModule(moduleAuthorizations).getAll();
        if (override) {
            cacheService.set(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList()));
        } else {
            cacheService.putAll(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList()));
        }
        extendCache(extendApi -> extendApi.authorizeModulePermissionRefreshCache(roleIds, moduleAuthorizations, override));
    }

    private void authorizeHomepagePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> homepageAuthorizations, boolean override) {
        AuthRoleHomepageCacheService cacheService = AuthApiHolder.getAuthRoleHomepageCacheService();
        Map<String, Long> authorizations = AuthAuthorizationHelper.collectionModule(homepageAuthorizations).getAll();
        if (override) {
            cacheService.set(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList()));
        } else {
            cacheService.putAll(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList()));
        }
        extendCache(extendApi -> extendApi.authorizeHomepagePermissionRefreshCache(roleIds, homepageAuthorizations, override));
    }

    private void authorizeMenuPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> menuAuthorizations, boolean override) {
        AuthRoleMenuCacheService cacheService = AuthApiHolder.getAuthRoleMenuCacheService();
        Map<MenuCacheKey, Map<String, Long>> groups = new LinkedHashMap<>(menuAuthorizations.size() * roleIds.size());
        for (Long roleId : roleIds) {
            groups.putAll(AuthAuthorizationHelper.collectionMenu(roleId, menuAuthorizations).getAll());
        }
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.putAll(groups);
        }
        extendCache(extendApi -> extendApi.authorizeMenuPermissionRefreshCache(roleIds, menuAuthorizations, override));
    }

    private void authorizeActionPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
        authorizeActionPermissionRefreshCacheByViewAction(roleIds, actionAuthorizations, override);
        authorizeActionPermissionRefreshCacheByModel(roleIds, actionAuthorizations, override);
        extendCache(extendApi -> extendApi.authorizeActionPermissionRefreshCache(roleIds, actionAuthorizations, override));
    }

    private void authorizeActionPermissionRefreshCacheByViewAction(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
        AuthRoleActionByViewActionCacheService cacheService = AuthApiHolder.getAuthRoleActionByViewActionCacheService();
        Map<ActionCacheKeyByViewAction, Map<String, Long>> groups = new LinkedHashMap<>(actionAuthorizations.size() * roleIds.size());
        for (Long roleId : roleIds) {
            groups.putAll(AuthAuthorizationHelper.collectionActionByViewAction(roleId, actionAuthorizations).getAll());
        }
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.putAll(groups);
        }
    }

    private void authorizeActionPermissionRefreshCacheByModel(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
        AuthRoleActionByModelCacheService cacheService = AuthApiHolder.getAuthRoleActionByModelCacheService();
        Map<ActionCacheKeyByModel, Map<String, Long>> groups = new LinkedHashMap<>(actionAuthorizations.size() * roleIds.size());
        for (Long roleId : roleIds) {
            groups.putAll(AuthAuthorizationHelper.collectionActionByModel(roleId, actionAuthorizations).getAll());
        }
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.putAll(groups);
        }
    }

    private void authorizeModelPermissionRefreshCache(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations) {
        AuthRoleModelCacheService cacheService = AuthApiHolder.getAuthRoleModelCacheService();
        Map<ModelCacheKey, Long> groups = new LinkedHashMap<>(modelAuthorizations.size() * roleIds.size());
        for (Long roleId : roleIds) {
            groups.putAll(AuthAuthorizationHelper.collectionModel(roleId, modelAuthorizations).getAll());
        }
        cacheService.set(groups);
        extendCache(extendApi -> extendApi.authorizeModelPermissionRefreshCache(roleIds, modelAuthorizations));
    }

    private void authorizeFieldPermissionRefreshCache(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations, boolean override) {
        AuthRoleFieldCacheService cacheService = AuthApiHolder.getAuthRoleFieldCacheService();
        Map<FieldCacheKey, Map<String, Long>> groups = new LinkedHashMap<>(fieldAuthorizations.size() * roleIds.size());
        for (Long roleId : roleIds) {
            groups.putAll(AuthAuthorizationHelper.collectionField(roleId, fieldAuthorizations).getAll());
        }
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.putAll(groups);
        }
        extendCache(extendApi -> extendApi.authorizeFieldPermissionRefreshCache(roleIds, fieldAuthorizations, override));
    }

    private void authorizeRowPermissionRefreshCache(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations, boolean override) {
        AuthRoleRowCacheService cacheService = AuthApiHolder.getAuthRoleRowCacheService();
        Map<RowCacheKey, Set<String>> groups = new LinkedHashMap<>(rowAuthorizations.size() * roleIds.size());
        for (Long roleId : roleIds) {
            groups.putAll(AuthAuthorizationHelper.collectionRowAuthorizations(roleId, rowAuthorizations));
        }
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.add(groups);
        }
        extendCache(extendApi -> extendApi.authorizeRowPermissionRefreshCache(roleIds, rowAuthorizations, override));
    }

    private boolean authorizeRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations,
                                                List<AuthModelAuthorization> modelAuthorizations,
                                                List<AuthFieldAuthorization> fieldAuthorizations,
                                                List<AuthRowAuthorization> rowAuthorizations,
                                                boolean override) {
        boolean isSuccess = false;
        if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
            authorizeResourcePermissionRefreshCache(resourceAuthorizations, override);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(modelAuthorizations)) {
            authorizeModelPermissionRefreshCache(modelAuthorizations);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
            authorizeFieldPermissionRefreshCache(fieldAuthorizations, override);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
            authorizeRowPermissionRefreshCache(rowAuthorizations, override);
            isSuccess = true;
        }
        return isSuccess;
    }

    private void authorizeResourcePermissionRefreshCache(List<AuthResourceAuthorization> resourceAuthorizations, boolean override) {
        resourceAuthorizationsConsumer(resourceAuthorizations,
                (moduleAuthorizations) -> authorizeModulePermissionRefreshCache(moduleAuthorizations, override),
                (homepageAuthorizations) -> authorizeHomepagePermissionRefreshCache(homepageAuthorizations, override),
                (menuAuthorizations) -> authorizeMenuPermissionRefreshCache(menuAuthorizations, override),
                (actionAuthorizations) -> authorizeActionPermissionRefreshCache(actionAuthorizations, override)
        );
    }

    private void authorizeModulePermissionRefreshCache(List<AuthResourceAuthorization> moduleAuthorizations, boolean override) {
        AuthRoleModuleCacheService cacheService = AuthApiHolder.getAuthRoleModuleCacheService();
        Map<Long, Map<String, Long>> authorizations = AuthAuthorizationHelper.collectionModuleAndRoles(moduleAuthorizations).getAll();
        if (override) {
            cacheService.set(authorizations);
        } else {
            cacheService.putAll(authorizations);
        }
        extendCache(extendApi -> extendApi.authorizeModulePermissionRefreshCache(moduleAuthorizations, override));
    }

    private void authorizeHomepagePermissionRefreshCache(List<AuthResourceAuthorization> homepageAuthorizations, boolean override) {
        AuthRoleHomepageCacheService cacheService = AuthApiHolder.getAuthRoleHomepageCacheService();
        Map<Long, Map<String, Long>> authorizations = AuthAuthorizationHelper.collectionModuleAndRoles(homepageAuthorizations).getAll();
        if (override) {
            cacheService.set(authorizations);
        } else {
            cacheService.putAll(authorizations);
        }
        extendCache(extendApi -> extendApi.authorizeHomepagePermissionRefreshCache(homepageAuthorizations, override));
    }

    private void authorizeMenuPermissionRefreshCache(List<AuthResourceAuthorization> menuAuthorizations, boolean override) {
        AuthRoleMenuCacheService cacheService = AuthApiHolder.getAuthRoleMenuCacheService();
        Map<MenuCacheKey, Map<String, Long>> groups = AuthAuthorizationHelper.collectionMenuAndRoles(menuAuthorizations).getAll();
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.putAll(groups);
        }
        extendCache(extendApi -> extendApi.authorizeMenuPermissionRefreshCache(menuAuthorizations, override));
    }

    private void authorizeActionPermissionRefreshCache(List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
        authorizeActionPermissionRefreshCacheByViewAction(actionAuthorizations, override);
        authorizeActionPermissionRefreshCacheByModel(actionAuthorizations, override);
        extendCache(extendApi -> extendApi.authorizeActionPermissionRefreshCache(actionAuthorizations, override));
    }

    private void authorizeActionPermissionRefreshCacheByViewAction(List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
        Map<ActionCacheKeyByViewAction, Map<String, Long>> groups = AuthAuthorizationHelper.collectionActionByViewActionAndRoles(actionAuthorizations).getAll();
        if (MapUtils.isNotEmpty(groups)) {
            AuthRoleActionByViewActionCacheService cacheService = AuthApiHolder.getAuthRoleActionByViewActionCacheService();
            if (override) {
                cacheService.set(groups);
            } else {
                cacheService.putAll(groups);
            }
        }
    }

    private void authorizeActionPermissionRefreshCacheByModel(List<AuthResourceAuthorization> actionAuthorizations, boolean override) {
        Map<ActionCacheKeyByModel, Map<String, Long>> groups = AuthAuthorizationHelper.collectionActionByModelAndRoles(actionAuthorizations).getAll();
        if (MapUtils.isNotEmpty(groups)) {
            AuthRoleActionByModelCacheService cacheService = AuthApiHolder.getAuthRoleActionByModelCacheService();
            if (override) {
                cacheService.set(groups);
            } else {
                cacheService.putAll(groups);
            }
        }
    }

    private void authorizeModelPermissionRefreshCache(List<AuthModelAuthorization> modelAuthorizations) {
        AuthRoleModelCacheService cacheService = AuthApiHolder.getAuthRoleModelCacheService();
        Map<ModelCacheKey, Long> groups = AuthAuthorizationHelper.collectionModelAndRoles(modelAuthorizations).getAll();
        cacheService.set(groups);
        extendCache(extendApi -> extendApi.authorizeModelPermissionRefreshCache(modelAuthorizations));
    }

    private void authorizeFieldPermissionRefreshCache(List<AuthFieldAuthorization> fieldAuthorizations, boolean override) {
        AuthRoleFieldCacheService cacheService = AuthApiHolder.getAuthRoleFieldCacheService();
        Map<FieldCacheKey, Map<String, Long>> groups = AuthAuthorizationHelper.collectionFieldAndRoles(fieldAuthorizations).getAll();
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.putAll(groups);
        }
        extendCache(extendApi -> extendApi.authorizeFieldPermissionRefreshCache(fieldAuthorizations, override));
    }

    private void authorizeRowPermissionRefreshCache(List<AuthRowAuthorization> rowAuthorizations, boolean override) {
        AuthRoleRowCacheService cacheService = AuthApiHolder.getAuthRoleRowCacheService();
        Map<RowCacheKey, Set<String>> groups = AuthAuthorizationHelper.collectionRowAndRoles(rowAuthorizations);
        if (override) {
            cacheService.set(groups);
        } else {
            cacheService.add(groups);
        }
        extendCache(extendApi -> extendApi.authorizeRowPermissionRefreshCache(rowAuthorizations, override));
    }

    // endregion

    // region revoke

    private boolean revokeRefreshPermissions(Set<Long> roleIds,
                                             List<AuthResourceAuthorization> resourceAuthorizations,
                                             List<AuthModelAuthorization> modelAuthorizations,
                                             List<AuthFieldAuthorization> fieldAuthorizations,
                                             List<AuthRowAuthorization> rowAuthorizations,
                                             boolean isDelete) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        boolean isSuccess = false;
        if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
            revokeResourcePermissionRefreshCache(roleIds, resourceAuthorizations, isDelete);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(modelAuthorizations)) {
            revokeModelPermissionRefreshCache(roleIds, modelAuthorizations, isDelete);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
            revokeFieldPermissionRefreshCache(roleIds, fieldAuthorizations, isDelete);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
            revokeRowPermissionRefreshCache(roleIds, rowAuthorizations, isDelete);
            isSuccess = true;
        }
        return isSuccess;
    }

    private void revokeResourcePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations, boolean isDelete) {
        resourceAuthorizationsConsumer(resourceAuthorizations,
                (moduleAuthorizations) -> revokeModulePermissionRefreshCache(roleIds, moduleAuthorizations, isDelete),
                (homepageAuthorizations) -> revokeHomepagePermissionRefreshCache(roleIds, homepageAuthorizations, isDelete),
                (menuAuthorizations) -> revokeMenuPermissionRefreshCache(roleIds, menuAuthorizations, isDelete),
                (actionAuthorizations) -> revokeActionPermissionRefreshCache(roleIds, actionAuthorizations, isDelete)
        );
    }

    private void revokeModulePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> moduleAuthorizations, boolean isDelete) {
        AuthRoleModuleCacheService cacheService = AuthApiHolder.getAuthRoleModuleCacheService();
        if (isDelete) {
            cacheService.delete(roleIds);
        } else {
            DiffValue<Map<String, Long>> diffValue = AuthAuthorizationHelper.collectionModule(moduleAuthorizations);
            Optional.of(diffValue.getUpdate())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.putAll(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList())));
            Optional.of(diffValue.getDelete().keySet())
                    .filter(CollectionUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList())));
        }
        extendCache(extendApi -> extendApi.revokeModulePermissionRefreshCache(roleIds, moduleAuthorizations, isDelete));
    }

    private void revokeHomepagePermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> homepageAuthorizations, boolean isDelete) {
        AuthRoleHomepageCacheService cacheService = AuthApiHolder.getAuthRoleHomepageCacheService();
        if (isDelete) {
            cacheService.delete(roleIds);
        } else {
            DiffValue<Map<String, Long>> diffValue = AuthAuthorizationHelper.collectionModule(homepageAuthorizations);
            Optional.of(diffValue.getUpdate())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.putAll(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList())));
            Optional.of(diffValue.getDelete().keySet())
                    .filter(CollectionUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(roleIds, roleIds.stream().map(v -> authorizations).collect(Collectors.toList())));
        }
        extendCache(extendApi -> extendApi.revokeHomepagePermissionRefreshCache(roleIds, homepageAuthorizations, isDelete));
    }

    private void revokeMenuPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> menuAuthorizations, boolean isDelete) {
        AuthRoleMenuCacheService cacheService = AuthApiHolder.getAuthRoleMenuCacheService();
        int initialCapacity = menuAuthorizations.size() * roleIds.size();
        Map<MenuCacheKey, Map<String, Long>> allGroups = new LinkedHashMap<>(initialCapacity);
        Map<MenuCacheKey, Map<String, Long>> updateGroups = new LinkedHashMap<>(initialCapacity);
        Map<MenuCacheKey, Map<String, Long>> deleteGroups = new LinkedHashMap<>(initialCapacity);
        for (Long roleId : roleIds) {
            DiffValue<Map<MenuCacheKey, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionMenu(roleId, menuAuthorizations);
            allGroups.putAll(diffValue.getAll());
            updateGroups.putAll(diffValue.getUpdate());
            deleteGroups.putAll(diffValue.getDelete());
        }
        if (isDelete) {
            cacheService.delete(allGroups.keySet());
        } else {
            if (MapUtils.isNotEmpty(updateGroups)) {
                cacheService.putAll(updateGroups.keySet(), updateGroups.values());
            }
            if (MapUtils.isNotEmpty(deleteGroups)) {
                cacheService.remove(deleteGroups.keySet(), deleteGroups.values().stream().map(Map::keySet).collect(Collectors.toList()));
            }
        }
        extendCache(extendApi -> extendApi.revokeMenuPermissionRefreshCache(roleIds, menuAuthorizations, isDelete));
    }

    private void revokeActionPermissionRefreshCache(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
        revokeActionPermissionRefreshCacheByViewAction(roleIds, actionAuthorizations, isDelete);
        revokeActionPermissionRefreshCacheByModel(roleIds, actionAuthorizations, isDelete);
        extendCache(extendApi -> extendApi.revokeActionPermissionRefreshCache(roleIds, actionAuthorizations, isDelete));
    }

    private void revokeActionPermissionRefreshCacheByViewAction(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
        AuthRoleActionByViewActionCacheService cacheService = AuthApiHolder.getAuthRoleActionByViewActionCacheService();
        int initialCapacity = actionAuthorizations.size() * roleIds.size();
        Map<ActionCacheKeyByViewAction, Map<String, Long>> allGroups = new LinkedHashMap<>(initialCapacity);
        Map<ActionCacheKeyByViewAction, Map<String, Long>> updateGroups = new LinkedHashMap<>(initialCapacity);
        Map<ActionCacheKeyByViewAction, Map<String, Long>> deleteGroups = new LinkedHashMap<>(initialCapacity);
        for (Long roleId : roleIds) {
            DiffValue<Map<ActionCacheKeyByViewAction, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionActionByViewAction(roleId, actionAuthorizations);
            allGroups.putAll(diffValue.getAll());
            updateGroups.putAll(diffValue.getUpdate());
            deleteGroups.putAll(diffValue.getDelete());
        }
        if (isDelete) {
            cacheService.delete(allGroups.keySet());
        } else {
            if (MapUtils.isNotEmpty(updateGroups)) {
                cacheService.putAll(updateGroups.keySet(), updateGroups.values());
            }
            if (MapUtils.isNotEmpty(deleteGroups)) {
                cacheService.remove(deleteGroups.keySet(), deleteGroups.values().stream().map(Map::keySet).collect(Collectors.toList()));
            }
        }
    }

    private void revokeActionPermissionRefreshCacheByModel(Set<Long> roleIds, List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
        AuthRoleActionByModelCacheService cacheService = AuthApiHolder.getAuthRoleActionByModelCacheService();
        int initialCapacity = actionAuthorizations.size() * roleIds.size();
        Map<ActionCacheKeyByModel, Map<String, Long>> allGroups = new LinkedHashMap<>(initialCapacity);
        Map<ActionCacheKeyByModel, Map<String, Long>> updateGroups = new LinkedHashMap<>(initialCapacity);
        Map<ActionCacheKeyByModel, Map<String, Long>> deleteGroups = new LinkedHashMap<>(initialCapacity);
        for (Long roleId : roleIds) {
            DiffValue<Map<ActionCacheKeyByModel, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionActionByModel(roleId, actionAuthorizations);
            allGroups.putAll(diffValue.getAll());
            updateGroups.putAll(diffValue.getUpdate());
            deleteGroups.putAll(diffValue.getDelete());
        }
        if (isDelete) {
            cacheService.delete(allGroups.keySet());
        } else {
            if (MapUtils.isNotEmpty(updateGroups)) {
                cacheService.putAll(updateGroups.keySet(), updateGroups.values());
            }
            if (MapUtils.isNotEmpty(deleteGroups)) {
                cacheService.remove(deleteGroups.keySet(), deleteGroups.values().stream().map(Map::keySet).collect(Collectors.toList()));
            }
        }
    }

    private void revokeModelPermissionRefreshCache(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations, boolean isDelete) {
        AuthRoleModelCacheService cacheService = AuthApiHolder.getAuthRoleModelCacheService();
        int initialCapacity = modelAuthorizations.size() * roleIds.size();
        Map<ModelCacheKey, Long> allGroups = new LinkedHashMap<>(initialCapacity);
        Map<ModelCacheKey, Long> updateGroups = new LinkedHashMap<>(initialCapacity);
        Map<ModelCacheKey, Long> deleteGroups = new LinkedHashMap<>(initialCapacity);
        for (Long roleId : roleIds) {
            DiffValue<Map<ModelCacheKey, Long>> diffValue = AuthAuthorizationHelper.collectionModel(roleId, modelAuthorizations);
            allGroups.putAll(diffValue.getAll());
            updateGroups.putAll(diffValue.getUpdate());
            deleteGroups.putAll(diffValue.getDelete());
        }
        if (isDelete) {
            cacheService.delete(allGroups.keySet());
        } else {
            if (MapUtils.isNotEmpty(updateGroups)) {
                cacheService.set(updateGroups.keySet(), updateGroups.values());
            }
            if (MapUtils.isNotEmpty(deleteGroups)) {
                cacheService.delete(deleteGroups.keySet());
            }
        }
        extendCache(extendApi -> extendApi.revokeModelPermissionRefreshCache(roleIds, modelAuthorizations, isDelete));
    }

    private void revokeFieldPermissionRefreshCache(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations, boolean isDelete) {
        AuthRoleFieldCacheService cacheService = AuthApiHolder.getAuthRoleFieldCacheService();
        int initialCapacity = fieldAuthorizations.size() * roleIds.size();
        Map<FieldCacheKey, Map<String, Long>> allGroups = new LinkedHashMap<>(initialCapacity);
        Map<FieldCacheKey, Map<String, Long>> updateGroups = new LinkedHashMap<>(initialCapacity);
        Map<FieldCacheKey, Map<String, Long>> deleteGroups = new LinkedHashMap<>(initialCapacity);
        for (Long roleId : roleIds) {
            DiffValue<Map<FieldCacheKey, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionField(roleId, fieldAuthorizations);
            allGroups.putAll(diffValue.getAll());
            updateGroups.putAll(diffValue.getUpdate());
            deleteGroups.putAll(diffValue.getDelete());
        }
        if (isDelete) {
            cacheService.delete(allGroups.keySet());
        } else {
            if (MapUtils.isNotEmpty(updateGroups)) {
                cacheService.putAll(updateGroups);
            }
            if (MapUtils.isNotEmpty(deleteGroups)) {
                cacheService.remove(deleteGroups.keySet(), deleteGroups.values().stream().map(Map::keySet).collect(Collectors.toList()));
            }
        }
        extendCache(extendApi -> extendApi.revokeFieldPermissionRefreshCache(roleIds, fieldAuthorizations, isDelete));
    }

    private void revokeRowPermissionRefreshCache(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations, boolean isDelete) {
        AuthRoleRowCacheService cacheService = AuthApiHolder.getAuthRoleRowCacheService();
        Map<RowCacheKey, Set<String>> groups = new LinkedHashMap<>(rowAuthorizations.size() * roleIds.size());
        for (Long roleId : roleIds) {
            groups.putAll(AuthAuthorizationHelper.collectionRowAuthorizations(roleId, rowAuthorizations));
        }
        if (isDelete) {
            cacheService.delete(groups.keySet());
        } else {
            cacheService.remove(groups);
        }
        extendCache(extendApi -> extendApi.revokeRowPermissionRefreshCache(roleIds, rowAuthorizations, isDelete));
    }

    private boolean revokeRefreshPermissions(List<AuthResourceAuthorization> resourceAuthorizations,
                                             List<AuthModelAuthorization> modelAuthorizations,
                                             List<AuthFieldAuthorization> fieldAuthorizations,
                                             List<AuthRowAuthorization> rowAuthorizations,
                                             boolean isDelete) {
        boolean isSuccess = false;
        if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
            revokeResourcePermissionRefreshCache(resourceAuthorizations, isDelete);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(modelAuthorizations)) {
            revokeModelPermissionRefreshCache(modelAuthorizations, isDelete);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
            revokeFieldPermissionRefreshCache(fieldAuthorizations, isDelete);
            isSuccess = true;
        }
        if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
            revokeRowPermissionRefreshCache(rowAuthorizations, isDelete);
            isSuccess = true;
        }
        return isSuccess;
    }

    private void revokeResourcePermissionRefreshCache(List<AuthResourceAuthorization> resourceAuthorizations, boolean isDelete) {
        resourceAuthorizationsConsumer(resourceAuthorizations,
                (moduleAuthorizations) -> revokeModulePermissionRefreshCache(moduleAuthorizations, isDelete),
                (homepageAuthorizations) -> revokeHomepagePermissionRefreshCache(homepageAuthorizations, isDelete),
                (menuAuthorizations) -> revokeMenuPermissionRefreshCache(menuAuthorizations, isDelete),
                (actionAuthorizations) -> revokeActionPermissionRefreshCache(actionAuthorizations, isDelete)
        );
    }

    private void revokeModulePermissionRefreshCache(List<AuthResourceAuthorization> moduleAuthorizations, boolean isDelete) {
        AuthRoleModuleCacheService cacheService = AuthApiHolder.getAuthRoleModuleCacheService();
        DiffValue<Map<Long, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionModuleAndRoles(moduleAuthorizations);
        if (isDelete) {
            Optional.of(diffValue.getAll())
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        } else {
            Optional.of(diffValue.getUpdate()).filter(MapUtils::isNotEmpty).ifPresent(cacheService::putAll);
            Optional.of(diffValue.getDelete())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        }
        extendCache(extendApi -> extendApi.revokeModulePermissionRefreshCache(moduleAuthorizations, isDelete));
    }

    private void revokeHomepagePermissionRefreshCache(List<AuthResourceAuthorization> homepageAuthorizations, boolean isDelete) {
        AuthRoleHomepageCacheService cacheService = AuthApiHolder.getAuthRoleHomepageCacheService();
        DiffValue<Map<Long, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionModuleAndRoles(homepageAuthorizations);
        if (isDelete) {
            Optional.of(diffValue.getAll())
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        } else {
            Optional.of(diffValue.getUpdate()).filter(MapUtils::isNotEmpty).ifPresent(cacheService::putAll);
            Optional.of(diffValue.getDelete())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        }
        extendCache(extendApi -> extendApi.revokeHomepagePermissionRefreshCache(homepageAuthorizations, isDelete));
    }

    private void revokeMenuPermissionRefreshCache(List<AuthResourceAuthorization> menuAuthorizations, boolean isDelete) {
        AuthRoleMenuCacheService cacheService = AuthApiHolder.getAuthRoleMenuCacheService();
        DiffValue<Map<MenuCacheKey, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionMenuAndRoles(menuAuthorizations);
        if (isDelete) {
            Optional.of(diffValue.getAll())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        } else {
            Optional.of(diffValue.getUpdate()).filter(MapUtils::isNotEmpty).ifPresent(cacheService::putAll);
            Optional.of(diffValue.getDelete())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        }
        extendCache(extendApi -> extendApi.revokeMenuPermissionRefreshCache(menuAuthorizations, isDelete));
    }

    private void revokeActionPermissionRefreshCache(List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
        revokeActionPermissionRefreshCacheByViewAction(actionAuthorizations, isDelete);
        revokeActionPermissionRefreshCacheByModel(actionAuthorizations, isDelete);
        extendCache(extendApi -> extendApi.revokeActionPermissionRefreshCache(actionAuthorizations, isDelete));
    }

    private void revokeActionPermissionRefreshCacheByViewAction(List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
        AuthRoleActionByViewActionCacheService cacheService = AuthApiHolder.getAuthRoleActionByViewActionCacheService();
        DiffValue<Map<ActionCacheKeyByViewAction, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionActionByViewActionAndRoles(actionAuthorizations);
        if (isDelete) {
            Optional.of(diffValue.getAll())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        } else {
            Optional.of(diffValue.getUpdate()).filter(MapUtils::isNotEmpty).ifPresent(cacheService::putAll);
            Optional.of(diffValue.getDelete())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        }
    }

    private void revokeActionPermissionRefreshCacheByModel(List<AuthResourceAuthorization> actionAuthorizations, boolean isDelete) {
        AuthRoleActionByModelCacheService cacheService = AuthApiHolder.getAuthRoleActionByModelCacheService();
        DiffValue<Map<ActionCacheKeyByModel, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionActionByModelAndRoles(actionAuthorizations);
        if (isDelete) {
            Optional.of(diffValue.getAll())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        } else {
            Optional.of(diffValue.getUpdate()).filter(MapUtils::isNotEmpty).ifPresent(cacheService::putAll);
            Optional.of(diffValue.getDelete())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        }
    }

    private void revokeModelPermissionRefreshCache(List<AuthModelAuthorization> modelAuthorizations, boolean isDelete) {
        AuthRoleModelCacheService cacheService = AuthApiHolder.getAuthRoleModelCacheService();
        DiffValue<Map<ModelCacheKey, Long>> diffValue = AuthAuthorizationHelper.collectionModelAndRoles(modelAuthorizations);
        if (isDelete) {
            cacheService.delete(diffValue.getAll().keySet());
        } else {
            Optional.of(diffValue.getUpdate()).filter(MapUtils::isNotEmpty).ifPresent(cacheService::set);
            Optional.of(diffValue.getDelete().keySet()).filter(CollectionUtils::isNotEmpty).ifPresent(cacheService::delete);
        }
        extendCache(extendApi -> extendApi.revokeModelPermissionRefreshCache(modelAuthorizations, isDelete));
    }

    private void revokeFieldPermissionRefreshCache(List<AuthFieldAuthorization> fieldAuthorizations, boolean isDelete) {
        AuthRoleFieldCacheService cacheService = AuthApiHolder.getAuthRoleFieldCacheService();
        DiffValue<Map<FieldCacheKey, Map<String, Long>>> diffValue = AuthAuthorizationHelper.collectionFieldAndRoles(fieldAuthorizations);
        if (isDelete) {
            Optional.of(diffValue.getAll())
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        } else {
            Optional.of(diffValue.getUpdate()).filter(MapUtils::isNotEmpty).ifPresent(cacheService::putAll);
            Optional.of(diffValue.getDelete())
                    .filter(MapUtils::isNotEmpty)
                    .ifPresent(authorizations -> cacheService.remove(authorizations.keySet(), authorizations.values().stream().map(Map::keySet).collect(Collectors.toList())));
        }
        extendCache(extendApi -> extendApi.revokeFieldPermissionRefreshCache(fieldAuthorizations, isDelete));
    }

    private void revokeRowPermissionRefreshCache(List<AuthRowAuthorization> rowAuthorizations, boolean isDelete) {
        AuthRoleRowCacheService cacheService = AuthApiHolder.getAuthRoleRowCacheService();
        Map<RowCacheKey, Set<String>> groups = AuthAuthorizationHelper.collectionRowAndRoles(rowAuthorizations);
        cacheService.remove(groups);
        extendCache(extendApi -> extendApi.revokeRowPermissionRefreshCache(rowAuthorizations, isDelete));
    }

    // endregion

    private void resourceAuthorizationsConsumer(List<AuthResourceAuthorization> resourceAuthorizations,
                                                Consumer<List<AuthResourceAuthorization>> moduleAuthorizationsConsumer,
                                                Consumer<List<AuthResourceAuthorization>> homepageAuthorizationsConsumer,
                                                Consumer<List<AuthResourceAuthorization>> menuAuthorizationsConsumer,
                                                Consumer<List<AuthResourceAuthorization>> actionAuthorizationsConsumer) {
        List<AuthResourceAuthorization> moduleAuthorizations = new ArrayList<>();
        List<AuthResourceAuthorization> homepageAuthorizations = new ArrayList<>();
        List<AuthResourceAuthorization> menuAuthorizations = new ArrayList<>();
        List<AuthResourceAuthorization> actionAuthorizations = new ArrayList<>();
        for (AuthResourceAuthorization resourceAuthorization : resourceAuthorizations) {
            ResourcePermissionTypeEnum type = resourceAuthorization.getType();
            ResourcePermissionSubtypeEnum subtype = resourceAuthorization.getSubtype();
            switch (type) {
                case MODULE: {
                    switch (subtype) {
                        case MODULE:
                            moduleAuthorizations.add(resourceAuthorization);
                            break;
                        case HOMEPAGE:
                            homepageAuthorizations.add(resourceAuthorization);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid resource authorization.");
                    }
                    break;
                }
                case MENU:
                    menuAuthorizations.add(resourceAuthorization);
                    break;
                case ACTION:
                    actionAuthorizations.add(resourceAuthorization);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid resource authorization.");
            }
        }
        if (!moduleAuthorizations.isEmpty()) {
            moduleAuthorizationsConsumer.accept(moduleAuthorizations);
        }
        if (!homepageAuthorizations.isEmpty()) {
            homepageAuthorizationsConsumer.accept(homepageAuthorizations);
        }
        if (!menuAuthorizations.isEmpty()) {
            menuAuthorizationsConsumer.accept(menuAuthorizations);
        }
        if (!actionAuthorizations.isEmpty()) {
            actionAuthorizationsConsumer.accept(actionAuthorizations);
        }
    }

    protected void extendCache(Consumer<AuthPermissionCacheExtendApi> consumer) {
        for (AuthPermissionCacheExtendApi extendApi : Spider.getLoader(AuthPermissionCacheExtendApi.class).getOrderedExtensions()) {
            consumer.accept(extendApi);
        }
    }
}
