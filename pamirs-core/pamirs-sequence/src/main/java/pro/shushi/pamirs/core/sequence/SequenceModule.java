package pro.shushi.pamirs.core.sequence;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

@Component
@Boot
@Module(
        name = SequenceModule.MODULE_NAME,
        displayName = "序列",
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE},
        exclusions = {}
)
@Module.module(SequenceModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class SequenceModule implements PamirsModule {

    public static final String MODULE_MODULE = "sequence";

    public static final String MODULE_NAME = "sequence";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.core.sequence",
        };
    }

}
