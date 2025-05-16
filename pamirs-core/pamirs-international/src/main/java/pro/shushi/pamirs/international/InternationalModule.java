package pro.shushi.pamirs.international;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;

@Component
@Boot
@Module(
        name = "international",
        displayName = "国际化",
        version = "5.0.0",
        dependencies = {"base","resource", "user"}
)
@Module.module(InternationalModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class InternationalModule implements PamirsModule {

    public static final String MODULE_MODULE = "international";

    public static final String MODULE_NAME = "international";
    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.international",
        };
    }

}
