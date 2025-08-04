//package pro.shushi.pamirs.framework.configure.printer.test;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import org.junit.Assert;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import pro.shushi.pamirs.AbstractBaseTest;
//import pro.shushi.pamirs.framework.configure.annotation.core.AnnotationConfigurer;
//import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
//import pro.shushi.pamirs.meta.api.dto.common.Result;
//import pro.shushi.pamirs.meta.api.dto.meta.Meta;
//import pro.shushi.pamirs.meta.enmu.TtypeDict;
//
//import jakarta.annotation.Resource;
//import java.util.List;
//
/// **
// * 模型定义配置计算基本测试
// *
// * @author d@shushi.pro
// * @version 1.0.0
// * date 2020-01-09 14:05
// */
//@DisplayName("模型定义配置计算基本测试")
//public class InheritedComputerTest extends AbstractBaseTest {
//
//    @Resource
//    private AnnotationConfigurer annotationConfigurer;
//
//    @Resource
//    private ModelDefinitionComputer modelDefinitionComputer;
//
//    @Test
//    @Order(1)
//    @DisplayName("测试模型定义跨模块继承字段冲突配置")
//    public void testModelDefinitionComputeSuccess(){
//
//        // 数据准备
//        Result<List<Meta>> configureResult = annotationConfigurer.extractDefinition(Sets.newHashSet("inherited1", "inherited2"), null);
//        Assert.assertTrue("配置错误", configureResult.isSuccess());
//        Assert.assertNotEquals("查看点", null, configureResult.getData().get(0).findFunction("base.Field", "checkFieldName"));
//        List<Meta> metaList = configureResult.getData();
//
//        Assert.assertEquals("未计算前注解扫描配置错误", TtypeDict.INTEGER.value(),
//                metaList.get(1).getModel("test.Model").getModelFields().get(0).getTtype().value());
//
//        // 执行测试用例
//        Result<Void> result = modelDefinitionComputer.compute(Lists.newArrayList(metaList.get(0), metaList.get(1)));
//
//        // 结果断言
//        Assert.assertTrue("计算元数据错误", result.isSuccess());
//        Assert.assertEquals("计算后注解扫描配置错误", TtypeDict.BOOLEAN.value(),
//                metaList.get(0).getModelField("test.InheritedModel2", "addField").getTtype().value());
//        Assert.assertEquals("计算后注解扫描配置错误", TtypeDict.STRING.value(),
//                metaList.get(1).getModelField("test.InheritedModel1", "addField").getTtype().value());
//        Assert.assertEquals("计算后注解扫描配置错误", TtypeDict.STRING.value(),
//                metaList.get(0).getModelField("test.Model", "addField").getTtype().value());
//        Assert.assertEquals("计算后注解扫描配置错误", TtypeDict.STRING.value(),
//                metaList.get(1).getModelField("test.Model", "addField").getTtype().value());
//
//    }
//
//}
