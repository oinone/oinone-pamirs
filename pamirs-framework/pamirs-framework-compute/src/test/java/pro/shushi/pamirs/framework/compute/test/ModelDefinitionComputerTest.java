package pro.shushi.pamirs.framework.compute.test;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.compute.system.check.field.meta.FieldNameChecker;
import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型定义配置计算基本测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("模型定义配置计算基本测试")
public class ModelDefinitionComputerTest extends AbstractBaseTest {

    @Resource
    private ModelDefinitionComputer modelDefinitionComputer;

    @Test
    @Order(1)
    @DisplayName("测试模型定义跨模块继承计算正确配置")
    public void testModelDefinitionComputeSuccess() {

        // 数据准备 参见 MetaPrinter
        String baseModule = "base";
        MetaData baseMeta = fetchMetaData(baseModule);

        String test1Module = "test1";
        Map<String, MetaData> dependencyMetaData1 = new ConcurrentHashMap<>();
        dependencyMetaData1.put(baseModule, baseMeta);
        Meta test1Meta = fetchMeta(test1Module, dependencyMetaData1);

        String test2Module = "test2";
        Map<String, MetaData> dependencyMetaData2 = new ConcurrentHashMap<>();
        dependencyMetaData2.put(baseModule, baseMeta);
        dependencyMetaData2.put(test1Module, test1Meta.getCurrentModuleData());
        Meta test2Meta = fetchMeta(test2Module, dependencyMetaData2);

        Assert.assertEquals("未计算前注解扫描配置错误", TtypeEnum.INTEGER.value(),
                test1Meta.getModel("test.Model").getModelFields().get(0).getTtype().value());

        // 执行测试用例
        Result<Void> result = modelDefinitionComputer.compute(ComputeContext.init(), Lists.newArrayList(test1Meta, test2Meta));

        // 结果断言
        Assert.assertTrue("计算元数据错误", result.isSuccess());
        Assert.assertEquals("计算后注解扫描配置错误", TtypeEnum.INTEGER.value(),
                test1Meta.getModel("test.Model").getModelFields().get(0).getTtype().value());

    }

    @Test
    @Order(1)
    @DisplayName("测试base模型定义计算正确配置")
    public void testBaseModelDefinitionComputeSuccess() {

        // 数据准备 参见 MetaPrinter
        String moduleName = "base";
        Meta base = fetchMeta(moduleName);

        // 执行测试用例
        Result<Void> result = modelDefinitionComputer.compute(ComputeContext.init(), Lists.newArrayList(base));

        // 结果断言
        Assert.assertTrue("计算元数据错误", result.isSuccess());
        Assert.assertEquals("计算元数据错误", "sequence_config", base.getModel(SequenceConfig.MODEL_MODEL).getTable());

        JsonUtils.toJSONString(base.getCurrentModuleData().getData().get(ExtPointImplementation.MODEL_MODEL));
        JsonUtils.toJSONString(base.getCurrentModuleData().getData().get(FunctionDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(base.getCurrentModuleData().getData().get(DataDictionary.MODEL_MODEL));
        JsonUtils.toJSONString(base.getCurrentModuleData().getData().get(ExtPoint.MODEL_MODEL));
        JsonUtils.toJSONString(base.getCurrentModuleData().getData().get(ModelDefinition.MODEL_MODEL));
        JsonUtils.toJSONString(base.getCurrentModuleData().getData().get(ModuleDefinition.MODEL_MODEL));

    }

    @Test
    @Order(1)
    @DisplayName("获取函数接口命名空间")
    public void testFunInterfaceNamespace() {

        // 执行测试用例
        String namespace = NamespaceAndFunUtils.namespace(FieldNameChecker.class);

        // 结果断言
        Assert.assertEquals("函数命名空间错误", "pamirs", namespace);

    }

}
