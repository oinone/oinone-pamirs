package pro.shushi.pamirs.auth.api.service.manager.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthModelPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleModelPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthQueryAuthorizationOperator;
import pro.shushi.pamirs.auth.api.service.permission.AuthFieldPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthModelPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthRowPermissionService;
import pro.shushi.pamirs.auth.api.service.relation.*;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 权限授权查询操作
 *
 * @author Adamancy Zhang at 17:23 on 2024-01-22
 */
@Service
public class AuthQueryAuthorizationOperatorImpl implements AuthQueryAuthorizationOperator {

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

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Autowired
    private AuthModelPermissionService authModelPermissionService;

    @Autowired
    private AuthFieldPermissionService authFieldPermissionService;

    @Autowired
    private AuthRowPermissionService authRowPermissionService;

    @Override
    public Map<Long, Set<Long>> queryUserRoleAuthorizations(Set<Long> roleIds) {
        return queryUserRoleAuthorizations(() -> authUserRoleService.queryListByRoleIds(roleIds), true);
    }

    @Override
    public Map<Long, Set<Long>> queryUserRoleAuthorizations(Set<Long> roleIds, Set<Long> userIds) {
        return queryUserRoleAuthorizations(() -> DataShardingHelper.build().collectionSharding(userIds,
                (sublist) -> authUserRoleService.queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                        .from(AuthUserRoleRel.MODEL_MODEL)
                        .in(AuthUserRoleRel::getRoleId, roleIds)
                        .in(AuthUserRoleRel::getUserId, sublist))), false);
    }

    private Map<Long, Set<Long>> queryUserRoleAuthorizations(Supplier<List<AuthUserRoleRel>> fetcher, boolean isFetchAll) {
        List<AuthUserRoleRel> authorizedValues = fetcher.get();
        Map<Long, Set<Long>> groups = new LinkedHashMap<>();
        for (AuthUserRoleRel authorizedValue : authorizedValues) {
            groups.computeIfAbsent(authorizedValue.getUserId(), (k) -> new HashSet<>())
                    .add(authorizedValue.getRoleId());
        }
        if (isFetchAll) {
            List<AuthUserRoleRel> allAuthorizedValues = authUserRoleService.queryRolesByAllFlag();
            if (CollectionUtils.isNotEmpty(allAuthorizedValues)) {
                Set<Long> allRoleIds = allAuthorizedValues.stream().map(AuthUserRoleRel::getRoleId).collect(Collectors.toSet());
                for (Set<Long> userRoles : groups.values()) {
                    userRoles.addAll(allRoleIds);
                }
            }
        }
        return groups;
    }

    @Override
    public Map<Long, Set<Long>> queryUserRoleAuthorizationsByUserIds(Set<Long> userIds) {
        return queryUserRoleAuthorizations(() -> authUserRoleService.queryListByUserIds(userIds), true);
    }

    @Override
    public Map<Long, List<AuthResourceAuthorization>> queryRoleResourceAuthorizations(Set<Long> roleIds) {
        return queryRoleResourceAuthorizations(roleIds, () -> authRoleResourcePermissionService.queryListByRoleIds(roleIds), true);
    }

    @Override
    public Map<Long, List<AuthResourceAuthorization>> queryRoleResourceAuthorizations(Set<Long> roleIds, Set<Long> permissionIds) {
        return queryRoleResourceAuthorizations(roleIds, () -> authRoleResourcePermissionService.queryListByRoleIds(roleIds), false);
    }

    @Override
    public List<AuthRoleResourcePermission> fillResourcePermissions(List<AuthRoleResourcePermission> roleResourcePermissions) {
        fillPermissions(roleResourcePermissions,
                AuthRoleResourcePermission::getPermissionId,
                AuthRoleResourcePermission::setPermission,
                (ids) -> authResourcePermissionService.queryListByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                        .from(AuthResourcePermission.MODEL_MODEL)
                        .setBatchSize(-1)
                        .in(AuthResourcePermission::getId, ids)));
        return roleResourcePermissions;
    }

    private Map<Long, List<AuthResourceAuthorization>> queryRoleResourceAuthorizations(Set<Long> roleIds, Supplier<List<AuthRoleResourcePermission>> fetcher, boolean isFetchAll) {
        List<AuthRoleResourcePermission> authorizedValues = fetcher.get();
        if (isFetchAll) {
            List<AuthRoleResourcePermission> allAuthorizedValues = authRoleResourcePermissionService.queryPermissionIdsByAllFlag();
            if (CollectionUtils.isNotEmpty(allAuthorizedValues)) {
                for (Long roleId : roleIds) {
                    authorizedValues.addAll(allAuthorizedValues.stream().map(v -> {
                        AuthRoleResourcePermission authorizedValue = new AuthRoleResourcePermission();
                        authorizedValue.setRoleId(roleId);
                        authorizedValue.setPermissionId(v.getPermissionId());
                        authorizedValue.setSource(v.getSource());
                        authorizedValue.setAuthorizedValue(v.getAuthorizedValue());
                        return authorizedValue;
                    }).collect(Collectors.toList()));
                }
            }
        }
        if (CollectionUtils.isEmpty(authorizedValues)) {
            return Collections.emptyMap();
        }
        authorizedValues = fillResourcePermissions(authorizedValues);
        Map<Long, List<AuthResourceAuthorization>> groups = new LinkedHashMap<>(roleIds.size());
        for (AuthRoleResourcePermission authorizedValue : authorizedValues) {
            groups.computeIfAbsent(authorizedValue.getRoleId(), (k) -> new ArrayList<>(16))
                    .add(AuthResourceAuthorization.from(authorizedValue));
        }
        return groups;
    }

    @Override
    public Map<Long, List<AuthModelAuthorization>> queryRoleModelAuthorizations(Set<Long> roleIds) {
        return queryRoleModelAuthorizations(roleIds, () -> authRoleModelPermissionService.queryListByRoleIds(roleIds), true);
    }

    @Override
    public Map<Long, List<AuthModelAuthorization>> queryRoleModelAuthorizations(Set<Long> roleIds, Set<Long> permissionIds) {
        return queryRoleModelAuthorizations(roleIds, () -> DataShardingHelper.build().collectionSharding(permissionIds,
                (sublist) -> authRoleModelPermissionService.queryListByWrapper(Pops.<AuthRoleModelPermission>lambdaQuery()
                        .from(AuthRoleModelPermission.MODEL_MODEL)
                        .in(AuthRoleModelPermission::getRoleId, roleIds)
                        .in(AuthRoleModelPermission::getPermissionId, sublist))), false);
    }

    @Override
    public List<AuthRoleModelPermission> fillModelPermissions(List<AuthRoleModelPermission> roleModelPermissions) {
        fillPermissions(roleModelPermissions,
                AuthRoleModelPermission::getPermissionId,
                AuthRoleModelPermission::setPermission,
                (ids) -> authModelPermissionService.queryListByWrapper(Pops.<AuthModelPermission>lambdaQuery()
                        .from(AuthModelPermission.MODEL_MODEL)
                        .setBatchSize(-1)
                        .in(AuthModelPermission::getId, ids)));
        return roleModelPermissions;
    }

    private Map<Long, List<AuthModelAuthorization>> queryRoleModelAuthorizations(Set<Long> roleIds, Supplier<List<AuthRoleModelPermission>> fetcher, boolean isFetchAll) {
        List<AuthRoleModelPermission> authorizedValues = fetcher.get();
        if (isFetchAll) {
            List<AuthRoleModelPermission> allAuthorizedValues = authRoleModelPermissionService.queryPermissionIdsByAllFlag();
            if (CollectionUtils.isNotEmpty(allAuthorizedValues)) {
                for (Long roleId : roleIds) {
                    authorizedValues.addAll(allAuthorizedValues.stream().map(v -> {
                        AuthRoleModelPermission authorizedValue = new AuthRoleModelPermission();
                        authorizedValue.setRoleId(roleId);
                        authorizedValue.setPermissionId(v.getPermissionId());
                        authorizedValue.setSource(v.getSource());
                        authorizedValue.setAuthorizedValue(v.getAuthorizedValue());
                        return authorizedValue;
                    }).collect(Collectors.toList()));
                }
            }
        }
        if (CollectionUtils.isEmpty(authorizedValues)) {
            return Collections.emptyMap();
        }
        authorizedValues = fillModelPermissions(authorizedValues);
        Map<Long, List<AuthModelAuthorization>> groups = new LinkedHashMap<>(roleIds.size());
        for (AuthRoleModelPermission authorizedValue : authorizedValues) {
            groups.computeIfAbsent(authorizedValue.getRoleId(), (k) -> new ArrayList<>(16))
                    .add(AuthModelAuthorization.from(authorizedValue));
        }
        return groups;
    }

    @Override
    public Map<Long, List<AuthFieldAuthorization>> queryRoleFieldAuthorizations(Set<Long> roleIds) {
        return queryRoleFieldAuthorizations(roleIds, () -> authRoleFieldPermissionService.queryListByRoleIds(roleIds), true);
    }

    @Override
    public Map<Long, List<AuthFieldAuthorization>> queryRoleFieldAuthorizations(Set<Long> roleIds, Set<Long> permissionIds) {
        return queryRoleFieldAuthorizations(roleIds, () -> DataShardingHelper.build().collectionSharding(permissionIds,
                (sublist) -> authRoleFieldPermissionService.queryListByWrapper(Pops.<AuthRoleFieldPermission>lambdaQuery()
                        .from(AuthRoleFieldPermission.MODEL_MODEL)
                        .in(AuthRoleFieldPermission::getRoleId, roleIds)
                        .in(AuthRoleFieldPermission::getPermissionId, sublist))), false);
    }

    @Override
    public List<AuthRoleFieldPermission> fillFieldPermissions(List<AuthRoleFieldPermission> roleFieldPermissions) {
        fillPermissions(roleFieldPermissions,
                AuthRoleFieldPermission::getPermissionId,
                AuthRoleFieldPermission::setPermission,
                (ids) -> authFieldPermissionService.queryListByWrapper(Pops.<AuthFieldPermission>lambdaQuery()
                        .from(AuthFieldPermission.MODEL_MODEL)
                        .setBatchSize(-1)
                        .in(AuthFieldPermission::getId, ids)));
        return roleFieldPermissions;
    }

    private Map<Long, List<AuthFieldAuthorization>> queryRoleFieldAuthorizations(Set<Long> roleIds, Supplier<List<AuthRoleFieldPermission>> fetcher, boolean isFetchAll) {
        List<AuthRoleFieldPermission> authorizedValues = fetcher.get();
        if (isFetchAll) {
            List<AuthRoleFieldPermission> allAuthorizedValues = authRoleFieldPermissionService.queryPermissionIdsByAllFlag();
            if (CollectionUtils.isNotEmpty(allAuthorizedValues)) {
                for (Long roleId : roleIds) {
                    authorizedValues.addAll(allAuthorizedValues.stream().map(v -> {
                        AuthRoleFieldPermission authorizedValue = new AuthRoleFieldPermission();
                        authorizedValue.setRoleId(roleId);
                        authorizedValue.setPermissionId(v.getPermissionId());
                        authorizedValue.setSource(v.getSource());
                        authorizedValue.setAuthorizedValue(v.getAuthorizedValue());
                        return authorizedValue;
                    }).collect(Collectors.toList()));
                }
            }
        }
        if (CollectionUtils.isEmpty(authorizedValues)) {
            return Collections.emptyMap();
        }
        authorizedValues = fillFieldPermissions(authorizedValues);
        Map<Long, List<AuthFieldAuthorization>> groups = new LinkedHashMap<>(roleIds.size());
        for (AuthRoleFieldPermission authorizedValue : authorizedValues) {
            groups.computeIfAbsent(authorizedValue.getRoleId(), (k) -> new ArrayList<>(16))
                    .add(AuthFieldAuthorization.from(authorizedValue));
        }
        return groups;
    }

    @Override
    public Map<Long, List<AuthRowAuthorization>> queryRoleRowAuthorizations(Set<Long> roleIds) {
        return queryRoleRowAuthorizations(roleIds, () -> authRoleRowPermissionService.queryListByRoleIds(roleIds), true);
    }

    @Override
    public Map<Long, List<AuthRowAuthorization>> queryRoleRowAuthorizations(Set<Long> roleIds, Set<Long> permissionIds) {
        return queryRoleRowAuthorizations(roleIds, () -> DataShardingHelper.build().collectionSharding(permissionIds,
                (sublist) -> authRoleRowPermissionService.queryListByWrapper(Pops.<AuthRoleRowPermission>lambdaQuery()
                        .from(AuthRoleRowPermission.MODEL_MODEL)
                        .in(AuthRoleRowPermission::getRoleId, roleIds)
                        .in(AuthRoleRowPermission::getPermissionId, sublist))), false);
    }

    @Override
    public List<AuthRoleRowPermission> fillRowPermissions(List<AuthRoleRowPermission> roleRowPermissions) {
        fillPermissions(roleRowPermissions,
                AuthRoleRowPermission::getPermissionId,
                AuthRoleRowPermission::setPermission,
                (ids) -> authRowPermissionService.queryListByWrapper(Pops.<AuthRowPermission>lambdaQuery()
                        .from(AuthRowPermission.MODEL_MODEL)
                        .setBatchSize(-1)
                        .in(AuthRowPermission::getId, ids)));
        return roleRowPermissions;
    }

    private Map<Long, List<AuthRowAuthorization>> queryRoleRowAuthorizations(Set<Long> roleIds, Supplier<List<AuthRoleRowPermission>> fetcher, boolean isFetchAll) {
        List<AuthRoleRowPermission> authorizedValues = fetcher.get();
        if (isFetchAll) {
            List<AuthRoleRowPermission> allAuthorizedValues = authRoleRowPermissionService.queryPermissionIdsByAllFlag();
            if (CollectionUtils.isNotEmpty(allAuthorizedValues)) {
                for (Long roleId : roleIds) {
                    authorizedValues.addAll(allAuthorizedValues.stream().map(v -> {
                        AuthRoleRowPermission authorizedValue = new AuthRoleRowPermission();
                        authorizedValue.setRoleId(roleId);
                        authorizedValue.setPermissionId(v.getPermissionId());
                        authorizedValue.setSource(v.getSource());
                        authorizedValue.setAuthorizedValue(v.getAuthorizedValue());
                        return authorizedValue;
                    }).collect(Collectors.toList()));
                }
            }
        }
        if (CollectionUtils.isEmpty(authorizedValues)) {
            return Collections.emptyMap();
        }
        authorizedValues = fillRowPermissions(authorizedValues);
        Map<Long, List<AuthRowAuthorization>> groups = new LinkedHashMap<>(roleIds.size());
        for (AuthRoleRowPermission authorizedValue : authorizedValues) {
            groups.computeIfAbsent(authorizedValue.getRoleId(), (k) -> new ArrayList<>(16))
                    .add(AuthRowAuthorization.from(authorizedValue));
        }
        return groups;
    }

    private <T, P extends IdModel> void fillPermissions(List<T> list,
                                                        Function<T, Long> permissionIdGetter, BiConsumer<T, P> permissionSetter,
                                                        Function<List<Long>, List<P>> query) {
        Map<Long, List<T>> fillPermissionMap = new HashMap<>();
        for (T item : list) {
            fillPermissionMap.computeIfAbsent(permissionIdGetter.apply(item), v -> new ArrayList<>()).add(item);
        }
        List<P> permissions = DataShardingHelper.build().collectionSharding(fillPermissionMap.keySet(), query);
        for (P permission : permissions) {
            List<T> target = fillPermissionMap.get(permission.getId());
            if (target != null) {
                target.forEach(v -> permissionSetter.accept(v, permission));
            }
        }
    }
}
