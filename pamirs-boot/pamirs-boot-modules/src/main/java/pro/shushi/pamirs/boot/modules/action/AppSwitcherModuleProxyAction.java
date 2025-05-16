package pro.shushi.pamirs.boot.modules.action;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.modules.enmu.AppLikeEnum;
import pro.shushi.pamirs.boot.modules.model.AppsModuleRelUser;
import pro.shushi.pamirs.boot.modules.pmodel.AppSwitcherModuleProxy;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.boot.web.utils.PageLoadHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用管理能力
 *
 * @author shier
 * date  2021/5/26 10:57 上午
 */
@Slf4j
@Base
@Component
@Model.model(AppSwitcherModuleProxy.MODEL_MODEL)
public class AppSwitcherModuleProxyAction {

    @Resource
    private AppConfigService appConfigService;

    @Resource
    private MetaCacheManager metaCacheManager;

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AppSwitcherModuleProxy> queryPage(Pagination<AppSwitcherModuleProxy> page, IWrapper<AppSwitcherModuleProxy> queryWrapper) {
        LambdaQueryWrapper<AppSwitcherModuleProxy> qw = ((QueryWrapper<AppSwitcherModuleProxy>) queryWrapper).lambda()
                .eq(AppSwitcherModuleProxy::getApplication, Boolean.TRUE)
                .eq(AppSwitcherModuleProxy::getShow, ActiveEnum.ACTIVE)
                .in(AppSwitcherModuleProxy::getState, Arrays.asList(ModuleStateEnum.INSTALLED, ModuleStateEnum.TOUPGRADE))
                .has(AppSwitcherModuleProxy::getClientTypes, PageLoadHelper.getCurrentClientType());

        Map<String, Object> queryData = queryWrapper.getQueryData();
        AppLikeEnum like = null;
        if (queryData != null && queryData.size() > 0) {
            String appLikeEnumName = (String) queryData.get(LambdaUtil.fetchFieldName(AppSwitcherModuleProxy::getLike));
            like = Optional.ofNullable(appLikeEnumName)
                    .filter(StringUtils::isNotBlank)
                    .map(AppLikeEnum::valueOf)
                    .orElse(null);
        }

        // 获取用户收藏的应用列表
        Set<String> likeModules = Models.origin()
                .queryListByWrapper(new Pagination<AppsModuleRelUser>().setSize(-1L),
                        Pops.<AppsModuleRelUser>lambdaQuery()
                                .from(AppsModuleRelUser.MODEL_MODEL)
                                .eq(AppsModuleRelUser::getUserId, PamirsSession.getUserId())
                                .eq(AppsModuleRelUser::getLike, true)
                )
                .stream()
                .map(AppsModuleRelUser::getModule)
                .collect(Collectors.toSet());
        if (null != like) {
            if (CollectionUtils.isNotEmpty(likeModules)) {
                switch (like) {
                    case LIKE:
                        qw.in(AppSwitcherModuleProxy::getModule, likeModules);
                        break;
                    case NOT_LIKE:
                        qw.notIn(AppSwitcherModuleProxy::getModule, likeModules);
                        break;
                }
            } else {
                // 没有收藏的应用
                switch (like) {
                    case LIKE:
                        qw.in(AppSwitcherModuleProxy::getModule, Collections.singletonList(""));
                        break;
                    case NOT_LIKE:
                        qw.notIn(AppSwitcherModuleProxy::getModule, Collections.singletonList(""));
                        break;
                }
            }
        }

        addClientTypeFilter(qw);
        Pagination<AppSwitcherModuleProxy> resultPage = Models.origin().queryPage(page, qw);
        List<AppSwitcherModuleProxy> dataList = resultPage.getContent();
        if (CollectionUtils.isEmpty(dataList)) {
            return resultPage;
        }
        // 添加内存数据
        fetchMemModules(dataList, queryData);

        AuthApi authApi = AuthApi.get();
        dataList = dataList.stream().filter(v -> authApi.canAccessModule(v.getModule()).getSuccess()).collect(Collectors.toList());
        resultPage.setContent(dataList);

        if (CollectionUtils.isEmpty(dataList)) {
            return resultPage;
        }

        //查询应用首页
        for (AppSwitcherModuleProxy module : dataList) {
            String homepageModel = module.getHomePageModel();
            String homepageName = module.getHomePageName();
            if (StringUtils.isNoneBlank(homepageModel, homepageName)) {
                ViewAction action = null;
                pro.shushi.pamirs.boot.base.model.Action cacheAction = metaCacheManager.fetchAction(homepageModel, homepageName);
                if (cacheAction instanceof ViewAction) {
                    action = BeanDefinitionUtils.getBean(UiIoManager.class).cloneData((ViewAction) cacheAction);
                } else if (cacheAction instanceof UrlAction) {
                    UrlAction urlAction = BeanDefinitionUtils.getBean(UiIoManager.class).cloneData((UrlAction) cacheAction);
                    module.setUrlHomePage(urlAction);
                    module.setHomePage(null);
                }
                if (action != null) {
                    module.setHomePage(action);
                    String resModel = action.getResModel();
                    String resViewName = action.getResViewName();
                    if (StringUtils.isNoneBlank(resModel, resViewName)) {
                        View resView = metaCacheManager.fetchView(resModel, resViewName, null, false);
                        if (resView != null) {
                            action.setResView(resView);
                            module.setHomepageViewId(resView.getId()).setHomepageViewSystemSource(module.getSystemSource());
                        }
                    }
                }
            }
            if (likeModules.contains(module.getModule())) {
                module.setLike(AppLikeEnum.LIKE);
            }
        }
//        updateLogoFromAppConfig(dataList);
        return resultPage;
    }

    /**
     * 添加内存中的模块
     *
     * @param dataList
     * @param queryData
     */
    private void fetchMemModules(List<AppSwitcherModuleProxy> dataList, Map<String, Object> queryData) {
        // 内存中存在的module
        Set<String> moduleKeySet = PamirsSession.getContext().getModuleCache().keySet();
        moduleKeySet = Sets.difference(moduleKeySet, dataList.stream().map(AppSwitcherModuleProxy::getModule).collect(Collectors.toSet()));
        List<AppSwitcherModuleProxy> memModuleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(moduleKeySet)) {
            for (String moduleKey : moduleKeySet) {
                ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(moduleKey);
                if (moduleDefinition != null) {
                    if (PageLoadHelper.isCurrentClientApplication(moduleDefinition)) {
                        moduleDefinition = BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(moduleDefinition);
                        memModuleList.add(ArgUtils.convert(ModuleDefinition.MODEL_MODEL, AppSwitcherModuleProxy.MODEL_MODEL, moduleDefinition));
                    }
                } else {
                    PamirsSession.getContext().getModuleCache().remove(moduleKey);
                }
            }
        }
        if (queryData != null && queryData.size() > 0) {
            if (CollectionUtils.isNotEmpty(memModuleList)) {
                String appDisplayName = (String) queryData.get(LambdaUtil.fetchFieldName(AppSwitcherModuleProxy::getDisplayName));
                if (StringUtils.isNotEmpty(appDisplayName)) {
                    memModuleList = memModuleList.stream().filter(module -> StringUtils.contains(module.getDisplayName(), appDisplayName)).collect(Collectors.toList());
                }
            }
        } else {
            if (CollectionUtils.isNotEmpty(memModuleList)) {
                memModuleList = memModuleList.stream().filter(module -> {
                    ModuleStateEnum state = module.getState();
                    return ModuleStateEnum.INSTALLED.equals(state) || ModuleStateEnum.TOUPGRADE.equals(state);
                }).collect(Collectors.toList());
            }
        }

        if (CollectionUtils.isNotEmpty(memModuleList)) {
            dataList.addAll(memModuleList);
        }
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "用户收藏应用")
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "用户收藏应用")
    public List<AppSwitcherModuleProxy> userAppList(AppSwitcherModuleProxy data) {

        List<AppSwitcherModuleProxy> dataList = new ArrayList<>();
        Long userId = PamirsSession.getUserId();
        OriginDataManager dataManager = Models.origin();
        List<AppsModuleRelUser> relUsers = dataManager.queryListByWrapper(
                Pops.<AppsModuleRelUser>lambdaQuery().from(AppsModuleRelUser.MODEL_MODEL).setBatchSize(-1)
                        .eq(AppsModuleRelUser::getUserId, userId)
                        .eq(AppsModuleRelUser::getLike, true));

        if (CollectionUtils.isNotEmpty(relUsers)) {
            if (relUsers.stream().allMatch(t -> t.getOrderNumber() == null)) {
                relUsers.sort(Comparator.comparing(AppsModuleRelUser::getWriteDate).reversed());
            } else {
                relUsers.forEach(t -> {
                    if (t.getOrderNumber() == null) t.setOrderNumber(0L);
                });
                relUsers.sort(Comparator.comparing(AppsModuleRelUser::getOrderNumber).thenComparing(Comparator.comparing(AppsModuleRelUser::getWriteDate).reversed()));
            }
            List<String> modules = relUsers.stream().map(AppsModuleRelUser::getModule).collect(Collectors.toList());

            LambdaQueryWrapper<AppSwitcherModuleProxy> qw = Pops.<AppSwitcherModuleProxy>lambdaQuery()
                    .from(AppSwitcherModuleProxy.MODEL_MODEL).setBatchSize(-1)
                    .in(AppSwitcherModuleProxy::getModule, modules);
            addClientTypeFilter(qw);
            List<AppSwitcherModuleProxy> apps = dataManager.queryListByWrapper(qw);

            for (AppsModuleRelUser relUser : relUsers) {
                if (apps.stream().anyMatch(t -> t.getModule().equals(relUser.getModule()))) {
                    dataList.add(apps.stream().filter(t -> t.getModule().equals(relUser.getModule())).findFirst().orElse(null));
                }
            }

            //查询应用首页
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(v -> {
                    v.setHomePageModel(v.getHomePageModel());
                    v.setHomePageName(v.getHomePageName());
                });
                Models.origin().listFieldQuery(dataList, AppSwitcherModuleProxy::getHomePage);
                Models.origin().listFieldQuery(dataList, AppSwitcherModuleProxy::getUrlHomePage);
                List<ViewAction> homePages = dataList.stream()
                        .map(AppSwitcherModuleProxy::getHomePage)
                        .filter(Objects::nonNull)
                        .filter(homePage -> homePage.getResView() != null)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(homePages)) {
                    Models.origin().listFieldQuery(homePages, ViewAction::getResView);
                }

                for (AppSwitcherModuleProxy module : dataList) {
                    module.setLike(AppLikeEnum.LIKE);

                    if (module.getHomePage() != null && module.getHomePage().getResView() != null) {
                        View homePageView = module.getHomePage().getResView();
                        module.setHomepageViewId(homePageView.getId()).setHomepageViewSystemSource(module.getSystemSource());
                    }
                }
            }
        }

//        updateLogoFromAppConfig(dataList);
        return dataList;
    }

    @Action(displayName = "收藏应用排序设置")
    public List<AppSwitcherModuleProxy> sortOrderNumber(List<AppSwitcherModuleProxy> modules) {
        Long userId = PamirsSession.getUserId();
        if (CollectionUtils.isEmpty(modules) || userId == null) {
            return null;
        }
        List<AppsModuleRelUser> updateList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(modules)) {
            long i = 1;
            for (AppSwitcherModuleProxy AppSwitcherModuleProxy : modules) {
                AppsModuleRelUser update = new AppsModuleRelUser();
                update.setModule(AppSwitcherModuleProxy.getModule());
                update.setUserId(userId);
                update.setOrderNumber(i);
                updateList.add(update);
                i++;
            }
            new AppsModuleRelUser().createOrUpdateBatch(updateList);
        }
        return modules;
    }

    private void updateLogoFromAppConfig(List<AppSwitcherModuleProxy> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        String companyCode = PamirsSession.getTransmittableExtend().get("companyCode");
        log.info("updateLogoFromAppConfig:companyCode:{}", companyCode);
        IWrapper<AppConfig> wrapper = Pops.<AppConfig>lambdaQuery()
                .from(AppConfig.MODEL_MODEL).setBatchSize(-1)
                .eq(StringUtils.isNotBlank(companyCode), AppConfig::getCompanyCode, companyCode)
                .eq(AppConfig::getScope, AppConfigScopeEnum.APP.value());
        List<AppConfig> appConfigList = new AppConfig().queryList(wrapper);
        if (CollectionUtils.isEmpty(appConfigList)) {
            return;
        }

        Map<String, String> moduleLogoMap = appConfigList.stream().collect(Collectors.toMap(AppConfig::getApp, AppConfig::getLogo));

        list.stream().forEach(AppSwitcherModuleProxy -> {
            String module = AppSwitcherModuleProxy.getModule();
            String appConfigLogo = moduleLogoMap.get(module);
            if (StringUtils.isNotBlank(appConfigLogo)) {
                AppSwitcherModuleProxy.setLogo(appConfigLogo);
            }
        });
    }

    @Action(displayName = "收藏")
    public AppSwitcherModuleProxy like(List<AppSwitcherModuleProxy> modules) {
        like0(modules, Boolean.TRUE);
        return new AppSwitcherModuleProxy();
    }

    @Action(displayName = "取消收藏")
    public AppSwitcherModuleProxy unLike(List<AppSwitcherModuleProxy> modules) {
        like0(modules, Boolean.FALSE);
        return new AppSwitcherModuleProxy();
    }

    private void like0(List<AppSwitcherModuleProxy> modules, Boolean like) {
        Long userId = PamirsSession.getUserId();
        if (CollectionUtils.isEmpty(modules) || userId == null) {
            return;
        }
        Models.origin().createOrUpdateBatch(
                modules.stream()
                        .filter(_m -> _m != null && StringUtils.isNotBlank(_m.getModule()))
                        .map(_m -> {
                            AppsModuleRelUser rel = new AppsModuleRelUser();
                            rel.setModule(_m.getModule());
                            rel.setUserId(userId);
                            rel.setLike(like);
                            rel.setOrderNumber(0L);
                            return rel;
                        })
                        .collect(Collectors.toList())
        );

    }

    private void addClientTypeFilter(LambdaQueryWrapper<AppSwitcherModuleProxy> qw) {
        ClientTypeEnum clientTypeEnum = PageLoadHelper.getCurrentClientType();
        qw.has(AppSwitcherModuleProxy::getClientTypes, clientTypeEnum);
    }
}
