package pro.shushi.pamirs.core.common;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;

@Component
@Boot
@Module(
        name = CommonModule.MODULE_NAME,
        displayName = "公共资源",
        version = "5.0.0"
)
@Module.module(CommonModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class CommonModule implements PamirsModule {

    public static final String MODULE_MODULE = "common";

    public static final String MODULE_NAME = "common";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.core.common",
        };
    }

}
