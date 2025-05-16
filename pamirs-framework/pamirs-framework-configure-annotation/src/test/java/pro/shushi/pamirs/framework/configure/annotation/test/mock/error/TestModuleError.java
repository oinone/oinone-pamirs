package pro.shushi.pamirs.framework.configure.annotation.test.mock.error;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

/**
 * 测试module配置类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 6:33 下午
 */
@Component
@Module.module("error")
@Module(
        displayName = "测试错误模块-模型配置错误",
        version = "1.0.0",
        dependencies = {ModuleConstants.MODULE_BASE},
        exclusions = {}
)
@Module.Advanced(author = "pamirs", contributors = "huidao", maintainer = "huidao", selfBuilt = true)
public class TestModuleError implements PamirsModule {

    @Override
    public String[] packagePrefix() {
        return new String[]{"pro.shushi.pamirs.framework.configure.annotation.test.mock.error.model"};
    }
}
