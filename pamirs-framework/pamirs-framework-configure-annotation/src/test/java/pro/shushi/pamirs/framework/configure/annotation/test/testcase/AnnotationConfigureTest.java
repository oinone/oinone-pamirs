package pro.shushi.pamirs.framework.configure.annotation.test.testcase;

import org.apache.commons.collections4.SetUtils;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.configure.annotation.core.AnnotationConfigurer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import jakarta.annotation.Resource;
import java.util.List;

import static pro.shushi.pamirs.meta.common.constants.ModuleConstants.MODULE_BASE;

/**
 * 注解配置基本测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("注解配置基本测试")
public class AnnotationConfigureTest extends AbstractBaseTest {

    @Resource
    private AnnotationConfigurer annotationConfigurer;

    @Test
    @Order(1)
    @DisplayName("测试元模型正确配置")
    public void testMetaModuleConvertSuccess() {

        // 执行测试用例
        Result<List<Meta>> result = annotationConfigurer.extractDefinition(SetUtils.hashSet(MODULE_BASE), null);

        // 结果断言
        Assert.assertTrue("扩展点配置错误", result.isSuccess());
        Assert.assertEquals("扫描模块元数据错误", "base", result.getData().get(0).getModule());

    }

    @Test
    @Order(1)
    @DisplayName("测试模块依赖关系")
    public void testModuleDependencyConvertSuccess() {

        // 执行测试用例
        Result<List<Meta>> result = annotationConfigurer.extractDefinition(SetUtils.hashSet("dependency"), null);

        // 结果断言
        Assert.assertTrue("扩展点配置错误", result.isSuccess());
        Assert.assertEquals("扫描模块元数据错误", "dependency", result.getData().get(0).getModule());
        Assert.assertEquals("扫描模块元数据错误", "base", result.getData().get(0).getData().get("base").getModule().getName());

    }

    @Test
    @Order(1)
    @DisplayName("测试test模块注解正确配置")
    public void testModuleTestConvertSuccess() {

        // 执行测试用例
        Result<List<Meta>> result = annotationConfigurer.extractDefinition(SetUtils.hashSet("test"), null);

        // 结果断言
        Assert.assertTrue("扩展点配置错误", result.isSuccess());
        Assert.assertEquals("扫描模块元数据错误", "test", result.getData().get(0).getModule());

    }

    @Test
    @Order(1)
    @DisplayName("测试模块注解错误配置，未配置module")
    public void testModuleNoModuleError() {

        // 执行测试用例
        Result<List<Meta>> result = annotationConfigurer.extractDefinition(null, SetUtils.hashSet("test", "error"));

        // 结果断言
        Assert.assertFalse("未检测出\"未配置module\"错误", result.isSuccess());

    }

    @Test
    @Order(1)
    @DisplayName("测试模型注解错误配置")
    public void testModelConvertError() {

        // 执行测试用例
        Result<List<Meta>> result = annotationConfigurer.extractDefinition(SetUtils.hashSet("error"), null);

        // 结果断言
        Assert.assertFalse("配置错误", result.isSuccess());
        Assert.assertEquals("错误模块", "error", result.getData().get(0).getModule());

    }

}
