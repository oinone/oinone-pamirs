package pro.shushi.pamirs.bizauth.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;


@Component
@Base
@Boot
@Module(
        name = AuthBusinessModule.MODULE_NAME,
        displayName = "权限",
        exclusions = {},
        dependencies = {ModuleConstants.MODULE_BASE, AuthModule.MODULE_MODULE, BusinessModule.MODULE_MODULE},
        clientTypes = {ClientTypeEnum.PC}
)
@Module.module(AuthBusinessModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true,application = false)
public class AuthBusinessModule implements PamirsModule {

    public static final String MODULE_MODULE = "auth_business";

    public static final String MODULE_NAME = "authBusiness";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.bizauth"
        };
    }

}