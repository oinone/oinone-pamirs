package pro.shushi.pamirs.auth.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataOperator;
import pro.shushi.pamirs.auth.view.entity.AuthGroupName;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupResourcePermissionProxy;
import pro.shushi.pamirs.auth.view.service.AuthGroupService;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.*;

/**
 * 权限组 - 资源权限
 *
 * @author Adamancy Zhang at 17:26 on 2024-02-02
 */
@Component
@Model.model(AuthGroupResourcePermissionProxy.MODEL_MODEL)
public class AuthGroupResourcePermissionProxyAction {

    @Autowired
    private AuthGroupService authGroupService;

    @Autowired
    private AuthGroupDataOperator authGroupDataOperator;

    @Function.Advanced(displayName = "根据条件分页查询记录列表和总数", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AuthGroupResourcePermissionProxy> queryPage(Pagination<AuthGroupResourcePermissionProxy> page, IWrapper<AuthGroupResourcePermissionProxy> queryWrapper) {
        Pagination<AuthGroupResourcePermissionProxy> pagination = Models.origin().queryPage(page, WrapperHelper.lambda(queryWrapper)
                .ne(AuthGroupResourcePermissionProxy::getSource, AuthorizationSourceEnum.BUILD_IN)
                .ne(AuthGroupResourcePermissionProxy::getType, AuthGroupTypeEnum.DATA));
        List<AuthGroupResourcePermissionProxy> permissions = pagination.getContent();
        if (CollectionUtils.isEmpty(permissions)) {
            return pagination;
        }
        Set<String> modules = new HashSet<>();
        Set<String> menuRepeatKeys = new HashSet<>();
        List<String> menuModules = new ArrayList<>();
        List<String> menuNames = new ArrayList<>();
        Map<String, AuthGroupName> groupNameMap = new HashMap<>(permissions.size());
        for (AuthGroupResourcePermissionProxy permission : permissions) {
            permission.setRoles(new ArrayList<>());
            permission.setManagementResourcePermissions(new ArrayList<>());
            permission.setAccessResourcePermissions(new ArrayList<>());
            if (AuthorizationSourceEnum.SYSTEM.equals(permission.getSource())) {
                String permissionName = permission.getName();
                AuthGroupName groupName = AuthGroupName.resolveGroupName(permissionName);
                if (groupName != null) {
                    groupNameMap.put(permissionName, groupName);
                    permission.setResourceType(groupName.getMetadataType());

                    if (PermissionMateDataEnum.MENU.equals(groupName.getMetadataType())) {
                        String menuModule = groupName.getModule();
                        String menuName = groupName.getName();
                        if (ObjectHelper.isNotRepeat(menuRepeatKeys, Menu.sign(menuModule, menuName))) {
                            menuModules.add(menuModule);
                            menuNames.add(menuName);
                        }
                    } else {
                        modules.add(groupName.getModule());
                    }
                }
                permission.setIsAllowDelete(Boolean.FALSE);
            } else {
                permission.setIsAllowDelete(Boolean.TRUE);
            }
        }
        List<ModuleDefinition> moduleDefinitions;
        if (CollectionUtils.isEmpty(modules)) {
            moduleDefinitions = new ArrayList<>();
        } else {
            moduleDefinitions = DataShardingHelper.build().collectionSharding(modules,
                    (sublist) -> Models.origin().queryListByWrapper(Pops.<ModuleDefinition>lambdaQuery()
                            .from(ModuleDefinition.MODEL_MODEL)
                            .select(ModuleDefinition::getModule, ModuleDefinition::getDisplayName)
                            .in(ModuleDefinition::getModule, sublist)));
        }
        List<Menu> menus;
        if (CollectionUtils.isEmpty(menuModules)) {
            menus = new ArrayList<>();
        } else {
            menus = DataShardingHelper.build().sharding(menuModules.size(),
                    (begin, end) -> Models.origin().queryListByWrapper(Pops.<Menu>lambdaQuery()
                            .from(Menu.MODEL_MODEL)
                            .setBatchSize(-1)
                            .select(Menu::getModule, Menu::getName, Menu::getDisplayName, Menu::getDefaultDisplayName)
                            .in(Arrays.asList(Menu::getModule, Menu::getName), menuModules.subList(begin, end), menuNames.subList(begin, end))));
        }
        MemoryListSearchCache<String, ModuleDefinition> moduleDefinitionCache = new MemoryListSearchCache<>(moduleDefinitions, ModuleDefinition::getModule);
        MemoryListSearchCache<String, Menu> menuCache = new MemoryListSearchCache<>(menus, v -> Menu.sign(v.getModule(), v.getName()));
        for (AuthGroupResourcePermissionProxy permission : permissions) {
            AuthGroupName groupName = groupNameMap.get(permission.getName());
            if (groupName == null) {
                continue;
            }
            if (PermissionMateDataEnum.MENU.equals(groupName.getMetadataType())) {
                Menu menu = menuCache.get(Menu.sign(groupName.getModule(), groupName.getName()));
                if (menu == null) {
                    permission.setIsAllowDelete(Boolean.TRUE);
                } else {
                    permission.setResourceDisplayName(menu.getDisplayName());
                }
            } else {
                ModuleDefinition moduleDefinition = moduleDefinitionCache.get(groupName.getModule());
                if (moduleDefinition == null) {
                    permission.setIsAllowDelete(Boolean.TRUE);
                } else {
                    permission.setResourceDisplayName(moduleDefinition.getDisplayName());
                }
            }
        }
        return pagination;
    }

    @Function.Advanced(displayName = "查询单条记录", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public AuthGroupResourcePermissionProxy queryOne(AuthGroupResourcePermissionProxy query) {
        return fetchOne(query);
    }

    @Action(displayName = "删除", bindingType = ViewTypeEnum.TABLE)
    public AuthGroupResourcePermissionProxy deleteOne(AuthGroupResourcePermissionProxy data) {
        data = fetchOne(data);
        Long groupId = data.getId();
        authGroupService.delete(groupId);
        AuthGroupResourcePermissionProxy result = new AuthGroupResourcePermissionProxy();
        result.setId(groupId);
        return result;
    }

    @Action(displayName = "启用", bindingType = ViewTypeEnum.TABLE)
    @Action.Advanced(invisible = "context.activeRecord.active")
    public AuthGroupResourcePermissionProxy active(AuthGroupResourcePermissionProxy data) {
        AuthGroupResourcePermissionProxy origin = fetchOne(data);
        if (authGroupService.active(origin.getId())) {
            origin.setActive(Boolean.TRUE);
        }
        return origin;
    }

    @Action(displayName = "禁用", bindingType = ViewTypeEnum.TABLE)
    @Action.Advanced(invisible = "!context.activeRecord.active")
    public AuthGroupResourcePermissionProxy disable(AuthGroupResourcePermissionProxy data) {
        AuthGroupResourcePermissionProxy origin = fetchOne(data);
        if (authGroupService.disable(origin.getId())) {
            origin.setActive(Boolean.FALSE);
        }
        return origin;
    }

    private AuthGroupResourcePermissionProxy fetchOne(AuthGroupResourcePermissionProxy query) {
        AuthGroupResourcePermissionProxy origin = FetchUtil.fetchOne(query);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_GROUP_ERROR).errThrow();
        }
        return origin;
    }
}
