package pro.shushi.pamirs.draft;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.PamirsModule;

import java.util.Set;

/**
 * 草稿模块
 *
 * @author Gesi at 15:30 on 2025/9/17
 */
@Base
@Component
@Module(
        name = DraftModule.MODULE_NAME,
        displayName = "草稿",
        version = "6.3.0"
)
@Module.module(DraftModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false, core = true)
public class DraftModule implements PamirsModule, InitializingBean {

    public static final String MODULE_MODULE = "draft";

    public static final String MODULE_NAME = "draft";

    @Autowired
    private BootConfiguration bootConfiguration;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 自启动
        Set<String> modules = bootConfiguration.getModules();
        modules.add(DraftModule.MODULE_MODULE);
    }
}
