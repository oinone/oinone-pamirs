package pro.shushi.pamirs.auth.api.loader.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeLoadExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.helper.AuthNodeConvertHelper;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽象权限节点加载器
 *
 * @author Adamancy Zhang at 10:03 on 2024-01-15
 */
public abstract class AbstractResourcePermissionNodeLoader {

    @Autowired
    protected AuthAccessService authAccessService;

    // region module

    protected List<UeModule> fetchAllModules() {
        return Models.origin().queryListByWrapper(Pops.<UeModule>lambdaQuery().from(UeModule.MODEL_MODEL)
                .eq(UeModule::getApplication, Boolean.TRUE)
                .in(UeModule::getState, ModuleStateEnum.INSTALLED, ModuleStateEnum.TOUPGRADE)
                .orderByAsc(UeModule::getPriority));
    }

    protected Set<String> fetchAllotAccessModulesByAuth() {
        Result<Set<String>> allotAccessModuleResult = authAccessService.canAccessModules();
        Set<String> allotAccessModules;
        if (allotAccessModuleResult != null && allotAccessModuleResult.getSuccess()) {
            allotAccessModules = allotAccessModuleResult.getData();
            if (allotAccessModules != null) {
                allotAccessModules = convertPathToModules(allotAccessModules);
            }
        } else {
            allotAccessModules = Collections.emptySet();
        }
        return allotAccessModules;
    }

    protected Set<String> fetchAllotManagementModulesByAuth() {
        Result<Set<String>> allotManagementModuleResult = authAccessService.canManagementModules();
        Set<String> allotManagementModules;
        if (allotManagementModuleResult != null && allotManagementModuleResult.getSuccess()) {
            allotManagementModules = allotManagementModuleResult.getData();
            if (allotManagementModules != null) {
                allotManagementModules = convertPathToModules(allotManagementModules);
            }
        } else {
            allotManagementModules = Collections.emptySet();
        }
        return allotManagementModules;
    }

    protected Set<String> convertPathToModules(Set<String> accessModules) {
        return accessModules.stream()
                .map(ResourcePath::resolveFirstPath)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    protected List<UeModule> fetchAllotAccessModules(List<UeModule> allModules, Set<String> allotAccessModules, Set<String> otherAllotAccessModules) {
        if (allotAccessModules == null) {
            return allModules;
        }
        Set<String> finalAllotManagementModules = Sets.union(allotAccessModules, otherAllotAccessModules);
        if (CollectionUtils.isEmpty(finalAllotManagementModules)) {
            return new ArrayList<>();
        }
        return allModules.stream()
                .filter(v -> authAccessService.canAccessModule(v.getModule()).getSuccess() || otherAllotAccessModules.contains(v.getModule()))
                .collect(Collectors.toList());
    }

    protected List<UeModule> fetchAllotManagementModules(List<UeModule> allModules, Set<String> allotManagementModules, Set<String> otherAllotManagementModules) {
        if (allotManagementModules == null) {
            return allModules;
        }
        Set<String> finalAllotManagementModules = Sets.union(allotManagementModules, otherAllotManagementModules);
        if (CollectionUtils.isEmpty(finalAllotManagementModules)) {
            return new ArrayList<>();
        }
        return allModules.stream()
                .filter(v -> authAccessService.canManagementModule(v.getModule()).getSuccess() || otherAllotManagementModules.contains(v.getModule()))
                .collect(Collectors.toList());
    }

    protected List<PermissionNode> convertModuleNodes(List<UeModule> modules, BiConsumer<UeModule, ModulePermissionNode> consumer) {
        List<PermissionNode> nodes = new ArrayList<>(modules.size());
        for (UeModule module : modules) {
            ModulePermissionNode node = convertModuleNode(module);
            consumer.accept(module, node);
            nodes.add(node);
        }
        return nodes;
    }

    protected void convertModuleNode(String module, ModulePermissionNode node, Map<String, List<Menu>> allotModuleMenuMep) {
        node.setHasNext(CollectionUtils.isNotEmpty(allotModuleMenuMep.get(module)));
        node.setCanAccess(authAccessService.canAccessModule(module).getSuccess());
        node.setCanAllot(authAccessService.canManagementModule(module).getSuccess());
    }

    protected void convertModuleNode(PermissionLoadContext loadContext, String module, ModulePermissionNode node, Map<String, List<Menu>> allotModuleMenuMep) {
        node.setHasNext(CollectionUtils.isNotEmpty(allotModuleMenuMep.get(module)));
        node.setCanAccess(loadContext.isAccessModule(module));
        node.setCanManagement(loadContext.isManagementModule(module));
        node.setCanAllot(loadContext.isAllotModule(module));
    }

    protected ModulePermissionNode convertModuleNode(UeModule module) {
        ModulePermissionNode node = AuthNodeConvertHelper.convertModuleNode(module);
        node.setNodes(new ArrayList<>());
        if (StringUtils.isBlank(node.getIcon())) {
            node.setIcon(CommonConstants.getDefaultAppLogoUrl());
        }
        return node;
    }

    // endregion

    // region homepage

    protected Set<String> fetchAllotAccessHomepagesByAuth() {
        Result<Set<String>> allotAccessHomepageResult = authAccessService.canAccessHomepages();
        Set<String> allotAccessHomepages;
        if (allotAccessHomepageResult != null && allotAccessHomepageResult.getSuccess()) {
            allotAccessHomepages = allotAccessHomepageResult.getData();
            if (allotAccessHomepages != null) {
                allotAccessHomepages = convertPathToModules(allotAccessHomepages);
            }
        } else {
            allotAccessHomepages = Collections.emptySet();
        }
        return allotAccessHomepages;
    }

    protected Set<String> fetchAllotManagementHomepagesByAuth() {
        Result<Set<String>> allotManagementHomepageResult = authAccessService.canManagementHomepages();
        Set<String> allotManagementHomepages;
        if (allotManagementHomepageResult != null && allotManagementHomepageResult.getSuccess()) {
            allotManagementHomepages = allotManagementHomepageResult.getData();
            if (allotManagementHomepages != null) {
                allotManagementHomepages = convertPathToModules(allotManagementHomepages);
            }
        } else {
            allotManagementHomepages = Collections.emptySet();
        }
        return allotManagementHomepages;
    }

    protected Map<String, ViewAction> fetchAllotAccessHomepages(List<UeModule> homepageModules) {
        Set<String> allotAccessHomepages = fetchAllotAccessHomepagesByAuth();
        if (allotAccessHomepages == null) {
            return collectionModuleHomepagesByModules(homepageModules);
        }
        if (allotAccessHomepages.isEmpty()) {
            return Collections.emptyMap();
        }
        homepageModules = homepageModules.stream()
                .filter(v -> authAccessService.canAccessHomepage(v.getModule()).getSuccess())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(homepageModules)) {
            return Collections.emptyMap();
        }
        return collectionModuleHomepagesByModules(homepageModules);
    }

    protected Map<String, ViewAction> fetchAllotManagementHomepages(List<UeModule> homepageModules, Set<String> allotManagementModules) {
        Set<String> allotManagementHomepages = fetchAllotManagementHomepagesByAuth();
        if (allotManagementHomepages == null) {
            return collectionModuleHomepagesByModules(homepageModules);
        }
        Set<String> finalAllotManagementHomepages = Sets.union(allotManagementHomepages, allotManagementModules);
        if (CollectionUtils.isEmpty(finalAllotManagementHomepages)) {
            return Collections.emptyMap();
        }
        homepageModules = homepageModules.stream()
                .filter(v -> authAccessService.canManagementModule(v.getModule()).getSuccess() || allotManagementModules.contains(v.getModule()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(homepageModules)) {
            return Collections.emptyMap();
        }
        return collectionModuleHomepagesByModules(homepageModules);
    }

    protected Map<String, ViewAction> collectionModuleHomepagesByModules(List<UeModule> modules) {
        List<ViewAction> homepageActions = DataShardingHelper.build().sharding(modules, (sublist) -> Models.origin().queryListByWrapper(Pops.<ViewAction>lambdaQuery()
                .from(ViewAction.MODEL_MODEL)
                .setBatchSize(-1)
                .in(Arrays.asList(ViewAction::getModel, ViewAction::getName),
                        sublist.stream().map(UeModule::getHomePageModel).collect(Collectors.toList()),
                        sublist.stream().map(UeModule::getHomePageName).collect(Collectors.toList()))
        ));
        MemoryListSearchCache<String, ViewAction> homepageActionCache = new MemoryListSearchCache<>(homepageActions, v -> Action.sign(v.getModel(), v.getName()));
        Map<String, ViewAction> moduleHomepages = new HashMap<>();
        for (UeModule module : modules) {
            String sign = Action.sign(module.getHomePageModel(), module.getHomePageName());
            ViewAction homepageAction = homepageActionCache.get(sign);
            if (homepageAction != null) {
                moduleHomepages.put(module.getModule(), homepageAction);
            }
        }
        return moduleHomepages;
    }

    protected HomepagePermissionNode convertHomepageNode(UeModule module) {
        return AuthNodeConvertHelper.convertHomepageNode(module);
    }

    protected HomepagePermissionNode convertHomepageNode(UeModule module, ViewAction action) {
        HomepagePermissionNode node = AuthNodeConvertHelper.convertHomepageNode(module, action);
        String moduleModule = module.getModule();
        node.setCanAccess(authAccessService.canAccessHomepage(moduleModule).getSuccess());
        node.setCanAllot(authAccessService.canManagementHomepage(moduleModule).getSuccess());
        return node;
    }

    protected HomepagePermissionNode convertHomepageNode(PermissionLoadContext loadContext, UeModule module, ViewAction action) {
        HomepagePermissionNode node = AuthNodeConvertHelper.convertHomepageNode(module, action);
        String moduleModule = module.getModule();
        node.setCanAccess(loadContext.isAccessHomepage(moduleModule));
        node.setCanManagement(loadContext.isManagementHomepage(moduleModule));
        node.setCanAllot(loadContext.isAllotHomepage(moduleModule));
        return node;
    }

    // endregion

    // region menu

    protected Map<String, Set<String>> fetchAllotAccessMenusByAuth(Set<String> modules) {
        Result<Map<String, Set<String>>> allotAccessModuleMenuResult = authAccessService.canAccessMenus(modules);
        Map<String, Set<String>> allotAccessModuleMenus;
        if (allotAccessModuleMenuResult != null && allotAccessModuleMenuResult.getSuccess()) {
            allotAccessModuleMenus = allotAccessModuleMenuResult.getData();
            if (allotAccessModuleMenus != null) {
                allotAccessModuleMenus = allotAccessModuleMenus.entrySet().stream().filter(entry -> CollectionUtils.isNotEmpty(entry.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
        } else {
            allotAccessModuleMenus = Collections.emptyMap();
        }
        return allotAccessModuleMenus;
    }

    protected Map<String, Set<String>> fetchAllotManagementMenusByAuth(Set<String> modules) {
        Result<Map<String, Set<String>>> allotManagementModuleMenuResult = authAccessService.canManagementMenus(modules);
        Map<String, Set<String>> allotManagementModuleMenus;
        if (allotManagementModuleMenuResult != null && allotManagementModuleMenuResult.getSuccess()) {
            allotManagementModuleMenus = allotManagementModuleMenuResult.getData();
            if (allotManagementModuleMenus != null) {
                allotManagementModuleMenus = allotManagementModuleMenus.entrySet().stream().filter(entry -> CollectionUtils.isNotEmpty(entry.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
        } else {
            allotManagementModuleMenus = Collections.emptyMap();
        }
        return allotManagementModuleMenus;
    }

    protected Map<String, List<Menu>> fetchAllotAccessMenus(Set<String> modules) {
        Map<String, Set<String>> allotAccessModuleMenus = fetchAllotAccessMenusByAuth(modules);
        if (allotAccessModuleMenus == null) {
            return collectionModuleMenus(fetchModuleMenus(modules));
        }
        modules = allotAccessModuleMenus.keySet();
        if (CollectionUtils.isEmpty(modules)) {
            return Collections.emptyMap();
        }
        List<Menu> allMenus = fetchModuleMenus(modules);
        return collectionModuleMenusWithParentMenu(modules, allMenus, menu -> {
            String module = menu.getModule();
            return authAccessService.canAccessMenu(module, menu.getName()).getSuccess();
        });
    }

    protected Map<String, List<Menu>> fetchAllotManagementMenus(Set<String> modules, Set<String> allotManagementModules) {
        Map<String, Set<String>> allotManagementModuleMenus = fetchAllotManagementMenusByAuth(modules);
        if (allotManagementModuleMenus == null) {
            return collectionModuleMenus(fetchModuleMenus(modules));
        }
        modules = Sets.union(allotManagementModuleMenus.keySet(), allotManagementModules);
        if (CollectionUtils.isEmpty(modules)) {
            return Collections.emptyMap();
        }
        List<Menu> allMenus = fetchModuleMenus(modules);
        return collectionModuleMenusWithParentMenu(modules, allMenus, menu -> {
            String module = menu.getModule();
            return allotManagementModules.contains(module) || authAccessService.canManagementMenu(module, menu.getName()).getSuccess();
        });
    }

    protected Map<String, List<Menu>> collectionModuleMenus(List<Menu> allMenus) {
        Map<String, List<Menu>> moduleMenus = new HashMap<>(8);
        for (Menu menu : allMenus) {
            moduleMenus.computeIfAbsent(menu.getModule(), k -> new ArrayList<>()).add(menu);
        }
        return moduleMenus;
    }

    protected Map<String, List<Menu>> collectionModuleMenusWithParentMenu(Set<String> modules, List<Menu> allMenus, Predicate<Menu> predicate) {
        MemoryListSearchCache<String, Menu> allMenuCache = new MemoryListSearchCache<>(allMenus, v -> Menu.sign(v.getModule(), v.getName()));
        Map<String, List<Menu>> moduleMenus = new HashMap<>(modules.size());
        Set<String> repeatSet = new HashSet<>(allMenus.size());
        for (Menu menu : allMenus) {
            String module = menu.getModule();
            if (!predicate.evaluate(menu)) {
                continue;
            }
            String menuSign = Menu.sign(module, menu.getName());
            if (ObjectHelper.isNotRepeat(repeatSet, menuSign)) {
                moduleMenus.computeIfAbsent(module, k -> new ArrayList<>()).add(menu);
                String parentName = menu.getParentName();
                while (StringUtils.isNotBlank(parentName)) {
                    String parentMenuSign = Menu.sign(module, parentName);
                    Menu parentMenu = allMenuCache.get(parentMenuSign);
                    if (parentMenu == null) {
                        break;
                    }
                    if (ObjectHelper.isRepeat(repeatSet, parentMenuSign)) {
                        break;
                    }
                    moduleMenus.computeIfAbsent(module, k -> new ArrayList<>()).add(parentMenu);
                    parentName = parentMenu.getParentName();
                }
            }
        }
        return moduleMenus;
    }

    protected List<Menu> fetchModuleMenus(Set<String> modules) {
        return DataShardingHelper.build(20).collectionSharding(modules.stream().filter(v -> !AuthHelper.isModuleInWhite(v)).collect(Collectors.toSet()),
                (sublist) -> Models.origin().queryListByWrapper(Pops.<Menu>lambdaQuery().from(Menu.MODEL_MODEL)
                        .setBatchSize(-1)
                        .and(v -> v.eq(Menu::getShow, ActiveEnum.ACTIVE.value())
                                .or(vv -> vv.isNull(Menu::getShow).eq(Menu::getDefaultShow, ActiveEnum.ACTIVE.value())))
                        .in(Menu::getModule, sublist)));
    }

    protected MenuPermissionNode convertMenuNode(Menu menu) {
        MenuPermissionNode node = AuthNodeConvertHelper.convertMenuNode(menu);
        node.setNodes(new ArrayList<>());
        String module = menu.getModule();
        String name = menu.getName();
        node.setCanAccess(authAccessService.canAccessMenu(module, name).getSuccess());
        node.setCanAllot(authAccessService.canManagementMenu(module, name).getSuccess());
        return node;
    }

    protected MenuPermissionNode convertMenuNode(Menu menu, PermissionNode parentNode) {
        MenuPermissionNode node = convertMenuNode(menu);
        if (parentNode != null) {
            node.setParentId(parentNode.getId());
            node.setParent(parentNode);
            parentNode.setHasNext(Boolean.TRUE);
        }
        return node;
    }

    protected MenuPermissionNode convertMenuNode(Menu menu, PermissionLoadContext loadContext) {
        MenuPermissionNode node = AuthNodeConvertHelper.convertMenuNode(menu);
        node.setNodes(new ArrayList<>());
        String module = menu.getModule();
        String name = menu.getName();
        if (loadContext == null) {
            node.setCanAccess(authAccessService.canAccessMenu(module, name).getSuccess());
            node.setCanAllot(authAccessService.canManagementModule(module).getSuccess() || authAccessService.canManagementMenu(module, name).getSuccess());
        } else {
            node.setCanAccess(loadContext.isAccessMenu(module, name));
            node.setCanAllot(loadContext.isAllotMenu(module, name));
        }
        if (loadContext != null) {
            node.setCanManagement(loadContext.isManagementMenu(module, name));
        }
        return node;
    }

    protected MenuPermissionNode convertMenuNode(Menu menu, PermissionNode parentNode, PermissionLoadContext loadContext) {
        MenuPermissionNode node = convertMenuNode(menu, loadContext);
        if (parentNode != null) {
            node.setParentId(parentNode.getId());
            node.setParent(parentNode);
            parentNode.setHasNext(Boolean.TRUE);
        }
        return node;
    }

    // endregion

    protected void extendLoadAllNodes(PermissionLoadContext defaultLoadContext, BiFunction<PermissionNodeLoadExtendApi, PermissionLoadContext, List<PermissionNode>> consumer) {
        List<PermissionNode> extendNodes = extendLoad(defaultLoadContext, consumer);
        processExtendAllPermissionNodes(defaultLoadContext, extendNodes);
    }

    protected void extendLoadRootNodes(PermissionLoadContext defaultLoadContext, BiFunction<PermissionNodeLoadExtendApi, PermissionLoadContext, List<PermissionNode>> consumer) {
        List<PermissionNode> extendNodes = extendLoad(defaultLoadContext, consumer);
        processExtendRootPermissionNodes(defaultLoadContext, extendNodes);
    }

    protected void extendLoadNextNodes(PermissionNode selected, Function<PermissionNodeLoadExtendApi, List<PermissionNode>> consumer) {
        List<PermissionNode> extendNodes = extendLoad(consumer);
        processExtendNextPermissionNodes(selected, extendNodes);
    }

    protected List<PermissionNode> extendLoad(PermissionLoadContext defaultLoadContext, BiFunction<PermissionNodeLoadExtendApi, PermissionLoadContext, List<PermissionNode>> consumer) {
        List<PermissionNode> extendNodes = new ArrayList<>();
        for (PermissionNodeLoadExtendApi extendApi : Spider.getLoader(PermissionNodeLoadExtendApi.class).getOrderedExtensions()) {
            PermissionLoadContext currentLoadContext = extendApi.generatorLoadContext(defaultLoadContext);
            if (currentLoadContext == null) {
                currentLoadContext = defaultLoadContext;
            }
            List<PermissionNode> targetNodes = consumer.apply(extendApi, currentLoadContext);
            if (CollectionUtils.isNotEmpty(targetNodes)) {
                extendNodes.addAll(targetNodes);
            }
        }
        return extendNodes;
    }

    protected List<PermissionNode> extendLoad(Function<PermissionNodeLoadExtendApi, List<PermissionNode>> consumer) {
        List<PermissionNode> extendNodes = new ArrayList<>();
        for (PermissionNodeLoadExtendApi extendApi : Spider.getLoader(PermissionNodeLoadExtendApi.class).getOrderedExtensions()) {
            List<PermissionNode> targetNodes = consumer.apply(extendApi);
            if (CollectionUtils.isNotEmpty(targetNodes)) {
                extendNodes.addAll(targetNodes);
            }
        }
        return extendNodes;
    }

    protected void processExtendAllPermissionNodes(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
    }

    protected void processExtendRootPermissionNodes(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
    }

    protected void processExtendNextPermissionNodes(PermissionNode selected, List<PermissionNode> nodes) {
    }
}
