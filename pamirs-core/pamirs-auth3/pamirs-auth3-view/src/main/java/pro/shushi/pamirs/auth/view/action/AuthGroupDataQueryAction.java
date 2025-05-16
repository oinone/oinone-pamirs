package pro.shushi.pamirs.auth.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.core.common.api.EditionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthFieldPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthRowPermissionService;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleFieldPermissionService;
import pro.shushi.pamirs.auth.view.model.AuthFieldPermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthRowPermissionItem;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupSystemPermissionProxy;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限组数据查询动作
 *
 * @author Adamancy Zhang at 19:54 on 2024-01-16
 */
@Component
@Model.model(AuthGroupSystemPermissionProxy.MODEL_MODEL)
public class AuthGroupDataQueryAction {

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Autowired
    private AuthFieldPermissionService authFieldPermissionService;

    @Autowired
    private AuthRoleFieldPermissionService authRoleFieldPermissionService;

    @Autowired
    private AuthRowPermissionService authRowPermissionService;

    @Autowired
    private EditionService editionService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "查询权限组数据")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public AuthGroupSystemPermissionProxy fetchData(AuthGroupSystemPermissionProxy data) {
        String model = data.getModel();
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_GROUP_MODEL_NULL_ERROR).errThrow();
        }
        boolean hasFieldPermission = true;
        boolean hasRowPermission = true;
        if (PermissionMateDataEnum.MENU.equals(data.getNodeType())) {
            boolean isViewAction = ActionTypeEnum.VIEW.equals(Optional.ofNullable(data.getResourceId())
                    .map(id -> Models.origin().queryOneByWrapper(Pops.<Menu>lambdaQuery()
                            .from(Menu.MODEL_MODEL)
                            .eq(Menu::getId, id)))
                    .map(Menu::getActionType)
                    .orElse(null));
            hasFieldPermission = isViewAction;
            hasRowPermission = isViewAction;
        }
        AuthGroup authGroup = null;
        Long groupId = data.getId();
        if (groupId != null) {
            AuthGroup origin = Models.origin().queryOneByWrapper(Pops.<AuthGroup>lambdaQuery()
                    .from(AuthGroup.MODEL_MODEL)
                    .eq(AuthGroup::getId, groupId));
            if (origin == null) {
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_GROUP_ERROR).errThrow();
            }
            authGroup = origin;
        }
        boolean isEnterpriseEdition = editionService.checkEdition();
        if (hasFieldPermission && isEnterpriseEdition) {
            data.setFieldPermissions(fetchFieldPermissions(model, authGroup));
        } else {
            data.setFieldPermissions(null);
        }
        if (hasRowPermission && isEnterpriseEdition) {
            data.setRowPermission(fetchRowPermission(model, authGroup));
        } else {
            data.setRowPermission(null);
        }
        return data;
    }

    private List<AuthFieldPermissionItem> fetchFieldPermissions(String model, AuthGroup group) {
        Map<String, Long> fieldPermissions = null;
        if (group != null) {
            List<AuthGroupFieldPermission> permissions = fetchGroupFieldPermissions(model, group.getId());
            if (CollectionUtils.isNotEmpty(permissions)) {
                fieldPermissions = new HashMap<>();
                for (AuthGroupFieldPermission permission : permissions) {
                    String field = permission.getPermission().getField();
                    fieldPermissions.compute(field, (key, value) -> {
                        if (value == null) {
                            value = 0L;
                        }
                        return value | permission.getAuthorizedValue();
                    });
                }
            }
        }
        List<ModelField> modelFields = Models.origin().queryListByWrapper(Pops.<ModelField>lambdaQuery()
                .from(ModelField.MODEL_MODEL)
                .eq(ModelField::getModel, model));
        if (CollectionUtils.isEmpty(modelFields)) {
            return new ArrayList<>();
        }
        List<AuthFieldPermissionItem> nodes = new ArrayList<>(modelFields.size());
        if (permissionNodeLoader.isLoadAllField()) {
            nodes.add(buildAllFieldNode(model, fieldPermissions));
        }
        for (ModelField modelField : modelFields) {
            if (Boolean.TRUE.equals(modelField.getPk())) {
                continue;
            }
            nodes.add(convertFieldNode(model, modelField, fieldPermissions));
        }
        return nodes;
    }

    private AuthFieldPermissionItem buildAllFieldNode(String model, Map<String, Long> fieldPermissions) {
        ModelField allField = new ModelField();
        allField.setId(AuthConstants.ALL_FLAG_LONG);
        allField.setField(AuthConstants.ALL_FLAG_STRING);
        allField.setDisplayName(AuthConstants.ALL_FLAG_DISPLAY_NAME);
        allField.setDescription(AuthConstants.ALL_FLAG_FIELD_DESCRIPTION);
        return convertFieldNode(model, allField, fieldPermissions);
    }

    private List<AuthGroupFieldPermission> fetchGroupFieldPermissions(String model, Long groupId) {
        List<AuthFieldPermission> fieldPermissions = authFieldPermissionService.queryListByWrapper(Pops.<AuthFieldPermission>lambdaQuery()
                .from(AuthFieldPermission.MODEL_MODEL)
                .setBatchSize(-1)
                .eq(AuthFieldPermission::getModel, model));
        if (CollectionUtils.isEmpty(fieldPermissions)) {
            return null;
        }
        List<AuthGroupFieldPermission> groupFieldPermissions = Models.origin().queryListByWrapper(Pops.<AuthGroupFieldPermission>lambdaQuery()
                .from(AuthGroupFieldPermission.MODEL_MODEL)
                .in(AuthGroupFieldPermission::getPermissionId, fieldPermissions.stream().map(AuthFieldPermission::getId).collect(Collectors.toSet()))
                .eq(AuthGroupFieldPermission::getGroupId, groupId));
        if (CollectionUtils.isEmpty(groupFieldPermissions)) {
            return null;
        }
        MemoryListSearchCache<Long, AuthFieldPermission> fieldPermissionCache = new MemoryListSearchCache<>(fieldPermissions, AuthFieldPermission::getId);
        for (AuthGroupFieldPermission groupFieldPermission : groupFieldPermissions) {
            AuthFieldPermission fieldPermission = fieldPermissionCache.get(groupFieldPermission.getPermissionId());
            if (fieldPermission != null) {
                groupFieldPermission.setPermission(fieldPermission);
            }
        }
        return groupFieldPermissions;
    }

    private AuthFieldPermissionItem convertFieldNode(String model, ModelField modelField, Map<String, Long> fieldPermissions) {
        AuthFieldPermissionItem node = new AuthFieldPermissionItem();
        String field = modelField.getField();
        node.setPath(ResourcePath.PATH_SPLIT + field);
        node.setModel(model);
        node.setField(field);
        node.setSource(AuthorizationSourceEnum.SYSTEM);
        node.setActive(Boolean.TRUE);
        node.setResourceId(modelField.getId());
        node.setDisplayValue(modelField.getDisplayName());
        node.setTtype(modelField.getTtype());
        node.setDescription(modelField.getDescription());
        if (fieldPermissions == null) {
            node.setPermRead(Boolean.TRUE);
            node.setPermWrite(Boolean.TRUE);
        } else {
            Long authorizedValue = fieldPermissions.get(field);
            if (authorizedValue == null) {
                node.setPermRead(Boolean.FALSE);
                node.setPermWrite(Boolean.FALSE);
            } else {
                node.setPermRead(FieldAuthorizedValueEnum.readable(authorizedValue));
                node.setPermWrite(FieldAuthorizedValueEnum.writable(authorizedValue));
            }
        }
        return node;
    }

    private AuthRowPermissionItem fetchRowPermission(String model, AuthGroup group) {
        AuthGroupRowPermission groupRowPermission = null;
        if (group != null) {
            groupRowPermission = fetchGroupRowPermission(group);
        }
        AuthRowPermissionItem rowPermission = new AuthRowPermissionItem();
        rowPermission.setModel(model);
        rowPermission.setPath(ResourcePath.PATH_SPLIT + model);
        rowPermission.setSource(AuthorizationSourceEnum.SYSTEM);
        rowPermission.setActive(Boolean.TRUE);
        if (groupRowPermission == null) {
            rowPermission.setPermRead(Boolean.TRUE);
            rowPermission.setPermWrite(Boolean.FALSE);
            rowPermission.setPermDelete(Boolean.FALSE);
        } else {
            AuthRowPermission permission = groupRowPermission.getPermission();
            String filter = permission.getFilter();
            rowPermission.setFilter(filter);
            rowPermission.setDomainExp(filter);
            rowPermission.setDomainExpDisplayName(permission.getDomainExpDisplayName());
            rowPermission.setDomainExpJson(permission.getDomainExpJson());
            Long authorizedValue = groupRowPermission.getAuthorizedValue();
            rowPermission.setPermRead(RowAuthorizedValueEnum.readable(authorizedValue));
//            rowPermission.setPermWrite(RowAuthorizedValueEnum.writable(authorizedValue));
//            rowPermission.setPermDelete(RowAuthorizedValueEnum.deletable(authorizedValue));
            rowPermission.setPermWrite(Boolean.FALSE);
            rowPermission.setPermDelete(Boolean.FALSE);
        }
        return rowPermission;
    }

    private AuthGroupRowPermission fetchGroupRowPermission(AuthGroup group) {
        Long groupId = group.getId();
        String code = AuthRowPermission.generatorCode(group.getName());
        AuthRowPermission rowPermission = authRowPermissionService.queryOneByWrapper(Pops.<AuthRowPermission>lambdaQuery()
                .from(AuthRowPermission.MODEL_MODEL)
                .eq(AuthRowPermission::getCode, code));
        if (rowPermission == null) {
            return null;
        }
        AuthGroupRowPermission groupRowPermission = Models.origin().queryOneByWrapper(Pops.<AuthGroupRowPermission>lambdaQuery()
                .from(AuthGroupRowPermission.MODEL_MODEL)
                .eq(AuthGroupRowPermission::getGroupId, groupId)
                .eq(AuthGroupRowPermission::getPermissionId, rowPermission.getId()));
        if (groupRowPermission == null) {
            return null;
        }
        groupRowPermission.setPermission(rowPermission);
        return groupRowPermission;
    }
}
