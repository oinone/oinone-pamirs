package pro.shushi.pamirs.framework.test.data.base;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;

/**
 * base module配置类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 6:33 下午
 */
@Component("baseTestModule")
@Base
@Boot
@Module(
        name = "base",
        displayName = "基础模块",
        version = "1.0.0",
        dependencies = {},
        exclusions = {}
)
@Module.module(BaseModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, web = false, author = "pamirs", contributors = "huidao", maintainer = "huidao")
public class BaseModule implements PamirsModule {

    public static final String MODULE_MODULE = "base";

    public static final String MODULE_NAME = "Base";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.meta.domain",
                "pro.shushi.pamirs.meta.enmu",
                "pro.shushi.pamirs.meta.enumclass",
                "pro.shushi.pamirs.meta.base",
                "pro.shushi.pamirs.meta.api.dto.common",
                "pro.shushi.pamirs.meta.api.dto.condition",
                "pro.shushi.pamirs.meta.api.dto.wrapper",
                "pro.shushi.pamirs.framework.orm",
                "pro.shushi.pamirs.framework.compute.system.check",

                "pro.shushi.pamirs.framework.test.data.base",
        };
    }

}
