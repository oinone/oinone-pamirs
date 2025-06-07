package pro.shushi.pamirs.framework.test.data.base.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dubbo.config.annotation.Service;
import pro.shushi.pamirs.framework.test.data.base.function.TestFunctionInterface;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Date;

import static pro.shushi.pamirs.framework.test.data.base.model.TestFunctionModel.MODEL_MODEL;

/**
 * 测试模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Service
@org.springframework.stereotype.Service
@Model.model(MODEL_MODEL)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestFunctionModel extends IdModel implements TestFunctionInterface {

    public static final String MODEL_MODEL = "test.TestFunctionModel";

    private static final long serialVersionUID = -6544276263402293633L;

    @Field.Integer
    @Field
    private Integer field;

    @Field
    private Boolean bool;

    @Field
    private TtypeEnum type;

    @Field
    private DataSourceEnum ds;

    @Field
    private Date date;

    @Function
    public TestFunctionModel test0(TestFunctionModel testModel) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + 1);
        }
        return testModel;
    }

    @Function
    public TestFunctionModel test(TestFunctionModel testModel) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + 1);
        }
        return testModel;
    }

    @Function.fun("testi")
    @Function
    public TestFunctionModel test(TestFunctionModel testModel, Integer i) {
        if (null == testModel) {
            return new TestFunctionModel().setField(i);
        }
        if (null == i) {
            i = 1;
        }
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + i);
        }
        return testModel;
    }

    @Function.fun("testq")
    @Function
    public String test(TestFunctionModel testModel, Integer i, QueryWrapper<TestFunctionModel> queryWrapper) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + i);
        }
        if (null != queryWrapper) {
            return "test";
        }
        return "";
    }

    @Function
    public TestFunctionModel testOverride(TestFunctionModel testModel) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + 1);
        }
        return testModel;
    }

    @Function
    public TestFunctionModel testOverride(TestFunctionModel testModel, Integer i) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + i);
        }
        return testModel;
    }

    @Function
    public TestFunctionModel testOutOverride(TestFunctionModel testModel) {
        if (null == testModel.getField()) {
            testModel.setField(1);
        } else {
            testModel.setField(testModel.getField() + 1);
        }
        return testModel;
    }

    @Function
    public TestFunctionModel testModelStartArg(TestFunctionModel testModel, Integer i) {
        return testModel.setField(i);
    }

    @Function
    public TestFunctionModel testSingleArg(Integer i) {
        return new TestFunctionModel().setField(i);
    }

    @Function
    public TestFunctionModel testNumberStartArg(Integer i, TestFunctionModel testModel) {
        if (null == i) {
            i = 1;
        }
        if (null == testModel) {
            return new TestFunctionModel().setField(i);
        }
        return testModel.setField(i);
    }

    @Function
    public TestFunctionModel testWithoutArg() {
        return new TestFunctionModel().setField(1);
    }

}
