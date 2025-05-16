package pro.shushi.pamirs.auth.view.action;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.AuthEnumerationHelper;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRelResource;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupManager;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.api.utils.AuthResourceCodeGenerator;
import pro.shushi.pamirs.auth.view.manager.AuthResourceNodeOperator;
import pro.shushi.pamirs.auth.view.tmodel.AuthResourcePermissionGroups;
import pro.shushi.pamirs.auth.view.utils.AuthGroupGenerator;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 资源权限节点对应权限组动作
 *
 * @author Adamancy Zhang at 19:56 on 2024-01-15
 */
@Component
@Model.model(AuthResourcePermissionGroups.MODEL_MODEL)
public class AuthResourcePermissionGroupsAction {

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Autowired
    private AuthResourceNodeOperator authResourceNodeOperator;

    @Autowired
    private AuthGroupManager authGroupManager;

    @Transactional(rollbackFor = Exception.class)
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "查询资源权限节点对应权限组")
    @Function(openLevel = FunctionOpenEnum.API)
    public AuthResourcePermissionGroups queryGroups(AuthResourcePermissionGroups data) {
        return authResourceNodeOperator.verificationResourceNode(data,
                (module) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorModuleResourceCode(module));
                    queryAndCreateModuleGroups(data, module);
                    return data;
                },
                (module, action) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorHomepageResourceCode(module, action));
                    queryAndCreateHomepageGroups(data, module, action);
                    return data;
                },
                (menu) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorMenuResourceCode(menu));
                    queryAndCreateMenuGroups(data, menu);
                    return data;
                },
                (serverAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorServerActionResourceCode(serverAction));
                    queryAndCreateServerActionGroups(data, serverAction);
                    return data;
                },
                (viewAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorViewActionResourceCode(viewAction));
                    queryAndCreateViewActionGroups(data, viewAction);
                    return data;
                },
                (urlAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorUrlActionResourceCode(urlAction));
                    queryAndCreateUrlActionGroups(data, urlAction);
                    return data;
                },
                (clientAction) -> {
                    data.setResourceCode(AuthResourceCodeGenerator.generatorClientActionResourceCode(clientAction));
                    queryAndCreateClientActionGroups(data, clientAction);
                    return data;
                });
    }

    private void queryAndCreateModuleGroups(AuthResourcePermissionGroups data, UeModule module) {
        consumerGroupRelResource(data, (runtimeGroupRelResources, managementGroupRelResources) -> {
            AuthResourcePermission resourcePermission = null;
            if (runtimeGroupRelResources.isEmpty()) {
                resourcePermission = authResourceNodeOperator.createOrUpdateModulePermission(data, module);

                data.setRuntimeGroups(Lists.newArrayList(createModuleGroup(data, AuthGroupTypeEnum.RUNTIME, module, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, runtimeGroupRelResources, AuthResourcePermissionGroups::setRuntimeGroups);
            }
            if (managementGroupRelResources.isEmpty()) {
                if (resourcePermission == null) {
                    resourcePermission = authResourceNodeOperator.createOrUpdateModulePermission(data, module);
                }

                data.setManagementGroups(Lists.newArrayList(createModuleGroup(data, AuthGroupTypeEnum.MANAGEMENT, module, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, managementGroupRelResources, AuthResourcePermissionGroups::setManagementGroups);
            }
        });
    }

    private AuthGroup createModuleGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type, UeModule module, Long permissionId) {
        AuthGroup group = AuthGroupGenerator.buildModuleAuthGroup(module.getModule(), type);
        AuthGroupRelResource groupRelResource = new AuthGroupRelResource();
        groupRelResource.setGroupName(group.getName());
        groupRelResource.setResourceCode(data.getResourceCode());
        groupRelResource.setPermissionId(permissionId);
        groupRelResource.setNodeType(ResourcePermissionSubtypeEnum.MODULE);
        groupRelResource.setGroupType(type);
        return authGroupManager.createResourcePermissionGroup(group, groupRelResource, null);
    }

    private void queryAndCreateHomepageGroups(AuthResourcePermissionGroups data, UeModule module, ViewAction action) {
        consumerGroupRelResource(data, (runtimeGroupMenuRels, managementGroupMenuRels) -> {
            AuthResourcePermission resourcePermission = null;
            if (runtimeGroupMenuRels.isEmpty()) {
                resourcePermission = authResourceNodeOperator.createOrUpdateHomepagePermission(data, module, action);

                data.setRuntimeGroups(Lists.newArrayList(createHomepageGroup(data, AuthGroupTypeEnum.RUNTIME, module, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, runtimeGroupMenuRels, AuthResourcePermissionGroups::setRuntimeGroups);
            }
            if (managementGroupMenuRels.isEmpty()) {
                if (resourcePermission == null) {
                    resourcePermission = authResourceNodeOperator.createOrUpdateHomepagePermission(data, module, action);
                }

                data.setManagementGroups(Lists.newArrayList(createHomepageGroup(data, AuthGroupTypeEnum.MANAGEMENT, module, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, managementGroupMenuRels, AuthResourcePermissionGroups::setManagementGroups);
            }
        });
    }

    private AuthGroup createHomepageGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type, UeModule module, Long permissionId) {

        AuthGroup group = AuthGroupGenerator.buildHomepageAuthGroup(module.getModule(), type);
        AuthGroupRelResource groupRelResource = new AuthGroupRelResource();
        groupRelResource.setGroupName(group.getName());
        groupRelResource.setResourceCode(data.getResourceCode());
        groupRelResource.setPermissionId(permissionId);
        groupRelResource.setNodeType(ResourcePermissionSubtypeEnum.HOMEPAGE);
        groupRelResource.setGroupType(type);

        AuthResourceAuthorization selected = new AuthResourceAuthorization();
        selected.setModule(module.getModule());
        selected.setPath(data.getPath());
        selected.setSource(AuthorizationSourceEnum.SYSTEM);
        selected.setActive(Boolean.TRUE);
        selected.setResourceId(data.getResourceId());

        return authGroupManager.createResourcePermissionGroup(group, groupRelResource, selected);
    }

    private void queryAndCreateMenuGroups(AuthResourcePermissionGroups data, Menu menu) {
        consumerGroupRelResource(data, (runtimeGroupMenuRels, managementGroupMenuRels) -> {
            if (runtimeGroupMenuRels.isEmpty()) {
                data.setRuntimeGroups(new ArrayList<>());
            } else {
                queryAndSetRuntimeGroups(data, runtimeGroupMenuRels, AuthResourcePermissionGroups::setRuntimeGroups);
            }
            if (managementGroupMenuRels.isEmpty()) {
                AuthResourcePermission resourcePermission = authResourceNodeOperator.createOrUpdateMenuPermission(data, menu);

                data.setManagementGroups(Lists.newArrayList(createMenuGroup(data, AuthGroupTypeEnum.MANAGEMENT, menu, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, managementGroupMenuRels, AuthResourcePermissionGroups::setManagementGroups);
            }
        });
    }

    private AuthGroup createMenuGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type, Menu menu, Long permissionId) {
        AuthGroup group = AuthGroupGenerator.buildMenuAuthGroup(menu.getModule(), menu.getName(), type);
        AuthGroupRelResource groupRelResource = new AuthGroupRelResource();
        groupRelResource.setGroupName(group.getName());
        groupRelResource.setResourceCode(data.getResourceCode());
        groupRelResource.setPermissionId(permissionId);
        groupRelResource.setNodeType(ResourcePermissionSubtypeEnum.MENU);
        groupRelResource.setGroupType(type);
        return authGroupManager.createResourcePermissionGroup(group, groupRelResource, null);
    }

    private void queryAndCreateServerActionGroups(AuthResourcePermissionGroups data, ServerAction action) {
        consumerGroupRelResource(data, (runtimeGroupMenuRels, managementGroupMenuRels) -> {
            if (runtimeGroupMenuRels.isEmpty()) {
                data.setRuntimeGroups(new ArrayList<>());
            } else {
                queryAndSetRuntimeGroups(data, runtimeGroupMenuRels, AuthResourcePermissionGroups::setRuntimeGroups);
            }
            if (managementGroupMenuRels.isEmpty()) {
                AuthResourcePermission resourcePermission = authResourceNodeOperator.createOrUpdateServerActionPermission(data, action);

                data.setManagementGroups(Lists.newArrayList(createServerActionGroup(data, AuthGroupTypeEnum.MANAGEMENT, action, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, managementGroupMenuRels, AuthResourcePermissionGroups::setManagementGroups);
            }
        });
    }

    private AuthGroup createServerActionGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type, ServerAction action, Long permissionId) {
        return createActionGroup(data, type, action, permissionId, (authGroupMenuRel) -> authGroupMenuRel.setNodeType(ResourcePermissionSubtypeEnum.SERVER_ACTION));
    }

    private void queryAndCreateViewActionGroups(AuthResourcePermissionGroups data, ViewAction action) {
        consumerGroupRelResource(data, (runtimeGroupMenuRels, managementGroupMenuRels) -> {
            if (runtimeGroupMenuRels.isEmpty()) {
                data.setRuntimeGroups(new ArrayList<>());
            } else {
                queryAndSetRuntimeGroups(data, runtimeGroupMenuRels, AuthResourcePermissionGroups::setRuntimeGroups);
            }
            if (managementGroupMenuRels.isEmpty()) {
                AuthResourcePermission resourcePermission = authResourceNodeOperator.createOrUpdateViewActionPermission(data, action);

                data.setManagementGroups(Lists.newArrayList(createViewActionGroup(data, AuthGroupTypeEnum.MANAGEMENT, action, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, managementGroupMenuRels, AuthResourcePermissionGroups::setManagementGroups);
            }
        });
    }

    private AuthGroup createViewActionGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type, ViewAction action, Long permissionId) {
        return createActionGroup(data, type, action, permissionId, (authGroupMenuRel) -> authGroupMenuRel.setNodeType(ResourcePermissionSubtypeEnum.VIEW_ACTION));
    }

    private void queryAndCreateUrlActionGroups(AuthResourcePermissionGroups data, UrlAction action) {
        consumerGroupRelResource(data, (runtimeGroupMenuRels, managementGroupMenuRels) -> {
            if (runtimeGroupMenuRels.isEmpty()) {
                data.setRuntimeGroups(new ArrayList<>());
            } else {
                queryAndSetRuntimeGroups(data, runtimeGroupMenuRels, AuthResourcePermissionGroups::setRuntimeGroups);
            }
            if (managementGroupMenuRels.isEmpty()) {
                AuthResourcePermission resourcePermission = authResourceNodeOperator.createOrUpdateUrlActionPermission(data, action);

                data.setManagementGroups(Lists.newArrayList(createUrlActionGroup(data, AuthGroupTypeEnum.MANAGEMENT, action, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, managementGroupMenuRels, AuthResourcePermissionGroups::setManagementGroups);
            }
        });
    }

    private AuthGroup createUrlActionGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type, UrlAction action, Long permissionId) {
        return createActionGroup(data, type, action, permissionId, (authGroupMenuRel) -> authGroupMenuRel.setNodeType(ResourcePermissionSubtypeEnum.URL_ACTION));
    }

    private void queryAndCreateClientActionGroups(AuthResourcePermissionGroups data, ClientAction action) {
        consumerGroupRelResource(data, (runtimeGroupMenuRels, managementGroupMenuRels) -> {
            if (runtimeGroupMenuRels.isEmpty()) {
                data.setRuntimeGroups(new ArrayList<>());
            } else {
                queryAndSetRuntimeGroups(data, runtimeGroupMenuRels, AuthResourcePermissionGroups::setRuntimeGroups);
            }
            if (managementGroupMenuRels.isEmpty()) {
                AuthResourcePermission resourcePermission = authResourceNodeOperator.createOrUpdateClientActionPermission(data, action);

                data.setManagementGroups(Lists.newArrayList(createClientActionGroup(data, AuthGroupTypeEnum.MANAGEMENT, action, resourcePermission.getId())));
            } else {
                queryAndSetRuntimeGroups(data, managementGroupMenuRels, AuthResourcePermissionGroups::setManagementGroups);
            }
        });
    }

    private AuthGroup createClientActionGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type, ClientAction action, Long permissionId) {
        return createActionGroup(data, type, action, permissionId, (authGroupMenuRel) -> authGroupMenuRel.setNodeType(ResourcePermissionSubtypeEnum.CLIENT_ACTION));
    }

    private AuthGroup createActionGroup(AuthResourcePermissionGroups data, AuthGroupTypeEnum type,
                                        Action action, Long permissionId,
                                        Consumer<AuthGroupRelResource> consumer) {
        AuthGroup group = AuthGroupGenerator.buildActionAuthGroup(action.getModel(), action.getName(), type);
        AuthGroupRelResource groupRelResource = new AuthGroupRelResource()
                .setGroupName(group.getName())
                .setResourceCode(data.getResourceCode())
                .setPermissionId(permissionId)
                .setGroupType(type);
        consumer.accept(groupRelResource);
        AuthResourceAuthorization selected = null;
        if (action instanceof ViewAction) {
            ViewAction viewAction = (ViewAction) action;
            selected = new AuthResourceAuthorization();
            selected.setModule(viewAction.getModule());
            selected.setModel(action.getModel());
            selected.setName(action.getName());
            selected.setPath(data.getPath());
            selected.setSource(AuthorizationSourceEnum.SYSTEM);
            selected.setActive(Boolean.TRUE);
            selected.setResourceId(action.getId());
        }
        return authGroupManager.createResourcePermissionGroup(group, groupRelResource, selected);
    }

    private void queryAndSetRuntimeGroups(AuthResourcePermissionGroups data, List<AuthGroupRelResource> groupRelResources,
                                          BiConsumer<AuthResourcePermissionGroups, List<AuthGroup>> setter) {
        List<AuthGroup> groups = Models.origin().queryListByWrapper(Pops.<AuthGroup>lambdaQuery()
                .from(AuthGroup.MODEL_MODEL)
                .in(AuthGroup::getId, groupRelResources.stream().map(AuthGroupRelResource::getGroupId).collect(Collectors.toSet())));
        if (groups.size() >= 2) {
            AuthGroup systemGroup = null;
            Iterator<AuthGroup> groupIterator = groups.iterator();
            while (groupIterator.hasNext()) {
                AuthGroup group = groupIterator.next();
                if (group.getName().contains(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
                    groupIterator.remove();
                    systemGroup = group;
                    break;
                }
            }
            if (systemGroup != null) {
                groups.add(0, systemGroup);
            }
        }
        setter.accept(data, groups);
    }

    private void consumerGroupRelResource(AuthResourcePermissionGroups data, BiConsumer<List<AuthGroupRelResource>, List<AuthGroupRelResource>> groupRelResourcesConsumer) {
        List<AuthGroupRelResource> groupRelResources = queryGroupRelResources(data);
        List<AuthGroupRelResource> runtimeGroupRelResources = new ArrayList<>(groupRelResources.size());
        List<AuthGroupRelResource> managementGroupRelResources = new ArrayList<>(groupRelResources.size());
        for (AuthGroupRelResource groupRelResource : groupRelResources) {
            AuthGroupTypeEnum groupType = groupRelResource.getGroupType();
            switch (groupType) {
                case RUNTIME:
                    runtimeGroupRelResources.add(groupRelResource);
                    break;
                case MANAGEMENT:
                    managementGroupRelResources.add(groupRelResource);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid group type. type = " + groupType);
            }
        }
        groupRelResourcesConsumer.accept(runtimeGroupRelResources, managementGroupRelResources);
    }

    private List<AuthGroupRelResource> queryGroupRelResources(AuthResourcePermissionGroups data) {
        return Models.origin().queryListByWrapper(Pops.<AuthGroupRelResource>lambdaQuery()
                .from(AuthGroupRelResource.MODEL_MODEL)
                .eq(AuthGroupRelResource::getResourceCode, data.getResourceCode())
                .eq(AuthGroupRelResource::getNodeType, AuthEnumerationHelper.getNodeType(data.getNodeType()))
                .ne(AuthGroupRelResource::getGroupType, AuthGroupTypeEnum.DATA.value()));
    }
}
