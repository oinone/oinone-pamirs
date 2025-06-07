package pro.shushi.pamirs.dev.tools;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;


@Component
@Boot
@Module(
        name = DevToolsModule.MODULE_NAME,
        displayName = "研发工具",
        version = "5.0.0",
        dependencies = {
                ModuleConstants.MODULE_BASE,
        },
        exclusions = {}
)
@Module.module(DevToolsModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = true)
public class DevToolsModule implements PamirsModule {

    public static final String MODULE_MODULE = "dev_tools";

    public static final String MODULE_NAME = "devTools";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.dev.tools"
        };
    }
}
