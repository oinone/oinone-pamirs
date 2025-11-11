package pro.shushi.pamirs.filling;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.PamirsModule;

/**
 * 快速填报
 *
 * @author Adamancy Zhang at 14:02 on 2025-11-11
 */
@Base
@Component
@Module(
        name = QuickFillingModule.MODULE_NAME,
        displayName = "快速填报",
        version = "6.3.0"
)
@Module.module(QuickFillingModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false, core = true)
public class QuickFillingModule implements PamirsModule {

    public static final String MODULE_MODULE = "quick_filling";

    public static final String MODULE_NAME = "quickFilling";

}