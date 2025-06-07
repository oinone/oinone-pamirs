package pro.shushi.pamirs.boot.test2.module;

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
@Component("testModule22")
@Module.module("test2")
@Module(
        name = "test2",
        displayName = "测试模块2",
        version = "1.0.0",
        dependencies = {"test"},
        exclusions = {}
)
@Module.Advanced(author = "pamirs", contributors = "huidao", maintainer = "huidao", selfBuilt = true)
public class TestModule2 implements PamirsModule {

    @Override
    public String[] packagePrefix() {
        return new String[]{"pro.shushi.pamirs.boot.test2"};
    }

}
