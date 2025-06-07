package pro.shushi.pamirs.auth.view.manager.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.behavior.AuthGroupRelationModel;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.relation.*;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataOperator;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleRowPermissionService;
import pro.shushi.pamirs.auth.view.entity.AuthGroupRevokeContext;
import pro.shushi.pamirs.auth.view.manager.AuthGroupAuthorizeService;
import pro.shushi.pamirs.auth.view.manager.AuthGroupRefreshCacheService;
import pro.shushi.pamirs.auth.view.utils.AuthGroupAuthorizationComputeHelper;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.core.common.diff.DiffList;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 权限组刷新缓存服务
 *
 * @author Adamancy Zhang at 22:27 on 2024-01-29
 */
@Service
public class AuthGroupRefreshCacheServiceImpl implements AuthGroupRefreshCacheService {

    @Autowired
    private AuthGroupDataOperator authGroupDataOperator;

    @Autowired
    private AuthGroupAuthorizeService authGroupAuthorizeService;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthRoleRowPermissionService authRoleRowPermissionService;

    @Override
    public void updateGroupRefresh(Long groupId,
                                   DiffList<AuthRole> diffRoles,
                                   DiffList<AuthGroupResourcePermission> diffActionPermissions,
                                   DiffList<AuthGroupFieldPermission> diffFieldPermissions) {
        List<AuthRole> allRoles = Optional.ofNullable(diffRoles).map(DiffList::getAll).orElse(null);
        if (CollectionUtils.isEmpty(allRoles)) {
            return;
        }
        DiffList<AuthGroupResourcePermission> prepareDiffActionPermissions = prepareDiffList(diffActionPermissions);
        DiffList<AuthGroupFieldPermission> prepareDiffFieldPermissions = prepareDiffList(diffFieldPermissions);
        AuthGroupRevokeContext context = new AuthGroupRevokeContext(groupId, allRoles);
        Map<Long, List<AuthGroupResourcePermission>> roleActionPermissionMap = null;
        Map<Long, List<AuthGroupFieldPermission>> roleFieldPermissionMap = null;
        Set<Long> allRoleIds = context.getRoleIds();
        if (prepareDiffActionPermissions.isDiff()) {
            roleActionPermissionMap = fetchGroupRefreshActionPermissions(context, prepareDiffActionPermissions.getAll());
        } else {
            List<AuthGroupResourcePermission> allActionPermissions = prepareDiffActionPermissions.getAll();
            if (CollectionUtils.isNotEmpty(allActionPermissions)) {
                roleActionPermissionMap = new HashMap<>();
                for (Long roleId : allRoleIds) {
                    roleActionPermissionMap.put(roleId, allActionPermissions);
                }
            }
        }
        if (prepareDiffFieldPermissions.isDiff()) {
            roleFieldPermissionMap = fetchGroupRefreshFieldPermissions(context, prepareDiffFieldPermissions.getAll());
        } else {
            List<AuthGroupFieldPermission> allFieldPermissions = prepareDiffFieldPermissions.getAll();
            if (CollectionUtils.isNotEmpty(allFieldPermissions)) {
                roleFieldPermissionMap = new HashMap<>();
                for (Long roleId : allRoleIds) {
                    roleFieldPermissionMap.put(roleId, allFieldPermissions);
                }
            }
        }
        for (Long roleId : allRoleIds) {
            List<AuthGroupResourcePermission> actionPermissions = null;
            if (roleActionPermissionMap != null) {
                actionPermissions = roleActionPermissionMap.get(roleId);
            }
            List<AuthGroupFieldPermission> fieldPermissions = null;
            if (roleFieldPermissionMap != null) {
                fieldPermissions = roleFieldPermissionMap.get(roleId);
            }
            authGroupAuthorizeService.refreshRolePermissions(Sets.newHashSet(roleId), actionPermissions, fieldPermissions, null);
        }
    }

    @Override
    public void updateRowPermissions(DiffList<AuthRole> diffRoles, DiffList<AuthGroupRowPermission> diffRowPermissions) {
        Set<Long> allRoleIds = Optional.ofNullable(diffRoles).map(DiffList::getAll).orElse(Collections.emptyList()).stream().map(AuthRole::getId).collect(Collectors.toSet());
        List<AuthGroupRowPermission> createRowPermissions = diffRowPermissions.getCreate();
        List<AuthGroupRowPermission> deleteRowPermissions = diffRowPermissions.getDelete();
        Set<Long> permissionIds = new HashSet<>();
        if (CollectionUtils.isEmpty(createRowPermissions)) {
            createRowPermissions = Collections.emptyList();
        } else {
            permissionIds.addAll(createRowPermissions.stream().map(AuthGroupRowPermission::getPermissionId).collect(Collectors.toSet()));
        }
        if (CollectionUtils.isEmpty(deleteRowPermissions)) {
            deleteRowPermissions = Collections.emptyList();
        } else {
            permissionIds.addAll(deleteRowPermissions.stream().map(AuthGroupRowPermission::getPermissionId).collect(Collectors.toSet()));
        }
        if (permissionIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<AuthRoleRowPermission> wrapper = Pops.<AuthRoleRowPermission>lambdaQuery()
                .from(AuthRoleRowPermission.MODEL_MODEL)
                .in(AuthRoleRowPermission::getPermissionId, permissionIds);
        if (!allRoleIds.isEmpty()) {
            wrapper.in(AuthRoleRowPermission::getRoleId, allRoleIds);
        }
        List<AuthRoleRowPermission> roleRowPermissions = authRoleRowPermissionService.queryListByWrapper(wrapper);
        if (CollectionUtils.isEmpty(roleRowPermissions)) {
            return;
        }
        Set<Long> updateRoleIds = roleRowPermissions.stream().map(AuthRoleRowPermission::getRoleId).collect(Collectors.toSet());
        List<AuthRole> updateRoles = authRoleService.fetchRoles(updateRoleIds);
        if (CollectionUtils.isEmpty(updateRoles)) {
            return;
        }
        MemoryListSearchCache<String, AuthRoleRowPermission> roleRowPermissionCache = new MemoryListSearchCache<>(roleRowPermissions, v -> v.getRoleId() + CharacterConstants.SEPARATOR_OCTOTHORPE + v.getPermissionId());
        for (AuthRole updateRole : updateRoles) {
            Long roleId = updateRole.getId();
            List<AuthGroupRowPermission> createRoleRowPermissions = createRowPermissions.stream().map(v -> generatorUpdateGroupRowPermission(roleRowPermissionCache, roleId, v)).filter(Objects::nonNull).collect(Collectors.toList());
            List<AuthGroupRowPermission> deleteRoleRowPermissions = deleteRowPermissions.stream().map(v -> generatorUpdateGroupRowPermission(roleRowPermissionCache, roleId, v)).filter(Objects::nonNull).collect(Collectors.toList());
            if (createRoleRowPermissions.isEmpty() && deleteRoleRowPermissions.isEmpty()) {
                continue;
            }
            authGroupAuthorizeService.refreshRolePermissions(Lists.newArrayList(updateRole), null, null, DiffCollection.list(createRoleRowPermissions, createRoleRowPermissions, null, deleteRoleRowPermissions));
        }
    }

    private AuthGroupRowPermission generatorUpdateGroupRowPermission(MemoryListSearchCache<String, AuthRoleRowPermission> roleRowPermissionCache, Long roleId, AuthGroupRowPermission target) {
        AuthRoleRowPermission roleRowPermission = roleRowPermissionCache.get(roleId + CharacterConstants.SEPARATOR_OCTOTHORPE + target.getPermissionId());
        if (roleRowPermission == null) {
            return null;
        }
        AuthGroupRowPermission groupRowPermission = AuthGroupRowPermission.transfer(target, new AuthGroupRowPermission());
        long authorizedValue = roleRowPermission.getAuthorizedValue() & groupRowPermission.getAuthorizedValue();
        if (authorizedValue == 0) {
            return null;
        }
        groupRowPermission.setAuthorizedValue(authorizedValue);
        return groupRowPermission;
    }

    /**
     * 对差量列表进行重新分组
     *
     * @param diffList 指定差量列表
     * @param <T>      任意类型
     * @return 适用于更新时刷新的差量分组
     */
    private <T extends AuthGroupRelationModel> DiffList<T> prepareDiffList(DiffList<T> diffList) {
        if (diffList == null) {
            return DiffCollection.emptyList();
        }
        List<T> create = Optional.ofNullable(diffList.getCreate()).orElseGet(ArrayList::new);
        List<T> update = Optional.ofNullable(diffList.getUpdate()).orElseGet(ArrayList::new);
        List<T> delete = Optional.ofNullable(diffList.getDelete()).orElseGet(ArrayList::new);
        List<T> all = CollectionHelper.connect(create, update);
        return DiffCollection.list(all, all, null, delete);
    }

    private Map<Long, List<AuthGroupResourcePermission>> fetchGroupRefreshActionPermissions(AuthGroupRevokeContext context, List<AuthGroupResourcePermission> authorizeActionPermissions) {
        List<AuthGroupResourcePermission> targetGroupActionPermissions = fetchGroupActionPermissions(context.getOtherActiveGroupIds(), authorizeActionPermissions);
        targetGroupActionPermissions.addAll(authorizeActionPermissions);
        Map<Long, Map<Long, Long>> changedValueMap = AuthGroupAuthorizationComputeHelper.computeFinalValueMap(context, targetGroupActionPermissions);
        if (changedValueMap.isEmpty()) {
            return null;
        }
        return AuthGroupAuthorizationComputeHelper.filterAndComputeChangedResourcePermissions(authorizeActionPermissions, changedValueMap);
    }

    private Map<Long, List<AuthGroupFieldPermission>> fetchGroupRefreshFieldPermissions(AuthGroupRevokeContext context, List<AuthGroupFieldPermission> authorizeFieldPermissions) {
        List<AuthGroupFieldPermission> targetGroupFieldPermissions = fetchGroupFieldPermissions(context.getOtherActiveGroupIds(), authorizeFieldPermissions);
        targetGroupFieldPermissions.addAll(authorizeFieldPermissions);
        Map<Long, Map<Long, Long>> changedValueMap = AuthGroupAuthorizationComputeHelper.computeFinalValueMap(context, targetGroupFieldPermissions);
        if (changedValueMap.isEmpty()) {
            return null;
        }
        return AuthGroupAuthorizationComputeHelper.filterAndComputeChangedFieldPermissions(authorizeFieldPermissions, changedValueMap);
    }

    private DiffList<AuthGroupRowPermission> fetchGroupRefreshRowPermissions(Context context, List<AuthGroupRowPermission> allGroupRowPermissions, DiffList<AuthGroupRowPermission> diffRowPermissions) {
        return null;
    }

    protected List<AuthGroupResourcePermission> fetchGroupActionPermissions(Set<Long> groupIds, List<AuthGroupResourcePermission> actionPermissions) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        Set<Long> permissionIds = actionPermissions.stream().map(AuthGroupResourcePermission::getPermissionId).collect(Collectors.toSet());
        return DataShardingHelper.build().collectionSharding(permissionIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
                .from(AuthGroupResourcePermission.MODEL_MODEL)
                .eq(AuthGroupResourcePermission::getPermissionType, ResourcePermissionTypeEnum.ACTION)
                .in(AuthGroupResourcePermission::getGroupId, groupIds)
                .in(AuthGroupResourcePermission::getPermissionId, sublist)));
    }

    private List<AuthGroupFieldPermission> fetchGroupFieldPermissions(Set<Long> groupIds, List<AuthGroupFieldPermission> fieldPermissions) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        Set<Long> permissionIds = fieldPermissions.stream().map(AuthGroupFieldPermission::getPermissionId).collect(Collectors.toSet());
        return DataShardingHelper.build().collectionSharding(permissionIds, sublist -> Models.origin().queryListByWrapper(Pops.<AuthGroupFieldPermission>lambdaQuery()
                .from(AuthGroupFieldPermission.MODEL_MODEL)
                .in(AuthGroupFieldPermission::getGroupId, groupIds)
                .in(AuthGroupFieldPermission::getPermissionId, sublist)));
    }

    private static class Context {

        private final Long groupId;

        private final Set<Long> roleIds;

        private Set<Long> otherGroupIds;

        private List<AuthGroupFieldPermission> fieldPermissions;

        private List<AuthGroupFieldPermission> otherFieldPermissions;

        private Context(Long groupId, Set<Long> roleIds) {
            this.groupId = groupId;
            this.roleIds = roleIds;
        }

        private Long getGroupId() {
            return groupId;
        }

        private Set<Long> getRoleIds() {
            return roleIds;
        }

        private Set<Long> getOtherGroupIds() {
            return getOrSet(() -> this.otherGroupIds, (v) -> this.otherGroupIds = v, this::getOtherGroupIds0);
        }

        private Set<Long> getOtherGroupIds0() {
            List<AuthGroupRelResource> authGroupRoleRels = Models.origin().queryListByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                    .from(AuthGroupRelResource.MODEL_MODEL)
                    .select(AuthGroupRelResource::getPermissionId)
                    .eq(AuthGroupRelResource::getGroupId, groupId));
            if (CollectionUtils.isEmpty(authGroupRoleRels)) {
                return Collections.emptySet();
            }
            Set<Long> permissionIds = authGroupRoleRels.stream().map(AuthGroupRelResource::getPermissionId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(permissionIds)) {
                return Collections.emptySet();
            }
            List<AuthGroupRelResource> otherRoleGroups = DataShardingHelper.build().collectionSharding(permissionIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                    .from(AuthGroupRelResource.MODEL_MODEL)
                    .select(AuthGroupRelResource::getGroupId)
                    .setBatchSize(-1)
                    .ne(AuthGroupRelResource::getGroupId, groupId)
                    .in(AuthGroupRelResource::getPermissionId, sublist)));
            if (CollectionUtils.isEmpty(otherRoleGroups)) {
                return Collections.emptySet();
            }
            Set<Long> otherGroupIds = otherRoleGroups.stream().map(AuthGroupRelResource::getGroupId).collect(Collectors.toSet());
            List<AuthGroupRole> filterRoleGroups = Models.origin().queryListByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                    .from(AuthGroupRole.MODEL_MODEL)
                    .select(AuthGroupRole::getGroupId)
                    .setBatchSize(-1)
                    .in(AuthGroupRole::getRoleId, roleIds)
                    .in(AuthGroupRole::getGroupId, otherGroupIds));
            if (CollectionUtils.isEmpty(filterRoleGroups)) {
                return Collections.emptySet();
            }
            otherGroupIds = filterRoleGroups.stream().map(AuthGroupRole::getGroupId).collect(Collectors.toSet());
            List<AuthGroup> otherGroups = DataShardingHelper.build().collectionSharding(otherGroupIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroup>lambdaQuery()
                    .from(AuthGroup.MODEL_MODEL)
                    .select(AuthGroup::getId)
                    .setBatchSize(-1)
                    .eq(AuthGroup::getActive, Boolean.TRUE)
                    .in(AuthGroup::getId, sublist)));
            if (CollectionUtils.isEmpty(otherGroups)) {
                return Collections.emptySet();
            }
            return otherGroups.stream().map(AuthGroup::getId).collect(Collectors.toSet());
        }

        private <R> R getOrSet(Supplier<R> getter, Consumer<R> setter, Supplier<R> fetcher) {
            R result = getter.get();
            if (result != null) {
                return result;
            }
            result = fetcher.get();
            setter.accept(result);
            return result;
        }
    }
}
