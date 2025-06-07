package pro.shushi.pamirs.framework.configure.printer.test;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.configure.annotation.core.AnnotationConfigurer;
import pro.shushi.pamirs.framework.test.data.base.model.TestBaseModel;
import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 打印元数据
 * <p>
 * 2020/7/3 10:52 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@DisplayName("打印元数据")
public class MetaPrinter extends AbstractBaseTest {

    @Resource
    private AnnotationConfigurer annotationConfigurer;

    @Resource
    private ModelDefinitionComputer modelDefinitionComputer;

    @Test
    @Order(1)
    @DisplayName("打印扫描元数据")
    public void testScanPrint() {

        // 打印
        Result<List<Meta>> configureResult = annotationConfigurer.extractDefinition(Sets.newHashSet("base", "test1", "test2"), null);
        Assert.assertTrue("配置错误", configureResult.isSuccess());
        Assert.assertNotEquals("查看点", null, configureResult.getData().get(0).findFunction("base.Field", "checkFieldName"));
        List<Meta> metaList = configureResult.getData();

        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData());

        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ExtPointImplementation.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(FunctionDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(DataDictionary.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ExtPoint.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ModelDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ModuleDefinition.MODEL_MODEL));

        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ExtPointImplementation.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(FunctionDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(DataDictionary.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ExtPoint.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ModelDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ModuleDefinition.MODEL_MODEL));

        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ExtPointImplementation.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(FunctionDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(DataDictionary.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ExtPoint.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ModelDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ModuleDefinition.MODEL_MODEL));

        Assert.assertTrue("查看点", configureResult.isSuccess());

    }

    @Test
    @Order(1)
    @DisplayName("打印计算元数据")
    public void testComputePrint() {

        // 打印
        Result<List<Meta>> configureResult = annotationConfigurer.extractDefinition(Sets.newHashSet("base", "test1", "test2"), null);
        Assert.assertTrue("配置错误", configureResult.isSuccess());
        Assert.assertNotEquals("查看点", null, configureResult.getData().get(0).findFunction("base.Field", "checkFieldName"));
        List<Meta> metaList = configureResult.getData();

        //Result<Void> result = modelDefinitionComputer.compute(configureResult.getData());
        //Assert.assertTrue("计算错误", result.isSuccess());

        Assert.assertEquals("主键计算错误", KeyGeneratorEnum.AUTO_INCREMENT, metaList.get(2).getData().get("base").getModelField(TestBaseModel.MODEL_MODEL, "id").getKeyGenerator());
        Assert.assertNull("主键计算错误", metaList.get(2).getData().get("base").getModelField(ModelDefinition.MODEL_MODEL, "id").getKeyGenerator());

        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData());

        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ExtPointImplementation.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(FunctionDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(DataDictionary.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ExtPoint.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ModelDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(2).getData().get("base").getData().get(ModuleDefinition.MODEL_MODEL));

        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ExtPointImplementation.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(FunctionDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(DataDictionary.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ExtPoint.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ModelDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(1).getData().get("test1").getData().get(ModuleDefinition.MODEL_MODEL));

        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ExtPointImplementation.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(FunctionDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(DataDictionary.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ExtPoint.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ModelDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(metaList.get(0).getData().get("test2").getData().get(ModuleDefinition.MODEL_MODEL));

        Assert.assertTrue("查看点", configureResult.isSuccess());

    }

}
