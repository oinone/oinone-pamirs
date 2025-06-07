package pro.shushi.pamirs.framework.test.data.dependency1.function;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 测试函数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Fun("namespace.Function")
public class TestFunction extends IdModel {

    @Function
    public Integer test(Long a) {
        return 1;
    }

}
