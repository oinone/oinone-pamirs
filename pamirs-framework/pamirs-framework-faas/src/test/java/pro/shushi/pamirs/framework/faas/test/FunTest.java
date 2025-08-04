package pro.shushi.pamirs.framework.faas.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.test.data.base.function.TestFunction;
import pro.shushi.pamirs.framework.test.data.base.model.TestFunctionModel;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.JsonUtils;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 函数测试
 * <p>
 * 2020/7/24 12:44 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@DisplayName("函数测试")
public class FunTest extends AbstractBaseTest {

    @Resource
    private TestFunction testFunction;

    @Test
    @Order(0)
    @DisplayName("测试函数")
    public void testFunInvoke() {

        // 准备数据
        String namespace = TestFunctionModel.MODEL_MODEL;
        String fun = "test";
        String fun0 = "test0";
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(namespace);
        modelConfig.setLname(TestFunctionModel.class.getName());
        fixFunction(namespace, fun, TestFunctionModel.class);
        fixFunction(namespace, fun0, TestFunctionModel.class);

        // 执行用例
        TestFunctionModel testModel = Fun.run(namespace, fun, new TestFunctionModel());
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(Fun.fetch(namespace, fun), testModel);
        AssertionErrors.assertEquals("函数调用", 2, testModel.getField());

        testModel = Fun.run(TestFunctionModel::test0, testModel);
        AssertionErrors.assertEquals("函数调用", 3, testModel.getField());

    }

    @Test
    @Order(0)
    @DisplayName("多参数测试函数")
    public void testFunMultiParamInvoke() {

        // 准备数据
        String namespace = TestFunctionModel.MODEL_MODEL;
        String fun = "test";
        String funi = "testi";
        String funq = "testq";
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(namespace);
        modelConfig.setLname(TestFunctionModel.class.getName());
        fixFunction(namespace, fun, TestFunctionModel.class);
        fixFunction(namespace, funi, TestFunctionModel.class);
        fixFunction(namespace, funq, TestFunctionModel.class);

        // 执行用例
        TestFunctionModel testModel = Fun.run(namespace, fun, new TestFunctionModel());
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, funi, testModel, 1);
        AssertionErrors.assertEquals("函数调用", 2, testModel.getField());

        String result = Fun.run(namespace, funq, testModel, 1, new QueryWrapper<>());
        AssertionErrors.assertEquals("函数调用", "test", result);

        result = Fun.run(namespace, funq, testModel, 1, null);
        AssertionErrors.assertEquals("函数调用", "", result);

    }

    @Test
    @Order(0)
    @DisplayName("奇怪参数测试函数")
    public void testFunStrangeParamInvoke() {

        // 准备数据
        String namespace = TestFunctionModel.MODEL_MODEL;
        String fun = "test";
        String testNumberStartArg = "testNumberStartArg";
        String testModelStartArg = "testModelStartArg";
        String testSingleArg = "testSingleArg";
        String testWithoutArg = "testWithoutArg";
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(namespace);
        modelConfig.setLname(TestFunctionModel.class.getName());
        fixFunction(namespace, fun, TestFunctionModel.class);
        fixFunction(namespace, testNumberStartArg, TestFunctionModel.class);
        fixFunction(namespace, testModelStartArg, TestFunctionModel.class);
        fixFunction(namespace, testSingleArg, TestFunctionModel.class);
        fixFunction(namespace, testWithoutArg, TestFunctionModel.class);

        // 执行用例
        TestFunctionModel testModel = Fun.run(TestFunctionModel::testNumberStartArg, 1, new TestFunctionModel());
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(TestFunctionModel::testModelStartArg, testModel, 1);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(TestFunctionModel::testSingleArg, 1);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(TestFunctionModel::testWithoutArg);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, testNumberStartArg, 1, testModel);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, testModelStartArg, testModel, 1);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, testWithoutArg);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

    }

    @Test
    @Order(0)
    @DisplayName("测试同类中相同技术名称函数，最下方函数生效")
    public void testSameNameFunInvoke() {

        // 准备数据
        String namespace = TestFunctionModel.MODEL_MODEL;
        String fun = "testOverride";
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(namespace);
        modelConfig.setLname(TestFunctionModel.class.getName());
        fixFunction(namespace, fun, TestFunctionModel.class);

        // 执行用例
        TestFunctionModel testModel = Fun.run(namespace, fun, new TestFunctionModel(), 1);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

    }

    @Test
    @Order(0)
    @DisplayName("测试类外定义函数")
    public void testOutFunInvoke() {

        // 准备数据
        String namespace = TestFunctionModel.MODEL_MODEL;
        String fun = "testOut";
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(namespace);
        modelConfig.setLname(TestFunctionModel.class.getName());
        fixFunction(namespace, fun, TestFunctionModel.class, TestFunction.class);

        // 执行用例
        TestFunctionModel testModel = Fun.run(namespace, fun, new TestFunctionModel());
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

    }

    @Test
    @Order(0)
    @DisplayName("测试类外重载，模型类中方法优先")
    public void testOverrideFunInvoke() {

        // 准备数据
        String namespace = TestFunctionModel.MODEL_MODEL;
        String fun = "testOutOverride";
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(namespace);
        modelConfig.setLname(TestFunctionModel.class.getName());
        fixFunction(namespace, fun, TestFunctionModel.class);

        // 执行用例
        TestFunctionModel testModel = Fun.run(namespace, fun, new TestFunctionModel());
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

    }

    @Test
    @Order(0)
    @DisplayName("map调用测试函数")
    public void testFunMapInvoke() {

        // 准备数据
        String namespace = TestFunctionModel.MODEL_MODEL;
        String fun = "test";
        String funi = "testi";
        String testNumberStartArg = "testNumberStartArg";
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(namespace);
        modelConfig.setLname(TestFunctionModel.class.getName());
        fixFunction(namespace, fun, TestFunctionModel.class);
        fixFunction(namespace, funi, TestFunctionModel.class);
        fixFunction(namespace, testNumberStartArg, TestFunctionModel.class);

        // 执行用例
        TestFunctionModel testModel = Fun.run(namespace, fun, new TestFunctionModel());
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, funi, convertMap(testModel), 1);
        AssertionErrors.assertEquals("函数调用", 2, testModel.getField());

        testModel = Fun.run(namespace, funi, null, 1);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, funi, convertMap(testModel), null);
        AssertionErrors.assertEquals("函数调用", 2, testModel.getField());

        testModel = Fun.run(namespace, testNumberStartArg, 1, convertMap(testModel));
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, testNumberStartArg, null, convertMap(testModel));
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

        testModel = Fun.run(namespace, testNumberStartArg, 1, null);
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());

    }

    @Test
    @Order(0)
    @DisplayName("调用Spring测试函数并且带函数管理功能")
    public void testSpringInvokeWithManagement() {
        TestFunctionModel testModel = testFunction.testOut(new TestFunctionModel());
        AssertionErrors.assertEquals("函数调用", 1, testModel.getField());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertMap(TestFunctionModel data) {
        return (Map<String, Object>) JsonUtils.parseObject(JsonUtils.toJSONString(data));
    }

}
