package pro.shushi.pamirs.resource.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CommonModule;
import pro.shushi.pamirs.core.sequence.SequenceModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

@Component
@Boot
@Module(
        name = ResourceModule.MODULE_NAME,
        displayName = "资源",
        priority = 130,
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE,
                SequenceModule.MODULE_MODULE,
                CommonModule.MODULE_MODULE,
        },
        exclusions = {}
)
@Module.module(ResourceModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = true)
public class ResourceModule implements PamirsModule {

    public static final String MODULE_MODULE = "resource";

    public static final String MODULE_NAME = "resource";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.resource",
        };
    }

}
