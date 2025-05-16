package pro.shushi.pamirs.auth.view.entity;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRelResource;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRole;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限撤销授权上下文
 *
 * @author Adamancy Zhang at 19:21 on 2024-09-30
 */
public class AuthGroupRevokeContext {

    private final AuthResourcePermissionService authResourcePermissionService;

    private final Long groupId;

    private final List<AuthRole> roles;

    private final Set<Long> roleIds;

    private List<AuthGroupRelResource> currentGroupRelResources;

    private List<AuthGroupRelResource> otherGroupRelResources;

    private Set<String> resourceCodes;

    private Set<Long> permissionIds;

    private Set<Long> queryResourcePermissionIds;

    private List<AuthResourcePermission> queryResourcePermissions;

    private Set<Long> otherGroupIds;

    private List<AuthGroupRole> allGroupRoles;

    private List<AuthGroupRole> currentGroupRoles;

    private List<AuthGroupRole> otherGroupRoles;

    private Set<Long> otherActiveGroupIds;

    public AuthGroupRevokeContext(Long groupId, List<AuthRole> roles) {
        this.authResourcePermissionService = BeanDefinitionUtils.getBean(AuthResourcePermissionService.class);
        this.groupId = groupId;
        this.roles = roles;
        this.roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
    }

    public Long getGroupId() {
        return groupId;
    }

    public List<AuthRole> getRoles() {
        return roles;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public List<AuthGroupRelResource> getCurrentGroupRelResources() {
        if (currentGroupRelResources == null) {
            currentGroupRelResources = queryCurrentGroupRelResources();
        }
        return currentGroupRelResources;
    }

    private List<AuthGroupRelResource> queryCurrentGroupRelResources() {
        List<AuthGroupRelResource> groupRelResources = Models.origin().queryListByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                .from(AuthGroupRelResource.MODEL_MODEL)
                .eq(AuthGroupRelResource::getGroupId, groupId));
        if (CollectionUtils.isEmpty(groupRelResources)) {
            return Collections.emptyList();
        }
        return groupRelResources;
    }

    public List<AuthGroupRelResource> getOtherGroupRelResources() {
        if (otherGroupRelResources == null) {
            otherGroupRelResources = queryOtherGroupRelResources();
        }
        return otherGroupRelResources;
    }

    public List<AuthGroupRelResource> queryOtherGroupRelResources() {
        Set<String> resourceCodes = getResourceCodes();
        if (resourceCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<AuthGroupRelResource> otherRoleGroups = Models.origin().queryListByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                .from(AuthGroupRelResource.MODEL_MODEL)
                .ne(AuthGroupRelResource::getGroupId, groupId)
                .in(AuthGroupRelResource::getResourceCode, resourceCodes));
        if (CollectionUtils.isEmpty(otherRoleGroups)) {
            return Collections.emptyList();
        }
        return otherRoleGroups;
    }

    public List<AuthGroupRelResource> getAllGroupRelResources() {
        List<AuthGroupRelResource> allGroupRelResources = new ArrayList<>(getCurrentGroupRelResources());
        allGroupRelResources.addAll(getOtherGroupRelResources());
        return allGroupRelResources;
    }

    public Set<String> getResourceCodes() {
        if (resourceCodes == null) {
            resourceCodes = queryResourceCodes();
        }
        return resourceCodes;
    }

    private Set<String> queryResourceCodes() {
        List<AuthGroupRelResource> groupRelResources = getCurrentGroupRelResources();
        if (CollectionUtils.isEmpty(groupRelResources)) {
            return Collections.emptySet();
        }
        return groupRelResources.stream().map(AuthGroupRelResource::getResourceCode).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Set<Long> getPermissionIds() {
        if (permissionIds == null) {
            permissionIds = queryPermissionIds();
        }
        return permissionIds;
    }

    private Set<Long> queryPermissionIds() {
        List<AuthGroupRelResource> groupRelResources = getCurrentGroupRelResources();
        if (CollectionUtils.isEmpty(groupRelResources)) {
            return Collections.emptySet();
        }
        return groupRelResources.stream().map(AuthGroupRelResource::getPermissionId).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Set<Long> getOtherGroupIds() {
        if (otherGroupIds == null) {
            otherGroupIds = queryOtherGroupIds();
        }
        return otherGroupIds;
    }

    private Set<Long> queryOtherGroupIds() {
        List<AuthGroupRelResource> otherRoleGroups = getOtherGroupRelResources();
        if (otherRoleGroups.isEmpty()) {
            return Collections.emptySet();
        }
        return otherRoleGroups.stream().map(AuthGroupRelResource::getGroupId).collect(Collectors.toSet());
    }

    public List<AuthGroupRole> getAllGroupRoles() {
        if (allGroupRoles == null) {
            allGroupRoles = queryAllGroupRoles();
        }
        return allGroupRoles;
    }

    public List<AuthGroupRole> getCurrentGroupRoles() {
        if (currentGroupRoles == null) {
            List<AuthGroupRole> allGroupRoles = getAllGroupRoles();
            if (allGroupRoles.isEmpty()) {
                return Collections.emptyList();
            }
            currentGroupRoles = allGroupRoles.stream().filter(v -> groupId.equals(v.getGroupId())).collect(Collectors.toList());
        }
        return currentGroupRoles;
    }

    public List<AuthGroupRole> getOtherGroupRoles() {
        if (otherGroupRoles == null) {
            List<AuthGroupRole> allGroupRoles = getAllGroupRoles();
            if (allGroupRoles.isEmpty()) {
                return Collections.emptyList();
            }
            otherGroupRoles = allGroupRoles.stream().filter(v -> !groupId.equals(v.getGroupId())).collect(Collectors.toList());
        }
        return otherGroupRoles;
    }

    private List<AuthGroupRole> queryAllGroupRoles() {
        Set<Long> roleIds = getRoleIds();
        Set<Long> otherGroupIds = getOtherGroupIds();
        Set<Long> allGroupIds = new HashSet<>(otherGroupIds);
        allGroupIds.add(getGroupId());
        List<AuthGroupRole> groupRoles = Models.origin().queryListByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                .from(AuthGroupRole.MODEL_MODEL)
                .select(AuthGroupRole::getGroupId, AuthGroupRole::getRoleId)
                .setBatchSize(-1)
                .in(AuthGroupRole::getRoleId, roleIds)
                .in(AuthGroupRole::getGroupId, allGroupIds));
        if (CollectionUtils.isEmpty(groupRoles)) {
            return Collections.emptyList();
        }
        return groupRoles;
    }

    public Set<Long> getOtherActiveGroupIds() {
        if (otherActiveGroupIds == null) {
            otherActiveGroupIds = queryOtherActiveGroupIds();
        }
        return otherActiveGroupIds;
    }

    private Set<Long> queryOtherActiveGroupIds() {
        List<AuthGroupRole> otherGroupRoles = getOtherGroupRoles();
        if (CollectionUtils.isEmpty(otherGroupRoles)) {
            return Collections.emptySet();
        }
        Set<Long> otherGroupIds = otherGroupRoles.stream().map(AuthGroupRole::getGroupId).collect(Collectors.toSet());
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

    public List<AuthResourcePermission> queryResourcePermissions(Set<Long> permissionIds) {
        if (queryResourcePermissionIds == null) {
            queryResourcePermissionIds = new HashSet<>();
        }
        if (queryResourcePermissions == null) {
            queryResourcePermissions = new ArrayList<>();
        }
        Set<Long> targetResourcePermissionIds = Sets.difference(permissionIds, queryResourcePermissionIds);
        if (targetResourcePermissionIds.isEmpty()) {
            return findResourcePermissions(permissionIds);
        }
        List<AuthResourcePermission> resourcePermissions = DataShardingHelper.build().collectionSharding(targetResourcePermissionIds,
                (sublist) -> authResourcePermissionService.queryListByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                        .from(AuthResourcePermission.MODEL_MODEL)
                        .in(AuthResourcePermission::getId, sublist)));
        queryResourcePermissionIds.addAll(permissionIds);
        queryResourcePermissions.addAll(resourcePermissions);
        return findResourcePermissions(permissionIds);
    }

    private List<AuthResourcePermission> findResourcePermissions(Set<Long> permissionIds) {
        if (CollectionUtils.isEmpty(queryResourcePermissions)) {
            return Collections.emptyList();
        }
        MemoryListSearchCache<Long, AuthResourcePermission> resourcePermissionCache = new MemoryListSearchCache<>(queryResourcePermissions, AuthResourcePermission::getId);
        List<AuthResourcePermission> resourcePermissions = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            AuthResourcePermission resourcePermission = resourcePermissionCache.get(permissionId);
            if (resourcePermission != null) {
                resourcePermissions.add(resourcePermission);
            }
        }
        return resourcePermissions;
    }
}
