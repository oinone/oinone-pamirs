package pro.shushi.pamirs.framework.configure.annotation.test.mock.base;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;

import static pro.shushi.pamirs.meta.common.constants.ModuleConstants.MODULE_BASE;
import static pro.shushi.pamirs.meta.common.constants.ModuleConstants.MODULE_BASE_NAME;

/**
 * base module配置类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 6:33 下午
 */
@Component("testBaseModule")
@Base
@Boot
@Module(
        name = MODULE_BASE_NAME,
        displayName = "基础模块",
        version = "1.0.0",
        dependencies = {},
        exclusions = {}
)
@Module.module(MODULE_BASE)
@Module.Advanced(selfBuilt = true, author = "pamirs", contributors = "huidao", maintainer = "huidao")
public class BaseModule implements PamirsModule {

    @Override
    public String[] packagePrefix() {
        return new String[]{"pro.shushi.pamirs.meta.domain", "pro.shushi.pamirs.meta.enumclass", "pro.shushi.pamirs.meta"};
    }

}
