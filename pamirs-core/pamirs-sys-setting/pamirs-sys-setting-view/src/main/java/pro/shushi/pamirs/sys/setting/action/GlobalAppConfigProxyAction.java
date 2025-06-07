package pro.shushi.pamirs.sys.setting.action;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.SystemConfigKeyConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.enmu.BindingTypeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.proxy.AdvancedHomePageMenuProxy;
import pro.shushi.pamirs.boot.base.proxy.AdvancedHomeUeModuleProxy;
import pro.shushi.pamirs.boot.base.tmodel.AdvancedHomePageConfig;
import pro.shushi.pamirs.boot.base.tmodel.HomePageConfigRules;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModelActionsCacheApi;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.boot.web.utils.PageLoadHelper;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.sys.setting.enmu.HomePageExpEnumerate;
import pro.shushi.pamirs.sys.setting.pmodel.GlobalAppConfigProxy;
import pro.shushi.pamirs.sys.setting.tmodel.SystemStyleConfig;
import pro.shushi.pamirs.sys.setting.tmodel.TranslationConfig;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;

@Component
@Model.model(GlobalAppConfigProxy.MODEL_MODEL)
public class GlobalAppConfigProxyAction {

    @Autowired
    private AppConfigService appConfigService;

    @Resource
    private MetaCacheManager metaCacheManager;

    private static final String TRANSLATION_MANAGE = LambdaUtil.fetchFieldName(TranslationConfig::getTranslationManage);
    private static final String TOOLBOX_TRANSLATION = LambdaUtil.fetchFieldName(TranslationConfig::getToolboxTranslation);


    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    public GlobalAppConfigProxy construct(GlobalAppConfigProxy data) {
        AppConfig appConfig = appConfigService.fetchGlobalConfig();
        return ArgUtils.convert(AppConfig.MODEL_MODEL, GlobalAppConfigProxy.MODEL_MODEL, appConfig);
    }


    @Action(displayName = "保存登录页配置")
    public GlobalAppConfigProxy saveLoginSetting(GlobalAppConfigProxy data) {
        _saveGlobalSetting(_appConfig -> {
            _appConfig.setLoginBackground(data.getLoginBackground());
            _appConfig.setLoginPageLogo(data.getLoginPageLogo());
            _appConfig.setLoginLayoutType(data.getLoginLayoutType());
        });
        return data;
    }

    @Action(displayName = "保存企业形象配置")
    public GlobalAppConfigProxy saveCorporateImageSetting(GlobalAppConfigProxy data) {
        _saveGlobalSetting(_appConfig -> {
            _appConfig.setPartnerName(data.getPartnerName());
            _appConfig.setOfficialWebsite(data.getOfficialWebsite());
            _appConfig.setSlogan(data.getSlogan());
            _appConfig.setIcpDesc(data.getIcpDesc());
            _appConfig.setLogo(data.getLogo());
            _appConfig.setAppSideLogo(data.getAppSideLogo());
            _appConfig.setFavicon(data.getFavicon());
        });
        return data;
    }

    @Action(displayName = "保存系统风格配置")
    public GlobalAppConfigProxy saveSysStyleSetting(GlobalAppConfigProxy data) {
        _saveGlobalSetting(_appConfig -> {
            _appConfig.setMode(data.getMode());
            _appConfig.setSize(data.getSize());
            _appConfig.setMultiTabTheme(data.getMultiTabTheme());
            _appConfig.setSideBarTheme(data.getSideBarTheme());

            Map<String, Object> fromData = data.getExtend();
            if (fromData != null) {

                Optional.ofNullable(fromData.get(SystemConfigKeyConstants.SYSTEM_STYLE_CONFIG))
                        .map(value -> JsonUtils.parseMap2Object((Map<String, Object>) value, SystemStyleConfig.class))
                        .ifPresent(styleConfig -> {
                            Map<String, Object> extend = getExtend(_appConfig);
                            // 更新 extend 中的配置信息
                            extend.put(SystemConfigKeyConstants.SYSTEM_STYLE_CONFIG, styleConfig);
                            _appConfig.setExtend(extend);
                        });
            }
        });
        return data;
    }

    @Action(displayName = "保存高级首页配置")
    public GlobalAppConfigProxy saveAdvancedHomePage(GlobalAppConfigProxy data) {
        _saveGlobalSetting(_appConfig -> {
            Map<String, Object> fromData = data.getExtend();
            if (fromData != null) {
                Optional.ofNullable(fromData.get(SystemConfigKeyConstants.ADVANCED_HOME_PAGE))
                        .map(value -> JsonUtils.parseMap2Object((Map<String, Object>) value, AdvancedHomePageConfig.class))
                        .ifPresent(advancedHomePage -> {
                            Map<String, Object> extend = getExtend(_appConfig);

                            if (Boolean.TRUE.equals(advancedHomePage.getState())) {
                                if (CollectionUtils.isNotEmpty(advancedHomePage.getRules())) {
                                    for (HomePageConfigRules rule : advancedHomePage.getRules()) {
                                        View bindHomePageView = validateRule(rule);
                                        if (bindHomePageView == null) {
                                            continue;
                                        }
                                        bindHomePageView = metaCacheManager.fetchView(bindHomePageView.getModel(), bindHomePageView.getName(), null);
                                        if (bindHomePageView == null) {
                                            continue;
                                        }
                                        ViewAction homePage;
                                        if (StringUtils.isBlank(rule.getCode())) {
                                            homePage = createHomePage(rule, bindHomePageView);
                                        } else {
                                            homePage = updateHomePage(rule, bindHomePageView);
                                        }
                                        refreshCache(homePage);
                                    }
                                }
                            }

                            // 更新 extend 中的配置信息
                            Map<String, Object> convertedMap = JsonUtils.parseMap(JsonUtils.toJSONString(advancedHomePage,
                                    SerializerFeature.BrowserCompatible,
                                    SerializerFeature.DisableCircularReferenceDetect,
                                    SerializerFeature.WriteDateUseDateFormat));
                            extend.put(SystemConfigKeyConstants.ADVANCED_HOME_PAGE, convertedMap);
                            _appConfig.setExtend(extend);
                        });
            }
        });
        return data;
    }

    private View validateRule(HomePageConfigRules rule) {
        if (StringUtils.isEmpty(rule.getRuleName())) {
            throw PamirsException.construct(HomePageExpEnumerate.RULE_NAME_NOT_EMPTY_EXCEPTION).errThrow();
        }

        if (rule.getBindHomePageModule() == null) {
            throw PamirsException.construct(HomePageExpEnumerate.EMPTY_APP_BINDING_EXCEPTION).errThrow();
        }

        // 校验菜单是否绑定页面
        if (BindingTypeEnum.MENU.equals(rule.getBindingType())) {
            AdvancedHomePageMenuProxy bindHomePageMenu = rule.getBindHomePageMenu();
            if (bindHomePageMenu != null) {
                List<Menu> menus = PageLoadHelper.fetchMenus(bindHomePageMenu.getModule());
                boolean exists = false;
                if (CollectionUtils.isNotEmpty(menus)) {
                    for (Menu menu : menus) {
                        if (bindHomePageMenu.getName().equals(menu.getName())) {
                            exists = true;
                            String model = menu.getModel();
                            String actionName = menu.getActionName();
                            pro.shushi.pamirs.boot.base.model.Action action = null;
                            if (StringUtils.isNoneBlank(model, actionName)) {
                                action = metaCacheManager.fetchAction(model, actionName);
                            }
                            if (action == null) {
                                throw PamirsException.construct(HomePageExpEnumerate.APPS_MANAGEMENT_PARAMS_ILLEGAL_MENU_UNBINDING_VIEW, menu.getDisplayName()).errThrow();
                            }
                        }
                    }
                }
                if (!exists) {
                    throw PamirsException.construct(HomePageExpEnumerate.EMPTY_MENU_EXCEPTION).errThrow();
                }
                return null;
            } else {
                throw PamirsException.construct(HomePageExpEnumerate.EMPTY_MENU_EXCEPTION).errThrow();
            }
        }

        View bindHomePageView = rule.getBindHomePageView();
        if (bindHomePageView == null) {
            throw PamirsException.construct(HomePageExpEnumerate.EMPTY_PAGE_BINDING_EXCEPTION).errThrow();
        }
        if (bindHomePageView.getModel() == null || bindHomePageView.getName() == null) {
            return null;
        }

        if (StringUtils.isEmpty(rule.getBindHomePageModule().getModule())) {
            return null;
        }
        return bindHomePageView;
    }

    private ViewAction createHomePage(HomePageConfigRules rule, View bindHomePageView) {
        rule.setCode(UUIDUtil.getUUIDNumberString());
        String actionModel = bindHomePageView.getModel();
        AdvancedHomeUeModuleProxy module = rule.getBindHomePageModule();

        ViewAction homePage = new ViewAction();
        homePage.construct();

        homePage.setName(rule.getCode());
        homePage.setDisplayName(rule.getRuleName());
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
        return homePage;
    }

    private ViewAction updateHomePage(HomePageConfigRules rule, View bindHomePageView) {
        String actionName = rule.getCode();
        String actionModel = bindHomePageView.getModel();

        ViewAction existHomePage = null;
        pro.shushi.pamirs.boot.base.model.Action action = metaCacheManager.fetchAction(actionModel, actionName);
        if (action instanceof ViewAction) {
            existHomePage = (ViewAction) action;
            ViewAction homePage = new ViewAction();
            homePage.setId(existHomePage.getId());
            // 更新view的信息
            homePage.setResModel(actionModel);
            homePage.setResViewName(bindHomePageView.getName());
            homePage.setViewType(bindHomePageView.getType());
            homePage.setOptionViewTypes(Lists.newArrayList(bindHomePageView.getType()));

            homePage.updateByPk();
            return homePage;
        } else {
            return createHomePage(rule, bindHomePageView);
        }
    }

    private static void refreshCache(ViewAction homePage) {
        ViewAction result = homePage.queryById();

        // TODO: 2023/1/5 抽个方法在底层
        // 刷新缓存
        PamirsSession.getContext().putExtendCacheEntity(ActionCacheApi.class, (cacheApi) -> {
            cacheApi.put(result.getSign(), result);
        });
        PamirsSession.getContext().putExtendCacheEntity(ModelActionsCacheApi.class, (cacheApi) -> {
            String model = result.getModel();
            //新建一个列表,全部处理完毕后再覆盖
            List<pro.shushi.pamirs.boot.base.model.Action> cacheActions = cacheApi.get(model);
            List<pro.shushi.pamirs.boot.base.model.Action> modelActions = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(cacheActions)) {
                modelActions.addAll(cacheActions);
            }
            modelActions.stream().filter(i -> result.getSign().equals(i.getSign())).findFirst().ifPresent(modelActions::remove);
            modelActions.add(result);

            cacheApi.put(model, modelActions);
        });
    }

    private void initModelData(String model, String module, MetaBaseModel meta) {
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
        modelData.create();
    }

    /**
     * 获取数据库中扩展配置
     *
     * @param _appConfig _appConfig
     * @return Map
     */
    private static Map<String, Object> getExtend(AppConfig _appConfig) {
        AppConfig originAppConfig = _appConfig.queryOne();
        Map<String, Object> extend;
        if (originAppConfig == null) {
            extend = new HashMap<>();
            _appConfig.setExtend(extend);
        } else {
            extend = originAppConfig.getExtend();
            if (extend == null) {
                extend = new HashMap<>();
            }
        }
        return extend;
    }

    @Action(displayName = "翻译管理配置")
    public GlobalAppConfigProxy saveTranslationManageSetting(GlobalAppConfigProxy data) {
        Map<String, Object> fromData = data.getExtend();
        if (fromData == null) {
            return data;
        }
        Boolean translationManage = Optional.ofNullable(fromData.get(TRANSLATION_MANAGE)).map(BooleanHelper::isTrueWithoutException).orElse(null);
        Boolean toolboxTranslation = Optional.ofNullable(fromData.get(TOOLBOX_TRANSLATION)).map(BooleanHelper::isTrueWithoutException).orElse(null);
        if (translationManage == null && toolboxTranslation == null) {
            return data;
        }
        _saveGlobalSetting(appConfig -> {
            Map<String, Object> extend = getExtend(appConfig);

            if (translationManage != null) {
                extend.put(TRANSLATION_MANAGE, translationManage);
            }
            if (toolboxTranslation != null) {
                extend.put(TOOLBOX_TRANSLATION, toolboxTranslation);
            }
            appConfig.setExtend(extend);
        });
        return data;
    }

    private AppConfig _saveGlobalSetting(Consumer<AppConfig> appConfigConsumer) {
        AppConfig appConfig = new AppConfig();
        appConfig.setCode(AppConfig.generateCode(AppConfigScopeEnum.GLOBAL));
        appConfig.setScope(AppConfigScopeEnum.GLOBAL);

        appConfigConsumer.accept(appConfig);
        return appConfigService.save(appConfig);
    }
}
