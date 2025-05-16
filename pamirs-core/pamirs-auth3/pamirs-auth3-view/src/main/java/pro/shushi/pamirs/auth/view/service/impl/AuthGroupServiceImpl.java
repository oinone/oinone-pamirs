package pro.shushi.pamirs.auth.view.service.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.helper.AuthEnumerationHelper;
import pro.shushi.pamirs.auth.api.helper.FetchResourceHelper;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthResourceFetchMethod;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.*;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataDiffService;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataOperator;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.api.utils.AuthResourceCodeGenerator;
import pro.shushi.pamirs.auth.view.entity.AuthGroupRevokeContext;
import pro.shushi.pamirs.auth.view.manager.AuthGroupAuthorizeService;
import pro.shushi.pamirs.auth.view.manager.AuthGroupRefreshCacheService;
import pro.shushi.pamirs.auth.view.manager.AuthPathMappingOperator;
import pro.shushi.pamirs.auth.view.manager.AuthResourceNodeOperator;
import pro.shushi.pamirs.auth.view.model.AuthRowPermissionItem;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupSystemPermissionProxy;
import pro.shushi.pamirs.auth.view.service.AuthGroupService;
import pro.shushi.pamirs.auth.view.utils.AuthGroupAuthorizationComputeHelper;
import pro.shushi.pamirs.auth.view.utils.AuthGroupAuthorizationConverter;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.diff.DiffList;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限组数据服务实现
 *
 * @author Adamancy Zhang at 11:03 on 2024-01-17
 */
@Service
@Fun(AuthGroupService.FUN_NAMESPACE)
public class AuthGroupServiceImpl implements AuthGroupService {

    @Autowired
    private AuthGroupDataDiffService authGroupDataDiffService;

    @Autowired
    private AuthGroupAuthorizeService authGroupAuthorizeService;

    @Autowired
    private AuthResourceNodeOperator authResourceNodeOperator;

    @Autowired
    private AuthGroupDataOperator authGroupDataOperator;

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Autowired
    private AuthGroupRefreshCacheService authGroupRefreshCacheService;

    @Autowired
    private AuthPathMappingOperator authPathMappingOperator;

    @Autowired
    private AuthAccessService authAccessService;

    @Function
    @Override
    public Boolean verifyIsManagement(AuthGroupSystemPermissionProxy data) {
        Long groupId = data.getId();
        if (groupId != null) {
            Boolean isVerify = verifyIsManagementByGroupId(groupId);
            if (isVerify != null) {
                return isVerify;
            }
        }
        Long resourceId = data.getResourceId();
        String path = data.getPath();
        if (resourceId != null && StringUtils.isNotBlank(path)) {
            Boolean isVerify = verifyIsManagementByResourceId(data);
            if (isVerify != null) {
                return isVerify;
            }
        }
        return Boolean.FALSE;
    }

    private Boolean verifyIsManagementByGroupId(Long groupId) {
        List<AuthGroupRelResource> groupRelResources = Models.origin().queryListByWrapper(new Pagination<AuthGroupRelResource>().setSize(1L), Pops.<AuthGroupRelResource>lambdaQuery()
                .from(AuthGroupRelResource.MODEL_MODEL)
                .setBatchSize(-1)
                .select(AuthGroupRelResource::getPermissionId)
                .eq(AuthGroupRelResource::getGroupId, groupId));
        if (CollectionUtils.isEmpty(groupRelResources)) {
            return null;
        }
        AuthGroupRelResource groupRelResource = groupRelResources.get(0);
        AuthResourcePermission resourcePermission = Models.origin().queryOneByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                .from(AuthResourcePermission.MODEL_MODEL)
                .eq(AuthResourcePermission::getId, groupRelResource.getPermissionId()));
        if (resourcePermission == null) {
            return null;
        }
        return verifyIsManagementByResourcePermission(resourcePermission);
    }

    private Boolean verifyIsManagementByResourceId(AuthGroupSystemPermissionProxy data) {
        String path = data.getPath();
        AuthResourcePermission resourcePermission = authResourceNodeOperator.verificationResourceNode(data,
                (module) -> new AuthResourcePermission().setType(ResourcePermissionTypeEnum.MODULE).setSubtype(ResourcePermissionSubtypeEnum.MODULE).setPath(path)
                        .setModule(module.getModule()),
                (module, action) -> new AuthResourcePermission().setType(ResourcePermissionTypeEnum.MODULE).setSubtype(ResourcePermissionSubtypeEnum.HOMEPAGE).setPath(path)
                        .setModule(module.getModule()).setModel(action.getModel()).setName(action.getName()),
                (menu) -> new AuthResourcePermission().setType(ResourcePermissionTypeEnum.MENU).setSubtype(ResourcePermissionSubtypeEnum.MENU).setPath(path)
                        .setModule(menu.getModule()).setModel(menu.getModel()).setName(menu.getName()),
                (serverAction) -> new AuthResourcePermission().setType(ResourcePermissionTypeEnum.ACTION).setSubtype(ResourcePermissionSubtypeEnum.SERVER_ACTION).setPath(path)
                        .setModel(serverAction.getModel()).setName(serverAction.getName()),
                (viewAction) -> new AuthResourcePermission().setType(ResourcePermissionTypeEnum.ACTION).setSubtype(ResourcePermissionSubtypeEnum.VIEW_ACTION).setPath(path)
                        .setModel(viewAction.getModel()).setName(viewAction.getName()),
                (urlAction) -> new AuthResourcePermission().setType(ResourcePermissionTypeEnum.ACTION).setSubtype(ResourcePermissionSubtypeEnum.URL_ACTION).setPath(path)
                        .setModel(urlAction.getModel()).setName(urlAction.getName()),
                (clientAction) -> new AuthResourcePermission().setType(ResourcePermissionTypeEnum.ACTION).setSubtype(ResourcePermissionSubtypeEnum.CLIENT_ACTION).setPath(path)
                        .setModel(clientAction.getModel()).setName(clientAction.getName()));
        if (resourcePermission == null) {
            return null;
        }
        return verifyIsManagementByResourcePermission(resourcePermission);
    }

    private Boolean verifyIsManagementByResourcePermission(AuthResourcePermission resourcePermission) {
        AuthResourceFetchMethod<?> method = FetchResourceHelper.buildResourceMethodMap().get(resourcePermission.getSubtype());
        if (method == null) {
            return null;
        }
        return method.isManagement(resourcePermission);
    }

    @Transactional(rollbackFor = Exception.class)
    @Function
    @Override
    public AuthGroupSystemPermissionProxy createSystemPermissionGroup(AuthGroupSystemPermissionProxy data) {
        verificationAndSet(data, null);
        AuthResourcePermission resourcePermission = generatorResourcePermission(data);
        verifyGroupDisplayName(data, null);
        if (ResourcePermissionTypeEnum.MENU.equals(resourcePermission.getType())) {
            data.setMenuName(resourcePermission.getName());
        }

        data = data.create();
        createGroupMenuRel(data, resourcePermission.getId());

        Long groupId = data.getId();
        List<AuthRole> roles = data.getRoles();
        Set<Long> roleIds = null;
        if (CollectionUtils.isNotEmpty(roles)) {
            roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        }

        DiffList<AuthRole> diffRoles = authGroupDataDiffService.saveRoles(groupId, roleIds, false);
        DiffList<AuthGroupResourcePermission> diffGroupActionPermissions = authGroupDataDiffService.saveActionPermissions(groupId, AuthGroupAuthorizationConverter.convertResourceAuthorizations(data.getActionPermissions()));
        DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions = authGroupDataDiffService.saveFieldPermissions(groupId, AuthGroupAuthorizationConverter.convertFieldAuthorizations(data.getFieldPermissions()));
        DiffList<AuthGroupRowPermission> diffRowPermissions = authGroupDataDiffService.saveRowPermissionBySystemPermission(groupId, AuthGroupAuthorizationConverter.convertRowAuthorization(getSystemPermissionRowPermission(data)));

        if (Boolean.TRUE.equals(data.getActive()) && diffRoles != null) {
            authGroupAuthorizeService.refreshRolePermissions(diffRoles, diffGroupActionPermissions, diffGroupFieldPermissions, diffRowPermissions);
        }

        AuthGroupSystemPermissionProxy result = new AuthGroupSystemPermissionProxy();
        result.setId(data.getId());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Function
    @Override
    public AuthGroupSystemPermissionProxy updateSystemPermissionGroup(AuthGroupSystemPermissionProxy data) {
        AuthGroupSystemPermissionProxy origin = queryOne(data);
        verificationAndSet(data, origin);
        AuthResourcePermission resourcePermission = generatorResourcePermission(data);
        verifyGroupDisplayName(data, origin);

        data.updateByPk();

        Long groupId = data.getId();
        List<AuthRole> roles = data.getRoles();
        Set<Long> roleIds = null;
        if (CollectionUtils.isNotEmpty(roles)) {
            roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        }
        DiffList<AuthRole> diffRoles = authGroupDataDiffService.saveRoles(groupId, roleIds, true);
        DiffList<AuthGroupResourcePermission> diffGroupActionPermissions = authGroupDataDiffService.saveActionPermissions(groupId, AuthGroupAuthorizationConverter.convertResourceAuthorizations(data.getActionPermissions()));
        DiffList<AuthGroupFieldPermission> diffGroupFieldPermissions = authGroupDataDiffService.saveFieldPermissions(groupId, AuthGroupAuthorizationConverter.convertFieldAuthorizations(data.getFieldPermissions()));
        AuthRowAuthorization rowAuthorization = AuthGroupAuthorizationConverter.convertRowAuthorization(getSystemPermissionRowPermission(data));
        DiffList<AuthGroupRowPermission> diffRowPermissions = authGroupDataDiffService.saveRowPermissionBySystemPermission(groupId, rowAuthorization);

        if (Boolean.TRUE.equals(origin.getActive()) && diffRoles != null) {
            authGroupRefreshCacheService.updateGroupRefresh(groupId, diffRoles, diffGroupActionPermissions, diffGroupFieldPermissions);
        }
        if (diffRowPermissions != null) {
            authGroupRefreshCacheService.updateRowPermissions(diffRoles, diffRowPermissions);
        }

        AuthGroupSystemPermissionProxy result = new AuthGroupSystemPermissionProxy();
        result.setId(data.getId());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Function
    @Override
    public Boolean delete(Long id) {
        Integer effectRow = Models.origin().deleteByWrapper(Pops.<AuthGroup>lambdaQuery()
                .from(AuthGroup.MODEL_MODEL)
                .eq(AuthGroup::getId, id));
        if (effectRow != 1) {
            return Boolean.FALSE;
        }

        deleteAllPermissions(id);

        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    @Function
    @Override
    public Boolean active(Long groupId) {
        Integer effectRow = Models.origin().updateByWrapper(new AuthGroup().setActive(Boolean.TRUE), Pops.<AuthGroup>lambdaUpdate()
                .from(AuthGroup.MODEL_MODEL)
                .eq(AuthGroup::getId, groupId)
                .eq(AuthGroup::getActive, Boolean.FALSE));
        if (effectRow == 1) {
            authorizeRefreshCache(groupId);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean isActivated(Long groupId) {
        return Models.origin().count(Pops.<AuthGroup>lambdaUpdate()
                .from(AuthGroup.MODEL_MODEL)
                .eq(AuthGroup::getId, groupId)
                .eq(AuthGroup::getActive, Boolean.TRUE)).compareTo(1L) == 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Function
    @Override
    public Boolean disable(Long groupId) {
        Integer effectRow = Models.origin().updateByWrapper(new AuthGroup().setActive(Boolean.FALSE), Pops.<AuthGroup>lambdaUpdate()
                .from(AuthGroup.MODEL_MODEL)
                .eq(AuthGroup::getId, groupId)
                .eq(AuthGroup::getActive, Boolean.TRUE));
        if (effectRow == 1) {
            revokeRefreshCache(groupId);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Transactional(rollbackFor = Exception.class)
    @Function
    @Override
    public List<AuthRole> modifyRoles(Long groupId, Set<Long> roleIds) {
        DiffList<AuthRole> diffRoles = authGroupDataDiffService.saveRoles(groupId, roleIds, true);
        if (diffRoles == null) {
            return null;
        }

        if (isActivated(groupId)) {
            Optional.ofNullable(diffRoles.getCreate())
                    .filter(CollectionUtils::isNotEmpty)
                    .ifPresent(roles -> authorizeRefreshCache(groupId, roles));

            Optional.ofNullable(diffRoles.getDelete())
                    .filter(CollectionUtils::isNotEmpty)
                    .ifPresent(roles -> revokeRefreshCache(groupId, roles));
        }

        return diffRoles.getAll();
    }

    private AuthResourcePermission generatorResourcePermission(AuthGroupSystemPermissionProxy data) {
        return authResourceNodeOperator.verificationResourceNode(data,
                (module) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorModuleResourceCode(module));
                    data.setResourceDisplayName(Optional.ofNullable(module.getDisplayName()).filter(StringUtils::isNotBlank).orElse(data.getResourceCode()));
                    return authResourceNodeOperator.createOrUpdateModulePermission(data, module);
                },
                (module, action) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorHomepageResourceCode(module, action));
                    data.setResourceDisplayName(Optional.ofNullable(module.getDisplayName())
                            .filter(StringUtils::isNotBlank)
                            .map(v -> v + CharacterConstants.SEPARATOR_BLANK + CharacterConstants.SEPARATOR_HYPHEN + CharacterConstants.SEPARATOR_BLANK + ViewActionConstants.homepage.displayName)
                            .orElse(data.getResourceCode()));
                    return authResourceNodeOperator.createOrUpdateHomepagePermission(data, module, action);
                },
                (menu) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorMenuResourceCode(menu));
                    data.setResourceDisplayName(Optional.ofNullable(menu.getDisplayName())
                            .filter(StringUtils::isNotBlank)
                            .orElse(data.getResourceCode()));
                    return authResourceNodeOperator.createOrUpdateMenuPermission(data, menu);
                },
                (serverAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorServerActionResourceCode(serverAction));
                    data.setResourceDisplayName(Optional.ofNullable(serverAction.getDisplayName())
                            .filter(StringUtils::isNotBlank)
                            .orElse(data.getResourceCode()));
                    return authResourceNodeOperator.createOrUpdateServerActionPermission(data, serverAction);
                },
                (viewAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorViewActionResourceCode(viewAction));
                    data.setResourceDisplayName(Optional.ofNullable(viewAction.getDisplayName())
                            .filter(StringUtils::isNotBlank)
                            .orElse(data.getResourceCode()));
                    return authResourceNodeOperator.createOrUpdateViewActionPermission(data, viewAction);
                },
                (urlAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorUrlActionResourceCode(urlAction));
                    data.setResourceDisplayName(Optional.ofNullable(urlAction.getDisplayName())
                            .filter(StringUtils::isNotBlank)
                            .orElse(data.getResourceCode()));
                    return authResourceNodeOperator.createOrUpdateUrlActionPermission(data, urlAction);
                },
                (clientAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorClientActionResourceCode(clientAction));
                    data.setResourceDisplayName(Optional.ofNullable(clientAction.getDisplayName())
                            .filter(StringUtils::isNotBlank)
                            .orElse(data.getResourceCode()));
                    return authResourceNodeOperator.createOrUpdateClientActionPermission(data, clientAction);
                });
    }

    private <T extends AuthGroup> void verificationAndSet(T data, T origin) {
        if (origin != null) {
            data.setId(origin.getId());
            data.setName(origin.getName());
            data.setType(origin.getType());
            data.setSource(origin.getSource());
            data.setActive(origin.getActive());
        } else {
            AuthorizationSourceEnum source = data.getSource();
            if (source == null) {
                source = AuthorizationSourceEnum.MANUAL;
                data.setSource(source);
            }
            if (AuthorizationSourceEnum.SYSTEM.equals(source)) {
                if (StringUtils.isBlank(data.getName())) {
                    throw PamirsException.construct(AuthExpEnumerate.AUTH_GROUP_NAME_NULL_ERROR).errThrow();
                }
            } else {
                String name = generatorAuthGroupName();
                data.setName(name);
            }

            VerificationHelper.setDefaultValue(data, AuthGroup::getType, AuthGroup::setType, AuthGroupTypeEnum.RUNTIME);
            VerificationHelper.setDefaultValue(data, AuthGroup::getActive, AuthGroup::setActive, Boolean.TRUE);
        }
    }

    private <T extends AuthGroup> T queryOne(T data) {
        T origin = FetchUtil.fetchOne(data);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_GROUP_ERROR).errThrow();
        }
        return origin;
    }

    private void createGroupMenuRel(AuthGroupSystemPermissionProxy data, Long permissionId) {
        AuthGroupRelResource groupRelResource = new AuthGroupRelResource();
        groupRelResource.setGroupId(data.getId());
        groupRelResource.setGroupName(data.getName());
        groupRelResource.setResourceCode(data.getResourceCode());
        groupRelResource.setPermissionId(permissionId);
        groupRelResource.setNodeType(AuthEnumerationHelper.getNodeType(data.getNodeType()));
        groupRelResource.setGroupType(data.getType());
        Models.origin().createOne(groupRelResource);
    }

    private String generatorAuthGroupName() {
        String name = UUIDUtil.getUUIDNumberString();
        while (Models.origin().count(Pops.<AuthGroup>lambdaQuery()
                .from(AuthGroup.MODEL_MODEL)
                .eq(AuthGroup::getName, name)).compareTo(1L) >= 0) {
            name = UUIDUtil.getUUIDNumberString();
        }
        return name;
    }

    private AuthRowPermissionItem getSystemPermissionRowPermission(AuthGroupSystemPermissionProxy data) {
        AuthRowPermissionItem rowPermission = data.getRowPermission();
        if (rowPermission == null) {
            return null;
        }
        // 使用系统权限组名称作为行权限项的编码，保证一个系统权限组对应唯一的行权限项
        rowPermission.setCode(AuthRowPermission.generatorCode(data.getName()));
        rowPermission.setDisplayName(String.format("通过【%s】自动生成的数据权限项", data.getResourceDisplayName()));
        return rowPermission;
    }

    private void verifyGroupDisplayName(AuthGroup data, AuthGroup origin) {
        String displayName = data.getDisplayName();
        if (origin != null && origin.getDisplayName().equals(displayName)) {
            return;
        }
        if (StringUtils.isBlank(displayName)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_GROUP_NAME_NULL_ERROR).errThrow();
        }
        if (displayName.contains(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_GROUP_NAME_ILLEGAL_ERROR).errThrow();
        }
        if (displayName.length() > 128) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_GROUP_NAME_TOO_LONG_ERROR).errThrow();
        }
        LambdaQueryWrapper<AuthGroup> wrapper = Pops.<AuthGroup>lambdaQuery()
                .from(AuthGroup.MODEL_MODEL)
                .eq(AuthGroup::getDisplayName, displayName)
                .ne(AuthGroup::getSource, AuthorizationSourceEnum.SYSTEM);
        if (data instanceof AuthGroupSystemPermissionProxy) {
            AuthGroupSystemPermissionProxy systemPermissionGroup = (AuthGroupSystemPermissionProxy) data;
            LambdaQueryWrapper<AuthGroupRelResource> groupRelResourceWrapper = Pops.<AuthGroupRelResource>lambdaQuery()
                    .from(AuthGroupRelResource.MODEL_MODEL)
                    .select(AuthGroupRelResource::getGroupId)
                    .eq(AuthGroupRelResource::getResourceCode, systemPermissionGroup.getResourceCode())
                    .eq(AuthGroupRelResource::getNodeType, systemPermissionGroup.getNodeType())
                    .eq(AuthGroupRelResource::getGroupType, AuthGroupTypeEnum.RUNTIME.value());
            if (origin != null) {
                groupRelResourceWrapper.ne(AuthGroupRelResource::getGroupId, origin.getId());
            }
            List<AuthGroupRelResource> groupRelResource = Models.origin().queryListByWrapper(groupRelResourceWrapper);
            if (CollectionUtils.isEmpty(groupRelResource)) {
                return;
            }
            if (CollectionUtils.isNotEmpty(groupRelResource)) {
                wrapper.in(AuthGroup::getId, groupRelResource.stream().map(AuthGroupRelResource::getGroupId).collect(Collectors.toSet()));
            }
        } else if (origin != null) {
            wrapper.ne(AuthGroup::getId, origin.getId());
        }
        if (Models.origin().count(wrapper).compareTo(1L) >= 0) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_GROUP_NAME_EXISTS_ERROR).errThrow();
        }
    }

    private void deleteAllPermissions(Long groupId) {
        revokeRefreshCache(groupId);

        Models.origin().deleteByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                .from(AuthGroupRole.MODEL_MODEL)
                .eq(AuthGroupRole::getGroupId, groupId));
        Models.origin().deleteByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                .from(AuthGroupRelResource.MODEL_MODEL)
                .eq(AuthGroupRelResource::getGroupId, groupId));

        Models.origin().deleteByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
                .from(AuthGroupResourcePermission.MODEL_MODEL)
                .eq(AuthGroupResourcePermission::getGroupId, groupId));
        Models.origin().deleteByWrapper(Pops.<AuthGroupFieldPermission>lambdaQuery()
                .from(AuthGroupFieldPermission.MODEL_MODEL)
                .eq(AuthGroupFieldPermission::getGroupId, groupId));
        Models.origin().deleteByWrapper(Pops.<AuthGroupRowPermission>lambdaQuery()
                .from(AuthGroupRowPermission.MODEL_MODEL)
                .eq(AuthGroupRowPermission::getGroupId, groupId));
    }

    private void authorizeRefreshCache(Long groupId) {
        List<AuthRole> roles = authGroupDataOperator.fetchRoles(groupId);
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        authorizeRefreshCache(groupId, roles);
    }

    private void authorizeRefreshCache(Long groupId, List<AuthRole> roles) {
        consumerGroupPermissions(groupId, (resourcePermissions, fieldPermissions, rowPermissions) -> {
            authGroupAuthorizeService.authorizeRolePermissions(roles, resourcePermissions, fieldPermissions, rowPermissions);
        });
    }

    private void revokeRefreshCache(Long groupId) {
        List<AuthRole> roles = authGroupDataOperator.fetchRoles(groupId);
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        revokeRefreshCache(groupId, roles);
    }

    private void revokeRefreshCache(Long groupId, List<AuthRole> roles) {
        AuthGroupRevokeContext context = new AuthGroupRevokeContext(groupId, roles);
        Set<Long> otherGroupIds = context.getOtherActiveGroupIds();
        if (otherGroupIds.isEmpty()) {
            revokeRefreshCacheAll(groupId, roles);
            return;
        }
        Map<Long, List<AuthGroupResourcePermission>> resourcePermissionsMap = fetchRevokeResourcePermissions(context);
        if (resourcePermissionsMap == null) {
            resourcePermissionsMap = new HashMap<>();
        }
        Map<Long, List<AuthGroupResourcePermission>> actionPermissionsMap = fetchRevokeActionPermissions(context);
        if (actionPermissionsMap == null) {
            actionPermissionsMap = new HashMap<>();
        }
        for (Map.Entry<Long, List<AuthGroupResourcePermission>> entry : actionPermissionsMap.entrySet()) {
            resourcePermissionsMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
        }
        Map<Long, List<AuthGroupFieldPermission>> fieldPermissionsMap = fetchRevokeFieldPermissions(context);
        Map<Long, List<AuthGroupRowPermission>> rowPermissionsMap = fetchRevokeRowPermissions(context);

        authGroupDataOperator.fillPermissions(
                CollectionHelper.flat(actionPermissionsMap),
                CollectionHelper.flat(fieldPermissionsMap),
                CollectionHelper.flat(rowPermissionsMap)
        );

        Set<Long> roleIds = context.getRoleIds();
        for (Long roleId : roleIds) {
            List<AuthGroupResourcePermission> resourcePermissions = resourcePermissionsMap.get(roleId);
            List<AuthGroupFieldPermission> fieldPermissions = fieldPermissionsMap.get(roleId);
            List<AuthGroupRowPermission> rowPermissions = rowPermissionsMap.get(roleId);
            if (CollectionUtils.isNotEmpty(resourcePermissions) ||
                    CollectionUtils.isNotEmpty(fieldPermissions) ||
                    CollectionUtils.isNotEmpty(rowPermissions)) {
                authGroupAuthorizeService.revokeRolePermissions(Sets.newHashSet(roleId), resourcePermissions, fieldPermissions, rowPermissions);
            }
        }
    }

    private void revokeRefreshCacheAll(Long groupId, List<AuthRole> roles) {
        consumerGroupPermissions(groupId, (resourcePermissions, fieldPermissions, rowPermissions) -> {
            authGroupAuthorizeService.revokeRolePermissions(roles, resourcePermissions, fieldPermissions, rowPermissions);
        });
    }

    private List<AuthGroupResourcePermission> fetchResourcePermissions(Long groupId) {
        List<AuthGroupRelResource> groupRelResources = Models.origin().queryListByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                .from(AuthGroupRelResource.MODEL_MODEL)
                .eq(AuthGroupRelResource::getGroupId, groupId));
        if (CollectionUtils.isEmpty(groupRelResources)) {
            return null;
        }
        return generatorGroupResourceAuthorizations(groupId, groupRelResources);
    }

    private Map<Long, List<AuthGroupResourcePermission>> fetchRevokeResourcePermissions(AuthGroupRevokeContext context) {
        Long groupId = context.getGroupId();
        Set<Long> roleIds = context.getRoleIds();
        List<AuthGroupRelResource> currentGroupRelResources = context.getCurrentGroupRelResources();
        if (CollectionUtils.isEmpty(currentGroupRelResources)) {
            return null;
        }
        List<AuthGroupRelResource> otherGroupRelResources = context.getOtherGroupRelResources();
        if (CollectionUtils.isEmpty(otherGroupRelResources)) {
            return generatorRolePermissionMap(generatorGroupResourceAuthorizations(groupId, currentGroupRelResources, context), roleIds);
        }
        Map<Long, Map<Long, Long>> revokeValueMap = AuthGroupAuthorizationComputeHelper.computeChangedValueMap(context, context.getAllGroupRelResources());
        if (revokeValueMap.isEmpty()) {
            return null;
        }
        Set<Long> permissionIds = new HashSet<>();
        for (Map<Long, Long> revokeValueEntry : revokeValueMap.values()) {
            permissionIds.addAll(revokeValueEntry.keySet());
        }
        List<AuthResourcePermission> resourcePermissions = context.queryResourcePermissions(permissionIds);
        if (CollectionUtils.isEmpty(resourcePermissions)) {
            return null;
        }
        MemoryListSearchCache<Long, AuthResourcePermission> resourcePermissionCache = new MemoryListSearchCache<>(resourcePermissions, AuthResourcePermission::getId);
        Map<Long, List<AuthGroupResourcePermission>> rolePermissions = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Long>> revokeValueEntry : revokeValueMap.entrySet()) {
            Long roleId = revokeValueEntry.getKey();
            List<AuthGroupResourcePermission> permissions = rolePermissions.computeIfAbsent(roleId, k -> new ArrayList<>());
            for (Map.Entry<Long, Long> revokeValues : revokeValueEntry.getValue().entrySet()) {
                Long permissionId = revokeValues.getKey();
                AuthResourcePermission resourcePermission = resourcePermissionCache.get(permissionId);
                if (resourcePermission == null) {
                    continue;
                }
                Long authorizedValue = revokeValues.getValue();
                permissions.add(generatorGroupResourcePermission(groupId, resourcePermission, authorizedValue));
            }
        }
        return rolePermissions;
    }

    private List<AuthGroupResourcePermission> generatorGroupResourceAuthorizations(Long groupId, List<AuthGroupRelResource> groupRelResources) {
        return generatorGroupResourceAuthorizations(groupId, groupRelResources, null);
    }

    private List<AuthGroupResourcePermission> generatorGroupResourceAuthorizations(Long groupId, List<AuthGroupRelResource> groupRelResources, AuthGroupRevokeContext revokeContext) {
        Set<Long> permissionIds = groupRelResources.stream().map(AuthGroupRelResource::getPermissionId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(permissionIds)) {
            return null;
        }
        List<AuthResourcePermission> resourcePermissions;
        if (revokeContext == null) {
            resourcePermissions = authResourcePermissionService.queryListByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                    .from(AuthResourcePermission.MODEL_MODEL)
                    .in(AuthResourcePermission::getId, permissionIds));
            if (CollectionUtils.isEmpty(resourcePermissions)) {
                return null;
            }
        } else {
            resourcePermissions = revokeContext.queryResourcePermissions(permissionIds);
        }
        MemoryListSearchCache<Long, AuthResourcePermission> resourcePermissionCache = new MemoryListSearchCache<>(resourcePermissions, AuthResourcePermission::getId);
        List<AuthGroupResourcePermission> permissions = new ArrayList<>();
        for (AuthGroupRelResource authGroupMenuRel : groupRelResources) {
            Long permissionId = authGroupMenuRel.getPermissionId();
            if (permissionId == null) {
                continue;
            }
            AuthResourcePermission resourcePermission = resourcePermissionCache.get(permissionId);
            if (resourcePermission == null) {
                continue;
            }
            Long authorizedValue = authGroupMenuRel.getAuthorizedValue();
            if (authorizedValue == null) {
                continue;
            }
            permissions.add(generatorGroupResourcePermission(groupId, resourcePermission, authorizedValue));
        }
        return permissions;
    }

    private <T> Map<Long, List<T>> generatorRolePermissionMap(List<T> resourcePermissions, Set<Long> roleIds) {
        Map<Long, List<T>> result = new HashMap<>(roleIds.size());
        for (Long roleId : roleIds) {
            result.put(roleId, resourcePermissions);
        }
        return result;
    }

    private AuthGroupResourcePermission generatorGroupResourcePermission(Long groupId, AuthResourcePermission resourcePermission, Long authorizedValue) {
        AuthGroupResourcePermission permission = new AuthGroupResourcePermission();
        permission.setGroupId(groupId);
        permission.setPermissionId(resourcePermission.getId());
        permission.setPermissionCode(resourcePermission.getCode());
        permission.setPermissionType(resourcePermission.getType());
        permission.setPermissionSubtype(resourcePermission.getSubtype());
        permission.setPermission(resourcePermission);
        permission.setAuthorizedValue(authorizedValue);
        return permission;
    }

    private Map<Long, List<AuthGroupResourcePermission>> fetchRevokeActionPermissions(AuthGroupRevokeContext context) {
        Long groupId = context.getGroupId();
        Set<Long> roleIds = context.getRoleIds();
        Set<Long> otherGroupIds = context.getOtherGroupIds();
        List<AuthGroupResourcePermission> actionPermissions = authGroupDataOperator.fetchActionPermissions(groupId);
        if (CollectionUtils.isEmpty(actionPermissions)) {
            return Collections.emptyMap();
        }
        Set<Long> permissionIds = actionPermissions.stream().map(AuthGroupResourcePermission::getPermissionId).collect(Collectors.toSet());
        List<AuthGroupResourcePermission> otherActionPermissions = Models.origin().queryListByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
                .from(AuthGroupResourcePermission.MODEL_MODEL)
                .eq(AuthGroupResourcePermission::getPermissionType, ResourcePermissionTypeEnum.ACTION)
                .in(AuthGroupResourcePermission::getGroupId, otherGroupIds)
                .in(AuthGroupResourcePermission::getPermissionId, permissionIds));
        if (CollectionUtils.isEmpty(otherActionPermissions)) {
            return generatorRolePermissionMap(actionPermissions, roleIds);
        }
        otherActionPermissions.addAll(actionPermissions);
        Map<Long, Map<Long, Long>> revokeValueMap = AuthGroupAuthorizationComputeHelper.computeChangedValueMap(context, otherActionPermissions);
        if (revokeValueMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return AuthGroupAuthorizationComputeHelper.filterAndComputeChangedResourcePermissions(actionPermissions, revokeValueMap);
    }

    private Map<Long, List<AuthGroupFieldPermission>> fetchRevokeFieldPermissions(AuthGroupRevokeContext context) {
        Long groupId = context.getGroupId();
        Set<Long> roleIds = context.getRoleIds();
        Set<Long> otherGroupIds = context.getOtherGroupIds();
        List<AuthGroupFieldPermission> fieldPermissions = authGroupDataOperator.fetchFieldPermissions(groupId);
        if (CollectionUtils.isEmpty(fieldPermissions)) {
            return Collections.emptyMap();
        }
        Set<Long> permissionIds = fieldPermissions.stream().map(AuthGroupFieldPermission::getPermissionId).collect(Collectors.toSet());
        List<AuthGroupFieldPermission> otherFieldPermissions = Models.origin().queryListByWrapper(Pops.<AuthGroupFieldPermission>lambdaQuery()
                .from(AuthGroupFieldPermission.MODEL_MODEL)
                .in(AuthGroupFieldPermission::getGroupId, otherGroupIds)
                .in(AuthGroupFieldPermission::getPermissionId, permissionIds));
        if (CollectionUtils.isEmpty(otherFieldPermissions)) {
            return generatorRolePermissionMap(fieldPermissions, roleIds);
        }
        otherFieldPermissions.addAll(fieldPermissions);
        Map<Long, Map<Long, Long>> revokeValueMap = AuthGroupAuthorizationComputeHelper.computeChangedValueMap(context, otherFieldPermissions);
        if (revokeValueMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return AuthGroupAuthorizationComputeHelper.filterAndComputeChangedFieldPermissions(fieldPermissions, revokeValueMap);
    }

    private Map<Long, List<AuthGroupRowPermission>> fetchRevokeRowPermissions(AuthGroupRevokeContext context) {
        Long groupId = context.getGroupId();
        Set<Long> roleIds = context.getRoleIds();
        Set<Long> otherGroupIds = context.getOtherGroupIds();
        List<AuthGroupRowPermission> rowPermissions = authGroupDataOperator.fetchRowPermissions(groupId);
        if (CollectionUtils.isEmpty(rowPermissions)) {
            return Collections.emptyMap();
        }
        Set<Long> permissionIds = rowPermissions.stream().map(AuthGroupRowPermission::getPermissionId).collect(Collectors.toSet());
        List<AuthGroupRowPermission> otherRowPermissions = Models.origin().queryListByWrapper(Pops.<AuthGroupRowPermission>lambdaQuery()
                .from(AuthGroupRowPermission.MODEL_MODEL)
                .in(AuthGroupRowPermission::getGroupId, otherGroupIds)
                .in(AuthGroupRowPermission::getPermissionId, permissionIds));
        if (CollectionUtils.isEmpty(otherRowPermissions)) {
            return generatorRolePermissionMap(rowPermissions, roleIds);
        }
        otherRowPermissions.addAll(rowPermissions);
        Map<Long, Map<Long, Long>> revokeValueMap = AuthGroupAuthorizationComputeHelper.computeChangedValueMap(context, otherRowPermissions);
        if (revokeValueMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return AuthGroupAuthorizationComputeHelper.filterAndComputeChangedRowPermissions(rowPermissions, revokeValueMap);
    }

    private void consumerGroupPermissions(Long groupId, GroupPermissionsConsumer consumer) {
        List<AuthGroupResourcePermission> resourcePermissions = fetchResourcePermissions(groupId);
        if (resourcePermissions == null) {
            resourcePermissions = new ArrayList<>();
        }
        List<AuthGroupResourcePermission> actionPermissions = authGroupDataOperator.fetchActionPermissions(groupId);
        if (actionPermissions == null) {
            actionPermissions = new ArrayList<>();
        }
        resourcePermissions.addAll(actionPermissions);

        List<AuthGroupFieldPermission> fieldPermissions = authGroupDataOperator.fetchFieldPermissions(groupId);
        List<AuthGroupRowPermission> rowPermissions = authGroupDataOperator.fetchRowPermissions(groupId);

        authGroupDataOperator.fillPermissions(actionPermissions, fieldPermissions, rowPermissions);

        consumer.accept(resourcePermissions, fieldPermissions, rowPermissions);
    }

    @FunctionalInterface
    private interface GroupPermissionsConsumer {

        void accept(List<AuthGroupResourcePermission> resourcePermissions,
                    List<AuthGroupFieldPermission> fieldPermissions,
                    List<AuthGroupRowPermission> rowPermissions);
    }
}
