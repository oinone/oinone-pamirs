package pro.shushi.pamirs.auth.view.action;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.relation.*;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataOperator;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupManager;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.service.manager.AuthAuthorizationOperator;
import pro.shushi.pamirs.auth.api.service.manager.AuthPermissionCacheManager;
import pro.shushi.pamirs.auth.view.helper.*;
import pro.shushi.pamirs.auth.view.manager.AuthGroupAuthorizeService;
import pro.shushi.pamirs.auth.view.manager.AuthResourceNodeOperator;
import pro.shushi.pamirs.auth.view.tmodel.ResourcePermissionNode;
import pro.shushi.pamirs.auth.view.tmodel.ResourcePermissionNodes;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 资源权限节点批量操作动作
 *
 * @author Adamancy Zhang at 10:18 on 2024-01-23
 */
@Component
@Model.model(ResourcePermissionNodes.MODEL_MODEL)
public class ResourcePermissionNodesAction {

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthAuthorizationOperator authAuthorizationOperator;

    @Autowired
    private AuthPermissionCacheManager authPermissionCacheManager;

    @Autowired
    private AuthResourceNodeOperator authResourceNodeOperator;

    @Autowired
    private AuthGroupDataOperator authGroupDataOperator;

    @Autowired
    private AuthGroupAuthorizeService authGroupAuthorizeService;

    @Autowired
    private AuthGroupManager authGroupManager;

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Autowired
    private AuthAccessService authAccessService;

    @Transactional(rollbackFor = Throwable.class)
    @Action(displayName = "批量授权", bindingType = ViewTypeEnum.FORM)
    public ResourcePermissionNodes authorizes(ResourcePermissionNodes data) {
        if (data == null) {
            return error();
        }
        List<AuthRole> roles = data.getRoles();
        List<ResourcePermissionNode> resourcePermissions = data.getPermissions();
        if (CollectionUtils.isEmpty(roles) || CollectionUtils.isEmpty(resourcePermissions)) {
            return error();
        }
        List<AuthRole> originRoles = authRoleService.fetchRoles(roles.stream().map(AuthRole::getId).peek(this::assertRoleId).collect(Collectors.toSet()));
        if (CollectionUtils.isEmpty(originRoles) || roles.size() != originRoles.size()) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
        }

        Set<Long> roleIds = originRoles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        Map<ResourcePermissionSubtypeEnum, AuthGroupResourceFetchMethod<?>> fetchResourceMethodMap = buildResourceMethodMap();
        List<AuthResourceAuthorization> resourceAuthorizations = collectionResourceAuthorizations(resourcePermissions, ResourceAuthorizedValueEnum.ACCESS.value(), fetchResourceMethodMap);
        if (CollectionUtils.isEmpty(resourceAuthorizations)) {
            return success(originRoles);
        }
        List<AuthResourceAuthorization> refreshResourceAuthorizations = authAuthorizationOperator.createAndAuthorizeResourcePermissions(roleIds, resourceAuthorizations);
        if (CollectionUtils.isNotEmpty(refreshResourceAuthorizations)) {
            authPermissionCacheManager.authorizeRefreshResourcePermissions(roleIds, refreshResourceAuthorizations);
        }
        Set<Long> groupIds = queryAndCreateGroups(resourceAuthorizations, AuthGroupTypeEnum.RUNTIME, fetchResourceMethodMap);
        createGroupRoles(roleIds, groupIds);
        authorizeGroupOtherPermissions(roleIds, groupIds);
        return success(originRoles);
    }

    public void authorizeGroupOtherPermissions(Set<Long> roleIds, Set<Long> groupIds) {
        List<AuthGroupResourcePermission> actionAuthorizations = authGroupDataOperator.fetchValidActionPermissions(groupIds);
        List<AuthGroupFieldPermission> fieldAuthorizations = authGroupDataOperator.fetchValidFieldPermissions(groupIds);
        List<AuthGroupRowPermission> rowAuthorizations = authGroupDataOperator.fetchValidRowPermissions(groupIds);
        authGroupDataOperator.fillPermissions(actionAuthorizations, fieldAuthorizations, rowAuthorizations);
        authGroupAuthorizeService.authorizeRolePermissions(roleIds, actionAuthorizations, fieldAuthorizations, rowAuthorizations);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Action(displayName = "批量取消授权", bindingType = ViewTypeEnum.FORM)
    public ResourcePermissionNodes revokes(ResourcePermissionNodes data) {
        throw PamirsException.construct(CommonExpEnumerate.UNSUPPORTED_OPERATION_ERROR).errThrow();
    }

    private ResourcePermissionNodes success(List<AuthRole> roles) {
        ResourcePermissionNodes result = new ResourcePermissionNodes();
        result.setRoles(roles);
        return result;
    }

    private ResourcePermissionNodes error() {
        return new ResourcePermissionNodes();
    }

    private void assertRoleId(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("Invalid role id");
        }
    }

    private List<AuthResourceAuthorization> collectionResourceAuthorizations(List<ResourcePermissionNode> nodes, Long authorizedValue,
                                                                             Map<ResourcePermissionSubtypeEnum, AuthGroupResourceFetchMethod<?>> fetchResourceMethodMap) {
        Set<Long> moduleResourceIds = new HashSet<>(8);
        Set<Long> homepageResourceIds = new HashSet<>(8);
        List<ResourcePermissionNode> validNodes = new ArrayList<>(nodes.size());
        for (ResourcePermissionNode node : nodes) {
            ResourcePermissionSubtypeEnum nodeType = node.getNodeType();
            Long resourceId = node.getResourceId();
            String path = node.getPath();
            if (nodeType == null) {
                continue;
            }
            if (resourceId == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ID_ERROR).errThrow();
            }
            if (StringUtils.isBlank(path)) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_PATH_ERROR).errThrow();
            }
            switch (nodeType) {
                case MODULE:
                    moduleResourceIds.add(resourceId);
                    break;
                case HOMEPAGE:
                    homepageResourceIds.add(resourceId);
                    break;
                default: {
                    AuthGroupResourceFetchMethod<?> method = fetchResourceMethodMap.get(nodeType);
                    if (method == null) {
                        throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
                    }
                    method.addResourceId(resourceId);
                    break;
                }
            }
            validNodes.add(node);
        }
        Set<Long> allModuleIds = Sets.union(moduleResourceIds, homepageResourceIds);
        List<UeModule> allModules;
        if (allModuleIds.isEmpty()) {
            allModules = Collections.emptyList();
        } else {
            allModules = FetchResourceHelper.fetchModules(allModuleIds);
        }
        MemoryListSearchCache<Long, UeModule> allModuleCache = new MemoryListSearchCache<>(allModules, UeModule::getId);

        List<UeModule> homepageModules = homepageResourceIds.stream()
                .map(allModuleCache::get)
                .filter(v -> v != null && StringUtils.isNoneBlank(v.getHomePageModel(), v.getHomePageName()))
                .collect(Collectors.toList());
        List<ViewAction> homepageActions;
        if (CollectionUtils.isEmpty(homepageModules)) {
            homepageActions = Collections.emptyList();
        } else {
            homepageActions = FetchResourceHelper.fetchHomepageActions(homepageModules);
        }
        MemoryListSearchCache<String, ViewAction> homepageActionCache = new MemoryListSearchCache<>(homepageActions, v -> ViewAction.sign(v.getModel(), v.getName()));

        Map<ResourcePermissionSubtypeEnum, MemoryListSearchCache<Long, ?>> resourceCacheMap = new HashMap<>(fetchResourceMethodMap.size());
        fetchResourceMethodMap.forEach((nodeType, method) -> resourceCacheMap.put(nodeType, method.query()));

        AuthGroupResourceFetchMethod<?> moduleResourceFetchMethod = fetchResourceMethodMap.get(ResourcePermissionSubtypeEnum.MODULE);
        AuthGroupResourceFetchMethod<?> homepageResourceFetchMethod = fetchResourceMethodMap.get(ResourcePermissionSubtypeEnum.HOMEPAGE);

        List<AuthResourceAuthorization> resourceAuthorizations = new ArrayList<>(nodes.size());
        for (ResourcePermissionNode node : validNodes) {
            ResourcePermissionSubtypeEnum nodeType = node.getNodeType();
            Long resourceId = node.getResourceId();
            String path = node.getPath();
            AuthResourceAuthorization authorization;
            switch (nodeType) {
                case MODULE: {
                    UeModule module = allModuleCache.get(resourceId);
                    if (module == null) {
                        throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
                    }
                    authorization = moduleResourceFetchMethod.generatorResourceAuthorization(FetchUtil.cast(module), path, authorizedValue);
                    break;
                }
                case HOMEPAGE: {
                    UeModule module = allModuleCache.get(resourceId);
                    if (module == null) {
                        throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
                    }
                    String homepageModel = module.getHomePageModel();
                    String homepageActionName = module.getHomePageName();
                    if (StringUtils.isAnyBlank(homepageModel, homepageActionName)) {
                        continue;
                    }
                    ViewAction action = homepageActionCache.get(ViewAction.sign(homepageModel, homepageActionName));
                    if (action == null) {
                        throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
                    }
                    module.setHomePage(action);
                    authorization = homepageResourceFetchMethod.generatorResourceAuthorization(FetchUtil.cast(module), path, authorizedValue);
                    break;
                }
                default: {
                    AuthGroupResourceFetchMethod<?> method = fetchResourceMethodMap.get(nodeType);
                    MemoryListSearchCache<Long, ?> resourceCache = resourceCacheMap.get(nodeType);
                    if (method == null || resourceCache == null) {
                        throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
                    }
                    Object data = resourceCache.get(resourceId);
                    if (data == null) {
                        continue;
                    }
                    authorization = method.generatorResourceAuthorization(FetchUtil.cast(data), path, authorizedValue);
                    break;
                }
            }
            if (authorization != null) {
                resourceAuthorizations.add(authorization);
            }
        }
        return resourceAuthorizations;
    }

    private Map<ResourcePermissionSubtypeEnum, AuthGroupResourceFetchMethod<?>> buildResourceMethodMap() {
        return MapHelper.<ResourcePermissionSubtypeEnum, AuthGroupResourceFetchMethod<?>>newInstance()
                .put(ResourcePermissionSubtypeEnum.MODULE, new AuthModuleGroupResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.HOMEPAGE, new AuthHomepageGroupResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.MENU, new AuthMenuGroupResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.SERVER_ACTION, new AuthServerActionGroupResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.VIEW_ACTION, new AuthViewActionGroupResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.URL_ACTION, new AuthUrlActionGroupResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.CLIENT_ACTION, new AuthClientActionGroupResourceFetchMethod(authAccessService))
                .build();
    }


    private Set<Long> queryAndCreateGroups(List<AuthResourceAuthorization> resourceAuthorizations, AuthGroupTypeEnum type,
                                           Map<ResourcePermissionSubtypeEnum, AuthGroupResourceFetchMethod<?>> fetchResourceMethodMap) {
        Map<Long, AuthGroup> createGroupMap = new HashMap<>(resourceAuthorizations.size());
        Map<Long, AuthResourceAuthorization> resourceAuthorizationCache = new HashMap<>(resourceAuthorizations.size());
        for (AuthResourceAuthorization resourceAuthorization : resourceAuthorizations) {
            ResourcePermissionSubtypeEnum subtype = resourceAuthorization.getSubtype();
            AuthGroupResourceFetchMethod<?> method = fetchResourceMethodMap.get(subtype);
            if (method == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
            }
            AuthGroup group = method.createAuthGroup(resourceAuthorization, type);
            createGroupMap.put(resourceAuthorization.getId(), group);
            resourceAuthorizationCache.put(resourceAuthorization.getId(), resourceAuthorization);
        }
        if (createGroupMap.isEmpty()) {
            return Collections.emptySet();
        }
        List<AuthGroup> existGroups = DataShardingHelper.build().collectionSharding(createGroupMap.values().stream().map(AuthGroup::getName).collect(Collectors.toSet()),
                (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroup>lambdaQuery()
                        .from(AuthGroup.MODEL_MODEL)
                        .in(AuthGroup::getName, sublist)));
        MemoryListSearchCache<String, AuthGroup> existGroupCache = new MemoryListSearchCache<>(existGroups, AuthGroup::getName);
        List<AuthGroup> needCreateGroups = new ArrayList<>(createGroupMap.size());
        List<AuthGroup> createGroups = new ArrayList<>(createGroupMap.size());
        for (AuthGroup createGroup : createGroupMap.values()) {
            AuthGroup existGroup = existGroupCache.get(createGroup.getName());
            if (existGroup == null) {
                needCreateGroups.add(createGroup);
            } else {
                createGroup.setId(existGroup.getId());
                createGroups.add(createGroup);
            }
        }
        if (CollectionUtils.isNotEmpty(needCreateGroups)) {
            createGroups.addAll(Models.origin().createBatch(needCreateGroups));
        }
        Set<String> resourceCodes = resourceAuthorizations.stream()
                .map(AuthResourceAuthorization::getResourceCode)
                .collect(Collectors.toSet());
        List<AuthGroupRelResource> existGroupRelResource = queryGroupRelResourceList(resourceCodes);
        MemoryListSearchCache<String, AuthGroupRelResource> existAuthGroupMenuRelCache = new MemoryListSearchCache<>(existGroupRelResource, this::generatorGroupRelResourceUniqueKey);

        MemoryListSearchCache<String, AuthGroup> createGroupCache = new MemoryListSearchCache<>(createGroups, AuthGroup::getName);
        List<AuthGroupRelResource> createGroupRelResources = new ArrayList<>(createGroups.size());
        Set<Long> groupIds = new HashSet<>(createGroupMap.size());
        for (Map.Entry<Long, AuthGroup> entry : createGroupMap.entrySet()) {
            AuthResourceAuthorization authorization = resourceAuthorizationCache.get(entry.getKey());
            AuthGroup createGroup = createGroupCache.get(entry.getValue().getName());
            Long groupId = createGroup.getId();
            String groupName = createGroup.getName();
            authorization.setGroupId(groupId);
            authorization.setGroupName(groupName);
            groupIds.add(groupId);

            AuthGroupRelResource createGroupRelResource = new AuthGroupRelResource();
            createGroupRelResource.setGroupId(groupId);
            createGroupRelResource.setGroupName(groupName);
            createGroupRelResource.setResourceCode(authorization.getResourceCode());
            createGroupRelResource.setPermissionId(authorization.getId());
            createGroupRelResource.setNodeType(authorization.getSubtype());
            createGroupRelResource.setGroupType(createGroup.getType());

            if (existAuthGroupMenuRelCache.get(generatorGroupRelResourceUniqueKey(createGroupRelResource)) == null) {
                createGroupRelResources.add(createGroupRelResource);
            }
        }
        if (!createGroupRelResources.isEmpty()) {
            Models.origin().createBatch(createGroupRelResources);
        }
        authGroupManager.createGroupActionPermissions(resourceAuthorizations);
        return groupIds;
    }

    private List<AuthGroupRelResource> queryGroupRelResourceList(Set<String> resourceCodes) {
        return DataShardingHelper.build().collectionSharding(resourceCodes, (sublist) -> Models.data().queryListByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                .from(AuthGroupRelResource.MODEL_MODEL)
                .eq(AuthGroupRelResource::getGroupType, AuthGroupTypeEnum.RUNTIME.value())
                .in(AuthGroupRelResource::getResourceCode, sublist)));
    }

    private String generatorGroupRelResourceUniqueKey(AuthGroupRelResource groupRelResource) {
        return groupRelResource.getGroupId() + CharacterConstants.SEPARATOR_OCTOTHORPE +
                groupRelResource.getResourceCode() + CharacterConstants.SEPARATOR_OCTOTHORPE +
                groupRelResource.getNodeType() + CharacterConstants.SEPARATOR_OCTOTHORPE +
                groupRelResource.getGroupType();
    }

    private void createGroupRoles(Set<Long> roleIds, Set<Long> groupIds) {
        List<AuthGroupRole> existGroupRoles = DataShardingHelper.build().collectionSharding(groupIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                .from(AuthGroupRole.MODEL_MODEL)
                .in(AuthGroupRole::getRoleId, roleIds)
                .in(AuthGroupRole::getGroupId, sublist)));
        MemoryListSearchCache<String, AuthGroupRole> existGroupRoleRelCache = new MemoryListSearchCache<>(existGroupRoles, v -> generatorGroupRoleUniqueKey(v.getRoleId(), v.getGroupId()));
        List<AuthGroupRole> createGroupRoles = new ArrayList<>(roleIds.size() * groupIds.size());
        for (Long roleId : roleIds) {
            for (Long groupId : groupIds) {
                AuthGroupRole existGroupRoleRel = existGroupRoleRelCache.get(generatorGroupRoleUniqueKey(roleId, groupId));
                if (existGroupRoleRel == null) {
                    AuthGroupRole authGroupRoleRel = new AuthGroupRole();
                    authGroupRoleRel.setRoleId(roleId);
                    authGroupRoleRel.setGroupId(groupId);
                    createGroupRoles.add(authGroupRoleRel);
                }
            }
        }
        if (!createGroupRoles.isEmpty()) {
            Models.origin().createBatch(createGroupRoles);
        }
    }

    private String generatorGroupRoleUniqueKey(Long roleId, Long groupId) {
        return roleId + CharacterConstants.SEPARATOR_OCTOTHORPE + groupId;
    }
}
