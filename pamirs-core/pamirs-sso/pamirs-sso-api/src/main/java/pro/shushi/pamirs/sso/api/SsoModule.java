package pro.shushi.pamirs.sso.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.user.api.UserModule;

@Component
@Module(
        name = SsoModule.MODULE_NAME,
        displayName = "单点登录",
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE, UserModule.MODULE_MODULE},
        exclusions = {}
)
@Module.module(SsoModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = true)
public class SsoModule implements PamirsModule {

    public static final String MODULE_MODULE = "sso";

    public static final String MODULE_NAME = "sso";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.sso"
        };
    }

}


