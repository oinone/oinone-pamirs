package pro.shushi.pamirs.trigger;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.core.common.CommonModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.trigger.tmodel.TaskActionTransientModel;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 22:47
 */
@UxHomepage(actionName = "TriggerMenus_PamirsOrganizationMenu", value = @UxRoute(TaskActionTransientModel.MODEL_MODEL))
@Component
@Module(
        name = TriggerModule.MODULE_NAME,
        displayName = "触发器",
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE, CommonModule.MODULE_MODULE},
        exclusions = {}
)
@Module.module(TriggerModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true)
public class TriggerModule implements PamirsModule {

    public static final String MODULE_MODULE = "trigger";

    public static final String MODULE_NAME = "trigger";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.trigger",
        };
    }

}
