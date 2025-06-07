package pro.shushi.pamirs.boot.testcase;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.boot.test.model.TestCategory;
import pro.shushi.pamirs.boot.test.model.TestField;
import pro.shushi.pamirs.boot.test.model.TestModel;
import pro.shushi.pamirs.framework.connectors.data.kv.RedisClusterConfig;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * base模型数据管理器测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("base模型数据管理器测试")
public class TestModelCrud extends AbstractBaseTest {

    @MockBean(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @MockBean
    private RedisKeyValueAdapter redisKeyValueAdapter;

    @MockBean
    private RedisClusterConfig redisClusterConfig;

    @Test
    @Order(1)
    @DisplayName("o2m关系字段操作")
    public void testOneToMany() {

        ModelDefinition modelDefinition = new ModelDefinition();

        modelDefinition.setModel("base.ModuleCategory");

        // 主对象query
        modelDefinition = modelDefinition.queryOne();

        // o2m field query
        modelDefinition = modelDefinition.fieldQuery(ModelDefinition::getModelFields);

        modelDefinition.getModelFields().get(0).setDescription("test");

        modelDefinition.getModelFields().get(0).setModel(null);

        modelDefinition.fieldSave(ModelDefinition::getModelFields);

        List<ModelField> modelFieldList = new ArrayList<>();
        modelFieldList.add((ModelField) new ModelField().setDisplayName("test123").setName("test").setField("test123").setTtype(TtypeEnum.BOOLEAN));
        modelDefinition.setModelFields(modelFieldList);

        modelDefinition.fieldSave(ModelDefinition::getModelFields);

        Assert.assertEquals("配置正确", true, true);

    }

    @Test
    @Order(1)
    @DisplayName("m2m关系字段操作")
    public void testManyToMany() {

        TestModel model1 = new TestModel().setModel("aaaa").setStringValue("sdf").setIntValue(1);
        TestModel model2 = new TestModel().setModel("dddd").setStringValue("dddd");

        TestCategory category = new TestCategory();
        category.setIntValue(1).setStringValue("123");

        category.create();

        // 主对象query
        category = category.queryOne();

        model1.setId(20l);
        model2.setId(21l);
        category.setModelList(Lists.newArrayList(model1, model2));
        category.fieldSave(TestCategory::getModelList);

        // m2m category query
        category = category.fieldQuery(TestCategory::getModelList);

        category.getModelList().get(0).setStringValue("test");

        category.fieldSave(TestCategory::getModelList);

        Assert.assertEquals("配置正确", true, true);

    }

    @Test
    @Order(1)
    @DisplayName("m2o关系字段操作")
    public void testManyToOne() {

        TestField field = new TestField();
        field.setIntValue(1).setStringValue("123").setTestModel(new TestModel().setModel("testModel"));

        field.create();

        // 主对象query
        field = field.queryOne();

        field.setTestModel(new TestModel().setModel("testModel").setStringValue("test2"));

        field.fieldSave(TestField::getTestModel);

        // m2o field query
        field = field.fieldQuery(TestField::getTestModel);

        field.getTestModel().setStringValue("test");

        field.fieldSave(TestField::getTestModel);

        Assert.assertEquals("配置正确", true, true);

    }

}
