package pro.shushi.pamirs.auth.api.loader.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.helper.AuthPathGenerateHelper;
import pro.shushi.pamirs.auth.api.loader.ActionPermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.entity.AllotMetadataCollection;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认访问权限节点加载器
 *
 * @author Adamancy Zhang at 14:22 on 2024-08-14
 */
@Slf4j
@Component(AuthConstants.ACCESS_LOADER_BEAN_NAME)
public class DefaultAccessPermissionNodeLoader extends AbstractResourcePermissionNodeLoader implements ResourcePermissionNodeLoader {

    @Autowired
    private ActionPermissionNodeLoader actionPermissionNodeLoader;

    @Override
    public List<PermissionNode> buildAllPermissions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PermissionNode> buildAllPermissions(Set<Long> roleIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PermissionNode> buildRootPermissions() {
        AllotMetadataCollection allotMetadataCollection = collectionAllotMetadata();
        if (allotMetadataCollection == null) {
            return new ArrayList<>();
        }

        List<UeModule> allotAccessModules = allotMetadataCollection.getAllotModules();
        Map<String, ViewAction> allotAccessModuleHomepages = allotMetadataCollection.getAllotModuleHomepages();
        Map<String, List<Menu>> allotAccessModuleMenus = allotMetadataCollection.getAllotModuleMenus();

        PermissionLoadContext loadContext = PermissionLoadContext.generatorContext(authAccessService, allotMetadataCollection);

        List<PermissionNode> nodes = convertModuleNodes(allotAccessModules, (m, n) -> {
            String mm = m.getModule();

            convertModuleNode(mm, n, allotAccessModuleMenus);

            if (AuthHelper.isModuleInWhite(mm)) {
                return;
            }

            List<Menu> allotAccessMenus = allotAccessModuleMenus.get(mm);

            List<Menu> menus = Optional.ofNullable(allotAccessMenus)
                    .map(vv -> vv.stream().filter(menu -> StringUtils.isNoneBlank(menu.getModel(), menu.getActionName())).collect(Collectors.toList()))
                    .orElse(null);
            MemoryListSearchCache<String, Menu> moduleAccessMenuCache = null;
            if (CollectionUtils.isNotEmpty(menus)) {
                moduleAccessMenuCache = new MemoryListSearchCache<>(menus, (v) -> Action.sign(v.getModel(), v.getActionName()));
            }

            convertHomepageNode(m, n, moduleAccessMenuCache, allotAccessModuleHomepages);

            if (CollectionUtils.isNotEmpty(allotAccessMenus)) {
                convertMenuNode(m, n, allotAccessMenus);
            }
        });

        loadContext.setCurrentLoadNodes(nodes);

        extendLoadRootNodes(loadContext, (extendApi, currentLoadContext) -> extendApi.buildRootPermissions(currentLoadContext, nodes));

        return nodes;
    }

    @Override
    public List<PermissionNode> buildNextPermissions(PermissionNode selected) {
        Long resourceId = selected.getResourceId();
        if (resourceId == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ID_ERROR).errThrow();
        }
        ResourcePermissionSubtypeEnum nodeType = selected.getNodeType();
        if (nodeType == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
        }
        List<PermissionNode> nodes;
        switch (nodeType) {
            case MODULE:
                nodes = buildNextPermissionsByModule(selected, resourceId);
                break;
            case HOMEPAGE:
                nodes = buildNextPermissionsByHomepage(selected, resourceId);
                break;
            case MENU:
                nodes = buildNextPermissionsByMenu(selected, resourceId);
                break;
            case VIEW_ACTION:
                nodes = buildNextPermissionsByViewAction(selected, resourceId);
                break;
            default:
                nodes = new ArrayList<>();
                break;
        }

        extendLoadNextNodes(selected, (extendApi) -> extendApi.buildNextPermissions(selected, nodes));

        return nodes;
    }

    @Override
    public List<PermissionNode> buildNextPermissionsByHomepage(PermissionNode selected, UeModule module, ViewAction homepageAction) {
        return FetchUtil.cast(actionPermissionNodeLoader.buildActionNodes(selected, module, homepageAction));
    }

    @Override
    public List<PermissionNode> buildNextPermissionsByMenu(PermissionNode selected, Menu menu, ViewAction menuViewAction) {
        menu.setViewAction(menuViewAction);
        return FetchUtil.cast(actionPermissionNodeLoader.buildActionNodes(selected, menu));
    }

    @Override
    public List<PermissionNode> buildNextPermissionsByViewAction(PermissionNode selected, ViewAction viewAction) {
        return FetchUtil.cast(actionPermissionNodeLoader.buildActionNodes(selected, viewAction));
    }

    private List<PermissionNode> buildNextPermissionsByModule(PermissionNode node, Long resourceId) {
        UeModule module = Models.origin().queryOneByWrapper(Pops.<UeModule>lambdaQuery()
                .from(UeModule.MODEL_MODEL)
                .eq(UeModule::getId, resourceId));
        if (module == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        List<PermissionNode> nodes = buildNextPermissionsByHomepage(node, module);
        List<Menu> menus = Models.origin().queryListByWrapper(Pops.<Menu>lambdaQuery()
                .from(Menu.MODEL_MODEL)
                .eq(Menu::getModule, module.getModule())
                .eq(Menu::getActionType, ActionTypeEnum.VIEW.value()));
        if (CollectionUtils.isNotEmpty(menus)) {
            menus = Models.origin().listFieldQuery(menus, Menu::getViewAction);
            for (Menu menu : menus) {
                ViewAction viewAction = menu.getViewAction();
                if (viewAction == null) {
                    log.error("Invalid the view action of the menu. module = {}, menu = {}, model = {}, name = {}", menu.getModule(), menu.getName(), menu.getModel(), menu.getActionName());
                } else {
                    nodes.addAll(buildNextPermissionsByMenu(node, menu, viewAction));
                }
            }
        }
        return nodes;
    }

    private List<PermissionNode> buildNextPermissionsByHomepage(PermissionNode node, Long resourceId) {
        UeModule module = Models.origin().queryOneByWrapper(Pops.<UeModule>lambdaQuery()
                .from(UeModule.MODEL_MODEL)
                .eq(UeModule::getId, resourceId));
        if (module == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        return buildNextPermissionsByHomepage(node, module);
    }

    private List<PermissionNode> buildNextPermissionsByHomepage(PermissionNode node, UeModule module) {
        String model = module.getHomePageModel();
        String actionName = module.getHomePageName();
        if (StringUtils.isAnyBlank(model, actionName)) {
            return new ArrayList<>();
        }
        ViewAction viewAction = Models.origin().queryOneByWrapper(Pops.<ViewAction>lambdaQuery()
                .from(ViewAction.MODEL_MODEL)
                .eq(ViewAction::getModel, model)
                .eq(ViewAction::getName, actionName));
        if (viewAction == null) {
            log.error("Invalid the view action of the homepage. model = {}, name = {}", model, actionName);
            return new ArrayList<>();
        }
        return FetchUtil.cast(actionPermissionNodeLoader.buildActionNodes(node, module, viewAction));
    }

    private List<PermissionNode> buildNextPermissionsByMenu(PermissionNode node, Long resourceId) {
        Menu menu = Models.origin().queryOneByWrapper(Pops.<Menu>lambdaQuery()
                .from(Menu.MODEL_MODEL)
                .eq(Menu::getId, resourceId));
        if (menu == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        if (!ActionTypeEnum.VIEW.equals(menu.getActionType())) {
            return new ArrayList<>();
        }
        String model = menu.getModel();
        String actionName = menu.getActionName();
        if (StringUtils.isAnyBlank(model, actionName)) {
            return new ArrayList<>();
        }
        ViewAction viewAction = Models.origin().queryOneByWrapper(Pops.<ViewAction>lambdaQuery()
                .from(ViewAction.MODEL_MODEL)
                .eq(ViewAction::getModel, model)
                .eq(ViewAction::getName, actionName));
        if (viewAction == null) {
            log.error("Invalid the view action of the menu. model = {}, name = {}", model, actionName);
            return new ArrayList<>();
        }
        return buildNextPermissionsByMenu(node, menu, viewAction);
    }


    private List<PermissionNode> buildNextPermissionsByViewAction(PermissionNode node, Long resourceId) {
        ViewAction viewAction = Models.origin().queryOneByWrapper(Pops.<ViewAction>lambdaQuery()
                .from(ViewAction.MODEL_MODEL)
                .eq(ViewAction::getId, resourceId));
        if (viewAction == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        return buildNextPermissionsByViewAction(node, viewAction);
    }

    protected void convertHomepageNode(UeModule module, ModulePermissionNode node,
                                       MemoryListSearchCache<String, Menu> moduleManagementMenuCache,
                                       Map<String, ViewAction> allotManagementHomepageCache) {
        String homepageModel = module.getHomePageModel();
        String homepageActionName = module.getHomePageName();
        if (StringUtils.isAnyBlank(homepageModel, homepageActionName)) {
            node.getNodes().add(convertHomepageNode(module));
            return;
        }
        String moduleModule = module.getModule();
        ViewAction homepage = allotManagementHomepageCache.get(moduleModule);
        if (homepage == null) {
            return;
        }
        String homepageSign = Action.sign(homepageModel, homepageActionName);
        if (moduleManagementMenuCache != null && moduleManagementMenuCache.get(homepageSign) != null) {
            return;
        }
        HomepagePermissionNode homepageNode = convertHomepageNode(module, homepage);
        node.getNodes().add(homepageNode);
    }

    protected void convertMenuNode(UeModule module, ModulePermissionNode node, List<Menu> allotManagementModuleMenus) {
        node.getNodes().addAll(convertMenuNode(node, allotManagementModuleMenus));
    }

    protected List<MenuPermissionNode> convertMenuNode(ModulePermissionNode moduleNode, List<Menu> allotManagementModuleMenus) {
        Map<String, MenuPermissionNode> rootMap = new HashMap<>(16);
        Map<String, MenuPermissionNode> childrenMap = new HashMap<>(16);
        Map<String, List<MenuPermissionNode>> lazySetParentMap = new HashMap<>(16);
        for (Menu menu : allotManagementModuleMenus) {
            String menuName = menu.getName();
            String parentMenuName = Optional.ofNullable(menu.getParentName()).filter(StringUtils::isNotBlank).orElse(null);

            MenuPermissionNode node;
            if (StringUtils.isBlank(parentMenuName)) {
                node = convertMenuNode(menu, moduleNode);
                node.setPath(AuthPathGenerateHelper.generatorMenuPath(moduleNode, node));
                rootMap.put(menuName, node);
            } else {
                node = convertMenuNode(menu);
                childrenMap.put(menuName, node);
                MenuPermissionNode parentNode = rootMap.get(parentMenuName);
                if (parentNode == null) {
                    parentNode = childrenMap.get(parentMenuName);
                }
                if (parentNode == null) {
                    lazySetParentMap.computeIfAbsent(parentMenuName, v -> new ArrayList<>()).add(node);
                } else {
                    node.setParentId(parentNode.getId());
                    node.setParent(parentNode);
                    node.setParentName(parentMenuName);
                    node.setPath(AuthPathGenerateHelper.generatorMenuPath(moduleNode, node, parentNode));
                    parentNode.setHasNext(Boolean.TRUE);
                    parentNode.getNodes().add(node);
                }
            }
            List<MenuPermissionNode> lazySetParentNodes = lazySetParentMap.remove(menuName);
            if (CollectionUtils.isNotEmpty(lazySetParentNodes)) {
                node.setHasNext(Boolean.TRUE);
                List<PermissionNode> targetNodes = node.getNodes();
                for (MenuPermissionNode lazySetParentNode : lazySetParentNodes) {
                    lazySetParentNode.setParentId(node.getId());
                    lazySetParentNode.setParent(node);
                    lazySetParentNode.setParentName(menuName);
                    lazySetParentNode.setPath(AuthPathGenerateHelper.generatorMenuPath(moduleNode, lazySetParentNode, node));
                    targetNodes.add(lazySetParentNode);
                }
            }
        }
        List<MenuPermissionNode> nodes = new ArrayList<>(rootMap.values());
        sortMenuNode(nodes);
        return nodes;
    }

    protected void sortMenuNode(List<MenuPermissionNode> nodes) {
        nodes.sort(Comparator.comparing(MenuPermissionNode::getPriority));
        nodes.stream().map(v -> FetchUtil.<List<MenuPermissionNode>>cast(v.getNodes()))
                .filter(CollectionUtils::isNotEmpty)
                .forEach(this::sortMenuNode);
    }

    protected AllotMetadataCollection collectionAllotMetadata() {
        List<UeModule> allModules = fetchAllModules();
        if (CollectionUtils.isEmpty(allModules)) {
            return null;
        }
        Set<String> menuModules = new HashSet<>(allModules.size());
        List<UeModule> allHomepageModules = new ArrayList<>(allModules.size());
        for (UeModule moduleDefinition : allModules) {
            String module = moduleDefinition.getModule();
            if (!AuthHelper.isModuleInWhite(module)) {
                menuModules.add(module);
            }
            if (StringUtils.isNoneBlank(moduleDefinition.getHomePageModel(), moduleDefinition.getHomePageName())) {
                allHomepageModules.add(moduleDefinition);
            }
        }
        Set<String> allotAccessModuleModules = fetchAllotAccessModulesByAuth();
        Map<String, ViewAction> allotAccessModuleHomepages = fetchAllotAccessHomepages(allHomepageModules);
        Map<String, List<Menu>> allotAccessModuleMenus = fetchAllotAccessMenus(menuModules);

        List<UeModule> allotAccessModules = fetchAllotAccessModules(allModules, allotAccessModuleModules, Sets.union(allotAccessModuleMenus.keySet(), allotAccessModuleHomepages.keySet()));
        if (CollectionUtils.isEmpty(allotAccessModules)) {
            return null;
        }
        return new AllotMetadataCollection(allotAccessModules, allotAccessModuleHomepages, allotAccessModuleMenus);
    }
}
