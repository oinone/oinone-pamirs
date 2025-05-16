package pro.shushi.pamirs.business.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.resource.api.ResourceModule;
import pro.shushi.pamirs.user.api.UserModule;

@Component
@UxHomepage(actionName = "BusinessMenus_PamirsPartnerMenu_PamirsCompanyMenu", value = @UxRoute(PamirsCompany.MODEL_MODEL))
@Module(
        name = BusinessModule.MODULE_NAME,
        displayName = "合作伙伴中心",
        version = "5.0.0",
        dependencies = {
                ModuleConstants.MODULE_BASE,
                ResourceModule.MODULE_MODULE,
                AuthModule.MODULE_MODULE,
                UserModule.MODULE_MODULE
        }
)
@Module.module(BusinessModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class BusinessModule implements PamirsModule {

    public static final String MODULE_MODULE = "business";

    public static final String MODULE_NAME = "business";

    public static final String DEFAULT_TYPE = "PAMIRS";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.business"
        };
    }


}
