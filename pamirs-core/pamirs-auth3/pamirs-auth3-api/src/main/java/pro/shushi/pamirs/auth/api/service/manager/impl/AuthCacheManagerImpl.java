package pro.shushi.pamirs.auth.api.service.manager.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.cache.redis.AuthRedisTemplate;
import pro.shushi.pamirs.auth.api.cache.service.AuthUserRoleCacheService;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.helper.AuthRoleHelper;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.manager.AuthCacheManager;
import pro.shushi.pamirs.auth.api.service.manager.AuthPermissionCacheManager;
import pro.shushi.pamirs.auth.api.service.manager.AuthQueryAuthorizationOperator;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限缓存管理
 * <p>
 * 此类中的所有方法实现必须保证耗时操作在刷入缓存之前全部完成，切勿随意抽取相同逻辑！！！
 * </p>
 *
 * @author Adamancy Zhang at 16:55 on 2024-01-29
 */
@Service
@Fun(AuthCacheManager.FUN_NAMESPACE)
public class AuthCacheManagerImpl implements AuthCacheManager {

    @Autowired
    @Qualifier(AuthConstants.REDIS_TEMPLATE_BEAN_NAME)
    protected AuthRedisTemplate<Long> authRedisTemplate;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthQueryAuthorizationOperator authQueryAuthorizationOperator;

    @Autowired
    private AuthPermissionCacheManager authPermissionCacheManager;

    @Function
    @Override
    public void refresh(List<AuthRole> roles) {
        int initialCapacity = roles.size();
        Set<Long> allRoleIds = new HashSet<>(initialCapacity);
        for (AuthRole role : roles) {
            Long roleId = role.getId();
            allRoleIds.add(roleId);
        }
        Map<Long, List<AuthResourceAuthorization>> resourceAuthorizations = authQueryAuthorizationOperator.queryRoleResourceAuthorizations(allRoleIds);
        Map<Long, List<AuthModelAuthorization>> modelAuthorizations = authQueryAuthorizationOperator.queryRoleModelAuthorizations(allRoleIds);
        Map<Long, List<AuthFieldAuthorization>> fieldAuthorizations = authQueryAuthorizationOperator.queryRoleFieldAuthorizations(allRoleIds);
        Map<Long, List<AuthRowAuthorization>> rowAuthorizations = authQueryAuthorizationOperator.queryRoleRowAuthorizations(allRoleIds);

        deleteRoleKeys(Sets.newHashSet(allRoleIds));

        for (Long roleId : allRoleIds) {
            authPermissionCacheManager.authorizeRefreshPermissions(Sets.newHashSet(roleId),
                    resourceAuthorizations.get(roleId),
                    modelAuthorizations.get(roleId),
                    fieldAuthorizations.get(roleId),
                    rowAuthorizations.get(roleId));
        }
    }

    @Function
    @Override
    public void refreshByUserIds(Set<Long> userIds, Boolean isRefreshRolePermissionCache) {
        Map<Long, Set<Long>> userRoleGroups = authQueryAuthorizationOperator.queryUserRoleAuthorizationsByUserIds(userIds);
        Set<Long> allRoleIds = new HashSet<>();
        for (Set<Long> roleIds : userRoleGroups.values()) {
            allRoleIds.addAll(roleIds);
        }
        AuthUserRoleCacheService authUserRoleCacheService = AuthApiHolder.getAuthUserRoleCacheService();
        if (CollectionUtils.isEmpty(allRoleIds)) {
            authUserRoleCacheService.set(userRoleGroups.keySet(), userRoleGroups.keySet().stream().map(v -> new HashSet<Long>()).collect(Collectors.toList()));
            return;
        }
        List<AuthRole> roles = DataShardingHelper.build().collectionSharding(allRoleIds, (sublist) -> authRoleService.queryListByWrapper(Pops.<AuthRole>lambdaQuery().from(AuthRole.MODEL_MODEL).in(AuthRole::getId, sublist)));
        Set<Long> activeRoleIds = new HashSet<>();
        for (AuthRole role : roles) {
            Long roleId = role.getId();
            allRoleIds.add(roleId);
            if (AuthRoleHelper.isActiveRole(role)) {
                activeRoleIds.add(roleId);
            }
        }

        if (isRefreshRolePermissionCache == null) {
            isRefreshRolePermissionCache = Boolean.FALSE;
        }
        if (isRefreshRolePermissionCache) {
            Map<Long, List<AuthResourceAuthorization>> resourceAuthorizations = authQueryAuthorizationOperator.queryRoleResourceAuthorizations(allRoleIds);
            Map<Long, List<AuthModelAuthorization>> modelAuthorizations = authQueryAuthorizationOperator.queryRoleModelAuthorizations(allRoleIds);
            Map<Long, List<AuthFieldAuthorization>> fieldAuthorizations = authQueryAuthorizationOperator.queryRoleFieldAuthorizations(allRoleIds);
            Map<Long, List<AuthRowAuthorization>> rowAuthorizations = authQueryAuthorizationOperator.queryRoleRowAuthorizations(allRoleIds);

            deleteRoleKeys(Sets.newHashSet(allRoleIds));

            authUserRoleCacheService.set(userRoleGroups.keySet(), userRoleGroups.values().stream()
                    .map(roleIds -> roleIds.stream().filter(activeRoleIds::contains).collect(Collectors.toSet()))
                    .collect(Collectors.toList()));

            for (Long roleId : allRoleIds) {
                authPermissionCacheManager.authorizeRefreshPermissions(Sets.newHashSet(roleId),
                        resourceAuthorizations.get(roleId),
                        modelAuthorizations.get(roleId),
                        fieldAuthorizations.get(roleId),
                        rowAuthorizations.get(roleId));
            }
        } else {
            authUserRoleCacheService.set(userRoleGroups.keySet(), userRoleGroups.values().stream()
                    .map(roleIds -> roleIds.stream().filter(activeRoleIds::contains).collect(Collectors.toSet()))
                    .collect(Collectors.toList()));
        }
    }

    @Function
    @Override
    public void refreshAll() {
        List<AuthRole> roles = authRoleService.queryListByWrapper(Pops.<AuthRole>lambdaQuery().from(AuthRole.MODEL_MODEL).ge(AuthRole::getId, 0));
        int initialCapacity = roles.size();
        Set<Long> allRoleIds = new HashSet<>(initialCapacity);
        Set<Long> activeRoleIds = new HashSet<>(initialCapacity);
        for (AuthRole role : roles) {
            Long roleId = role.getId();
            allRoleIds.add(roleId);
            if (AuthRoleHelper.isActiveRole(role)) {
                activeRoleIds.add(roleId);
            }
        }

        AuthUserRoleCacheService authUserRoleCacheService = AuthApiHolder.getAuthUserRoleCacheService();
        Map<Long, Set<Long>> userRoleGroups = authQueryAuthorizationOperator.queryUserRoleAuthorizations(allRoleIds);

        Map<Long, List<AuthResourceAuthorization>> resourceAuthorizations = authQueryAuthorizationOperator.queryRoleResourceAuthorizations(allRoleIds);
        Map<Long, List<AuthModelAuthorization>> modelAuthorizations = authQueryAuthorizationOperator.queryRoleModelAuthorizations(allRoleIds);
        Map<Long, List<AuthFieldAuthorization>> fieldAuthorizations = authQueryAuthorizationOperator.queryRoleFieldAuthorizations(allRoleIds);
        Map<Long, List<AuthRowAuthorization>> rowAuthorizations = authQueryAuthorizationOperator.queryRoleRowAuthorizations(allRoleIds);

        deleteRoleKeys(Sets.newHashSet(allRoleIds));

        authUserRoleCacheService.set(userRoleGroups.keySet(), userRoleGroups.values().stream()
                .map(roleIds -> roleIds.stream().filter(activeRoleIds::contains).collect(Collectors.toSet()))
                .collect(Collectors.toList()));

        for (Long roleId : allRoleIds) {
            authPermissionCacheManager.authorizeRefreshPermissions(Sets.newHashSet(roleId),
                    resourceAuthorizations.get(roleId),
                    modelAuthorizations.get(roleId),
                    fieldAuthorizations.get(roleId),
                    rowAuthorizations.get(roleId));
        }
    }

    @Override
    public void refreshAllUserRole() {
        List<AuthRole> roles = authRoleService.queryListByWrapper(Pops.<AuthRole>lambdaQuery().from(AuthRole.MODEL_MODEL).ge(AuthRole::getId, 0));
        int initialCapacity = roles.size();
        Set<Long> allRoleIds = new HashSet<>(initialCapacity);
        Set<Long> activeRoleIds = new HashSet<>(initialCapacity);
        for (AuthRole role : roles) {
            Long roleId = role.getId();
            allRoleIds.add(roleId);
            if (AuthRoleHelper.isActiveRole(role)) {
                activeRoleIds.add(roleId);
            }
        }
        Map<Long, Set<Long>> userRoleGroups = authQueryAuthorizationOperator.queryUserRoleAuthorizations(allRoleIds);
        AuthUserRoleCacheService authUserRoleCacheService = AuthApiHolder.getAuthUserRoleCacheService();
        authUserRoleCacheService.set(userRoleGroups.keySet(), userRoleGroups.values().stream()
                .map(roleIds -> roleIds.stream().filter(activeRoleIds::contains).collect(Collectors.toSet()))
                .collect(Collectors.toList()));
    }

    private void deleteRoleKeys(Set<Long> roleIds) {
        Set<String> allKeys = new HashSet<>();
        for (Long roleId : roleIds) {
            Set<String> keys = authRedisTemplate.keys(generatorPermissionKeyPattern(roleId));
            if (keys == null) {
                continue;
            }
            allKeys.addAll(keys);
        }
        authRedisTemplate.delete(allKeys);
    }

    private String generatorPermissionKeyPattern(Long roleId) {
        return AuthConstants.AUTH_CACHE_KEY_PREFIX + roleId + CharacterConstants.SEPARATOR_COLON + CharacterConstants.SEPARATOR_ASTERISK;
    }
}
