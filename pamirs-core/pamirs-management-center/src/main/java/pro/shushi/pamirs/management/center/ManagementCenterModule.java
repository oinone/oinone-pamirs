package pro.shushi.pamirs.management.center;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.user.api.UserModule;

/**
 * 管理中心应用
 *
 * @author Adamancy Zhang at 16:04 on 2024-01-06
 */
@Component
@Boot
@Module(
        name = ManagementCenterModule.MODULE_NAME,
        displayName = "管理中心",
        version = "5.0.0",
        priority = 120,
        dependencies = {ModuleConstants.MODULE_BASE, UserModule.MODULE_MODULE, AuthModule.MODULE_MODULE, BusinessModule.MODULE_MODULE}
)
@Module.module(ManagementCenterModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true)
public class ManagementCenterModule implements PamirsModule {

    public static final String MODULE_MODULE = CommonConstants.MANAGEMENT_CENTER_MODULE;

    public static final String MODULE_NAME = CommonConstants.MANAGEMENT_CENTER_MODULE_NAME;

    @Override
    public String[] packagePrefix() {
        return new String[]{
                this.getClass().getPackage().getName()
        };
    }
}
