package pro.shushi.pamirs.my;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.user.api.UserModule;

@Component
@Boot
@Module(
        name = MyCenterModule.MODULE_NAME,
        displayName = "个人中心",
        version = "5.0.0",
        show = ActiveEnum.INACTIVE,
        dependencies = {
                ModuleConstants.MODULE_BASE,
                UserModule.MODULE_MODULE
        },
        exclusions = {}
)
@Module.module(MyCenterModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = true)
public class MyCenterModule implements PamirsModule {

    public static final String MODULE_MODULE = "my_center";

    public static final String MODULE_NAME = "MyCenter";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.my"
        };
    }
}
