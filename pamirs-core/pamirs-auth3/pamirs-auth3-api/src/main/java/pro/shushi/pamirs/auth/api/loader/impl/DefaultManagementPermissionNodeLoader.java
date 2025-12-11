package pro.shushi.pamirs.auth.api.loader.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.*;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.authorized.ResourceAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.helper.AuthPathGenerateHelper;
import pro.shushi.pamirs.auth.api.loader.ActionPermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.cache.PermissionNodeCache;
import pro.shushi.pamirs.auth.api.loader.entity.AllotMetadataCollection;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.utils.AuthAuthorizationHelper;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.TranslateUtils;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.query.QueryActions;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认管理权限节点加载器
 *
 * @author Adamancy Zhang at 09:50 on 2024-01-15
 */
@Slf4j
@Component(AuthConstants.MANAGEMENT_LOADER_BEAN_NAME)
public class DefaultManagementPermissionNodeLoader extends AbstractResourcePermissionNodeLoader implements ResourcePermissionNodeLoader {

    @Autowired
    private ActionPermissionNodeLoader actionPermissionNodeLoader;

    @Override
    public List<PermissionNode> buildAllPermissions(Set<Long> roleIds) {
        AllotMetadataCollection allotMetadataCollection = collectionAllotMetadata();
        if (allotMetadataCollection == null) {
            return new ArrayList<>();
        }

        List<UeModule> allotManagementModules = allotMetadataCollection.getAllotModules();
        Map<String, ViewAction> allotManagementModuleHomepages = allotMetadataCollection.getAllotModuleHomepages();
        Map<String, List<Menu>> allotManagementModuleMenus = allotMetadataCollection.getAllotModuleMenus();

        collectionValidHomepages(allotMetadataCollection, allotManagementModuleHomepages.values());
        collectionValidMenus(allotMetadataCollection, CollectionHelper.flat(allotManagementModuleMenus.values()));

        PermissionLoadContext loadContext = generatorLoadContext(allotMetadataCollection, roleIds);

        List<HomepagePermissionNode> allHomepageNodes = new ArrayList<>();
        List<MenuPermissionNode> allMenuNodes = new ArrayList<>();
        List<PermissionNode> nodes = convertModuleNodes(allotManagementModules, (m, n) -> {
            String mm = m.getModule();

            convertModuleNode(loadContext, mm, n, allotManagementModuleMenus);

            if (AuthHelper.isModuleInWhite(mm)) {
                return;
            }

            List<Menu> allotManagementMenus = allotManagementModuleMenus.get(mm);

            List<Menu> menus = Optional.ofNullable(allotManagementMenus)
                    .map(vv -> vv.stream().filter(menu -> StringUtils.isNoneBlank(menu.getModel(), menu.getActionName())).collect(Collectors.toList()))
                    .orElse(null);
            MemoryListSearchCache<String, Menu> moduleManagementMenuCache = null;
            if (CollectionUtils.isNotEmpty(menus)) {
                moduleManagementMenuCache = new MemoryListSearchCache<>(menus, (v) -> Action.sign(v.getModel(), v.getActionName()));
            }

            HomepagePermissionNode homepageNode = convertHomepageNode(loadContext, m, n, moduleManagementMenuCache, allotManagementModuleHomepages);
            if (homepageNode != null) {
                allHomepageNodes.add(homepageNode);
            }

            if (CollectionUtils.isNotEmpty(allotManagementMenus)) {
                allMenuNodes.addAll(convertMenuNode(loadContext, m, n, allotManagementMenus));
            }
        });

        fetchHomepageViewActionPermissionNodes(loadContext, allotManagementModules, allotManagementModuleHomepages, allHomepageNodes);

        fetchMenuViewActionPermissionNodes(loadContext, allotMetadataCollection.getValidMenus(), allMenuNodes);

        loadContext.setCurrentLoadNodes(nodes);

        extendLoadAllNodes(loadContext, (extendApi, currentLoadContext) -> extendApi.buildAllPermissions(currentLoadContext, nodes, roleIds));

        return nodes;
    }

    @Override
    public List<PermissionNode> buildRootPermissions() {
        AllotMetadataCollection allotMetadataCollection = collectionAllotMetadata();
        if (allotMetadataCollection == null) {
            return new ArrayList<>();
        }

        List<UeModule> allotManagementModules = allotMetadataCollection.getAllotModules();
        Map<String, ViewAction> allotManagementModuleHomepages = allotMetadataCollection.getAllotModuleHomepages();
        Map<String, List<Menu>> allotManagementModuleMenus = allotMetadataCollection.getAllotModuleMenus();

        PermissionLoadContext loadContext = generatorLoadContext(allotMetadataCollection);

        List<PermissionNode> nodes = convertModuleNodes(allotManagementModules, (m, n) -> {
            String mm = m.getModule();

            convertModuleNode(mm, n, allotManagementModuleMenus);

            if (AuthHelper.isModuleInWhite(mm)) {
                return;
            }

            List<Menu> allotManagementMenus = allotManagementModuleMenus.get(mm);

            List<Menu> menus = Optional.ofNullable(allotManagementMenus)
                    .map(vv -> vv.stream().filter(menu -> StringUtils.isNoneBlank(menu.getModel(), menu.getActionName())).collect(Collectors.toList()))
                    .orElse(null);
            MemoryListSearchCache<String, Menu> moduleManagementMenuCache = null;
            if (CollectionUtils.isNotEmpty(menus)) {
                moduleManagementMenuCache = new MemoryListSearchCache<>(menus, (v) -> Action.sign(v.getModel(), v.getActionName()));
            }

            convertHomepageNode(m, n, moduleManagementMenuCache, allotManagementModuleHomepages);

            if (CollectionUtils.isNotEmpty(allotManagementMenus)) {
                convertMenuNode(m, n, allotManagementMenus);
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

    protected PermissionLoadContext generatorLoadContext() {
        return PermissionLoadContext.generatorContext(authAccessService, new AllotMetadataCollection());
    }

    protected PermissionLoadContext generatorLoadContext(AllotMetadataCollection allotMetadataCollection) {
        return PermissionLoadContext.generatorContext(authAccessService, allotMetadataCollection);
    }

    protected PermissionLoadContext generatorLoadContext(AllotMetadataCollection allotMetadataCollection, Set<Long> roleIds) {
        return PermissionLoadContext.generatorContext(authAccessService, allotMetadataCollection, roleIds);
    }

    private List<PermissionNode> buildNextPermissionsByModule(PermissionNode node, Long resourceId) {
        UeModule module = Models.origin().queryOneByWrapper(Pops.<UeModule>lambdaQuery()
                .from(UeModule.MODEL_MODEL)
                .eq(UeModule::getId, resourceId));
        if (module == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        return buildNextPermissionsByModule(node, module);
    }

    private List<PermissionNode> buildNextPermissionsByModule(PermissionNode node, UeModule module) {
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
                    log.error("Invalid the view action of the menu. module: {}, menu: {}, model: {}, name: {}", menu.getModule(), menu.getName(), menu.getModel(), menu.getActionName());
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
            log.error("Invalid the view action of the homepage. model: {}, name: {}", model, actionName);
            return new ArrayList<>();
        }
        return FetchUtil.cast(actionPermissionNodeLoader.buildActionNodes(node, module, viewAction));
    }

    private List<PermissionNode> buildNextPermissionsByMenu(PermissionNode node, Long resourceId) {
        Menu menu = Models.origin().queryOneByWrapper(Pops.<Menu>lambdaQuery()
                .from(Menu.MODEL_MODEL)
                .eq(Menu::getId, resourceId));
        if (menu == null) {
            log.error("Invalid menu resource. id: {}", resourceId);
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
            log.error("Invalid the view action of the menu. menuId: {}, module: {}, menuName: {}, model: {}, actionName: {}", resourceId, menu.getModule(), menu.getName(), model, actionName);
            return new ArrayList<>();
        }
        return buildNextPermissionsByMenu(node, menu, viewAction);
    }


    private List<PermissionNode> buildNextPermissionsByViewAction(PermissionNode node, Long resourceId) {
        ViewAction viewAction = Models.origin().queryOneByWrapper(Pops.<ViewAction>lambdaQuery()
                .from(ViewAction.MODEL_MODEL)
                .eq(ViewAction::getId, resourceId));
        if (viewAction == null) {
            log.error("Invalid view action resource. id: {}", resourceId);
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        return buildNextPermissionsByViewAction(node, viewAction);
    }

    protected HomepagePermissionNode convertHomepageNode(UeModule module, ModulePermissionNode node,
                                                         MemoryListSearchCache<String, Menu> moduleManagementMenuCache,
                                                         Map<String, ViewAction> allotManagementHomepageCache) {
        String homepageModel = module.getHomePageModel();
        String homepageActionName = module.getHomePageName();
        if (StringUtils.isAnyBlank(homepageModel, homepageActionName)) {
            HomepagePermissionNode homepageNode = convertHomepageNode(module);
            node.getNodes().add(convertHomepageNode(module));
            return homepageNode;
        }
        String moduleModule = module.getModule();
        ViewAction homepage = allotManagementHomepageCache.get(moduleModule);
        if (homepage == null) {
            return null;
        }
        String homepageSign = Action.sign(homepageModel, homepageActionName);
        if (moduleManagementMenuCache != null && moduleManagementMenuCache.get(homepageSign) != null) {
            return null;
        }
        HomepagePermissionNode homepageNode = convertHomepageNode(module, homepage);
        node.getNodes().add(homepageNode);
        return homepageNode;
    }

    protected HomepagePermissionNode convertHomepageNode(PermissionLoadContext loadContext, UeModule module, ModulePermissionNode node,
                                                         MemoryListSearchCache<String, Menu> moduleManagementMenuCache,
                                                         Map<String, ViewAction> allotManagementHomepageCache) {
        String homepageModel = module.getHomePageModel();
        String homepageActionName = module.getHomePageName();
        if (StringUtils.isAnyBlank(homepageModel, homepageActionName)) {
            HomepagePermissionNode homepageNode = convertHomepageNode(module);
            node.getNodes().add(convertHomepageNode(module));
            return homepageNode;
        }
        String moduleModule = module.getModule();
        ViewAction homepage = allotManagementHomepageCache.get(moduleModule);
        if (homepage == null) {
            return null;
        }
        String homepageSign = Action.sign(homepageModel, homepageActionName);
        if (moduleManagementMenuCache != null && moduleManagementMenuCache.get(homepageSign) != null) {
            return null;
        }
        HomepagePermissionNode homepageNode = convertHomepageNode(loadContext, module, homepage);
        node.getNodes().add(homepageNode);
        return homepageNode;
    }

    protected void collectionValidHomepages(AllotMetadataCollection allotMetadataCollection, Collection<ViewAction> homepageViewActions) {
        for (ViewAction homepageViewAction : homepageViewActions) {
            allotMetadataCollection.addValidViewAction(homepageViewAction);
        }
    }

    protected void convertMenuNode(UeModule module, ModulePermissionNode node, List<Menu> allotManagementModuleMenus) {
        List<MenuPermissionNode> menuNodes = convertMenuNode(node, allotManagementModuleMenus, null);
        sortAndCollectionMenuNodes(menuNodes);
        node.getNodes().addAll(menuNodes);
    }

    protected List<MenuPermissionNode> convertMenuNode(PermissionLoadContext loadContext, UeModule module, ModulePermissionNode node, List<Menu> allotManagementModuleMenus) {
        List<MenuPermissionNode> menuNodes = convertMenuNode(node, allotManagementModuleMenus, loadContext);
        List<MenuPermissionNode> allMenuNodes = sortAndCollectionMenuNodes(menuNodes);
        node.getNodes().addAll(menuNodes);
        return allMenuNodes;
    }

    protected List<MenuPermissionNode> convertMenuNode(ModulePermissionNode moduleNode, List<Menu> allotManagementModuleMenus, PermissionLoadContext loadContext) {
        Map<String, MenuPermissionNode> rootMap = new HashMap<>(16);
        Map<String, MenuPermissionNode> childrenMap = new HashMap<>(16);
        Map<String, List<MenuPermissionNode>> lazySetParentMap = new HashMap<>(16);
        for (Menu menu : allotManagementModuleMenus) {
            String menuName = menu.getName();
            String parentMenuName = Optional.ofNullable(menu.getParentName()).filter(StringUtils::isNotBlank).orElse(null);
            MenuPermissionNode node;
            if (StringUtils.isBlank(parentMenuName)) {
                node = convertMenuNode(menu, moduleNode, loadContext);
                node.setPath(AuthPathGenerateHelper.generatorMenuPath(moduleNode, node));
                rootMap.put(menuName, node);
            } else {
                node = convertMenuNode(menu, loadContext);
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
        return new ArrayList<>(rootMap.values());
    }

    protected List<MenuPermissionNode> sortAndCollectionMenuNodes(List<MenuPermissionNode> nodes) {
        List<MenuPermissionNode> allNodes = new ArrayList<>();
        sortAndCollectionMenuNodes(allNodes, nodes);
        return allNodes;
    }

    protected SortAndCollectionMenuResult sortAndCollectionMenuNodes(List<MenuPermissionNode> allNodes, List<MenuPermissionNode> nodes) {
        allNodes.addAll(nodes);
        nodes.sort(Comparator.comparing(MenuPermissionNode::getPriority));
        boolean isAllCanAccess = true;
        boolean isAllCanManagement = true;
        for (MenuPermissionNode node : nodes) {
            List<MenuPermissionNode> children = FetchUtil.cast(node.getNodes());
            if (CollectionUtils.isNotEmpty(children)) {
                SortAndCollectionMenuResult result = sortAndCollectionMenuNodes(allNodes, children);
                if (result.getIsAllCanAccess()) {
                    node.setCanAccess(Boolean.TRUE);
                }
                if (result.getIsAllCanManagement()) {
                    node.setCanManagement(Boolean.TRUE);
                }
            }
            if (Boolean.FALSE.equals(node.getCanAccess())) {
                isAllCanAccess = false;
            }
            if (Boolean.FALSE.equals(node.getCanManagement())) {
                isAllCanManagement = false;
            }
        }
        return new SortAndCollectionMenuResult(isAllCanAccess, isAllCanManagement);
    }

    protected void collectionValidMenus(AllotMetadataCollection allotMetadataCollection, List<Menu> menus) {
        for (Menu menu : menus) {
            if (!ActionTypeEnum.VIEW.equals(menu.getActionType())) {
                continue;
            }
            String model = menu.getModel();
            String actionName = menu.getActionName();
            if (StringUtils.isAnyBlank(model, actionName)) {
                continue;
            }
            allotMetadataCollection.addValidMenu(menu);
        }
        allotMetadataCollection.setValidMenus(fetchAllotMenuViewActions(allotMetadataCollection.getValidMenus()));
    }

    protected List<Menu> fetchAllotMenuViewActions(List<Menu> validMenus) {
        QueryActions<ViewAction> queryViewActions = new QueryActions<>(ActionTypeEnum.VIEW);
        for (Menu menu : validMenus) {
            queryViewActions.add(menu.getModel(), menu.getActionName());
        }
        List<ViewAction> viewActions = queryViewActions.query();
        if (viewActions.isEmpty()) {
            return new ArrayList<>();
        }
        List<Menu> finalValidMenus = new ArrayList<>(validMenus.size());
        MemoryListSearchCache<String, ViewAction> viewActionCache = new MemoryListSearchCache<>(viewActions, v -> Action.sign(v.getModel(), v.getName()));
        for (Menu menu : validMenus) {
            ViewAction viewAction = viewActionCache.get(Action.sign(menu.getModel(), menu.getActionName()));
            if (viewAction != null) {
                menu.setViewAction(viewAction);
                finalValidMenus.add(menu);
            }
        }
        return finalValidMenus;
    }

    protected void fetchHomepageViewActionPermissionNodes(PermissionLoadContext loadContext,
                                                          List<UeModule> allotModules,
                                                          Map<String, ViewAction> allotManagementModuleHomepages,
                                                          List<HomepagePermissionNode> allHomepageNodes) {
        MemoryListSearchCache<String, UeModule> allotModulesCache = new MemoryListSearchCache<>(allotModules, ModuleDefinition::getModule);
        for (HomepagePermissionNode node : allHomepageNodes) {
            String module = node.getModule();
            ViewAction viewAction = allotManagementModuleHomepages.get(module);
            if (viewAction == null) {
                continue;
            }
            UeModule moduleDefinition = allotModulesCache.get(module);
            if (moduleDefinition == null) {
                continue;
            }
            List<PermissionNode> nodes = buildHomepageActionNodesWithCache(module + CharacterConstants.SEPARATOR_OCTOTHORPE + AuthConstants.HOMEPAGE_TYPE, node, moduleDefinition, viewAction);
            if (nodes == null) {
                continue;
            }
            fillCanAccessByAction(loadContext, module, null, nodes);
            node.setNodes(nodes);
            if (nodes.isEmpty()) {
                node.setHasNext(Boolean.FALSE);
            } else {
                node.setHasNext(Boolean.TRUE);
            }
        }
    }

    protected List<PermissionNode> buildHomepageActionNodesWithCache(String sign, HomepagePermissionNode selected, UeModule module, ViewAction homepageAction) {
        return PermissionNodeCache.get(sign, () -> actionPermissionNodeLoader.buildActionNodes(selected, module, homepageAction));
    }

    protected void fetchMenuViewActionPermissionNodes(PermissionLoadContext loadContext, List<Menu> validMenus, List<MenuPermissionNode> allMenuNodes) {
        MemoryListSearchCache<String, MenuPermissionNode> allMenuNodeCache = new MemoryListSearchCache<>(allMenuNodes, v -> Menu.sign(v.getModule(), v.getName()));
        for (Menu menu : validMenus) {
            String module = menu.getModule();
            String menuName = menu.getName();
            String sign = Menu.sign(module, menuName);
            MenuPermissionNode selected = allMenuNodeCache.get(sign);
            if (selected == null || selected.getHasNext()) {
                continue;
            }
            List<PermissionNode> nodes = buildMenuActionNodesWithCache(sign, selected, menu);
            if (nodes == null) {
                continue;
            }
            fillCanAccessByAction(loadContext, module, menuName, nodes);
            selected.setNodes(nodes);
            if (nodes.isEmpty()) {
                selected.setHasNext(Boolean.FALSE);
            } else {
                selected.setHasNext(Boolean.TRUE);
            }
        }
    }

    protected List<PermissionNode> buildMenuActionNodesWithCache(String sign, MenuPermissionNode selected, Menu menu) {
        List<PermissionNode> nodes = PermissionNodeCache.get(sign, () -> actionPermissionNodeLoader.buildActionNodes(selected, menu));
        TranslateService translateService = TranslateServiceHolder.get();
        if (translateService.needTranslate()) {
            if (CollectionUtils.isNotEmpty(nodes)) {
                translateNodes(nodes);
            }
        }
        return nodes;
    }

    private void translateNodes(List<PermissionNode> nodes) {
        for (PermissionNode node : nodes) {
            node.setDisplayValue(TranslateUtils.placeholder(node.getDisplayValue()));
            List<PermissionNode> nextNodes = node.getNodes();
            if (CollectionUtils.isNotEmpty(nextNodes)) {
                translateNodes(nextNodes);
            }
        }
    }

    protected void fillCanAccessByAction(PermissionLoadContext loadContext, String module, String menuName, List<PermissionNode> nodes) {
        for (PermissionNode node : nodes) {
            String path = node.getPath();
            node.setCanAccess(loadContext.isAccessAction(path));
            node.setCanManagement(loadContext.isManagementAction(path));
            node.setCanAllot(loadContext.isAllotAction(module, menuName, path));
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                fillCanAccessByAction(loadContext, module, menuName, children);
            }
        }
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
        Set<String> allotManagementModuleModules = fetchAllotManagementModulesByAuth();
        Map<String, ViewAction> allotManagementModuleHomepages = fetchAllotManagementHomepages(allHomepageModules, allotManagementModuleModules);
        Map<String, List<Menu>> allotManagementModuleMenus = fetchAllotManagementMenus(menuModules, allotManagementModuleModules);

        List<UeModule> allotManagementModules = fetchAllotManagementModules(allModules, allotManagementModuleModules, Sets.union(allotManagementModuleMenus.keySet(), allotManagementModuleHomepages.keySet()));
        if (CollectionUtils.isEmpty(allotManagementModules)) {
            return null;
        }
        return new AllotMetadataCollection(allotManagementModules, allotManagementModuleHomepages, allotManagementModuleMenus);
    }

    @Override
    protected void processExtendAllPermissionNodes(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
        for (PermissionNode node : nodes) {
            loadExtendMetadata(loadContext, node);
        }

        loadContext.loadExtendPermissions();

        for (PermissionNode node : nodes) {
            processExtendAllPermissionNodes(loadContext, node, true);
        }
    }

    protected boolean processExtendAllPermissionNodes(PermissionLoadContext loadContext, PermissionNode node, boolean needRemove) {
        fillCanAccess(loadContext, node);
        boolean hasAnyCanAllot = Boolean.TRUE.equals(node.getCanAllot());
        List<PermissionNode> children = node.getNodes();
        if (CollectionUtils.isEmpty(children)) {
            List<PermissionNode> nextNodes = buildNextPermissionsWithCache(node);
            if (nextNodes != null) {
                node.setNodes(nextNodes);
                if (fillCanAccess(loadContext, nextNodes)) {
                    hasAnyCanAllot = true;
                }
            }
        } else {
            Iterator<PermissionNode> childrenIterator = children.iterator();
            while (childrenIterator.hasNext()) {
                PermissionNode child = childrenIterator.next();
                if (processExtendAllPermissionNodes(loadContext, child, false)) {
                    hasAnyCanAllot = true;
                } else {
                    childrenIterator.remove();
                }
            }
        }
        if (!hasAnyCanAllot && needRemove) {
            PermissionNode parentNode = node.getParent();
            List<PermissionNode> parentNodes;
            if (parentNode == null) {
                parentNodes = loadContext.getCurrentLoadNodes();
            } else {
                parentNodes = parentNode.getNodes();
            }
            if (CollectionUtils.isNotEmpty(parentNodes)) {
                parentNodes.remove(node);
            }
        }
        return hasAnyCanAllot;
    }

    @Override
    protected void processExtendRootPermissionNodes(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
        for (PermissionNode node : nodes) {
            loadExtendMetadata(loadContext, node);
        }

        loadContext.loadExtendPermissions();

        for (PermissionNode node : nodes) {
            fillCanAccess(loadContext, node);
            boolean hasAnyCanAllot = Boolean.TRUE.equals(node.getCanAllot());
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                if (fillCanAccess(loadContext, children)) {
                    hasAnyCanAllot = true;
                }
            }
            if (!hasAnyCanAllot) {
                List<PermissionNode> parentNodes = Optional.ofNullable(node.getParent()).map(PermissionNode::getNodes).orElse(null);
                if (CollectionUtils.isNotEmpty(parentNodes)) {
                    parentNodes.remove(node);
                }
            }
        }
    }

    @Override
    protected void processExtendNextPermissionNodes(PermissionNode selected, List<PermissionNode> nodes) {
        Long groupId = selected.getGroupId();
        if (groupId == null) {
            return;
        }
        Map<String, AuthResourceAuthorization> authorizationMap = AuthAuthorizationHelper.fetchActionAuthorizationMap(groupId);
        if (MapUtils.isEmpty(authorizationMap)) {
            return;
        }
        Set<String> accessActions = new HashSet<>();
        for (AuthResourceAuthorization resourceAuthorization : authorizationMap.values()) {
            if (ResourceAuthorizedValueEnum.isAccess(resourceAuthorization.getAuthorizedValue())) {
                accessActions.add(resourceAuthorization.getPath());
            }
        }
        PermissionLoadContext loadContext = generatorLoadContext();
        loadContext.addAccessActions(accessActions);

        for (PermissionNode node : nodes) {
            fillCanAccess(loadContext, node);
            boolean hasAnyCanAllot = Boolean.TRUE.equals(node.getCanAllot());
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                if (fillCanAccess(loadContext, children)) {
                    hasAnyCanAllot = true;
                }
            }
            if (!hasAnyCanAllot) {
                List<PermissionNode> parentNodes = Optional.ofNullable(node.getParent()).map(PermissionNode::getNodes).orElse(null);
                if (CollectionUtils.isNotEmpty(parentNodes)) {
                    parentNodes.remove(node);
                }
            }
        }
    }

    private void loadExtendMetadata(PermissionLoadContext loadContext, PermissionNode node) {
        node.setDisplayValue(TranslateUtils.translateValues(node.getDisplayValue()));
        if (node instanceof ActionPermissionNode) {
            ActionPermissionNode actionPermissionNode = (ActionPermissionNode) node;
            String model = actionPermissionNode.getModel();
            String actionName = actionPermissionNode.getAction();
            if (StringUtils.isNoneBlank(model, actionName)) {
                loadContext.addExtendAction(model, actionName);
            }
        }
        List<PermissionNode> nextNodes = node.getNodes();
        if (CollectionUtils.isNotEmpty(nextNodes)) {
            for (PermissionNode nextNode : nextNodes) {
                loadExtendMetadata(loadContext, nextNode);
            }
        }
    }

    protected boolean fillCanAccess(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
        boolean hasAnyCanAllot = false;
        Iterator<PermissionNode> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            PermissionNode node = nodeIterator.next();
            fillCanAccess(loadContext, node);
            boolean isCanAllot = Boolean.TRUE.equals(node.getCanAllot());
            if (isCanAllot) {
                hasAnyCanAllot = true;
            }
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                if (fillCanAccess(loadContext, children)) {
                    hasAnyCanAllot = true;
                    isCanAllot = true;
                } else {
                    children.clear();
                }
            }
            if (!isCanAllot) {
                nodeIterator.remove();
            }
        }
        return hasAnyCanAllot;
    }

    protected boolean fillCanAccess(PermissionLoadContext loadContext, PermissionNode node) {
        ResourcePermissionSubtypeEnum nodeType = node.getNodeType();
        if (nodeType == null) {
            node.setCanAccess(Boolean.FALSE);
            node.setCanManagement(Boolean.FALSE);
            node.setCanAllot(Boolean.FALSE);
            return false;
        }
        switch (nodeType) {
            case MODULE:
                if (node instanceof ModulePermissionNode) {
                    ModulePermissionNode modulePermissionNode = (ModulePermissionNode) node;
                    String module = modulePermissionNode.getModule();
                    node.setCanAccess(loadContext.isAccessModule(module));
                    node.setCanManagement(loadContext.isManagementModule(module));
                    node.setCanAllot(loadContext.isAllotModule(module));
                } else {
                    log.error("Invalid module permission node. id: {}, resourceId: {}; path: {}", node.getId(), node.getResourceId(), node.getPath());
                }
                break;
            case HOMEPAGE:
                if (node instanceof HomepagePermissionNode) {
                    HomepagePermissionNode homepagePermissionNode = (HomepagePermissionNode) node;
                    String module = homepagePermissionNode.getModule();
                    node.setCanAccess(loadContext.isAccessHomepage(module));
                    node.setCanManagement(loadContext.isManagementHomepage(module));
                    node.setCanAllot(loadContext.isAllotHomepage(module));
                } else {
                    log.error("Invalid homepage permission node. id: {}, resourceId: {}; path: {}", node.getId(), node.getResourceId(), node.getPath());
                }
                break;
            case MENU:
                if (node instanceof MenuPermissionNode) {
                    MenuPermissionNode homepagePermissionNode = (MenuPermissionNode) node;
                    String module = homepagePermissionNode.getModule();
                    String name = homepagePermissionNode.getName();
                    node.setCanAccess(loadContext.isAccessMenu(module, name));
                    node.setCanManagement(loadContext.isManagementMenu(module, name));
                    node.setCanAllot(loadContext.isAllotMenu(module, name));
                } else {
                    log.error("Invalid menu permission node. id: {}, resourceId: {}; path: {}", node.getId(), node.getResourceId(), node.getPath());
                }
                break;
            case SERVER_ACTION:
            case VIEW_ACTION:
            case URL_ACTION:
            case CLIENT_ACTION:
                if (node instanceof ActionPermissionNode) {
                    ActionPermissionNode actionPermissionNode = (ActionPermissionNode) node;
                    String module = actionPermissionNode.getModule();
                    String path = actionPermissionNode.getPath();
                    node.setCanAccess(loadContext.isAccessAction(path));
                    node.setCanManagement(loadContext.isManagementAction(path));
                    node.setCanAllot(loadContext.isAllotAction(module, path));
                } else {
                    log.error("Invalid action permission node. id: {}, resourceId: {}; path: {}", node.getId(), node.getResourceId(), node.getPath());
                }
                break;
            default:
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
        }
        return true;
    }

    protected List<PermissionNode> buildNextPermissionsWithCache(PermissionNode node) {
        if (!ResourcePermissionSubtypeEnum.VIEW_ACTION.equals(node.getNodeType())) {
            return null;
        }
        return PermissionNodeCache.get(node.getPath(), () -> buildNextPermissions(node));
    }

    protected static class SortAndCollectionMenuResult {

        private final boolean isAllCanAccess;

        private final boolean isAllCanManagement;

        public SortAndCollectionMenuResult(boolean isAllCanAccess, boolean isAllCanManagement) {
            this.isAllCanAccess = isAllCanAccess;
            this.isAllCanManagement = isAllCanManagement;
        }

        public boolean getIsAllCanAccess() {
            return isAllCanAccess;
        }

        public boolean getIsAllCanManagement() {
            return isAllCanManagement;
        }
    }
}
