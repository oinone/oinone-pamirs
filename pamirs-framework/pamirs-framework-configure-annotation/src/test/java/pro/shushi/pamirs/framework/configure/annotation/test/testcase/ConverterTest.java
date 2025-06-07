package pro.shushi.pamirs.framework.configure.annotation.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.configure.annotation.core.converter.model.DictionaryConverter;
import pro.shushi.pamirs.framework.configure.annotation.core.converter.module.ModuleConverter;
import pro.shushi.pamirs.framework.configure.annotation.test.mock.right.TestModule;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import javax.annotation.Resource;

import static pro.shushi.pamirs.meta.common.constants.ModuleConstants.MODULE_BASE;

/**
 * 转换器测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("转换器测试")
public class ConverterTest extends AbstractBaseTest {

    public final static String TEST_MODULE = "test";

    @Resource
    private ModuleConverter moduleConverter;

    @Resource
    private DictionaryConverter dictionaryConverter;

    @SuppressWarnings("rawtypes")
    @Test
    @Order(1)
    @DisplayName("测试模块转换器")
    public void testModuleConverterValidate() {
        Class testClass = TestModule.class;
        MetaNames names = new MetaNames();
        names.setModule(TEST_MODULE);
        names.setModel(testClass.getName());
        ExecuteContext validateContext = new ExecuteContext();
        Result result = moduleConverter.validate(validateContext, names, testClass);
        Assert.assertTrue("annotation configure validate:success", result.isSuccess());
        ModuleDefinition r = moduleConverter.convert(names, testClass, new ModuleDefinition());
        Assert.assertEquals("annotation configure convert:module", "test", r.getModule());
        Assert.assertEquals("annotation configure convert:name", "test", r.getName());
    }

    @SuppressWarnings("rawtypes")
    @Test
    @Order(1)
    @DisplayName("测试数据字典转换器")
    public void testDictionaryConverter() {
        Class testClass = TtypeEnum.class;
        MetaNames names = new MetaNames();
        names.setModule(MODULE_BASE);
        names.setModel(testClass.getName());
        ExecuteContext validateContext = new ExecuteContext();
        Result result = dictionaryConverter.validate(validateContext, names, testClass);
        Assert.assertTrue("annotation configure validate:success", result.isSuccess());
        DataDictionary r = dictionaryConverter.convert(names, testClass, new DataDictionary());
        Assert.assertEquals("annotation configure convert:module", MODULE_BASE, r.getModule());
        Assert.assertEquals("annotation configure convert:dictionary", "base.Ttype", r.getDictionary());
    }

}
