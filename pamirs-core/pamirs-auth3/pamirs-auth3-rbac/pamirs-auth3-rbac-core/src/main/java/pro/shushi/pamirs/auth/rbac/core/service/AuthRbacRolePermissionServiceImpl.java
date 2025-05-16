package pro.shushi.pamirs.auth.rbac.core.service;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthAuthorizationSceneApi;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthFieldAuthorizationExtendApi;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthResourceAuthorizationExtendApi;
import pro.shushi.pamirs.auth.api.extend.authorization.AuthRowAuthorizationExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthAuthorizationExtendExecutor;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthResourceFetchMethod;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeLoader;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.authorize.AuthFieldAuthorizeService;
import pro.shushi.pamirs.auth.api.service.authorize.AuthResourceAuthorizeService;
import pro.shushi.pamirs.auth.api.service.authorize.AuthRowAuthorizeService;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.service.manager.AuthAuthorizationOperator;
import pro.shushi.pamirs.auth.api.service.manager.AuthPermissionCacheManager;
import pro.shushi.pamirs.auth.api.service.permission.AuthFieldPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthRowPermissionService;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleFieldPermissionService;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleRowPermissionService;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacFieldPermissionItem;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacResourcePermissionItem;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacRowPermissionItem;
import pro.shushi.pamirs.auth.rbac.api.pmodel.AuthRbacRolePermissionProxy;
import pro.shushi.pamirs.auth.rbac.api.service.AuthRbacRolePermissionService;
import pro.shushi.pamirs.auth.rbac.core.utils.AuthRbacAuthorizationComputeHelper;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.orm.json.PamirsJsonUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang at 21:41 on 2024-08-12
 */
@Service
@Fun(AuthRbacRolePermissionService.FUN_NAMESPACE)
public class AuthRbacRolePermissionServiceImpl implements AuthRbacRolePermissionService {

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Autowired
    private AuthFieldPermissionService authFieldPermissionService;

    @Autowired
    private AuthRoleFieldPermissionService authRoleFieldPermissionService;

    @Autowired
    private AuthRowPermissionService authRowPermissionService;

    @Autowired
    private AuthRoleRowPermissionService authRoleRowPermissionService;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthResourceAuthorizeService authResourceAuthorizeService;

    @Autowired
    private AuthFieldAuthorizeService authFieldAuthorizeService;

    @Autowired
    private AuthRowAuthorizeService authRowAuthorizeService;

    @Autowired
    private AuthAuthorizationOperator authAuthorizationOperator;

    @Autowired
    private AuthPermissionCacheManager authPermissionCacheManager;

    @Autowired
    private AuthAccessService authAccessService;

    @Function
    @Override
    public AuthRbacRolePermissionProxy queryOne(AuthRbacRolePermissionProxy data) {
        AuthRbacRolePermissionProxy origin = FetchUtil.fetchOne(data);
        if (origin == null) {
            return null;
        }
        buildResourcePermissions(origin);
        return origin;
    }

    private void buildResourcePermissions(AuthRbacRolePermissionProxy data) {
        ResourcePermissionNodeLoader loader = permissionNodeLoader.getManagementLoader();
        List<PermissionNode> nodes = loader.buildAllPermissions(Sets.newHashSet(data.getId()));
        if (CollectionUtils.isNotEmpty(nodes)) {
            data.setNodesJson(PamirsJsonUtils.toJSONString(nodes));
        }
    }

    @Function
    @Override
    public List<AuthRbacFieldPermissionItem> queryFieldPermissions(Long roleId, String model) {
        List<ModelField> modelFields = Optional.ofNullable(PamirsSession.getContext().getSimpleModelConfig(model))
                .map(ModelConfig::getModelDefinition)
                .map(ModelDefinition::getModelFields)
                .orElse(null);
        if (CollectionUtils.isEmpty(modelFields)) {
            return null;
        }
        List<AuthRbacFieldPermissionItem> fieldPermissionItems = queryRoleFieldPermissions(roleId, model);
        MemoryListSearchCache<String, AuthRbacFieldPermissionItem> fieldPermissionItemCache = new MemoryListSearchCache<>(fieldPermissionItems, AuthRbacFieldPermissionItem::getField);
        List<AuthRbacFieldPermissionItem> finalFieldPermissionItems = new ArrayList<>();
        AuthRbacFieldPermissionItem item = fieldPermissionItemCache.get(AuthConstants.ALL_FLAG_STRING);
        if (item == null) {
            item = new AuthRbacFieldPermissionItem();
            item.setModel(model);
            item.setField(AuthConstants.ALL_FLAG_STRING);
            item.setPermRead(Boolean.TRUE);
            item.setPermWrite(Boolean.TRUE);
        }
        item.setDisplayName(AuthConstants.ALL_FLAG_DISPLAY_NAME);
        item.setDescription(AuthConstants.ALL_FLAG_FIELD_DESCRIPTION);
        finalFieldPermissionItems.add(item);
        for (ModelField modelField : modelFields) {
            if (Boolean.TRUE.equals(modelField.getPk())) {
                continue;
            }
            String field = modelField.getField();
            item = fieldPermissionItemCache.get(field);
            if (item == null) {
                item = new AuthRbacFieldPermissionItem();
                item.setModel(model);
                item.setField(field);
                item.setPermRead(Boolean.TRUE);
                item.setPermWrite(Boolean.TRUE);
            }
            item.setDisplayName(modelField.getDisplayName());
            item.setDescription(modelField.getDescription());
            item.setTtype(modelField.getTtype());
            finalFieldPermissionItems.add(item);
        }
        return finalFieldPermissionItems;
    }

    private List<AuthRbacFieldPermissionItem> queryRoleFieldPermissions(Long roleId, String model) {
        List<AuthFieldPermission> fieldPermissions = authFieldPermissionService.queryListByWrapper(Pops.<AuthFieldPermission>lambdaQuery()
                .from(AuthFieldPermission.MODEL_MODEL)
                .select(AuthFieldPermission::getId, AuthFieldPermission::getField)
                .eq(AuthFieldPermission::getModel, model));
        if (CollectionUtils.isEmpty(fieldPermissions)) {
            return Collections.emptyList();
        }
        Map<Long, AuthFieldPermission> permissionMap = new HashMap<>(fieldPermissions.size());
        for (AuthFieldPermission fieldPermission : fieldPermissions) {
            permissionMap.put(fieldPermission.getId(), fieldPermission);
        }
        List<AuthRoleFieldPermission> roleFieldPermissions = authRoleFieldPermissionService.queryListByWrapper(Pops.<AuthRoleFieldPermission>lambdaQuery()
                .from(AuthRoleFieldPermission.MODEL_MODEL)
                .eq(AuthRoleFieldPermission::getRoleId, roleId)
                .in(AuthRoleFieldPermission::getPermissionId, new HashSet<>(permissionMap.keySet())));
        if (CollectionUtils.isEmpty(roleFieldPermissions)) {
            return Collections.emptyList();
        }
        List<AuthRbacFieldPermissionItem> fieldPermissionItems = new ArrayList<>();
        for (AuthRoleFieldPermission roleFieldPermission : roleFieldPermissions) {
            Long permissionId = roleFieldPermission.getPermissionId();
            AuthFieldPermission fieldPermission = permissionMap.get(permissionId);
            if (fieldPermission == null) {
                continue;
            }
            Long authorizedValue = roleFieldPermission.getAuthorizedValue();
            AuthRbacFieldPermissionItem item = new AuthRbacFieldPermissionItem();
            item.setModel(model);
            item.setField(fieldPermission.getField());
            item.setPermRead(FieldAuthorizedValueEnum.readable(authorizedValue));
            item.setPermWrite(FieldAuthorizedValueEnum.writable(authorizedValue));
            fieldPermissionItems.add(item);
        }
        return fieldPermissionItems;
    }

    @Function
    @Override
    public List<AuthRbacRowPermissionItem> queryRowPermissions(AuthRbacRolePermissionProxy origin) {
        Long roleId = origin.getId();
        if (roleId == null) {
            return null;
        }
        List<AuthRoleRowPermission> roleRowPermissions = queryRoleRowPermissions(roleId);
        if (CollectionUtils.isEmpty(roleRowPermissions)) {
            return null;
        }
        Map<Long, AuthRoleRowPermission> roleRowPermissionMap = new HashMap<>(roleRowPermissions.size());
        for (AuthRoleRowPermission roleRowPermission : roleRowPermissions) {
            Long permissionId = roleRowPermission.getPermissionId();
            roleRowPermissionMap.put(permissionId, roleRowPermission);
        }
        List<AuthRowPermission> permissions = queryRowPermissions(roleRowPermissionMap.keySet());
        List<AuthRbacRowPermissionItem> permissionItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(permissions)) {
            for (AuthRowPermission permission : permissions) {
                AuthRbacRowPermissionItem permissionItem = AuthRbacRowPermissionItem.transfer(permission, new AuthRbacRowPermissionItem());
                permissionItem.setDomainExp(permissionItem.getFilter());
                Long permissionId = permissionItem.getId();
                AuthRoleRowPermission roleRowPermission = roleRowPermissionMap.get(permissionId);
                if (roleRowPermission == null) {
                    permissionItem.setPermRead(Boolean.TRUE);
                    permissionItem.setPermWrite(Boolean.FALSE);
                    permissionItem.setPermDelete(Boolean.FALSE);
                } else {
                    Long authorizedValue = roleRowPermission.getAuthorizedValue();
                    permissionItem.setPermRead(RowAuthorizedValueEnum.readable(authorizedValue));
                    permissionItem.setPermWrite(RowAuthorizedValueEnum.writable(authorizedValue));
                    permissionItem.setPermDelete(RowAuthorizedValueEnum.deletable(authorizedValue));
                }
                permissionItems.add(permissionItem);
            }
            permissionItems = Models.origin().listFieldQuery(permissionItems, AuthRbacRowPermissionItem::getModelDefinition);
        }
        return permissionItems;
    }

    private List<AuthRoleRowPermission> queryRoleRowPermissions(Long id) {
        return authRoleRowPermissionService.queryListByWrapper(Pops.<AuthRoleRowPermission>lambdaQuery()
                .from(AuthRoleRowPermission.MODEL_MODEL)
                .ne(AuthRoleRowPermission::getSource, AuthorizationSourceEnum.BUILD_IN)
                .eq(AuthRoleRowPermission::getRoleId, id));
    }

    private List<AuthRowPermission> queryRowPermissions(Set<Long> permissionIds) {
        return DataShardingHelper.build().collectionSharding(permissionIds,
                (sublist) -> authRowPermissionService.queryListByWrapper(Pops.<AuthRowPermission>lambdaQuery()
                        .from(AuthRowPermission.MODEL_MODEL)
                        .in(AuthRowPermission::getId, sublist)
                        .isNotNull(AuthRowPermission::getFilter)
                        .ne(AuthRowPermission::getSource, AuthorizationSourceEnum.BUILD_IN)
                        .setBatchSize(-1)));
    }

    @Transactional
    @Function
    @Override
    public AuthRbacRolePermissionProxy update(AuthRbacRolePermissionProxy data) {
        AuthRole result = authRoleService.update(AuthRole.transfer(data, new AuthRole()));
        if (result == null) {
            return null;
        }
        Set<Long> roleIds = Sets.newHashSet(result.getId());
        boolean isRefresh = false;
        List<AuthRbacResourcePermissionItem> resourcePermissions = mergeResourcePermissions(data.getResourcePermissions(), data.getManagementPermissions());
        List<AuthResourceAuthorization> resourceAuthorizations = collectionResourcePermissions(resourcePermissions);
        if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
            resourceAuthorizations = authAuthorizationOperator.fillResourcePermissionIds(resourceAuthorizations);
            resourceAuthorizations = authResourceAuthorizeService.updates(roleIds, resourceAuthorizations, AuthorizationSourceEnum.MANUAL);
            if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
                isRefresh = true;
            }
        }
        List<AuthFieldAuthorization> fieldAuthorizations = collectionFieldPermissions(data.getId(), data.getFieldPermissions());
        if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
            fieldAuthorizations = authAuthorizationOperator.fillFieldPermissionIds(fieldAuthorizations);
            fieldAuthorizations = authFieldAuthorizeService.updates(roleIds, fieldAuthorizations, AuthorizationSourceEnum.MANUAL);
            if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
                isRefresh = true;
            }
        }
        List<AuthRowAuthorization> rowAuthorizations = collectionRowPermissions(data.getRowPermissions());
        if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
            rowAuthorizations = authAuthorizationOperator.fillRowPermissionIds(rowAuthorizations);
        }
        rowAuthorizations = authRowAuthorizeService.fullUpdates(roleIds, rowAuthorizations, AuthorizationSourceEnum.MANUAL);
        if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
            isRefresh = true;
        }
        if (isRefresh) {
            authPermissionCacheManager.authorizeRefreshPermissions(roleIds, resourceAuthorizations, null, fieldAuthorizations, rowAuthorizations);
            if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
                List<AuthRowAuthorization> revokeRowAuthorizations = rowAuthorizations.stream()
                        .map(v -> AuthRowAuthorization.transfer(v, new AuthRowAuthorization()).setAuthorizedValue(RowAuthorizedValueEnum.fullValue() & ~v.getAuthorizedValue()))
                        .collect(Collectors.toList());
                if (!revokeRowAuthorizations.isEmpty()) {
                    authPermissionCacheManager.revokeRefreshRowPermissions(roleIds, revokeRowAuthorizations);
                }
            }
            if (CollectionUtils.isNotEmpty(resourceAuthorizations)) {
                final List<AuthResourceAuthorization> finalResourceAuthorizations = resourceAuthorizations;
                AuthAuthorizationExtendExecutor.execute(AuthResourceAuthorizationExtendApi.class,
                        AuthAuthorizationSceneApi.RBAC_SCENE,
                        api -> api.updates(roleIds, finalResourceAuthorizations));
            }
            if (CollectionUtils.isNotEmpty(fieldAuthorizations)) {
                final List<AuthFieldAuthorization> finalFieldAuthorizations = fieldAuthorizations;
                AuthAuthorizationExtendExecutor.execute(AuthFieldAuthorizationExtendApi.class,
                        AuthAuthorizationSceneApi.RBAC_SCENE,
                        api -> api.updates(roleIds, finalFieldAuthorizations));
            }
            if (CollectionUtils.isNotEmpty(rowAuthorizations)) {
                final List<AuthRowAuthorization> finalRowAuthorizations = rowAuthorizations;
                AuthAuthorizationExtendExecutor.execute(AuthRowAuthorizationExtendApi.class,
                        AuthAuthorizationSceneApi.RBAC_SCENE,
                        api -> api.updates(roleIds, finalRowAuthorizations));
            }
        }
        return AuthRbacRolePermissionProxy.transfer(result, new AuthRbacRolePermissionProxy());
    }

    private List<AuthRbacResourcePermissionItem> mergeResourcePermissions(List<AuthRbacResourcePermissionItem> resourcePermissions, List<AuthRbacResourcePermissionItem> managementPermissions) {
        if (CollectionUtils.isEmpty(managementPermissions)) {
            return resourcePermissions;
        }
        List<AuthRbacResourcePermissionItem> mergedPermissions = new ArrayList<>();
        if (CollectionUtils.isEmpty(resourcePermissions)) {
            resourcePermissions = Collections.emptyList();
        }
        MemoryListSearchCache<String, AuthRbacResourcePermissionItem> resourcePermissionCache = new MemoryListSearchCache<>(resourcePermissions, AuthRbacResourcePermissionItem::getPath);
        for (AuthRbacResourcePermissionItem managementPermission : managementPermissions) {
            String path = managementPermission.getPath();
            if (StringUtils.isBlank(path)) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_PATH_ERROR).errThrow();
            }
            AuthRbacResourcePermissionItem resourcePermission = resourcePermissionCache.compute(path, (k, v) -> v);
            if (resourcePermission == null) {
                mergedPermissions.add(managementPermission);
            } else {
                mergedPermissions.add(resourcePermission);
                resourcePermission.setCanManagement(managementPermission.getCanManagement());
            }
        }
        resourcePermissionCache.fill();
        mergedPermissions.addAll(resourcePermissionCache.getNotComputedCache().values());
        return mergedPermissions;
    }

    private List<AuthResourceAuthorization> collectionResourcePermissions(List<AuthRbacResourcePermissionItem> resourcePermissions) {
        Map<ResourcePermissionSubtypeEnum, AuthResourceFetchMethod<?>> fetchResourceMethodMap = FetchResourceHelper.buildResourceMethodMap();
        Set<Long> moduleResourceIds = new HashSet<>(8);
        Set<Long> homepageResourceIds = new HashSet<>(8);
        List<AuthRbacResourcePermissionItem> validResourcePermissions = new ArrayList<>();
        for (AuthRbacResourcePermissionItem resourcePermission : resourcePermissions) {
            ResourcePermissionSubtypeEnum subtype = resourcePermission.getSubtype();
            Long resourceId = resourcePermission.getResourceId();
            String path = resourcePermission.getPath();
            if (subtype == null) {
                continue;
            }
            if (resourceId == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ID_ERROR).errThrow();
            }
            if (StringUtils.isBlank(path)) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_PATH_ERROR).errThrow();
            }
            resourcePermission.setAuthorizedValue(AuthRbacAuthorizationComputeHelper.computeResourceAuthorizedValue(resourcePermission));
            switch (subtype) {
                case MODULE:
                    moduleResourceIds.add(resourceId);
                    break;
                case HOMEPAGE:
                    homepageResourceIds.add(resourceId);
                    break;
                default: {
                    AuthResourceFetchMethod<?> method = fetchResourceMethodMap.get(subtype);
                    if (method == null) {
                        throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
                    }
                    method.addResourceId(resourceId);
                    break;
                }
            }
            validResourcePermissions.add(resourcePermission);
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

        AuthResourceFetchMethod<?> moduleResourceFetchMethod = fetchResourceMethodMap.get(ResourcePermissionSubtypeEnum.MODULE);
        AuthResourceFetchMethod<?> homepageResourceFetchMethod = fetchResourceMethodMap.get(ResourcePermissionSubtypeEnum.HOMEPAGE);

        List<AuthResourceAuthorization> resourceAuthorizations = new ArrayList<>(validResourcePermissions.size());
        for (AuthRbacResourcePermissionItem resourcePermission : validResourcePermissions) {
            ResourcePermissionSubtypeEnum nodeType = resourcePermission.getSubtype();
            Long resourceId = resourcePermission.getResourceId();
            String path = resourcePermission.getPath();
            Long authorizedValue = resourcePermission.getAuthorizedValue();
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
                    AuthResourceFetchMethod<?> method = fetchResourceMethodMap.get(nodeType);
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

    private List<AuthFieldAuthorization> collectionFieldPermissions(Long roleId, List<AuthRbacFieldPermissionItem> fieldPermissions) {
        if (CollectionUtils.isEmpty(fieldPermissions)) {
            return null;
        }
        boolean isModify = false;
        String model = "";
        Set<String> excludeFields = new HashSet<>();
        List<AuthFieldAuthorization> permissions = new ArrayList<>(fieldPermissions.size());
        for (AuthRbacFieldPermissionItem fieldPermission : fieldPermissions) {
            fieldPermission.refreshCode();
            AuthFieldAuthorization permission = new AuthFieldAuthorization();
            AuthFieldPermission.transfer(fieldPermission, permission);
            permission.setAuthorizedValue(AuthRbacAuthorizationComputeHelper.computeFieldAuthorizedValue(fieldPermission));
            permissions.add(permission);
            if (AuthConstants.ALL_FLAG_STRING.equals(fieldPermission.getField())) {
                model = permission.getModel();
                isModify = true;
            }
            excludeFields.add(permission.getField());
        }

        //当全部状态值修改之后，重新计算当前字段权限默认值
        if (isModify) {
            fullDefaultPermissions(excludeFields, permissions, model, roleId);
        }
        return permissions;
    }

    private void fullDefaultPermissions(Set<String> excludeFields, List<AuthFieldAuthorization> permissions, String model, Long roleId) {
        List<AuthRbacFieldPermissionItem> fieldPermissionItems = queryRoleFieldPermissions(roleId, model);
        if (CollectionUtils.isNotEmpty(fieldPermissionItems)) {
            excludeFields.addAll(fieldPermissionItems.stream().map(AuthFieldPermission::getField).collect(Collectors.toSet()));
        }

        List<ModelField> modelFields = Optional.ofNullable(PamirsSession.getContext().getSimpleModelConfig(model))
                .map(ModelConfig::getModelDefinition)
                .map(ModelDefinition::getModelFields)
                .orElse(null);
        if (CollectionUtils.isEmpty(modelFields)) {
            return;
        }
        for (ModelField modelField : modelFields) {
            String field = modelField.getField();
            if (excludeFields.contains(field)) {
                continue;
            }
            //将界面上显示为true 的字段，赋予实际值
            AuthRbacFieldPermissionItem fieldPermission = new AuthRbacFieldPermissionItem();
            fieldPermission.setModel(model);
            fieldPermission.setField(field);
            fieldPermission.setPermRead(Boolean.TRUE);
            fieldPermission.setPermWrite(Boolean.TRUE);
            fieldPermission.refreshCode();

            AuthFieldAuthorization permission = new AuthFieldAuthorization();
            AuthFieldPermission.transfer(fieldPermission, permission);
            permission.setAuthorizedValue(AuthRbacAuthorizationComputeHelper.computeFieldAuthorizedValue(fieldPermission));
            permissions.add(permission);
        }
    }

    private List<AuthRowAuthorization> collectionRowPermissions(List<AuthRbacRowPermissionItem> rowPermissions) {
        if (CollectionUtils.isEmpty(rowPermissions)) {
            return Collections.emptyList();
        }
        List<AuthRowAuthorization> permissions = new ArrayList<>(rowPermissions.size());
        for (AuthRbacRowPermissionItem rowPermission : rowPermissions) {
            rowPermission.setFilter(rowPermission.getDomainExp());
            AuthRowAuthorization permission = new AuthRowAuthorization();
            AuthRowPermission.transfer(rowPermission, permission);
            permission.setAuthorizedValue(AuthRbacAuthorizationComputeHelper.computeRowAuthorizedValue(rowPermission));
            permissions.add(permission);
        }
        return permissions;
    }
}
