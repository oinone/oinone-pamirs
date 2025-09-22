package pro.shushi.pamirs.eip.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.core.common.CommonModule;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;
import pro.shushi.pamirs.trigger.TriggerModule;
import pro.shushi.pamirs.user.api.UserModule;

@Component
@Boot
@Module(
        name = EipModule.MODULE_NAME,
        displayName = "集成应用",
        version = "5.0.0",
        dependencies = {
                ModuleConstants.MODULE_BASE,
                CommonModule.MODULE_MODULE,
                UserModule.MODULE_MODULE,
                FileModule.MODULE_MODULE,
                TriggerModule.MODULE_MODULE,
                BusinessModule.MODULE_MODULE
        },
        clientTypes = {ClientTypeEnum.PC}
)
@Module.module(EipModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true)
@UxHomepage(actionName = "EipMenus_EipMenu_EipIntegrationInterfaceMenu", value = @UxRoute(EipIntegrationInterface.MODEL_MODEL))
public class EipModule implements PamirsModule {

    public static final String MODULE_MODULE = "eip";

    public static final String MODULE_NAME = "eip";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.eip",
        };
    }

}
