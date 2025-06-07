package pro.shushi.pamirs.boot.web.spi.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.SystemConfigKeyConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.BindingTypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.tmodel.AdvancedHomePageConfig;
import pro.shushi.pamirs.boot.base.tmodel.HomePageConfigRules;
import pro.shushi.pamirs.boot.web.entity.MenuNode;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.boot.web.spi.api.HomepageFetcherApi;
import pro.shushi.pamirs.boot.web.spi.api.UserAndAuthApi;
import pro.shushi.pamirs.boot.web.spi.holder.AuthVerificationApiHolder;
import pro.shushi.pamirs.boot.web.utils.MemoryMetadataHelper;
import pro.shushi.pamirs.boot.web.utils.MenuNodeHelper;
import pro.shushi.pamirs.boot.web.utils.PageLoadHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.Resource;
import java.util.*;

import static pro.shushi.pamirs.meta.enmu.ModuleStateEnum.INSTALLED;
import static pro.shushi.pamirs.meta.enmu.ModuleStateEnum.TOUPGRADE;

/**
 * 默认首页获取服务
 *
 * @author Adamancy Zhang at 18:24 on 2023-12-21
 */
@Component
@Order
@SPI.Service
@Slf4j
public class DefaultHomepageFetcher implements HomepageFetcherApi {

    @Resource
    private AppConfigService appConfigService;

    @Resource
    private MetaCacheManager metaCacheManager;

    @Override
    public UeModule fetchGlobalHomepage() {
        AppConfig appConfig = appConfigService.fetchGlobalConfig();
        if (appConfig == null) {
            return fetchDefaultGlobalHomepage();
        }

        AdvancedHomePageConfig globalHomePage = null;
        Map<String, Object> extend = appConfig.getExtend();
        if (extend != null) {
            globalHomePage = Optional.ofNullable(extend.get(SystemConfigKeyConstants.ADVANCED_HOME_PAGE))
                    .map(value -> JsonUtils.parseMap2Object((Map<String, Object>) value, AdvancedHomePageConfig.class))
                    .orElse(null);
        }
        if (globalHomePage == null || Boolean.FALSE.equals(globalHomePage.getState()) || CollectionUtils.isEmpty(globalHomePage.getRules())) {
            return fetchDefaultGlobalHomepage();
        }

        Map<String, Object> map = loadContextInfo();
        for (HomePageConfigRules rule : globalHomePage.getRules()) {
            UeModule ueModule = validateRule(rule, map);
            if (ueModule == null) {
                continue;
            }
            if (BindingTypeEnum.MENU.equals(rule.getBindingType())) {
                Menu bindHomePageMenu = rule.getBindHomePageMenu();
                if (bindHomePageMenu != null && bindHomePageMenu.getModule() != null && bindHomePageMenu.getName() != null) {
                    List<Menu> menus = PageLoadHelper.fetchMenus(bindHomePageMenu.getModule());
                    if (CollectionUtils.isNotEmpty(menus)) {
                        for (Menu menu : menus) {
                            if (bindHomePageMenu.getName().equals(menu.getName())) {
                                if (AuthApi.get().canAccessMenu(menu.getModule(), menu.getName()).getSuccess()) {
                                    ueModule.setHomePageModel(menu.getModel()).setHomePageName(menu.getActionName());
                                    return ueModule;
                                }
                            }
                        }
                    }
                }
            } else {
                View view = rule.getBindHomePageView();
                if (view != null && view.getName() != null && view.getModel() != null) {
                    String actionName = rule.getCode();
                    String actionModel = view.getModel();
                    ViewAction viewAction = null;
                    Action action = metaCacheManager.fetchAction(actionModel, actionName);
                    if (action instanceof ViewAction) {
                        viewAction = (ViewAction) action;
                    }
                    if (viewAction != null) {
                        String module = viewAction.getModule();
                        if (ueModule.getModule().equals(module)) {
                            AccessResourceInfo backupInfo = AccessResourceInfoSession.getInfo();
                            AccessResourceInfo info = PageLoadHelper.generatorAccessResourceInfo(ueModule.getModule(), viewAction);
                            AccessResourceInfoSession.setInfo(info);
                            String path = ResourcePath.generatorPath(viewAction.getModel(), viewAction.getName());
                            viewAction.setSessionPath(path);
                            if (AuthApi.get().canAccessAction(path).getSuccess()) {
                                ueModule.setHomePageModel(viewAction.getModel());
                                ueModule.setHomePageName(viewAction.getName());
                                return ueModule;
                            } else {
                                AccessResourceInfoSession.setInfo(backupInfo);
                            }
                        }
                    }
                }
            }
        }

        return fetchDefaultGlobalHomepage();
    }


    /**
     * 首页规则校验是否有效
     *
     * @param rule 单条规则
     * @param map  用户/角色/....
     * @return module
     */
    private UeModule validateRule(HomePageConfigRules rule, Map<String, Object> map) {
        if (Boolean.FALSE.equals(rule.getEnabled())) {
            return null;
        }
        if (StringUtils.isEmpty(rule.getRuleName())) {
            return null;
        }
        UeModule ueModule = rule.getBindHomePageModule();
        if (ueModule == null) {
            return null;
        }
        if (StringUtils.isEmpty(ueModule.getModule())) {
            return null;
        }
        String expression = rule.getExpression();
        if (StringUtils.isEmpty(expression)) {
            return null;
        }
        Boolean flag = Boolean.FALSE;
        try {
            flag = Exp.<Boolean>run(expression, map);
        } catch (Exception e) {
            log.error("expression 解析错误", e);
            return null;
        }
        if (Boolean.FALSE.equals(Optional.ofNullable(flag).orElse(Boolean.FALSE))) {
            return null;
        }
        return ueModule;
    }

    public Map<String, Object> loadContextInfo() {
        HashMap<String, Object> result = new HashMap<>();
        // 通过namespace获取function列表,调用全部函数. 为了支持独立部署时,远程调用获取前端低代码文件
        List<FunctionDefinition> loadFunctions = Models.origin().queryListByWrapper(Pops.<FunctionDefinition>lambdaQuery().from(FunctionDefinition.MODEL_MODEL).setBatchSize(-1).eq(FunctionDefinition::getNamespace, UserAndAuthApi.FUN_NAMESPACE));
        if (CollectionUtils.isEmpty(loadFunctions)) {
            return result;
        }

        for (FunctionDefinition loadFunction : loadFunctions) {
            try {
                Map<String, Object> tempMap = Fun.run(loadFunction.getNamespace(), loadFunction.getFun());
                if (tempMap != null) {
                    result.putAll(tempMap);
                }
            } catch (Exception e) {
                // 支持远程调用,如果调用失败,不影响启动
                log.error("警告! 函数错误,可忽略. namespace:" + loadFunction.getNamespace() + ", fun:" + loadFunction.getFun(), e);
            }
        }

        return result;
    }

    public UeModule fetchDefaultGlobalHomepage() {
        LambdaQueryWrapper<ModuleDefinition> wrapper = Pops.<ModuleDefinition>lambdaQuery().from(ModuleDefinition.MODEL_MODEL)
                .eq(ModuleDefinition::getApplication, true)
                .in(ModuleDefinition::getState, TOUPGRADE.value(), INSTALLED.value())
                .in(ModuleDefinition::getModule, Spider.getDefaultExtension(ModulesApi.class).modules())
                .has(ModuleDefinition::getClientTypes, PageLoadHelper.getCurrentClientType());
        Pagination<ModuleDefinition> page = new Pagination<>();
        page.orderBy(SortDirectionEnum.ASC, ModuleDefinition::getPriority);
        List<ModuleDefinition> list = Models.origin().queryListByWrapper(page, wrapper);
        list.addAll(MemoryMetadataHelper.fetchMemoryModules(list));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        AuthApi authApi = AuthApi.get();
        for (ModuleDefinition moduleDefinition : list) {
            String module = moduleDefinition.getModule();
            if (!authApi.canAccessModule(module).getSuccess()) {
                continue;
            }
            String homepageModel = moduleDefinition.getHomePageModel();
            String homepageName = moduleDefinition.getHomePageName();
            if (StringUtils.isNoneBlank(homepageModel, homepageName)) {
                Menu moduleMenu = isMenuAction(module, homepageModel, homepageName);
                if (moduleMenu != null) {
                    if (authApi.canAccessMenu(module, moduleMenu.getName()).getSuccess()) {
                        return ArgUtils.convert(ModuleDefinition.MODEL_MODEL, UeModule.MODEL_MODEL, moduleDefinition);
                    }
                } else if (authApi.canAccessHomepage(module).getSuccess()) {
                    return ArgUtils.convert(ModuleDefinition.MODEL_MODEL, UeModule.MODEL_MODEL, moduleDefinition);
                }
            }
            Menu menu = fetchHighPriorityMenu(module);
            if (menu != null) {
                moduleDefinition.setHomePageModel(menu.getModel())
                        .setHomePageName(menu.getActionName());
                return ArgUtils.convert(ModuleDefinition.MODEL_MODEL, UeModule.MODEL_MODEL, moduleDefinition);
            }
        }
        return null;
    }

    @Override
    public Action fetchApplicationHomePage(UeModule module, String homepageModel, String homepageName) {
        AppConfig appConfig = appConfigService.fetchGlobalConfig();
        if (appConfig == null) {
            return fetchDefaultApplicationHomepage(module, homepageModel, homepageName);
        }
        AdvancedHomePageConfig appHomePage = null;
        Map<String, Object> extend = appConfig.getExtend();
        if (extend != null) {
            appHomePage = Optional.ofNullable(extend.get(SystemConfigKeyConstants.ADVANCED_HOME_PAGE))
                    .map(value -> JsonUtils.parseMap2Object((Map<String, Object>) value, AdvancedHomePageConfig.class))
                    .orElse(null);
        }
        if (appHomePage == null || Boolean.FALSE.equals(appHomePage.getState()) || CollectionUtils.isEmpty(appHomePage.getRules())) {
            return fetchDefaultApplicationHomepage(module, homepageModel, homepageName);
        }

        Map<String, Object> map = loadContextInfo();
        for (HomePageConfigRules rule : appHomePage.getRules()) {
            UeModule ueModule = validateRule(rule, map);
            if (ueModule == null) {
                continue;
            }
            if (!ueModule.getModule().equals(module.getModule())) {
                continue;
            }
            if (BindingTypeEnum.MENU.equals(rule.getBindingType())) {
                Menu bindHomePageMenu = rule.getBindHomePageMenu();
                if (bindHomePageMenu != null && bindHomePageMenu.getModule() != null && bindHomePageMenu.getName() != null) {
                    List<Menu> menus = PageLoadHelper.fetchMenus(bindHomePageMenu.getModule());
                    if (CollectionUtils.isNotEmpty(menus)) {
                        for (Menu menu : menus) {
                            if (bindHomePageMenu.getName().equals(menu.getName())) {
                                if (AuthVerificationApiHolder.get().verifyHomepageActionAccess(module, menu.getModel(), menu.getActionName())) {
                                    return new ViewAction().setActionType(ActionTypeEnum.VIEW).setModel(menu.getModel()).setName(menu.getActionName());
                                }
                            }
                        }
                    }
                }
            } else {
                View view = rule.getBindHomePageView();
                if (view != null && view.getName() != null && view.getModel() != null) {
                    String actionName = rule.getCode();
                    String actionModel = view.getModel();
                    ViewAction viewAction = null;
                    Action action = metaCacheManager.fetchAction(actionModel, actionName);
                    if (action instanceof ViewAction) {
                        viewAction = (ViewAction) action;
                    }
                    if (viewAction != null) {
                        String viewModule = viewAction.getModule();
                        if (ueModule.getModule().equals(viewModule)) {
                            AccessResourceInfo backupInfo = AccessResourceInfoSession.getInfo();
                            AccessResourceInfo info = PageLoadHelper.generatorAccessResourceInfo(ueModule.getModule(), viewAction);
                            AccessResourceInfoSession.setInfo(info);
                            String path = ResourcePath.generatorPath(viewAction.getModel(), viewAction.getName());
                            viewAction.setSessionPath(path);
                            if (AuthApi.get().canAccessAction(path).getSuccess()) {
                                return viewAction;
                            } else {
                                AccessResourceInfoSession.setInfo(backupInfo);
                            }
                        }
                    }
                }
            }
        }

        return fetchDefaultApplicationHomepage(module, homepageModel, homepageName);
    }

    protected Menu isMenuAction(String module, String model, String actionName) {
        return PageLoadHelper.isMenuAction(module, model, actionName);
    }

    protected Action fetchDefaultApplicationHomepage(UeModule module, String homepageModel, String homepageName) {
        boolean isAccessHomepage;
        if (StringUtils.isAnyBlank(homepageModel, homepageName)) {
            isAccessHomepage = false;
        } else {
            isAccessHomepage = AuthVerificationApiHolder.get().verifyHomepageActionAccess(module, homepageModel, homepageName);
        }
        if (isAccessHomepage) {
            return new ViewAction().setActionType(ActionTypeEnum.VIEW).setModel(homepageModel).setName(homepageName);
        }
        Menu menu = fetchHighPriorityMenu(module.getModule());
        if (menu != null) {
            return new ViewAction().setActionType(ActionTypeEnum.VIEW).setModel(menu.getModel()).setName(menu.getActionName());
        }
        return null;
    }

    protected Menu fetchHighPriorityMenu(String module) {
        List<Menu> menus = PageLoadHelper.fetchValidMenus(module);
        if (CollectionUtils.isEmpty(menus)) {
            return null;
        }
        Collection<MenuNode> menuNodes = MenuNodeHelper.buildMenuTree(module, menus);
        MenuNode target = MenuNodeHelper.findHighPriorityMenu(menuNodes);
        if (target != null && StringUtils.isNoneBlank(target.menu.getModel(), target.menu.getActionName())) {
            return target.menu;
        }
        return null;
    }
}
