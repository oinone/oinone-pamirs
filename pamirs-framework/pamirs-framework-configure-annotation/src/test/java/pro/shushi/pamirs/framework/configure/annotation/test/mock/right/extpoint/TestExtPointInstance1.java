package pro.shushi.pamirs.framework.configure.annotation.test.mock.right.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * 扩展点实现1
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/5 5:47 下午
 */
@Fun("testExtPoint")
public class TestExtPointInstance1 implements TestExtPoint {

    @ExtPoint.Implement(expression = "false", priority = 1)
    @Override
    public String test(String name) {
        return name;
    }

}
