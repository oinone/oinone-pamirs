package pro.shushi.pamirs.boot.standard.test;

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
@Component
@Base
@Boot
@Module(
        name = "standard",
        displayName = "基础模块",
        version = "1.0.0",
        dependencies = {"base"},
        exclusions = {}
)
@Module.module("standard")
@Module.Advanced(selfBuilt = true, author = "pamirs", contributors = "huidao", maintainer = "huidao")
public class StandardModule implements PamirsModule {

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.boot.standard.test.function"
        };
    }

}
