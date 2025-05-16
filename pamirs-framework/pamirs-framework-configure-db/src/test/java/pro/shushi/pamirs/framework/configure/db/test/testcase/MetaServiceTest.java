package pro.shushi.pamirs.framework.configure.db.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.configure.db.model.ModelFieldStatic;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.meta.api.core.configure.MetaModelFetcher;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 元数据加载测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("元数据加载测试")
public class MetaServiceTest extends AbstractBaseTest {

    @Resource
    private MetaModelFetcher metaModelFetcher;

    @Resource
    private MetaSimulateService metaSimulateService;

    @Test
    @Order(1)
    @DisplayName("测试获取元模型编码")
    public void testFetchMetaModels() {
        List<String> models = metaModelFetcher.fetchMetaModels();
        Assert.assertEquals("fetch meta models error", ModuleDefinition.MODEL_MODEL, models.get(0));
    }

    @Test
    @Order(1)
    @DisplayName("测试静态模型配置执行")
    public void testStaticModelConfigExecute() {
        Map<String, String> modelMap = new HashMap<>();
        modelMap.put(ModelFieldStatic.MODEL_MODEL, ModelField.MODEL_MODEL);
        Object result = metaSimulateService.transientStaticExecute(modelMap, () -> null);
        Assert.assertNull("static model config error", result);
    }

}
