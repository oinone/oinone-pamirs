package pro.shushi.pamirs.auth.api.service.manager.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.cache.service.AuthUserRoleCacheService;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.helper.AuthRoleHelper;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.authorize.AuthUserAuthorizeService;
import pro.shushi.pamirs.auth.api.service.manager.AuthUserRoleDiffService;
import pro.shushi.pamirs.auth.api.service.manager.AuthUserRoleOperator;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.core.common.diff.DiffSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户角色差量服务
 *
 * @author Adamancy Zhang at 17:06 on 2024-01-20
 */
@Service
public class AuthUserRoleDiffServiceImpl implements AuthUserRoleDiffService {

    @Autowired
    private AuthUserRoleOperator authUserRoleOperator;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthUserAuthorizeService authUserAuthorizeService;

    @Override
    public DiffSet<Long> saveRoles(Long userId, List<AuthRole> roles) {
        List<AuthRole> existRoles = authUserRoleOperator.fetchRoles(userId);
        if (CollectionUtils.isEmpty(roles) && CollectionUtils.isEmpty(existRoles)) {
            return null;
        }
        MemoryListSearchCache<Long, AuthRole> existRoleCache = new MemoryListSearchCache<>(existRoles, AuthRole::getId);
        Set<Long> authorizeRoleIds = new HashSet<>(roles.size());

        Set<Long> allTargetRoles = new HashSet<>(roles.size());
        Set<Long> createTargetRoles = new HashSet<>(roles.size());
        Set<Long> updateTargetRoles = new HashSet<>(roles.size());
        for (AuthRole role : roles) {
            Long roleId = role.getId();
            AuthRole existRole = existRoleCache.compute(roleId, (k, v) -> v);
            allTargetRoles.add(roleId);
            if (existRole == null) {
                if (AuthRoleHelper.isActiveRole(role)) {
                    createTargetRoles.add(roleId);
                }
                authorizeRoleIds.add(roleId);
            } else {
                if (AuthRoleHelper.isActiveRole(role)) {
                    updateTargetRoles.add(roleId);
                }
            }
        }
        existRoleCache.fill();
        Set<Long> deleteTargetRoles = existRoleCache.getNotComputedCache().values().stream().map(AuthRole::getId).collect(Collectors.toSet());

        if (!authorizeRoleIds.isEmpty()) {
            authUserAuthorizeService.authorizes(Sets.newHashSet(userId), authorizeRoleIds, AuthorizationSourceEnum.MANUAL);
        }
        if (!deleteTargetRoles.isEmpty()) {
            authUserAuthorizeService.revokes(Sets.newHashSet(userId), deleteTargetRoles);
        }
        return DiffCollection.set(allTargetRoles, createTargetRoles, updateTargetRoles, deleteTargetRoles);
    }

    @Override
    public void refreshUserRoles(Long userId, DiffSet<Long> diffRoleIds) {
        Set<Long> createRoleIds = diffRoleIds.getCreate();
        if (CollectionUtils.isNotEmpty(createRoleIds)) {
            authorizeRoleRefreshCache(userId, createRoleIds);
        }
        Set<Long> deleteRoleIds = diffRoleIds.getDelete();
        if (CollectionUtils.isNotEmpty(deleteRoleIds)) {
            revokeRoleRefreshCache(userId, deleteRoleIds);
        }
    }

    @Override
    public void refreshUserRoles(Set<Long> userIds, DiffSet<Long> diffRoleIds) {
        Set<Long> createRoleIds = diffRoleIds.getCreate();
        Set<Long> deleteRoleIds = diffRoleIds.getDelete();
        if (CollectionUtils.isEmpty(createRoleIds) && CollectionUtils.isEmpty(deleteRoleIds)) {
            return;
        }
        Map<Long, Set<Long>> createUserRoleMap = new LinkedHashMap<>();
        Map<Long, Set<Long>> deleteUserRoleMap = new LinkedHashMap<>();
        for (Long userId : userIds) {
            if (createRoleIds != null) {
                createUserRoleMap.putIfAbsent(userId, createRoleIds);
            }
            if (deleteRoleIds != null) {
                deleteUserRoleMap.putIfAbsent(userId, deleteRoleIds);
            }
        }
        AuthUserRoleCacheService cacheService = AuthApiHolder.getAuthUserRoleCacheService();
        if (MapUtils.isNotEmpty(createUserRoleMap)) {
            cacheService.add(createUserRoleMap.keySet(), createUserRoleMap.values());
        }
        if (MapUtils.isNotEmpty(deleteUserRoleMap)) {
            cacheService.remove(deleteUserRoleMap.keySet(), deleteUserRoleMap.values());
        }
    }

    private void authorizeRoleRefreshCache(Long userId, Set<Long> roleIds) {
        AuthUserRoleCacheService cacheService = AuthApiHolder.getAuthUserRoleCacheService();
        cacheService.add(userId, roleIds);
    }

    private void revokeRoleRefreshCache(Long userId, Set<Long> roleIds) {
        AuthUserRoleCacheService cacheService = AuthApiHolder.getAuthUserRoleCacheService();
        cacheService.remove(userId, roleIds);
    }
}
