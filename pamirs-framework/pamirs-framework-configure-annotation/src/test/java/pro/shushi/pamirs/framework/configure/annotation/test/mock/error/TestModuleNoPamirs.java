package pro.shushi.pamirs.framework.configure.annotation.test.mock.error;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

/**
 * 测试module配置类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 6:33 下午
 */
@Component
@Module.module("testNoPamirs")
@Module(
        displayName = "测试错误模块-不继承PamirsModule",
        version = "1.0.0",
        dependencies = {ModuleConstants.MODULE_BASE},
        exclusions = {}
)
@Module.Advanced(author = "pamirs", contributors = "huidao", maintainer = "huidao", selfBuilt = true)
public class TestModuleNoPamirs {

}
