package pro.shushi.pamirs.apps.core.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.apps.api.enmu.AppsExpEnumerate;
import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;
import pro.shushi.pamirs.apps.api.pmodel.AppsModuleMenuProxy;
import pro.shushi.pamirs.apps.api.service.AppsManagementModuleService;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModelActionsCacheApi;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.apps.api.enmu.AppsExpEnumerate.APPS_NOCODE_MODULE_NOT_SUPPORT;

@Slf4j
@Service
@Order(1)
public class DefaultAppsManagementModuleServiceImpl implements AppsManagementModuleService {

    @Override
    public AppsManagementModule create(AppsManagementModule data) {
        throw PamirsException.construct(APPS_NOCODE_MODULE_NOT_SUPPORT)
                .errThrow();
    }

    @Override
    public AppsManagementModule update(AppsManagementModule data) {
        throw PamirsException.construct(APPS_NOCODE_MODULE_NOT_SUPPORT)
                .errThrow();
    }

    protected ModelData initModelData(String model, String module, MetaBaseModel meta) {
        ModelData modelData = new ModelData();
        modelData.setLowCode(Boolean.TRUE);
        modelData.setSource(SystemSourceEnum.UI);
        modelData.setModule(module);
        modelData.setLoadModule(module);
        modelData.setDateInit(new Date());
        modelData.setDateUpdate(modelData.getDateInit());

        modelData.setModel(model);
        modelData.setResId(meta.getId());
        modelData.code(modelData.getModel(), meta.getSign());
        modelData.construct();
        return modelData.create();
    }

    @Override
    public AppsManagementModule bindHomePage(AppsManagementModule data) {
        if (data == null || data.getId() == null) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_PARAMS_ILLEGAL).errThrow();
        }
        AppsManagementModule module = new AppsManagementModule().setId(data.getId()).queryById();

        Action homePage = null;
        if (data.getBindHomePageView() != null && data.getBindHomePageView().getId() != null) {
            View bindHomePageView = data.getBindHomePageView().queryById();
            homePage = generateHomePage(module, bindHomePageView);
        } else if (data.getBindHomePageMenu() != null && data.getBindHomePageMenu().getId() != null) {
            AppsModuleMenuProxy bindHomePageMenu = data.getBindHomePageMenu().queryById();
            homePage = bindHomePageMenu.fieldQuery(Menu::getViewAction).getViewAction();
            if (homePage == null) {
                throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_PARAMS_ILLEGAL_MENU_UNBINDING_VIEW).errThrow();
            }
        } else if (data.getUrlHomePage() != null && data.getUrlHomePage().getUrl() != null) {
            homePage = generateHomePage(module, data.getUrlHomePage());
        }

        if (homePage == null) {
            throw PamirsException.construct(AppsExpEnumerate.SYSTEM_ERROR).errThrow();
        }
        UeModule update = new UeModule();
        update.setId(module.getId());
        update.setHomePageModel(homePage.getModel());
        update.setHomePageName(homePage.getName());
        Models.origin().updateByPk(update);

        //刷新模块缓存
        UeModule ueModule = new UeModule().setId(module.getId()).queryById();
        PamirsSession.getContext().getModuleCache().put(module.getSign(), ueModule);
        return module;
    }

    private ViewAction generateHomePage(UeModule module, View bindHomePageView) {
        String actionName = module.getModule() + CommonConstants.LOW_CODE_HOMEPAGE_SUFFIX;
        String actionModel = bindHomePageView.getModel();
        ViewAction existHomePage = new ViewAction().setModel(actionModel).setName(actionName).queryOne();
        ViewAction homePage;
        if (existHomePage == null) {
            homePage = new ViewAction();
            homePage.construct();

            homePage.setName(actionName);
            homePage.setDisplayName(CommonConstants.HOMEPAGE_DISPLAY_NAME);
            homePage.setModule(module.getModule());
            homePage.setResModule(module.getModule());

            homePage.setModel(actionModel);
            homePage.setResModel(actionModel);
            homePage.setResViewName(bindHomePageView.getName());
            homePage.setViewType(bindHomePageView.getType());
            homePage.setOptionViewTypes(Lists.newArrayList(bindHomePageView.getType()));

            homePage.setTarget(ActionTargetEnum.ROUTER);
            homePage.setContextType(ActionContextTypeEnum.CONTEXT_FREE);
            homePage.setSystemSource(SystemSourceEnum.SYSTEM);

            homePage.setSys(false);
            homePage.setActionType(ActionTypeEnum.VIEW);
            homePage.setPriority(999);

            homePage = homePage.create();

            // modelData
            initModelData(ViewAction.MODEL_MODEL, module.getModule(), homePage);
        } else {
            homePage = new ViewAction();
            homePage.setId(existHomePage.getId());

            // 更新view的信息
            homePage.setResModel(actionModel);
            homePage.setResViewName(bindHomePageView.getName());
            homePage.setViewType(bindHomePageView.getType());
            homePage.setOptionViewTypes(Lists.newArrayList(bindHomePageView.getType()));

            homePage.updateByPk();

            //模型和name都不变,不处理modelData
        }

        ViewAction result = homePage.queryById();

        // TODO: 2023/1/5 抽个方法在底层
        // 刷新缓存
        PamirsSession.getContext().putExtendCacheEntity(ActionCacheApi.class, (cacheApi) -> {
            cacheApi.put(result.getSign(), result);
        });
        PamirsSession.getContext().putExtendCacheEntity(ModelActionsCacheApi.class, (cacheApi) -> {
            String model = result.getModel();
            //新建一个列表,全部处理完毕后再覆盖
            List<Action> cacheActions = cacheApi.get(model);
            List<Action> modelActions = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(cacheActions)) {
                modelActions.addAll(cacheActions);
            }
            modelActions.stream().filter(i -> result.getSign().equals(i.getSign())).findFirst().ifPresent(modelActions::remove);
            modelActions.add(result);

            cacheApi.put(model, modelActions);
        });
        return result;
    }

    private Action generateHomePage(UeModule module, UrlAction urlHomePage) {
        String bindHomeUrl = urlHomePage.getUrl();
        ActionTargetEnum target = Optional.ofNullable(urlHomePage.getTarget()).orElse(ActionTargetEnum.ROUTER);
        String actionName = module.getModule() + CommonConstants.LOW_CODE_HOMEPAGE_SUFFIX;
        String actionModel = UrlAction.MODEL_MODEL;
        UrlAction existHomePage = new UrlAction().setModel(actionModel).setName(actionName).queryOne();
        UrlAction homePage;
        if (existHomePage == null) {
            homePage = new UrlAction();
            homePage.construct();
            homePage.setUrl(bindHomeUrl);
            homePage.setName(actionName);
            homePage.setDisplayName(CommonConstants.HOMEPAGE_DISPLAY_NAME);
            homePage.setModel(actionModel);
            homePage.setTarget(target);
            homePage.setContextType(ActionContextTypeEnum.CONTEXT_FREE);
            homePage.setSystemSource(SystemSourceEnum.SYSTEM);
            homePage.setSys(false);
            homePage.setActionType(ActionTypeEnum.URL);
            homePage.setPriority(999);
            homePage = homePage.create();
            // modelData
            initModelData(ViewAction.MODEL_MODEL, module.getModule(), homePage);
        } else {
            homePage = new UrlAction();
            homePage.setId(existHomePage.getId());
            homePage.setUrl(bindHomeUrl);
            homePage.setTarget(target);
            homePage.updateByPk();
        }
        Action result = homePage.queryById();

        // TODO: 2023/1/5 抽个方法在底层
        // 刷新缓存
        PamirsSession.getContext().putExtendCacheEntity(ActionCacheApi.class, (cacheApi) -> {
            cacheApi.put(result.getSign(), result);
        });
        PamirsSession.getContext().putExtendCacheEntity(ModelActionsCacheApi.class, (cacheApi) -> {
            String model = result.getModel();
            //新建一个列表,全部处理完毕后再覆盖
            List<Action> cacheActions = cacheApi.get(model);
            List<Action> modelActions = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(cacheActions)) {
                modelActions.addAll(cacheActions);
            }
            modelActions.stream().filter(i -> result.getSign().equals(i.getSign())).findFirst().ifPresent(modelActions::remove);
            modelActions.add(result);

            cacheApi.put(model, modelActions);
        });
        return result;
    }

    @Override
    public AppsManagementModule install(AppsManagementModule module) {
        if (module == null || module.getId() == null) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_PARAMS_ILLEGAL).errThrow();
        }
        AppsManagementModule existModule = new AppsManagementModule().queryById(module.getId());
        if (existModule == null) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_MODULE_NOT_EXIST).errThrow();
        }
        if (Boolean.TRUE.equals(existModule.getSys())) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_PARAMS_ILLEGAL_NOCODE).errThrow();
        }
        if (!ModuleStateEnum.UNINSTALLED.equals(existModule.getState())) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_MODULE_INSTALL_APP_STATUS_ERROR).errThrow();
        }
        // 判断依赖模块是否都安装
        List<String> moduleDependencies = existModule.getModuleDependencies();
        if (CollectionUtils.isNotEmpty(moduleDependencies)) {
            List<UeModule> dependencyModules = Models.origin().queryListByWrapper(
                    Pops.<UeModule>lambdaQuery()
                            .from(UeModule.MODEL_MODEL)
                            .in(UeModule::getState, ModuleStateEnum.INSTALLED, ModuleStateEnum.TOUPGRADE)
                            .in(UeModule::getModule, moduleDependencies)
            );
            Set<String> dependencyModuleModules = dependencyModules.stream().map(UeModule::getModule).collect(Collectors.toSet());
            moduleDependencies = moduleDependencies.stream().filter(_m -> !dependencyModuleModules.contains(_m)).collect(Collectors.toList());
            if (moduleDependencies.size() > 0) {
                throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_MODULE_INSTALL_ERROR, String.join(",", moduleDependencies)).errThrow();
            }
        }

        // TODO: 2023/1/16 元数据计算
        UeModule updateModule = new UeModule();
        updateModule.setId(existModule.getId());
        updateModule.setState(ModuleStateEnum.INSTALLED);
        updateModule.updateById();

        UeModule result = new UeModule().queryById(module.getId());
        //缓存
        PamirsSession.getContext().getModuleCache().put(result.getSign(), result);
        Spider.getDefaultExtension(ModulesApi.class).modules().add(result.getModule());

        existModule.setState(result.getState());
        return existModule;
    }

    @Override
    public AppsManagementModule upgrade(AppsManagementModule module) {
        // TODO: 2023/1/4
        return module;
    }

    @Override
    public AppsManagementModule reload(AppsManagementModule module) {
        // TODO: 2023/1/4
        return module;
    }

    @Override
    public AppsManagementModule uninstall(AppsManagementModule module) {
        if (module == null || module.getId() == null) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_PARAMS_ILLEGAL).errThrow();
        }
        AppsManagementModule existModule = new AppsManagementModule().queryById(module.getId());
        if (existModule == null) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_MODULE_NOT_EXIST).errThrow();
        }
        if (Boolean.TRUE.equals(existModule.getSys())) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_PARAMS_ILLEGAL_NOCODE).errThrow();
        }
        if (!ModuleStateEnum.INSTALLED.equals(existModule.getState())) {
            throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_MODULE_UNINSTALL_APP_UNINSTALL).errThrow();
        }

        // 从db查询已安装的模块
        List<UeModule> installedModules = Models.origin().queryListByWrapper(
                Pops.<UeModule>lambdaQuery()
                        .from(UeModule.MODEL_MODEL)
                        .in(UeModule::getState, ModuleStateEnum.INSTALLED, ModuleStateEnum.TOUPGRADE)
                        .like(UeModule::getModuleDependencies, module) // 模糊查询依赖当前模块的模块
        );
        if (CollectionUtils.isNotEmpty(installedModules)) {
            for (UeModule installedModule : installedModules) {
                List<String> installedModuleDependencies = installedModule.getModuleDependencies();
                if (CollectionUtils.isNotEmpty(installedModuleDependencies) && installedModuleDependencies.contains(module.getModule())) {
                    throw PamirsException.construct(AppsExpEnumerate.APPS_MANAGEMENT_MODULE_UNINSTALL_HAS_DEPENDENCY, installedModule.getDisplayName()).errThrow();
                }
            }
        }

        UeModule updateModule = new UeModule();
        updateModule.setId(existModule.getId());
        updateModule.setState(ModuleStateEnum.UNINSTALLED);
        updateModule.updateById();

        UeModule result = new UeModule().queryById(module.getId());
        //缓存
        PamirsSession.getContext().getModuleCache().put(result.getSign(), result);
        Spider.getDefaultExtension(ModulesApi.class).modules().remove(result.getModule());

        existModule.setState(result.getState());
        return existModule;
    }
}
