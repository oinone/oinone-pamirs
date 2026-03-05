package pro.shushi.pamirs.user.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.core.common.version.Version;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.resource.api.ResourceModule;
import pro.shushi.pamirs.user.api.model.PamirsUser;

@UxHomepage(actionName = "UserMenus_PamirsUserMenu", value = @UxRoute(PamirsUser.MODEL_MODEL))
@Component
@Module(
        name = UserModule.MODULE_NAME,
        displayName = "用户中心",
        version = UserModule.MODULE_VERSION,
        dependencies = {ModuleConstants.MODULE_BASE, ResourceModule.MODULE_MODULE, AuthModule.MODULE_MODULE},
        exclusions = {}
)
@Module.module(UserModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class UserModule implements PamirsModule {

    public static final String MODULE_MODULE = "user";

    public static final String MODULE_NAME = "user";

    public static final String MODULE_VERSION = "7.2.0";

    public static final Version VERSION = Version.parse(UserModule.MODULE_VERSION);

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.user"
        };
    }

}
