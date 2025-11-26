package pro.shushi.pamirs.core.common;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;

import java.util.Set;

/**
 * 公共资源
 *
 * @author Adamancy Zhang at 17:46 on 2025-11-12
 */
@Base
@Component
@Boot
@Module(
        name = CommonModule.MODULE_NAME,
        displayName = "公共资源",
        version = "5.0.0"
)
@Module.module(CommonModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false, core = true)
public class CommonModule implements PamirsModule, InitializingBean {

    public static final String MODULE_MODULE = "common";

    public static final String MODULE_NAME = "common";

    @Autowired
    private BootConfiguration bootConfiguration;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 自启动
        Set<String> modules = bootConfiguration.getModules();
        modules.add(CommonModule.MODULE_MODULE);
    }
}
