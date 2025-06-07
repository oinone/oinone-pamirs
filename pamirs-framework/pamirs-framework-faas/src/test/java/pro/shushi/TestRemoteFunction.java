package pro.shushi;

import org.apache.dubbo.config.annotation.Service;
import pro.shushi.pamirs.framework.test.data.base.model.TestFunctionModel;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * 2020/7/24 7:11 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Service
@Fun(TestFunctionModel.MODEL_MODEL)
public class TestRemoteFunction {

    @Function
    public TestFunctionModel testOut(TestFunctionModel testModel) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + 1);
        }
        return testModel;
    }

    @Function
    public TestFunctionModel testOutOverride(TestFunctionModel testModel, Integer i) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + i);
        }
        return testModel;
    }

}
