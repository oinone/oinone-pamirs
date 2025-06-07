package pro.shushi.pamirs.auth.core.service.group;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.helper.AuthEnumerationHelper;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeLoader;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRelResource;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataDiffService;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataOperator;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupManager;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限组管理
 *
 * @author Adamancy Zhang at 20:42 on 2024-06-20
 */
@Slf4j
@Component
@Fun(AuthGroupManager.FUN_NAMESPACE)
public class AuthGroupManagerImpl implements AuthGroupManager {

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Autowired
    private AuthGroupDataOperator authGroupDataOperator;

    @Autowired
    private AuthGroupDataDiffService authGroupDataDiffService;

    @Autowired
    private MetaCacheManager metaCacheManager;

    @Transactional(rollbackFor = Throwable.class)
    @Function
    @Override
    public AuthGroup createResourcePermissionGroup(AuthGroup group, AuthGroupRelResource groupRelResource, AuthResourceAuthorization selected) {
        group = Models.origin().createOne(group);
        Long groupId = group.getId();
        groupRelResource.setGroupId(groupId);
        Models.origin().createOne(groupRelResource);
        if (selected != null && AuthGroupTypeEnum.RUNTIME.equals(group.getType())) {
            selected.setGroupId(groupId);
            createGroupResourcePermissions(selected);
        }
        return group;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Function
    @Override
    public Boolean createGroupActionPermissions(List<AuthResourceAuthorization> resourceAuthorizations) {
        for (AuthResourceAuthorization resourceAuthorization : resourceAuthorizations) {
            createGroupResourcePermissions(resourceAuthorization);
        }
        return Boolean.TRUE;
    }

    private void createGroupResourcePermissions(AuthResourceAuthorization selected) {
        Long resourceId = selected.getResourceId();
        ResourcePermissionSubtypeEnum nodeType = selected.getSubtype();
        Long groupId = selected.getGroupId();
        String path = selected.getPath();
        if (StringUtils.isBlank(path) || resourceId == null || nodeType == null || groupId == null) {
            log.error("Create group resource permission error. path: {}, resourceId: {}, nodeType: {}, groupId: {}", path, resourceId, nodeType, groupId);
            return;
        }
        if (ResourcePermissionSubtypeEnum.MODULE.equals(nodeType)) {
            return;
        }
        ResourcePermissionNodeLoader loader = permissionNodeLoader.getManagementLoader();
        PermissionNode selectedNode = new PermissionNode();
        selectedNode.setId(resourceId.toString());
        selectedNode.setGroupId(groupId);
        selectedNode.setNodeType(nodeType);
        selectedNode.setResourceId(resourceId);
        selectedNode.setPath(path);
        List<PermissionNode> nodes = loader.buildNextPermissions(selectedNode);
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        List<AuthResourceAuthorization> resourceAuthorizations = collectionResourceAuthorizations(nodes, selected);
        authGroupDataDiffService.onlyCreateActionPermissions(groupId, resourceAuthorizations);
    }

    private List<AuthResourceAuthorization> collectionResourceAuthorizations(List<PermissionNode> nodes, AuthResourcePermission resourcePermission) {
        List<AuthResourceAuthorization> resourceAuthorizations = new ArrayList<>();
        for (PermissionNode node : nodes) {
            AuthResourceAuthorization resourceAuthorization = generatorResourceAuthorization(node, resourcePermission);
            if (resourceAuthorization != null) {
                resourceAuthorizations.add(resourceAuthorization);
            }
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                resourceAuthorizations.addAll(collectionResourceAuthorizations(children, resourcePermission));
            }
        }
        return resourceAuthorizations;
    }

    private AuthResourceAuthorization generatorResourceAuthorization(PermissionNode node, AuthResourcePermission resourcePermission) {
        if (node instanceof ActionPermissionNode) {
            return generatorActionPermissionNode((ActionPermissionNode) node, resourcePermission);
        }
        return null;
    }

    private AuthResourceAuthorization generatorActionPermissionNode(ActionPermissionNode node, AuthResourcePermission resourcePermission) {
        ActionTypeEnum actionType = node.getActionType();
        ResourcePermissionSubtypeEnum subtype = AuthEnumerationHelper.getActionResourceSubtype(actionType);
        if (subtype == null) {
            log.error("Create group resource permission error. model: {}, name: {}, actionType: {}", node.getModel(), node.getAction(), actionType);
            return null;
        }
        ResourcePermissionTypeEnum type = AuthEnumerationHelper.getResourceType(subtype);
        if (type == null) {
            return null;
        }
        AuthResourceAuthorization resourceAuthorization = new AuthResourceAuthorization();
        resourceAuthorization.setPath(node.getPath());
        resourceAuthorization.setModule(resourcePermission.getModule());
        resourceAuthorization.setModel(node.getModel());
        resourceAuthorization.setName(node.getAction());
        resourceAuthorization.setType(type);
        resourceAuthorization.setSubtype(subtype);
        resourceAuthorization.setSource(resourcePermission.getSource());
        resourceAuthorization.setActive(resourcePermission.getActive());
        resourceAuthorization.setResourceId(node.getResourceId());
        resourceAuthorization.setAuthorizedValue(ResourceAuthorizedValueEnum.ACCESS.value());
        return resourceAuthorization;
    }
}
