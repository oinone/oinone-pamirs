package pro.shushi.pamirs.grouping;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.core.common.CommonModule;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.PamirsModule;

import java.util.Set;

/**
 * 分组
 *
 * @author Adamancy Zhang at 17:22 on 2025-11-12
 */
@Base
@Component
@Module(
        name = GroupingModule.MODULE_NAME,
        displayName = "分组",
        version = "6.3.0",
        dependencies = {
                CommonModule.MODULE_MODULE
        }
)
@Module.module(GroupingModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class GroupingModule implements PamirsModule, InitializingBean {

    public static final String MODULE_MODULE = "grouping";

    public static final String MODULE_NAME = "grouping";

    @Autowired
    private BootConfiguration bootConfiguration;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 自启动
        Set<String> modules = bootConfiguration.getModules();
        modules.add(GroupingModule.MODULE_MODULE);
    }
}
