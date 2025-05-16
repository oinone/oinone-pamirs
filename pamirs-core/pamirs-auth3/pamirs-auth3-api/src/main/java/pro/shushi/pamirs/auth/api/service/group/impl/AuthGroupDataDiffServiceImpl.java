package pro.shushi.pamirs.auth.api.service.group.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.helper.AuthEnumerationHelper;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataDiffService;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataOperator;
import pro.shushi.pamirs.auth.api.service.permission.AuthFieldPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthRowPermissionService;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.EncryptHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.core.common.diff.DiffList;
import pro.shushi.pamirs.core.common.directive.DirectiveHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限组差量保存服务实现
 *
 * @author Adamancy Zhang at 09:56 on 2024-01-19
 */
@Service
public class AuthGroupDataDiffServiceImpl implements AuthGroupDataDiffService {

    private static final int CREATE_DIRECTIVE = 1;

    private static final int UPDATE_DIRECTIVE = 2;

    private static final int DELETE_DIRECTIVE = 4;

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Autowired
    private AuthFieldPermissionService authFieldPermissionService;

    @Autowired
    private AuthRowPermissionService authRowPermissionService;

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthGroupDataOperator authGroupDataOperator;

    @Autowired
    private MetaCacheManager metaCacheManager;

    @Override
    public DiffList<AuthRole> saveRoles(Long groupId, Set<Long> roleIds, boolean isUpdate) {
        List<AuthRole> existRoles;
        if (isUpdate) {
            existRoles = authGroupDataOperator.fetchRoles(groupId);
        } else {
            existRoles = Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(roleIds) && CollectionUtils.isEmpty(existRoles)) {
            return null;
        }
        List<AuthRole> targetRoles;
        if (CollectionUtils.isEmpty(roleIds)) {
            targetRoles = Collections.emptyList();
        } else {
            targetRoles = authRoleService.fetchRoles(roleIds);
        }

        MemoryListSearchCache<Long, AuthRole> existRoleCache = new MemoryListSearchCache<>(existRoles, AuthRole::getId);
        List<AuthGroupRole> groupRoles = new ArrayList<>(targetRoles.size());
        List<AuthRole> allTargetRoles = new ArrayList<>(targetRoles.size());
        List<AuthRole> createTargetRoles = new ArrayList<>(4);
        List<AuthRole> updateTargetRoles = new ArrayList<>(8);
        List<AuthRole> deleteTargetRoles = new ArrayList<>(2);
        for (AuthRole targetRole : targetRoles) {
            Long roleId = targetRole.getId();
            AuthRole existRole = existRoleCache.compute(roleId, (k, v) -> v);
            allTargetRoles.add(targetRole);
            if (existRole == null) {
                AuthGroupRole groupRole = new AuthGroupRole();
                groupRole.setGroupId(groupId);
                groupRole.setRoleId(roleId);
                groupRoles.add(groupRole);
                createTargetRoles.add(targetRole);
            } else {
                updateTargetRoles.add(existRole);
            }
        }
        existRoleCache.fill();
        deleteTargetRoles.addAll(existRoleCache.getNotComputedCache().values());

        if (!groupRoles.isEmpty()) {
            Models.origin().createBatch(groupRoles);
        }
        if (!deleteTargetRoles.isEmpty()) {
            deleteGroupRoles(groupId, deleteTargetRoles);
        }
        return DiffCollection.list(allTargetRoles, createTargetRoles, updateTargetRoles, deleteTargetRoles);
    }

    private void deleteGroupRoles(Long groupId, List<AuthRole> roles) {
        DataShardingHelper.build().collectionSharding(roles.stream().map(AuthRole::getId).collect(Collectors.toSet()), (sublist) -> {
            Models.origin().deleteByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                    .from(AuthGroupRole.MODEL_MODEL)
                    .eq(AuthGroupRole::getGroupId, groupId)
                    .in(AuthGroupRole::getRoleId, sublist));
            return null;
        });
    }

    @Override
    public DiffList<AuthGroupResourcePermission> saveActionPermissions(Long groupId, List<AuthResourceAuthorization> actionPermissions) {
        if (CollectionUtils.isEmpty(actionPermissions)) {
            return null;
        }

        List<AuthGroupResourcePermission> groupActionPermissions = fillResourcePermissions(groupId, actionPermissions);

        if (groupActionPermissions.isEmpty()) {
            return null;
        }

        return saveGroupActionPermissions(groupId, groupActionPermissions, CREATE_DIRECTIVE | UPDATE_DIRECTIVE | DELETE_DIRECTIVE);
    }

    @Override
    public DiffList<AuthGroupResourcePermission> onlyCreateActionPermissions(Long groupId, List<AuthResourceAuthorization> actionPermissions) {
        if (CollectionUtils.isEmpty(actionPermissions)) {
            return null;
        }

        List<AuthGroupResourcePermission> groupActionPermissions = fillResourcePermissions(groupId, actionPermissions);

        if (groupActionPermissions.isEmpty()) {
            return null;
        }

        return saveGroupActionPermissions(groupId, groupActionPermissions, CREATE_DIRECTIVE | DELETE_DIRECTIVE);
    }

    private Set<String> verificationAndSetActionPermissionCodes(List<AuthResourceAuthorization> resourceAuthorizations) {
        Set<String> codes = new HashSet<>(resourceAuthorizations.size());
        for (AuthResourceAuthorization resourceAuthorization : resourceAuthorizations) {
            boolean isAllAction = AuthConstants.ALL_FLAG_LONG.equals(resourceAuthorization.getId());
            String path = resourceAuthorization.getPath();
            if (StringUtils.isBlank(path)) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_RESOURCE_PERMISSION_PATH_IS_NULL_ERROR).errThrow();
            }
            if (isAllAction) {
                String code = EncryptHelper.shortCode(path);
                resourceAuthorization.setCode(code);
                codes.add(code);
            } else {
                String model = resourceAuthorization.getModel();
                String name = resourceAuthorization.getName();
                if (StringUtils.isBlank(model)) {
                    throw PamirsException.construct(AuthExpEnumerate.AUTH_RESOURCE_PERMISSION_MODEL_IS_NULL_ERROR).errThrow();
                }
                if (StringUtils.isBlank(name)) {
                    throw PamirsException.construct(AuthExpEnumerate.AUTH_RESOURCE_PERMISSION_NAME_IS_NULL_ERROR).errThrow();
                }
                Action action = metaCacheManager.fetchAction(model, name);
                if (action == null) {
                    throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
                }
                String code = AuthResourcePermission.generatorCode(model, name, path);
                ResourcePermissionSubtypeEnum subtype = AuthEnumerationHelper.getActionResourceSubtype(action.getActionType());
                if (subtype == null) {
                    throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
                }
                resourceAuthorization.setCode(code);
                resourceAuthorization.setSubtype(subtype);
                resourceAuthorization.setType(AuthEnumerationHelper.getResourceType(subtype));
                codes.add(code);
            }
        }
        return codes;
    }

    private List<AuthGroupResourcePermission> fillResourcePermissions(Long groupId, List<AuthResourceAuthorization> actionPermissions) {
        Set<String> codes = verificationAndSetActionPermissionCodes(actionPermissions);

        List<AuthResourcePermission> existActionPermissions = DataShardingHelper.build().collectionSharding(codes, (sublist) -> authResourcePermissionService.queryListByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                .from(AuthResourcePermission.MODEL_MODEL)
                .setBatchSize(-1)
                .eq(AuthResourcePermission::getType, ResourcePermissionTypeEnum.ACTION.value())
                .in(AuthResourcePermission::getCode, sublist)));

        MemoryListSearchCache<String, AuthResourcePermission> existActionPermissionCache = new MemoryListSearchCache<>(existActionPermissions, AuthResourcePermission::getCode);
        List<AuthResourcePermission> createActionPermissions = new ArrayList<>(16);
        List<AuthGroupResourcePermission> groupActionPermissions = new ArrayList<>(16);
        List<AuthGroupResourcePermission> lazyGroupActionPermissions = new ArrayList<>(16);

        for (AuthResourceAuthorization actionPermission : actionPermissions) {
            String code = actionPermission.getCode();
            AuthResourcePermission existActionPermission = existActionPermissionCache.get(code);

            AuthGroupResourcePermission groupActionPermission = new AuthGroupResourcePermission();
            groupActionPermission.setGroupId(groupId);
            groupActionPermission.setPermissionType(ResourcePermissionTypeEnum.ACTION);
            groupActionPermission.setPermissionSubtype(actionPermission.getSubtype());
            groupActionPermission.setAuthorizedValue(actionPermission.getAuthorizedValue());

            if (existActionPermission == null) {
                createActionPermissions.add(generatorResourcePermission(null, actionPermission));

                groupActionPermission.setPermissionCode(code);
                lazyGroupActionPermissions.add(groupActionPermission);
            } else {
                groupActionPermission.setPermissionId(existActionPermission.getId());
                groupActionPermission.setPermission(existActionPermission);
                groupActionPermissions.add(groupActionPermission);
            }
        }

        if (!createActionPermissions.isEmpty()) {
            createActionPermissions = authResourcePermissionService.createBatch(createActionPermissions);
            MemoryListSearchCache<String, AuthResourcePermission> createActionPermissionCache = new MemoryListSearchCache<>(createActionPermissions, AuthResourcePermission::getCode);
            for (AuthGroupResourcePermission groupActionPermission : lazyGroupActionPermissions) {
                String code = groupActionPermission.getPermissionCode();
                AuthResourcePermission actionPermission = createActionPermissionCache.get(code);
                if (actionPermission == null) {
                    throw new IllegalArgumentException("Invalid field permission. code = " + code);
                }
                groupActionPermission.setPermissionId(actionPermission.getId());
                groupActionPermission.setPermission(actionPermission);
                groupActionPermissions.add(groupActionPermission);
            }
        }
        return groupActionPermissions;
    }

    private AuthResourcePermission generatorResourcePermission(String module, AuthResourceAuthorization resourceAuthorization) {
        AuthResourcePermission actionPermission = new AuthResourcePermission();
        actionPermission.setCode(resourceAuthorization.getCode());
        actionPermission.setModule(module);
        actionPermission.setModel(resourceAuthorization.getModel());
        actionPermission.setName(Optional.ofNullable(resourceAuthorization.getName()).filter(StringUtils::isNotBlank).orElse(null));
        actionPermission.setPath(resourceAuthorization.getPath());
        actionPermission.setType(resourceAuthorization.getType());
        actionPermission.setSubtype(resourceAuthorization.getSubtype());
        actionPermission.setSource(AuthorizationSourceEnum.SYSTEM);
        actionPermission.setActive(Boolean.TRUE);
        return actionPermission;
    }

    private DiffList<AuthGroupResourcePermission> saveGroupActionPermissions(Long groupId, List<AuthGroupResourcePermission> groupActionPermissions, int directive) {
        List<AuthGroupResourcePermission> existGroupActionPermissions = authGroupDataOperator.fetchActionPermissions(groupId);
        MemoryListSearchCache<Long, AuthGroupResourcePermission> existGroupActionPermissionCache = new MemoryListSearchCache<>(existGroupActionPermissions, AuthGroupResourcePermission::getPermissionId);
        List<AuthGroupResourcePermission> createGroupActionPermissions = new ArrayList<>(groupActionPermissions.size());
        List<AuthGroupResourcePermission> updateGroupActionPermissions = new ArrayList<>(groupActionPermissions.size());
        for (AuthGroupResourcePermission groupActionPermission : groupActionPermissions) {
            AuthGroupResourcePermission existGroupActionPermission = existGroupActionPermissionCache.compute(groupActionPermission.getPermissionId(), (k, v) -> {
                v.setPermission(groupActionPermission.getPermission());
                return v;
            });
            if (existGroupActionPermission == null) {
                createGroupActionPermissions.add(groupActionPermission);
            } else if (!existGroupActionPermission.getAuthorizedValue().equals(groupActionPermission.getAuthorizedValue())) {
                updateGroupActionPermissions.add(groupActionPermission);
            }
        }
        existGroupActionPermissionCache.fill();
        List<AuthGroupResourcePermission> deleteGroupActionPermissions = new ArrayList<>(existGroupActionPermissionCache.getNotComputedCache().values());
        List<AuthGroupResourcePermission> allGroupActionPermissions = new ArrayList<>(existGroupActionPermissionCache.getComputedCache().values());

        if (DirectiveHelper.isEnabled(directive, CREATE_DIRECTIVE) && !createGroupActionPermissions.isEmpty()) {
            createGroupActionPermissions = authGroupDataOperator.createActionPermissions(createGroupActionPermissions);
            allGroupActionPermissions.addAll(createGroupActionPermissions);
        }
        if (DirectiveHelper.isEnabled(directive, UPDATE_DIRECTIVE) && !updateGroupActionPermissions.isEmpty()) {
            authGroupDataOperator.updateActionPermissions(updateGroupActionPermissions);
        }
        if (DirectiveHelper.isEnabled(directive, DELETE_DIRECTIVE) && !deleteGroupActionPermissions.isEmpty()) {
            authGroupDataOperator.deleteActionPermissionsByGroupId(groupId, deleteGroupActionPermissions);
        }
        return DiffCollection.list(allGroupActionPermissions, createGroupActionPermissions, updateGroupActionPermissions, deleteGroupActionPermissions);
    }

    @Override
    public DiffList<AuthGroupFieldPermission> saveFieldPermissions(Long groupId, List<AuthFieldAuthorization> fieldAuthorization) {
        if (CollectionUtils.isEmpty(fieldAuthorization)) {
            return null;
        }

        List<AuthGroupFieldPermission> groupFieldPermissions = fillFieldPermissions(groupId, fieldAuthorization);

        if (groupFieldPermissions.isEmpty()) {
            return null;
        }

        return saveGroupFieldPermissions(groupId, groupFieldPermissions);
    }

    private Set<String> verificationAndSetFieldPermissionCodes(List<AuthFieldAuthorization> fieldPermissionItems) {
        Set<String> codes = new HashSet<>();
        for (AuthFieldAuthorization fieldPermissionItem : fieldPermissionItems) {
            String model = fieldPermissionItem.getModel();
            String field = fieldPermissionItem.getField();
            if (StringUtils.isBlank(model)) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_FIELD_PERMISSION_MODEL_IS_NULL_ERROR).errThrow();
            }
            if (StringUtils.isBlank(field)) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_FIELD_PERMISSION_FIELD_IS_NULL_ERROR).errThrow();
            }
            String code = AuthFieldPermission.generatorCode(model, field);
            fieldPermissionItem.setCode(code);
            codes.add(code);
        }
        return codes;
    }

    private List<AuthGroupFieldPermission> fillFieldPermissions(Long groupId, List<AuthFieldAuthorization> fieldPermissions) {
        Set<String> codes = verificationAndSetFieldPermissionCodes(fieldPermissions);

        List<AuthFieldPermission> existFieldPermissions = authFieldPermissionService.queryListByWrapper(Pops.<AuthFieldPermission>lambdaQuery()
                .from(AuthFieldPermission.MODEL_MODEL)
                .setBatchSize(-1)
                .in(AuthFieldPermission::getCode, codes));

        MemoryListSearchCache<String, AuthFieldPermission> existFieldPermissionCache = new MemoryListSearchCache<>(existFieldPermissions, AuthFieldPermission::getCode);
        List<AuthFieldPermission> createFieldPermissions = new ArrayList<>(8);
        List<AuthGroupFieldPermission> groupFieldPermissions = new ArrayList<>(32);
        List<AuthGroupFieldPermission> lazyGroupFieldPermissions = new ArrayList<>(8);

        for (AuthFieldAuthorization fieldPermissionItem : fieldPermissions) {
            String code = fieldPermissionItem.getCode();

            AuthGroupFieldPermission groupFieldPermission = new AuthGroupFieldPermission();
            groupFieldPermission.setGroupId(groupId);
            groupFieldPermission.setPermissionCode(code);
            groupFieldPermission.setAuthorizedValue(fieldPermissionItem.getAuthorizedValue());

            AuthFieldPermission existFieldPermission = existFieldPermissionCache.get(code);
            if (existFieldPermission == null) {
                createFieldPermissions.add(generatorFieldPermission(fieldPermissionItem));

                lazyGroupFieldPermissions.add(groupFieldPermission);
            } else {
                groupFieldPermission.setPermissionId(existFieldPermission.getId());
                groupFieldPermission.setPermission(existFieldPermission);
                groupFieldPermissions.add(groupFieldPermission);
            }
        }

        if (!createFieldPermissions.isEmpty()) {
            createFieldPermissions = authFieldPermissionService.createBatch(createFieldPermissions);
            MemoryListSearchCache<String, AuthFieldPermission> createFieldPermissionCache = new MemoryListSearchCache<>(createFieldPermissions, AuthFieldPermission::getCode);
            for (AuthGroupFieldPermission lazyGroupFieldPermission : lazyGroupFieldPermissions) {
                String code = lazyGroupFieldPermission.getPermissionCode();
                AuthFieldPermission fieldPermission = createFieldPermissionCache.get(code);
                lazyGroupFieldPermission.setPermissionId(fieldPermission.getId());
                lazyGroupFieldPermission.setPermission(fieldPermission);
                groupFieldPermissions.add(lazyGroupFieldPermission);
            }
        }
        return groupFieldPermissions;
    }

    private AuthFieldPermission generatorFieldPermission(AuthFieldAuthorization fieldPermissionItem) {
        AuthFieldPermission fieldPermission = new AuthFieldPermission();
        fieldPermission.setCode(fieldPermissionItem.getCode());
        fieldPermission.setModel(fieldPermissionItem.getModel());
        fieldPermission.setField(fieldPermissionItem.getField());
        fieldPermission.setSource(AuthorizationSourceEnum.SYSTEM);
        fieldPermission.setActive(Boolean.TRUE);
        return fieldPermission;
    }

    private DiffList<AuthGroupFieldPermission> saveGroupFieldPermissions(Long groupId, List<AuthGroupFieldPermission> groupFieldPermissions) {
        List<AuthGroupFieldPermission> existGroupFieldPermissions = authGroupDataOperator.fetchFieldPermissions(groupId);
        MemoryListSearchCache<Long, AuthGroupFieldPermission> existGroupFieldPermissionCache = new MemoryListSearchCache<>(existGroupFieldPermissions, AuthGroupFieldPermission::getPermissionId);
        List<AuthGroupFieldPermission> createGroupFieldPermissions = new ArrayList<>(groupFieldPermissions.size());
        List<AuthGroupFieldPermission> updateGroupFieldPermissions = new ArrayList<>(groupFieldPermissions.size());
        for (AuthGroupFieldPermission groupFieldPermission : groupFieldPermissions) {
            AuthGroupFieldPermission existGroupFieldPermission = existGroupFieldPermissionCache.compute(groupFieldPermission.getPermissionId(), (k, v) -> v);
            if (existGroupFieldPermission == null) {
                createGroupFieldPermissions.add(groupFieldPermission);
            } else if (!existGroupFieldPermission.getAuthorizedValue().equals(groupFieldPermission.getAuthorizedValue())) {
                updateGroupFieldPermissions.add(groupFieldPermission);
            }
        }
        existGroupFieldPermissionCache.fill();
        List<AuthGroupFieldPermission> deleteGroupFieldPermissions = new ArrayList<>(existGroupFieldPermissionCache.getNotComputedCache().values());
        List<AuthGroupFieldPermission> allGroupFieldPermissions = new ArrayList<>(existGroupFieldPermissionCache.getComputedCache().values());

        if (!createGroupFieldPermissions.isEmpty()) {
            createGroupFieldPermissions = authGroupDataOperator.createFieldPermissions(createGroupFieldPermissions);
            allGroupFieldPermissions.addAll(createGroupFieldPermissions);
        }
        if (!updateGroupFieldPermissions.isEmpty()) {
            authGroupDataOperator.updateFieldPermissions(updateGroupFieldPermissions);
        }
        if (!deleteGroupFieldPermissions.isEmpty()) {
            authGroupDataOperator.deleteFieldPermissionsByGroupId(groupId, deleteGroupFieldPermissions);
        }
        return DiffCollection.list(allGroupFieldPermissions, createGroupFieldPermissions, updateGroupFieldPermissions, deleteGroupFieldPermissions);
    }

    @Override
    public DiffList<AuthGroupRowPermission> saveRowPermissionBySystemPermission(Long groupId, AuthRowAuthorization rowAuthorization) {
        if (rowAuthorization == null) {
            return null;
        }

        String model = rowAuthorization.getModel();
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROW_PERMISSION_MODEL_IS_NULL_ERROR).errThrow();
        }

        String code = rowAuthorization.getCode();
        if (StringUtils.isBlank(code)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_ROW_PERMISSION_CODE_IS_NULL_ERROR).errThrow();
        }

        AuthRowPermission existRowPermission = authRowPermissionService.queryOneByWrapper(Pops.<AuthRowPermission>lambdaQuery()
                .from(AuthRowPermission.MODEL_MODEL)
                .eq(AuthRowPermission::getCode, code));

        boolean isNeedDelete = false;
        boolean isNeedUpdate = false;
        String filter = rowAuthorization.getFilter();
        AuthRowPermission rowPermission;
        if (existRowPermission == null) {
            rowPermission = authRowPermissionService.create(generatorRowPermission(rowAuthorization, filter));
        } else {
            AuthRowPermission updateRowPermission = generatorRowPermission(rowAuthorization, filter);
            updateRowPermission.setId(existRowPermission.getId());
            rowPermission = authRowPermissionService.update(updateRowPermission);
            if (!StringHelper.equals(model, existRowPermission.getModel()) || !StringHelper.equals(filter, existRowPermission.getFilter())) {
                isNeedUpdate = true;
                isNeedDelete = true;
            }
        }

        if (rowPermission == null) {
            throw new IllegalArgumentException("Invalid row permission.");
        }

        Long rowPermissionId = rowPermission.getId();
        AuthRowAuthorization newRowAuthorization = AuthRowAuthorization.transfer(rowPermission, new AuthRowAuthorization());
        newRowAuthorization.setAuthorizedValue(rowAuthorization.getAuthorizedValue());

        List<AuthGroupRowPermission> existGroupRowPermissions = authGroupDataOperator.fetchRowPermissions(groupId);

        MemoryListSearchCache<Long, AuthGroupRowPermission> existGroupRowPermissionCache = new MemoryListSearchCache<>(existGroupRowPermissions, AuthGroupRowPermission::getPermissionId);

        AuthGroupRowPermission existGroupRowPermission = existGroupRowPermissionCache.compute(rowPermissionId, (k, v) -> v);

        List<AuthGroupRowPermission> refreshCreateGroupRowPermissions = new ArrayList<>(2);
        List<AuthGroupRowPermission> refreshDeleteGroupRowPermissions = new ArrayList<>(2);

        Long newAuthorizedValue = newRowAuthorization.getAuthorizedValue();
        AuthGroupRowPermission groupRowPermission = new AuthGroupRowPermission();
        groupRowPermission.setGroupId(groupId);
        groupRowPermission.setPermissionId(rowPermissionId);
        groupRowPermission.setPermissionCode(code);
        groupRowPermission.setPermission(rowPermission);
        groupRowPermission.setAuthorizedValue(newAuthorizedValue);
        if (existGroupRowPermission == null) {
            groupRowPermission = Models.origin().createOne(groupRowPermission);
            List<AuthGroupRowPermission> allGroupRowPermissions = Lists.newArrayList(groupRowPermission);
            return DiffCollection.list(allGroupRowPermissions, allGroupRowPermissions, null, null);
        } else {
            Long oldAuthorizedValue = existGroupRowPermission.getAuthorizedValue();
            if (oldAuthorizedValue > newAuthorizedValue) {
                Long updateAuthorizedValue = oldAuthorizedValue & ~newAuthorizedValue;
                refreshDeleteGroupRowPermissions.add(generatorGroupRowPermission(newRowAuthorization, groupId, updateAuthorizedValue));
                Models.origin().updateByPk(groupRowPermission);
                isNeedUpdate = true;
            } else if (newAuthorizedValue > oldAuthorizedValue) {
                Long updateAuthorizedValue = newAuthorizedValue & ~oldAuthorizedValue;
                refreshCreateGroupRowPermissions.add(generatorGroupRowPermission(newRowAuthorization, groupId, updateAuthorizedValue));
                Models.origin().updateByPk(groupRowPermission);
                isNeedUpdate = true;
            }
        }

        if (!isNeedUpdate) {
            return null;
        }

        List<AuthGroupRowPermission> allGroupRowPermissions = Lists.newArrayList(groupRowPermission);
        if (isNeedDelete) {
            AuthGroupRowPermission deleteGroupRowPermission = new AuthGroupRowPermission();
            deleteGroupRowPermission.setGroupId(groupId);
            deleteGroupRowPermission.setPermissionId(rowPermissionId);
            deleteGroupRowPermission.setPermissionCode(code);
            deleteGroupRowPermission.setPermission(existRowPermission);
            deleteGroupRowPermission.setAuthorizedValue(existGroupRowPermission.getAuthorizedValue());
            return DiffCollection.list(allGroupRowPermissions, allGroupRowPermissions, null, Lists.newArrayList(deleteGroupRowPermission));
        }
        return DiffCollection.list(allGroupRowPermissions, refreshCreateGroupRowPermissions, null, refreshDeleteGroupRowPermissions);
    }

    private AuthRowPermission generatorRowPermission(AuthRowAuthorization rowPermissionItem, String filter) {
        AuthRowPermission rowPermission = new AuthRowPermission();
        rowPermission.setCode(rowPermissionItem.getCode());
        rowPermission.setDisplayName(rowPermissionItem.getDisplayName());
        rowPermission.setModel(rowPermissionItem.getModel());
        if (StringUtils.isBlank(filter)) {
            rowPermission.setFilter(null);
        } else {
            rowPermission.setFilter(filter);
        }
        rowPermission.setDomainExpDisplayName(rowPermissionItem.getDomainExpDisplayName());
        rowPermission.setDomainExpJson(rowPermissionItem.getDomainExpJson());
        rowPermission.setSource(AuthorizationSourceEnum.SYSTEM);
        rowPermission.setActive(Boolean.TRUE);
        return rowPermission;
    }

    @Override
    public DiffList<AuthGroupRowPermission> saveRowPermissionsByDataPermission(Long groupId, List<AuthRowAuthorization> rowPermissions) {
        if (CollectionUtils.isEmpty(rowPermissions)) {
            return null;
        }

        rowPermissions = checkExistRowAuthorizations(rowPermissions);

        List<AuthGroupRowPermission> allGroupRowPermissions = new ArrayList<>(2);
        List<AuthGroupRowPermission> createGroupRowPermissions = new ArrayList<>(2);
        List<AuthGroupRowPermission> updateGroupRowPermissions = new ArrayList<>(2);
        List<AuthGroupRowPermission> deleteGroupRowPermissions = new ArrayList<>(2);

        List<AuthGroupRowPermission> refreshCreateGroupRowPermissions = new ArrayList<>(2);
        List<AuthGroupRowPermission> refreshDeleteGroupRowPermissions = new ArrayList<>(2);

        List<AuthGroupRowPermission> existGroupRowPermissions = authGroupDataOperator.fetchRowPermissions(groupId);
        authGroupDataOperator.fillPermissions(null, null, existGroupRowPermissions);
        MemoryListSearchCache<Long, AuthGroupRowPermission> existGroupRowPermissionCache = new MemoryListSearchCache<>(existGroupRowPermissions, AuthGroupRowPermission::getPermissionId);
        for (AuthRowAuthorization rowPermission : rowPermissions) {
            Long permissionId = rowPermission.getId();
            AuthGroupRowPermission existGroupRowPermission = existGroupRowPermissionCache.compute(permissionId, (k, v) -> v);
            Long newAuthorizedValue = rowPermission.getAuthorizedValue();
            AuthGroupRowPermission groupRowPermission = new AuthGroupRowPermission();
            groupRowPermission.setGroupId(groupId);
            groupRowPermission.setPermissionId(permissionId);
            groupRowPermission.setPermissionCode(rowPermission.getCode());
            groupRowPermission.setPermission(rowPermission);
            groupRowPermission.setAuthorizedValue(newAuthorizedValue);
            allGroupRowPermissions.add(groupRowPermission);
            if (existGroupRowPermission == null) {
                createGroupRowPermissions.add(groupRowPermission);
                refreshCreateGroupRowPermissions.add(groupRowPermission);
            } else {
                Long oldAuthorizedValue = existGroupRowPermission.getAuthorizedValue();
                if (oldAuthorizedValue > newAuthorizedValue) {
                    Long updateAuthorizedValue = oldAuthorizedValue & ~newAuthorizedValue;
                    updateGroupRowPermissions.add(groupRowPermission);
                    refreshDeleteGroupRowPermissions.add(generatorGroupRowPermission(rowPermission, groupId, updateAuthorizedValue));
                } else if (newAuthorizedValue > oldAuthorizedValue) {
                    Long updateAuthorizedValue = newAuthorizedValue & ~oldAuthorizedValue;
                    updateGroupRowPermissions.add(groupRowPermission);
                    refreshCreateGroupRowPermissions.add(generatorGroupRowPermission(rowPermission, groupId, updateAuthorizedValue));
                }
            }
        }
        existGroupRowPermissionCache.fill();
        deleteGroupRowPermissions.addAll(existGroupRowPermissionCache.getNotComputedCache().values());

        if (!createGroupRowPermissions.isEmpty()) {
            Models.origin().createBatch(createGroupRowPermissions);
        }
        if (!updateGroupRowPermissions.isEmpty()) {
            Models.origin().updateBatch(updateGroupRowPermissions);
        }
        if (!deleteGroupRowPermissions.isEmpty()) {
            Set<Long> permissionIds = new HashSet<>(deleteGroupRowPermissions.size());
            for (AuthGroupRowPermission deleteGroupRowPermission : deleteGroupRowPermissions) {
                permissionIds.add(deleteGroupRowPermission.getPermissionId());
                refreshDeleteGroupRowPermissions.add(deleteGroupRowPermission);
            }
            DataShardingHelper.build().collectionSharding(permissionIds, (sublist) -> {
                Models.origin().deleteByWrapper(Pops.<AuthGroupRowPermission>lambdaQuery()
                        .from(AuthGroupRowPermission.MODEL_MODEL)
                        .eq(AuthGroupRowPermission::getGroupId, groupId)
                        .in(AuthGroupRowPermission::getPermissionId, sublist));
                return null;
            });
        }

        return DiffList.list(allGroupRowPermissions, refreshCreateGroupRowPermissions, null, refreshDeleteGroupRowPermissions);
    }

    private List<AuthRowAuthorization> checkExistRowAuthorizations(List<AuthRowAuthorization> rowPermissions) {
        Map<String, AuthRowAuthorization> permissionMap = generatorPermissionMap(rowPermissions);
        List<AuthRowAuthorization> existRowPermissions = DataShardingHelper.build().collectionSharding(permissionMap.keySet(), sublist -> Models.origin().queryListByWrapper(Pops.<AuthRowAuthorization>lambdaQuery()
                .from(AuthRowAuthorization.MODEL_MODEL)
                .in(AuthRowAuthorization::getCode, sublist)));
        for (AuthRowAuthorization existRowPermission : existRowPermissions) {
            AuthRowAuthorization rowPermission = permissionMap.remove(existRowPermission.getCode());
            if (rowPermission == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_PERMISSION_NOT_EXIST_ERROR).errThrow();
            }
            existRowPermission.setAuthorizedValue(rowPermission.getAuthorizedValue());
        }
        if (!permissionMap.isEmpty()) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_PERMISSION_NOT_EXIST_ERROR).errThrow();
        }
        return existRowPermissions;
    }

    private Map<String, AuthRowAuthorization> generatorPermissionMap(List<AuthRowAuthorization> rowPermissions) {
        int initialCapacity = rowPermissions.size();
        Map<String, AuthRowAuthorization> permissionMap = new HashMap<>(initialCapacity);
        for (AuthRowAuthorization permission : rowPermissions) {
            String permissionCode = permission.getCode();
            if (StringUtils.isBlank(permissionCode)) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_ROW_PERMISSION_CODE_IS_NULL_ERROR).errThrow();
            }
            if (StringUtils.isBlank(permission.getModel())) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_ROW_PERMISSION_MODEL_IS_NULL_ERROR).errThrow();
            }
            permissionMap.put(permissionCode, permission);
        }
        return permissionMap;
    }

    private AuthGroupRowPermission generatorGroupRowPermission(AuthRowAuthorization rowPermission, Long groupId, Long authorizedValue) {
        AuthGroupRowPermission updateGroupRowPermission = new AuthGroupRowPermission();
        updateGroupRowPermission.setGroupId(groupId);
        updateGroupRowPermission.setPermissionId(rowPermission.getId());
        updateGroupRowPermission.setPermissionCode(rowPermission.getCode());
        updateGroupRowPermission.setPermission(rowPermission);
        updateGroupRowPermission.setAuthorizedValue(authorizedValue);
        return updateGroupRowPermission;
    }
}
