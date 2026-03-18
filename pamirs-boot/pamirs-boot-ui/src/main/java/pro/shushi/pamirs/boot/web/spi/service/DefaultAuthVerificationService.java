package pro.shushi.pamirs.boot.web.spi.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.spi.api.AuthVerificationApi;
import pro.shushi.pamirs.boot.web.spi.holder.UserIdentityHolder;
import pro.shushi.pamirs.boot.web.utils.PageLoadHelper;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

/**
 * 默认权限验证服务
 *
 * @author Adamancy Zhang at 11:10 on 2023-12-23
 */
@Component
@Order
@SPI.Service
public class DefaultAuthVerificationService implements AuthVerificationApi {

    @Override
    public boolean verifyHomepageActionAccess(ModuleDefinition moduleDefinition, String model, String actionName) {
        if (UserIdentityHolder.isAdmin()) {
            return true;
        }
        String module = moduleDefinition.getModule();
        if (StringUtils.isAnyBlank(model, actionName)) {
            return AuthApi.get().canAccessHomepage(module).getSuccess();
        } else {
            Menu menu = PageLoadHelper.isMenuAction(module, model, actionName);
            if (menu == null) {
                return AuthApi.get().canAccessHomepage(module).getSuccess();
            }
            return AuthApi.get().canAccessMenu(module, menu.getName()).getSuccess();
        }
    }

    @Override
    public boolean verifyActionAccess(ModuleDefinition module, ActionTypeEnum actionType, String model, String actionName) {
        if (UserIdentityHolder.isAdmin()) {
            return true;
        }
        checkModuleAuth(module);
        return checkMenuAuth(module, model, actionName);
    }

    protected void checkModuleAuth(ModuleDefinition moduleDefinition) {
        boolean canAccess = AuthApi.get().canAccessModule(moduleDefinition.getModule()).getSuccess();
        if (!canAccess) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_MODULE_CAN_NOT_ACCESS_ERROR)
                    .appendMsg(I18nUtils.getMessage("pamirs.boot.ui.auth.moduleAccessDenied", moduleDefinition.getDisplayName(), moduleDefinition.getModule())).errThrow();
        }
    }

    protected boolean checkMenuAuth(ModuleDefinition moduleDefinition, String model, String actionName) {
        String module = moduleDefinition.getModule();

        String homepageModel = moduleDefinition.getHomePageModel();
        String homepageName = moduleDefinition.getHomePageName();

        AuthApi authApi = AuthApi.get();
        boolean canAccess = true;
        boolean isHomepageAction = StringUtils.isNoneBlank(homepageModel, homepageName) && homepageModel.equals(model) && homepageName.equals(actionName);
        if (isHomepageAction) {
            canAccess = authApi.canAccessHomepage(module).getSuccess();
            if (canAccess) {
                return true;
            }
        }

        Menu menu = PageLoadHelper.isMenuAction(module, model, actionName);
        if (menu != null) {
            canAccess = authApi.canAccessMenu(module, menu.getName()).getSuccess();
            if (canAccess) {
                return true;
            }
        }

        return authApi.canAccessAction(model, actionName).getSuccess();
    }
}
