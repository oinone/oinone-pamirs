package pro.shushi.pamirs.framework.test.data.base.function;

import pro.shushi.pamirs.framework.test.data.base.model.TestFunctionModel;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * 2020/7/24 7:11 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(TestFunctionModel.MODEL_MODEL)
public interface TestFunctionInterface {

    TestFunctionModel test0(TestFunctionModel testModel);

    TestFunctionModel test(TestFunctionModel testModel);

}
