package pro.shushi.pamirs.user.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.loader.PageLoadAction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.ExtensionServiceLoader;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.user.api.service.UserLoginService;
import pro.shushi.pamirs.user.api.service.UserModuleLoginService;

import java.util.List;

/**
 * @author shier
 * date  2021/3/5 5:22 下午
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultUserLoginRedirectService implements UserLoginService {

    @Autowired
    private PageLoadAction pageLoadAction;

    @Override
    public ViewAction afterLoginRedirectViewAction() {
        ViewAction viewAction = pageLoadAction.homepage(new ViewAction()
                .setNeedCompileMask(false)
                .setNeedCompileView(false));
        String module = viewAction.getModule();
        ExtensionServiceLoader<UserModuleLoginService> loader = Spider.getLoader(UserModuleLoginService.class);
        List<UserModuleLoginService> extensions = loader.getExtensions();
        for (UserModuleLoginService loginService : extensions) {
            if (module.equals(loginService.module())) {
                return loginService.afterLoginRedirectViewAction();
            }
        }
        return viewAction;
//        List<UeModule> list;
//        boolean loadLocal = MetaOnlineLocalUtil.metaOnline();
//        if (loadLocal) {
//            ModuleCacheApi moduleCacheApi = PamirsSession.getContext().getModuleCache();
//            Set<String> modules = moduleCacheApi.keySet();
//            list = Optional.ofNullable(modules)
//                    .map(Set::stream)
//                    .orElse(Stream.empty())
//                    .map(moduleCacheApi::get)
//                    .filter(Objects::nonNull)
//                    .filter(ModuleDefinition::getApplication)
//                    .filter(module -> ModuleStateEnum.INSTALLED.equals(module.getState()) || ModuleStateEnum.TOUPGRADE.equals(module.getState()))
//                    .map(module -> ArgUtils.<ModuleDefinition, UeModule>convert(ModuleDefinition.MODEL_MODEL, UeModule.MODEL_MODEL, module))
//                    .collect(Collectors.toList());
//        } else {
//            list = new UeModule().queryList(Pops.<UeModule>lambdaQuery()
//                    .from(UeModule.MODEL_MODEL)
//                    .setBatchSize(-1)
//                    .select(UeModule::getModule, UeModule::getHomePageModel, UeModule::getDefaultHomePageModel, UeModule::getHomePageName, UeModule::getDefaultHomePageName)
//                    .eq(UeModule::getState, Lists.newArrayList(ModuleStateEnum.INSTALLED.value(), ModuleStateEnum.TOUPGRADE.value())));
//        }
//        AuthApi authApi = AuthApi.get();
//        list = list.stream().filter(v -> StringUtils.isNoneBlank(v.getHomePageModel(), v.getHomePageName()) && authApi.canAccessModule(v.getModule()).getSuccess())
//                .filter(ModuleDefinition::getApplication)
//                .sorted(Comparator.comparing(ModuleDefinition::getPriority)).collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(list)) {
//            AuthVerificationHelper.checkLogin();
//            log.warn("当前用户id为{},{}", PamirsSession.getUserId(), UserExpEnumerate.USER_HAS_NO_ACCESS_MODULE_ERROR.msg());
//            return new ViewAction();
//        }
//        UeModule firstPriorityModule = list.get(0);
//        ViewAction homepage = moduleHomePage(firstPriorityModule.getModule(), firstPriorityModule.getHomePageModel(), firstPriorityModule.getHomePageName());
//        if (homepage == null) {
//            homepage = firstPriorityModule.fieldQuery(UeModule::getHomePage).getHomePage();
//        }
//        return homepage;
    }

    @Deprecated
    ViewAction moduleHomePage(String module, String homePageModel, String homePageName) {
        ExtensionServiceLoader<UserModuleLoginService> loader = Spider.getLoader(UserModuleLoginService.class);
        List<UserModuleLoginService> extensions = loader.getExtensions();
        for (UserModuleLoginService loginService : extensions) {
            if (module.equals(loginService.module())) {
                return loginService.afterLoginRedirectViewAction();
            }
        }
        //都不存在的时候走DB查询
        UeModule ueModule = new UeModule().setHomePageModel(homePageModel).setHomePageName(homePageName).setModule(module).fieldQuery(UeModule::getHomePage);
        return ueModule.getHomePage();
    }
}
