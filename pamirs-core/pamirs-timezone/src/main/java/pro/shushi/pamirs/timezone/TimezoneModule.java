package pro.shushi.pamirs.timezone;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

/**
 * 时区模块
 *
 * @author Adamancy Zhang at 13:17 on 2021-09-03
 */
@Component
@Module(
        name = TimezoneModule.MODULE_NAME,
        displayName = "时区",
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE}
)
@Module.module(TimezoneModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class TimezoneModule implements PamirsModule {

    public static final String MODULE_MODULE = "timezone";

    public static final String MODULE_NAME = "timezone";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.timezone"
        };
    }
}
