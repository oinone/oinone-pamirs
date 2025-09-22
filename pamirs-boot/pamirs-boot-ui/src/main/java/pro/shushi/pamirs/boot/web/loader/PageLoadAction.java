package pro.shushi.pamirs.boot.web.loader;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.boot.web.service.MaskService;
import pro.shushi.pamirs.boot.web.service.ViewService;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.boot.web.spi.api.HomepageFetcherApi;
import pro.shushi.pamirs.boot.web.spi.holder.AuthVerificationApiHolder;
import pro.shushi.pamirs.boot.web.utils.PageLoadHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 首页与页面加载管理器
 * <p>
 * 2021/5/26 12:07 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Base
@Service
@Model.model(ViewAction.MODEL_MODEL)
public class PageLoadAction {

    @Resource
    private AppConfigService appConfigService;
    @Resource
    private ViewService viewService;
    @Resource
    private MaskService maskService;
    @Resource
    private MetaCacheManager metaCacheManager;
    @Resource
    private UiIoManager uiIoManager;

    /**
     * 获取首页元数据
     *
     * @param request 请求参数, 可选参数：module
     * @return 首页元数据
     */
    @Base
    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "加载首页")
    public ViewAction homepage(ViewAction request) {
        // 查询应用配置和首页
        /*
            如果用户访问指定应用域名（请求app不为空），则需要获取并返回应用首页（若无权限则继续查找）；
            如果请求app为空，则需要获取并返回产品首页（若无权限则继续查找）；
            若未指定产品首页，则获取并返回优先级最高的应用的首页（若未找到首页报错）。
         */
        String app = findFirstNonEmptyItem(request.getResModule(), request.getModule());
        boolean isFetchGlobalHomepage = StringUtils.isBlank(app);
        String homePageModel = request.getModel();
        String homePageName = request.getName();
        String theme = request.getTheme();
        String mask = request.getMask();

        // 查询应用配置及首页
        AppConfig appConfig = appConfigService.fetchRequestAppConfig(null, app);
        app = findFirstNonEmptyItem(app, appConfig.getApp());
        if (StringUtils.isBlank(homePageModel)) {
            homePageModel = appConfig.getHomePageModel();
            homePageName = appConfig.getHomePageName();
        }
        ViewAction result = fetchHighPriorityHomepage(app, homePageModel, homePageName, isFetchGlobalHomepage);
        if (isFetchGlobalHomepage) {
            result.setDefaultHomePage(appConfig.getDefaultHomePage());
        }

        result.setTheme(findFirstNonEmptyItem(theme, result.getTheme(), appConfig.getTheme()))
                .setMask(findFirstNonEmptyItem(mask, result.getMask(), appConfig.getMask()));

        // 加载UI相关资源
        result.setNeedCompileView(request.getNeedCompileView())
                .setNeedCompileMask(request.getNeedCompileMask());
        return loadUI(result);
    }

    /**
     * 获取页面元数据
     *
     * @param request 请求参数, 必填参数：id
     * @return 首页元数据
     */
    @Base
    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "加载页面")
    public ViewAction load(ViewAction request) {
        // 获取跳转动作
        Long id = request.getId();
        String model = request.getModel();
        String name = request.getName();
        ViewTypeEnum viewType = request.getViewType();
        if (null == id && (StringUtils.isBlank(model) || StringUtils.isBlank(name) && null == viewType)) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_ACTION_LOAD_PARAM_ERROR).errThrow();
        }
        ViewAction result = null;
        if (StringUtils.isNoneBlank(model, name)) {
            Action cacheAction = metaCacheManager.fetchAction(model, name);
            if (cacheAction instanceof ViewAction) {
                result = (ViewAction) uiIoManager.cloneData(cacheAction);
                if (result == null) {
                    log.error("ViewAction is null,model:{}, name:{}", model, name);
                }
            }
        } else if (null != id) {
            result = Models.data().queryOneByWrapper(Pops.<ViewAction>lambdaQuery().from(ViewAction.MODEL_MODEL).eq(ViewAction::getId, id));
        }
        if (null == result) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_ACTION_NOT_EXIST_ERROR).errThrow();
        }
        ModuleDefinition moduleDefinition = PageLoadHelper.getPageLoadModule(model);
        if (moduleDefinition == null) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_MODULE_CAN_NOT_ACCESS_ERROR).errThrow();
        }

        AccessResourceInfo info = PageLoadHelper.generatorAccessResourceInfo(moduleDefinition, result);
        if (info != null) {
            AccessResourceInfoSession.setInfo(info);
            result.setSessionPath(info.toString());
        }
        model = result.getModel();
        name = result.getName();
        if (!AuthVerificationApiHolder.get().verifyActionAccess(moduleDefinition, result.getActionType(), model, name)) {
            log.error("ViewAction access denied. model: {}, name: {}", model, name);
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_ACTION_NOT_EXIST_ERROR).errThrow();
        }

        // 设置主题和母版编码
        result.setTheme(findFirstNonEmptyItem(request.getTheme(), result.getTheme()));
        result.setMask(findFirstNonEmptyItem(request.getMask(), result.getMask()));

        if (StringUtils.isBlank(result.getMask()) && Boolean.TRUE.equals(request.getNeedCompileMask())) {
            // 查询应用配置
            String app = findFirstNonEmptyItem(
                    request.getResModule(), request.getModule(),
                    result.getResModule(), result.getModule()
            );
            AppConfig appConfig = appConfigService.fetchRequestAppConfig(null, app);
            result.setTheme(findFirstNonEmptyItem(result.getTheme(), appConfig.getTheme()));
            result.setMask(findFirstNonEmptyItem(result.getMask(), appConfig.getMask()));
        }

        // 加载UI相关资源
        result.setNeedCompileView(request.getNeedCompileView())
                .setNeedCompileMask(request.getNeedCompileMask());
        return loadUI(result);
    }

    /**
     * 获取优先级最高的入口应用
     *
     * @param ueModule 请求参数
     * @return 入口应用
     */
    @Base
    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "获取入口应用")
    public UeModule home(UeModule ueModule) {
        UeModule result = authModule(ueModule);
        if (null == result) {
            return Spider.getDefaultExtension(HomepageFetcherApi.class).fetchGlobalHomepage();
        }
        return result;
    }

    private UeModule authModule(UeModule moduleDefinition) {
        String module = moduleDefinition.getModule();
        String moduleName = moduleDefinition.getName();
        ModuleDefinition cacheModule = null;
        UeModule result;
        if (StringUtils.isNotBlank(module)) {
            cacheModule = PamirsSession.getContext().getModule(module);
            if (cacheModule == null) {
                cacheModule = new UeModule().setModule(module).queryOne();
                if (cacheModule == null) {
                    throw PamirsException.construct(BootUxdExpEnumerate.BASE_LOAD_MODULE_META_MODULE_DATA_ERROR).errThrow();
                }
            }
        } else if (StringUtils.isNotBlank(moduleName)) {
            cacheModule = PamirsSession.getContext().getModuleCache().getByName(moduleName);
            if (cacheModule == null) {
                cacheModule = new UeModule().setName(moduleName).queryOne();
                if (cacheModule == null) {
                    throw PamirsException.construct(BootUxdExpEnumerate.BASE_LOAD_MODULE_META_MODULE_DATA_ERROR).errThrow();
                }
            }
        }
        if (cacheModule == null) {
            return null;
        }
        if (!AuthApi.get().canAccessModule(cacheModule.getModule()).getSuccess()) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_MODULE_CAN_NOT_ACCESS_ERROR).errThrow();
        }
        ModuleDefinition cloneModule = uiIoManager.cloneData(cacheModule);
        result = ArgUtils.convert(ModuleDefinition.MODEL_MODEL, ModuleDefinition.UE_MODEL_MODEL, cloneModule);
        return result;
    }

    /**
     * 获取窗口动作元数据
     *
     * @param viewAction 请求参数
     * @return 窗口动作元数据
     */
    private ViewAction loadUI(ViewAction viewAction) {

        // 设置目标模块和模型
        PageLoadHelper.fillModuleAndModel(viewAction);

        // 处理视图
        loadView(viewAction);

        // 处理母版
        loadMask(viewAction);

        return viewAction;
    }

    private String findFirstNonEmptyItem(String... items) {
        if (null == items) {
            return null;
        }
        for (String item : items) {
            if (StringUtils.isNotBlank(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 处理视图
     *
     * @param viewAction 窗口动作
     */
    private void loadView(ViewAction viewAction) {
        if (null == viewAction.getNeedCompileView() || !viewAction.getNeedCompileView()) {
            return;
        }

        String resModel = Optional.ofNullable(viewAction.getResModel()).orElse(viewAction.getModel());
        View mainView = metaCacheManager.fetchView(resModel, viewAction.getResViewName(), viewAction.getViewType());
        viewAction.setResView(mainView);
        fillViewModel(mainView, resModel);

        List<View> optionViewList = fetchOptionViewList(viewAction, resModel);
        boolean noOptions = null == optionViewList;
        int viewListSize = noOptions ? 0 : optionViewList.size();
        List<View> viewList = new ArrayList<>(1 + viewListSize);
        viewList.add(mainView);
        if (!noOptions) {
            viewList.addAll(optionViewList);
        }

        // 编译视图
        viewService.compile(viewList);
        // 加载布局
        viewService.layout(viewList);
        // 处理视图权限
        viewService.auth(viewList);
        // 国际化视图
        viewService.internationalization(viewList);
        // 个性化视图
        viewService.userPreference(viewList, viewAction);
    }

    private void fillViewModel(View view, String model) {
        ModelConfig mainViewModelConfig = PamirsSession.getContext().getModelConfig(model);
        if (mainViewModelConfig == null) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_ACTION_MODEL_CONFIG_ERROR)
                    .appendMsg("model: " + model).errThrow();
        }
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setModel(mainViewModelConfig.getModel());
        modelDefinition.setName(mainViewModelConfig.getName());

        String module = mainViewModelConfig.getModule();
        ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(module);
        if (moduleDefinition == null) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_VIEW_ACTION_MODULE_CONFIG_ERROR).errThrow();
        }
        modelDefinition.setModule(module);
        modelDefinition.setModuleName(moduleDefinition.getName());

        view.setModelDefinition(modelDefinition);
    }

    private List<View> fetchOptionViewList(ViewAction viewAction, String model) {
        List<View> optionViewList = null;
        List<String> optionViewNames = viewAction.getOptionViewNames();
        if (CollectionUtils.isNotEmpty(optionViewNames)) {
            optionViewList = new ArrayList<>();
            for (String optionViewName : optionViewNames) {
                View optionView = metaCacheManager.fetchView(model, optionViewName, null);
                if (optionView != null) {
                    optionViewList.add(optionView);
                    fillViewModel(optionView, model);
                }
            }
            if (optionViewList.isEmpty()) {
                optionViewList = null;
            }
        }
        return optionViewList;
    }

    /**
     * 编译母版
     *
     * @param viewAction 窗口动作
     */
    private void loadMask(ViewAction viewAction) {
        if (null == viewAction.getNeedCompileMask() || !viewAction.getNeedCompileMask()) {
            return;
        }

        MaskDefinition maskDefinition = new MaskDefinition().setName(viewAction.getMask()).queryOne();
        // 编译母版
        maskService.compile(maskDefinition);
        // 加载布局
        maskService.layout(maskDefinition);
        // 处理权限
        maskService.auth(maskDefinition);
        // 国际化
        maskService.internationalization(maskDefinition);

    }

    /**
     * 获取高优先级首页
     *
     * @param currentModule 当前应用
     * @return 首页
     */
    private ViewAction fetchHighPriorityHomepage(String currentModule, String homepageModel, String homepageName, boolean isFetchGlobalHomepage) {
        UeModule home = new UeModule();
        if (null != currentModule) {
            home.setModule(currentModule);
        }
        boolean isNeedVerifyAccess = true;
        if (isFetchGlobalHomepage) {
            home = home(home);
            isNeedVerifyAccess = false;
        } else {
            home = authModule(home);
        }
        if (null == home) {
            throw PamirsException.construct(BaseExpEnumerate.BASE_HOME_IS_NOT_EXISTS_ERROR).errThrow();
        }
        if (null == homepageModel || !home.getModule().equals(currentModule)) {
            homepageModel = home.getHomePageModel();
            homepageName = home.getHomePageName();
        }
        if (isNeedVerifyAccess) {
            Action homepageAction = Spider.getDefaultExtension(HomepageFetcherApi.class).fetchApplicationHomePage(home, homepageModel, homepageName);
            if (homepageAction == null) {
                homepageModel = null;
                homepageName = null;
            } else {
                homepageModel = homepageAction.getModel();
                homepageName = homepageAction.getName();
            }
        }
        if (StringUtils.isAnyBlank(homepageModel, homepageName)) {
            throw PamirsException.construct(BaseExpEnumerate.BASE_HOME_PAGE_IS_NOT_EXISTS_ERROR)
                    .appendMsg("应用名：" + home.getDisplayName() + "（" + home.getModule() + "）").errThrow();
        }
        ViewAction action = null;
        Action cacheAction = PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(homepageModel, homepageName);
        if (cacheAction instanceof ViewAction) {
            action = uiIoManager.cloneData((ViewAction) cacheAction);
        }
        if (null == action) {
            log.error("未找到首页, module:{}, homepageModel:{}, homepageName:{}", home.getModule(), homepageModel, homepageName);
            throw PamirsException.construct(BaseExpEnumerate.BASE_HOME_PAGE_IS_NOT_EXISTS_ERROR)
                    .appendMsg("module:" + home.getModule()).errThrow();
        }

        action.setModuleDefinition(home);
        action.setModule(home.getModule());
        action.setModuleName(home.getName());
        if (home.getModule().equals(action.getResModule())) {
            action.setResModuleDefinition(new ModuleDefinition().setModule(home.getModule()).setName(home.getName()));
        }

        AccessResourceInfo info = PageLoadHelper.generatorAccessResourceInfo(home, action);
        if (info != null) {
            AccessResourceInfoSession.setInfo(info);
            action.setSessionPath(info.toString());
        }

        return action;
    }
}
