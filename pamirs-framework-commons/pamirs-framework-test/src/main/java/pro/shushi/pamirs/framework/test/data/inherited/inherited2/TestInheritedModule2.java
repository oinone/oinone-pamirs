package pro.shushi.pamirs.framework.test.data.inherited.inherited2;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;

/**
 * 测试module配置类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 6:33 下午
 */
@Component
@Module.module("inherited2")
@Module(
        displayName = "测试模块",
        version = "1.0.0",
        dependencies = {"test1"},
        exclusions = {}
)
@Module.Advanced(author = "pamirs", contributors = "huidao", maintainer = "huidao", selfBuilt = true)
public class TestInheritedModule2 implements PamirsModule {

    @Override
    public String[] packagePrefix() {
        return new String[]{"pro.shushi.pamirs.framework.test.data.inherited.inherited2"};
    }

}
