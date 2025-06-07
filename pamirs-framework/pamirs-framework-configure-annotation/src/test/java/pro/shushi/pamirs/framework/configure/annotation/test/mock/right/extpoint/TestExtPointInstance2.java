package pro.shushi.pamirs.framework.configure.annotation.test.mock.right.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * 扩展点实现2
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/5 5:47 下午
 */
@Fun
public class TestExtPointInstance2 implements TestExtPoint {

    @ExtPoint.Implement(expression = "true", priority = 2)
    @Override
    public String test(String name) {
        return name;
    }

}
