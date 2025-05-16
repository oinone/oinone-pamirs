package pro.shushi.pamirs.auth.api.helper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.fetch.*;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取资源帮助类
 *
 * @author Adamancy Zhang at 19:40 on 2024-03-02
 */
public class FetchResourceHelper {

    private FetchResourceHelper() {
        // reject create object
    }

    public static List<UeModule> fetchModules(Set<Long> ids) {
        return DataShardingHelper.build().collectionSharding(ids, (sublist) -> Models.origin().queryListByWrapper(Pops.<UeModule>lambdaQuery()
                .from(UeModule.MODEL_MODEL)
                .select(UeModule::getId, UeModule::getModule, UeModule::getName, UeModule::getHomePageModel, UeModule::getDefaultHomePageModel, UeModule::getHomePageName, UeModule::getDefaultHomePageName)
                .setBatchSize(-1)
                .in(UeModule::getId, sublist)));
    }

    public static List<ViewAction> fetchHomepageActions(List<UeModule> modules) {
        return DataShardingHelper.build().collectionSharding(modules, (sublist) -> Models.origin().queryListByWrapper(Pops.<ViewAction>lambdaQuery()
                .from(ViewAction.MODEL_MODEL)
                .select(ViewAction::getId, ViewAction::getModel, ViewAction::getName, ViewAction::getActionType)
                .setBatchSize(-1)
                .in(ViewAction::getModel, sublist.stream().map(UeModule::getHomePageModel).collect(Collectors.toList()))
                .in(ViewAction::getName, sublist.stream().map(UeModule::getHomePageName).collect(Collectors.toList()))));
    }

    public static List<Menu> fetchMenusAndFillParent(Set<Long> ids) {
        List<Menu> menus = fetchMenus(ids);
        // FIXME: zbh 20240911 此处需要优化菜单查询效率
//        Set<String> menuModules = menus.stream().map(Menu::getModule).collect(Collectors.toSet());
//        List<Menu> allModuleMenus = FetchResourceHelper.fetchModuleMenus(menuModules);
        MemoryListSearchCache<String, Menu> menuCache = new MemoryListSearchCache<>(menus, v -> Menu.sign(v.getModule(), v.getName()));
        fillParentMenus(menuCache, menus);
        return menus;
    }

    public static List<Menu> fetchMenus(Set<Long> ids) {
        return DataShardingHelper.build().collectionSharding(ids, (sublist) -> Models.origin().queryListByWrapper(Pops.<Menu>lambdaQuery()
                .from(Menu.MODEL_MODEL)
                .select(Menu::getId, Menu::getModule, Menu::getName, Menu::getParentName, Menu::getDefaultParentName, Menu::getModel, Menu::getActionName, Menu::getActionType)
                .setBatchSize(-1)
                .isNotNull(Menu::getModel)
                .isNotNull(Menu::getActionName)
                .in(Menu::getId, sublist)));
    }

    public static List<Menu> fetchModuleMenus(Set<String> modules) {
        return Models.origin().queryListByWrapper(Pops.<Menu>lambdaQuery()
                .from(Menu.MODEL_MODEL)
                .select(Menu::getId, Menu::getModule, Menu::getName, Menu::getParentName, Menu::getDefaultParentName, Menu::getModel, Menu::getActionName, Menu::getActionType)
                .in(Menu::getModule, modules));
    }

    public static List<Menu> fetchMenus(List<String> modules, List<String> names) {
        return Models.origin().queryListByWrapper(Pops.<Menu>lambdaQuery()
                .from(Menu.MODEL_MODEL)
                .select(Menu::getId, Menu::getModule, Menu::getName, Menu::getParentName, Menu::getDefaultParentName, Menu::getModel, Menu::getActionName, Menu::getActionType)
                .isNotNull(Menu::getModel)
                .isNotNull(Menu::getActionName)
                .in(Arrays.asList(Menu::getModule, Menu::getName), modules, names));
    }

    public static void fillParentMenus(MemoryListSearchCache<String, Menu> menuCache, List<Menu> menus) {
        List<String> parentModules = new ArrayList<>(menus.size());
        List<String> parentNames = new ArrayList<>(menus.size());
        Map<String, Menu> setterMap = new HashMap<>();
        for (Menu menu : menus) {
            String parentName = menu.getParentName();
            while (StringUtils.isNotBlank(parentName)) {
                String module = menu.getModule();
                String menuSign = Menu.sign(module, parentName);
                Menu parentMenu = menuCache.get(menuSign);
                if (parentMenu == null) {
                    parentModules.add(module);
                    parentNames.add(parentName);
                    setterMap.put(menuSign, menu);
                    break;
                } else {
                    List<Menu> children = parentMenu.getChildren();
                    if (children == null) {
                        parentMenu.setChildren(Collections.singletonList(null));
                    }
                    menu = parentMenu;
                    parentName = menu.getParentName();
                }
            }
        }
        if (CollectionUtils.isEmpty(parentNames)) {
            return;
        }
        List<Menu> parentMenus = FetchResourceHelper.fetchMenus(parentModules, parentNames);
        if (CollectionUtils.isEmpty(parentMenus)) {
            return;
        }
        for (Menu parentMenu : parentMenus) {
            String module = parentMenu.getModule();
            String name = parentMenu.getName();
            Menu target = setterMap.get(Menu.sign(module, name));
            if (target != null) {
                List<Menu> children = parentMenu.getChildren();
                if (children == null) {
                    parentMenu.setChildren(Collections.singletonList(null));
                }
            }
        }
        fillParentMenus(menuCache, parentMenus);
    }

    public static <T extends Action> List<T> fetchActions(Set<Long> actionResourceIds, String model) {
        return DataShardingHelper.build().collectionSharding(actionResourceIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<T>lambdaQuery()
                .from(model)
                .select(Action::getId, Action::getModel, Action::getName)
                .setBatchSize(-1)
                .in(Action::getId, sublist)));
    }

    public static Map<ResourcePermissionSubtypeEnum, AuthResourceFetchMethod<? extends MetaBaseModel>> buildResourceMethodMap() {
        AuthAccessService authAccessService = BeanDefinitionUtils.getBean(AuthAccessService.class);
        return MapHelper.<ResourcePermissionSubtypeEnum, AuthResourceFetchMethod<?>>newInstance()
                .put(ResourcePermissionSubtypeEnum.MODULE, new AuthModuleResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.HOMEPAGE, new AuthHomepageResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.MENU, new AuthMenuResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.SERVER_ACTION, new AuthServerActionResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.VIEW_ACTION, new AuthViewActionResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.URL_ACTION, new AuthUrlActionResourceFetchMethod(authAccessService))
                .put(ResourcePermissionSubtypeEnum.CLIENT_ACTION, new AuthClientActionResourceFetchMethod(authAccessService))
                .build();
    }
}
