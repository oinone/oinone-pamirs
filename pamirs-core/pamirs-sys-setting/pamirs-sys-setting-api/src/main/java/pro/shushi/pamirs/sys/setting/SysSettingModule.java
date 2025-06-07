package pro.shushi.pamirs.sys.setting;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;

/**
 * SysSettingModule
 *
 * @author yakir on 2022/11/08 10:19.
 */
@Component
@Boot
@Module(
        name = SysSettingModule.MODULE_NAME,
        displayName = "系统配置",
        version = "5.0.0",
        show = ActiveEnum.INACTIVE,
        dependencies = {
                ModuleConstants.MODULE_BASE,
        },
        exclusions = {}
)
@Module.module(SysSettingModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = true)
public class SysSettingModule implements PamirsModule {

    public static final String MODULE_MODULE = "sys_setting";

    public static final String MODULE_NAME = "sysSetting";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.sys.setting"
        };
    }
}
