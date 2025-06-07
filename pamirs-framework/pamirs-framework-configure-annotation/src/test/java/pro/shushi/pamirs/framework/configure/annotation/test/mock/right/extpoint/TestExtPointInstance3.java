package pro.shushi.pamirs.framework.configure.annotation.test.mock.right.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;

/**
 * 扩展点实现2
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/5 5:47 下午
 */
public class TestExtPointInstance3 {

    @ExtPoint.Implement(expression = "true", priority = 2)
    public String test(String name) {
        return name;
    }

}
