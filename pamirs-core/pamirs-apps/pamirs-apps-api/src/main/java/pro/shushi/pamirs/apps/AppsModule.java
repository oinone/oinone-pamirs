package pro.shushi.pamirs.apps;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.core.common.CommonModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;


@Base
@UxHomepage(@UxRoute(model = AppsManagementModule.MODEL_MODEL, viewName = "apps_module_gallery", viewType = ViewTypeEnum.GALLERY))
@Component
@Boot
@Module(
        name = AppsModule.MODULE_NAME,
        displayName = "应用中心",
        version = "5.0.0",
        priority = 140,
        dependencies = {
                ModuleConstants.MODULE_BASE,
                CommonModule.MODULE_MODULE,
                AuthModule.MODULE_MODULE,
        },
        clientTypes = {ClientTypeEnum.PC}
)
@Module.module(AppsModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = true)
public class AppsModule implements PamirsModule {

    public static final String MODULE_MODULE = "apps";

    public static final String MODULE_NAME = "apps";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.apps",
        };
    }

}
