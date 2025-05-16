package pro.shushi.pamirs.framework.configure.annotation.test.mock.error.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 测试模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Model.model("test.Model")
@Module(displayName = "错误配置", version = "错误配置")
public class TestModel extends IdModel {

}
