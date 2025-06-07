package pro.shushi.pamirs.framework.configure.annotation.test.mock.right.extpoint;

import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * 测试扩展点接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/5 5:47 下午
 */
@Fun("testExtPoint")
public interface TestExtPoint {

    @ExtPoint.name("test")
    String test(String name);

}
