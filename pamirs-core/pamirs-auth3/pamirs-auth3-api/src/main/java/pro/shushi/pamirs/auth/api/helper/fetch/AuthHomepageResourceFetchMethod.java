package pro.shushi.pamirs.auth.api.helper.fetch;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;

import java.util.List;
import java.util.Set;

/**
 * 获取首页资源方法
 *
 * @author Adamancy Zhang at 09:27 on 2024-09-11
 */
public class AuthHomepageResourceFetchMethod extends AuthResourceFetchMethod<UeModule> {

    public AuthHomepageResourceFetchMethod(AuthAccessService authAccessService) {
        super(ResourcePermissionSubtypeEnum.HOMEPAGE, authAccessService);
    }

    @Override
    public List<UeModule> query(Set<Long> resourceIds) {
//        List<UeModule> modules = FetchResourceHelper.fetchUeModules(resourceIds);
//        if (CollectionUtils.isEmpty(modules)) {
//            return Collections.emptyList();
//        }
//        List<UeModule> validModules = new ArrayList<>();
//        for (UeModule module : modules) {
//            module.unsetHomePage();
//            String homepageModel = module.getHomePageModel();
//            String homepageName = module.getHomePageName();
//            if (StringUtils.isAnyBlank(homepageModel, homepageName)) {
//                continue;
//            }
//            validModules.add(module);
//        }
//        if (validModules.isEmpty()) {
//            return modules;
//        }
//        List<ViewAction> homepageActions = FetchResourceHelper.fetchHomepageActions(validModules);
//        if (CollectionUtils.isEmpty(homepageActions)) {
//            return Collections.emptyList();
//        }
//        MemoryListSearchCache<String, ViewAction> homepageActionCache = new MemoryListSearchCache<>(homepageActions, v -> Action.sign(v.getModel(), v.getName()));
//        for (UeModule module : validModules) {
//            module.setHomePage(homepageActionCache.get(Action.sign(module.getHomePageModel(), module.getHomePageName())));
//        }
//        return modules;
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isManagement(UeModule data, String path) {
        ViewAction homepage = data.getHomePage();
        if (homepage == null) {
            return Boolean.FALSE;
        }
        return authAccessService.canManagementModule(data.getModule()).getSuccess() ||
                authAccessService.canManagementHomepage(data.getModule()).getSuccess();
    }

    @Override
    public boolean isManagement(AuthResourcePermission resourcePermission) {
        String module = resourcePermission.getModule();
        return authAccessService.canManagementModule(module).getSuccess() ||
                authAccessService.canManagementHomepage(module).getSuccess();
    }

    @Override
    public AuthResourceAuthorization rawGeneratorResourceAuthorization(UeModule data, String path, Long authorizedValue) {
        ViewAction homepage = data.getHomePage();
        return AuthPermissionGenerator.generatorHomepageAuthorization(data, homepage, path, authorizedValue).setResourceId(data.getId());
    }
}