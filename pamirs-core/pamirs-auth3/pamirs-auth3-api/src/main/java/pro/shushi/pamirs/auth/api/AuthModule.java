package pro.shushi.pamirs.auth.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CommonModule;
import pro.shushi.pamirs.core.common.version.Version;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;

/**
 * 权限模块
 *
 * @author Adamancy Zhang at 15:46 on 2024-01-04
 */
@Component
@Base
@Boot
@Module(
        name = AuthModule.MODULE_NAME,
        displayName = "权限",
        version = AuthModule.MODULE_VERSION,
        dependencies = {ModuleConstants.MODULE_BASE, CommonModule.MODULE_MODULE, "file"},
        clientTypes = {ClientTypeEnum.PC}
)
@Module.module(AuthModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class AuthModule implements PamirsModule {

    public static final String MODULE_MODULE = "auth";

    public static final String MODULE_NAME = "auth";

    public static final String MODULE_VERSION = "7.2.0";

    public static final Version VERSION = Version.parse(AuthModule.MODULE_VERSION);

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.auth"
        };
    }
}
