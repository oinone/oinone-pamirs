package pro.shushi.pamirs.boot.orm.test.testcase;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.boot.orm.test.mock.model.TestManyToManyModel;
import pro.shushi.pamirs.boot.orm.test.mock.service.ModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.entity.MapWrapper;
import pro.shushi.pamirs.meta.base.GenericModel;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 数据管理器测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@SuppressWarnings({"rawtypes", "ConstantConditions"})
@DisplayName("数据管理器测试")
public class DataManagerTest extends AbstractBaseTest {

    private final static Long initialValue = 1000000000000000000L;

    @Test
    @Order(0)
    @DisplayName("测试GenericModel")
    @Transactional
    public void testGenericModel() {
        new ModelService<>(
                /*entity*/new GenericModel(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 1).setValue(ModuleCategory::getName, "test0"),
                /*updateEntity*/new GenericModel(ModuleCategory.MODEL_MODEL),
                /*query*/new GenericModel(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getId, initialValue + 2L),
                Lists.newArrayList(
                        new GenericModel(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 2).setValue(ModuleCategory::getId, initialValue + 2L).setValue(ModuleCategory::getName, "test1"),
                        new GenericModel(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 3).setValue(ModuleCategory::getId, initialValue + 3L).setValue(ModuleCategory::getName, "test2")
                ),
                Pops.query(new GenericModel(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getName, "test0")),
                new Pagination<>().setModel(ModuleCategory.MODEL_MODEL)
        ).test();
    }

    @Test
    @Order(0)
    @DisplayName("测试DataMap")
    @Transactional
    public void testDataMap() {
        new ModelService<>(
                /*entity*/new DataMap(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 1).setValue(ModuleCategory::getName, "test0"),
                /*updateEntity*/new DataMap(ModuleCategory.MODEL_MODEL),
                /*query*/new DataMap(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getId, initialValue + 2L),
                Lists.newArrayList(
                        new DataMap(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 2).setValue(ModuleCategory::getId, initialValue + 2L).setValue(ModuleCategory::getName, "test1"),
                        new DataMap(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 3).setValue(ModuleCategory::getId, initialValue + 3L).setValue(ModuleCategory::getName, "test2")
                ),
                Pops.query(new DataMap(ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getName, "test0")),
                new Pagination<>().setModel(ModuleCategory.MODEL_MODEL)
        ).test();
    }

    @Test
    @Order(0)
    @DisplayName("测试Map")
    @Transactional
    public void testMap() {
        new ModelService<>(
                /*entity*/MapWrapper.wrap(new HashMap<>(), ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 1).setValue(ModuleCategory::getName, "test0").getData(),
                /*updateEntity*/MapWrapper.wrap(new HashMap<>(), ModuleCategory.MODEL_MODEL).getData(),
                /*query*/MapWrapper.wrap(new HashMap<>(), ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getId, initialValue + 2L).getData(),
                Lists.newArrayList(
                        MapWrapper.wrap(new HashMap<>(), ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 2).setValue(ModuleCategory::getId, initialValue + 2L).setValue(ModuleCategory::getName, "test1").getData(),
                        MapWrapper.wrap(new HashMap<>(), ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getCode, ModuleCategory.MODEL_MODEL + 3).setValue(ModuleCategory::getId, initialValue + 3L).setValue(ModuleCategory::getName, "test2").getData()
                ),
                Pops.query(MapWrapper.wrap(new HashMap<>(), ModuleCategory.MODEL_MODEL).setValue(ModuleCategory::getName, "test0").getData()),
                new Pagination<>().setModel(ModuleCategory.MODEL_MODEL)
        ).test();
    }

    @Test
    @Order(0)
    @DisplayName("测试EntityModel")
    @Transactional
    public void testEntityModel() {
        new ModelService<>(
                /*entity*/new ModuleCategory().setName("test0").setCode(ModuleCategory.MODEL_MODEL + 1),
                /*updateEntity*/new ModuleCategory().setName("test0"),
                /*query*/new ModuleCategory().setId(initialValue + 2L),
                Lists.newArrayList(
                        new ModuleCategory().setName("test1").setCode(ModuleCategory.MODEL_MODEL + 2).setId(initialValue + 2L),
                        new ModuleCategory().setName("test2").setCode(ModuleCategory.MODEL_MODEL + 3).setId(initialValue + 3L)
                ),
                Pops.query(new ModuleCategory().setName("test0")),
                new Pagination<>().setModel(ModuleCategory.MODEL_MODEL)
        ).test();
    }

    @Test
    @Order(0)
    @DisplayName("测试一对多")
    @Transactional
    public void testOne2Many() {

        ModuleCategory entity = new ModuleCategory().setName("test0").setCode(ModuleCategory.MODEL_MODEL + 1);
        List<ModuleCategory> dataList = new ArrayList<>();
        dataList.add((ModuleCategory) new ModuleCategory().setName("test1").setCode(ModuleCategory.MODEL_MODEL + 2).setId(initialValue + 2L));
        dataList.add((ModuleCategory) new ModuleCategory().setName("test2").setCode(ModuleCategory.MODEL_MODEL + 3).setId(initialValue + 3L));

        Models.data().createOne(new ModuleDefinition().setModule("test3").setName("test3").setCategory(ModuleCategory.MODEL_MODEL + 2));

        FieldUtils.setFieldValue(entity, "modules", Lists.newArrayList(
                new ModuleDefinition().setModule("test1").setName("test1"),
                Models.data().createOne(new ModuleDefinition().setModule("test2").setName("test2"))
        ));
        ModuleCategory result = Models.data().fieldSave(entity, ModuleCategory::getModules);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "modules")).get(0), "id"));

        result = Models.data().fieldQuery(entity, ModuleCategory::getModules);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "modules")).get(1), "createDate"));

        FieldUtils.setFieldValue(((List) FieldUtils.getFieldValue(result, "modules")).get(0), "url", "test");
        result = Models.data().fieldSave(entity, ModuleCategory::getModules);
        result = Models.data().fieldQuery(result, ModuleCategory::getModules);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "modules")).get(0), "url"));

        result = Models.data().relationDelete(entity, ModuleCategory::getModules);
        result = Models.data().fieldQuery(result, ModuleCategory::getModules);
        Assert.assertEquals(0, ((List) FieldUtils.getFieldValue(result, "modules")).size());

        List<ModuleCategory> resultList = Models.data().listFieldQuery(dataList, ModuleCategory::getModules);
        Assert.assertNotNull(resultList.get(0).getModules());
        resultList.get(0).getModules().get(0).setSummary("testtest");
        resultList = Models.data().listFieldSave(dataList, ModuleCategory::getModules);
        Assert.assertEquals("testtest", resultList.get(0).getModules().get(0).getSummary());
        resultList = Models.data().listRelationDelete(dataList, ModuleCategory::getModules);
        Assert.assertNull(resultList.get(0).getModules());
        resultList = Models.data().listFieldQuery(dataList, ModuleCategory::getModules);
        Assert.assertTrue(CollectionUtils.isEmpty(resultList.get(0).getModules()));

    }

    @Test
    @Order(0)
    @DisplayName("测试多对一")
    @Transactional
    public void testMany2One() {

        ModuleCategory parent = new ModuleCategory().setName("test0").setCode(ModuleCategory.MODEL_MODEL + 1);
        ModuleCategory children1 = Models.data().createOne((ModuleCategory) new ModuleCategory().setName("test1").setCode(ModuleCategory.MODEL_MODEL + 2).setId(initialValue + 2L));
        ModuleCategory children2 = Models.data().createOne((ModuleCategory) new ModuleCategory().setName("test2").setCode(ModuleCategory.MODEL_MODEL + 3).setId(initialValue + 3L));
        List<ModuleCategory> dataList = new ArrayList<>();
        dataList.add(children1);
        dataList.add(children2);

        FieldUtils.setFieldValue(children1, "parent", parent);
        ModuleCategory result = Models.data().fieldSave(children1, ModuleCategory::getParent);
        Assert.assertNotNull(FieldUtils.getFieldValue(FieldUtils.getFieldValue(result, "parent"), "id"));

        result = Models.data().fieldQuery(children1, ModuleCategory::getParent);
        Assert.assertNotNull(result.getParent());
        result = Models.data().relationDelete(children1, ModuleCategory::getParent);
        Assert.assertNull(result.getParent());
        List<ModuleCategory> resultList = Models.data().listFieldQuery(dataList, ModuleCategory::getParent);
        Assert.assertNull(resultList.get(0).getParent());
        FieldUtils.setFieldValue(children1, "parent", parent);
        resultList = Models.data().listFieldSave(dataList, ModuleCategory::getParent);
        Assert.assertNotNull(resultList.get(0).getParent());
        Models.data().listRelationDelete(dataList, ModuleCategory::getParent);
        resultList = Models.data().listFieldSave(dataList, ModuleCategory::getParent);
        Assert.assertNull(resultList.get(0).getParent());

    }

    @Test
    @Order(0)
    @DisplayName("测试多对多")
    @Transactional
    public void testMany2Many() {

        TestManyToManyModel testManyToManyModel = Models.data().createOne(new TestManyToManyModel().setName("test"));

        ModuleCategory children1 = Models.data().createOne((ModuleCategory) new ModuleCategory().setName("test1").setCode(ModuleCategory.MODEL_MODEL + 2).setId(initialValue + 2L));
        ModuleCategory children2 = new ModuleCategory().setName("test2").setCode(ModuleCategory.MODEL_MODEL + 3)/*.setId(initialValue + 3L)不能设置id*/;
        List<ModuleCategory> dataList = new ArrayList<>();
        dataList.add(children1);
        dataList.add(children2);

        testManyToManyModel.setCategoryList(dataList);

        TestManyToManyModel result = Models.data().fieldSave(testManyToManyModel, TestManyToManyModel::getCategoryList);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "categoryList")).get(0), "id"));

        result = Models.data().fieldQuery(testManyToManyModel, TestManyToManyModel::getCategoryList);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "categoryList")).get(1), "createDate"));

        FieldUtils.setFieldValue(((List) FieldUtils.getFieldValue(result, "categoryList")).get(0), "name", "testt");
        result = Models.data().fieldSave(result, TestManyToManyModel::getCategoryList);
        result = Models.data().fieldQuery(result, TestManyToManyModel::getCategoryList);
        Assert.assertEquals("testt", FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "categoryList")).get(0), "name"));

        result = Models.data().relationDelete(result, TestManyToManyModel::getCategoryList);
        result = Models.data().fieldQuery(result, TestManyToManyModel::getCategoryList);
        Assert.assertEquals(0, ((List) FieldUtils.getFieldValue(result, "categoryList")).size());

        testManyToManyModel.setCategoryList(dataList);
        testManyToManyModel.getCategoryList().get(0).setDescription("testtest");
        List<TestManyToManyModel> list = Lists.newArrayList(testManyToManyModel);
        List<TestManyToManyModel> resultList = Models.data().listFieldSave(list, TestManyToManyModel::getCategoryList);
        Assert.assertEquals("testtest", resultList.get(0).getCategoryList().get(0).getDescription());

        resultList = Models.data().listFieldQuery(list, TestManyToManyModel::getCategoryList);
        Assert.assertNotNull(resultList.get(0).getCategoryList());

        resultList = Models.data().listRelationDelete(list, TestManyToManyModel::getCategoryList);
        Assert.assertNull(resultList.get(0).getCategoryList());
        resultList = Models.data().listFieldQuery(list, TestManyToManyModel::getCategoryList);
        Assert.assertTrue(CollectionUtils.isEmpty(resultList.get(0).getCategoryList()));

    }

    @Test
    @Order(0)
    @DisplayName("测试自身死循环")
    @Transactional
    public void testReentry() throws InterruptedException {

        ModuleCategory self = Models.data().createOne(new ModuleCategory().setName("test0").setCode(ModuleCategory.MODEL_MODEL + 1));
        FieldUtils.setFieldValue(self, "parent", self);
        ModuleCategory result = Models.data().queryByPk(self);
        Date beforeDate = result.getWriteDate();
        Thread.sleep(1000);

        self.setSequence(1);
        result = Models.data().fieldSave(self, ModuleCategory::getParent);
        result = Models.data().fieldQuery(result, ModuleCategory::getParent);
        Date afterDate = result.getParent().getWriteDate();
        Assert.assertNotEquals(beforeDate.getTime(), afterDate.getTime());

        result = Models.data().relationDelete(self, ModuleCategory::getParent);
        result = Models.data().fieldQuery(result, ModuleCategory::getParent);
        Assert.assertNull(result.getParent());

    }

    @Test
    @Order(0)
    @DisplayName("测试依赖死循环")
    @Transactional
    public void testDependency() throws InterruptedException {

        ModuleCategory parent = Models.data().createOne(new ModuleCategory().setName("test0").setCode(ModuleCategory.MODEL_MODEL + 1));
        ModuleCategory children1 = Models.data().createOne((ModuleCategory) new ModuleCategory().setName("test1").setCode(ModuleCategory.MODEL_MODEL + 2).setId(initialValue + 2L));
        ModuleCategory children2 = Models.data().createOne((ModuleCategory) new ModuleCategory().setName("test2").setCode(ModuleCategory.MODEL_MODEL + 3).setId(initialValue + 3L));
        FieldUtils.setFieldValue(children1, "parent", parent);
        FieldUtils.setFieldValue(children2, "parent", children1);
        FieldUtils.setFieldValue(parent, "parent", children2);
        ModuleCategory result = Models.data().queryByPk(children1);
        Date beforeDate = result.getWriteDate();
        Thread.sleep(1000);

        children1.setSequence(1);

        result = Models.data().fieldSave(children1, ModuleCategory::getParent);
        result = Models.data().fieldQuery(result, ModuleCategory::getParent);
        Date afterDate = result.getParent().getWriteDate();
        Assert.assertNotEquals(beforeDate.getTime(), afterDate.getTime());

        result = Models.data().relationDelete(children1, ModuleCategory::getParent);
        result = Models.data().fieldQuery(result, ModuleCategory::getParent);
        Assert.assertNull(result.getParent());

    }

    @Test
    @Order(0)
    @DisplayName("测试空where")
    public void testEmptyWhere() {
        try {
            Models.data().queryOneByWrapper(Pops.query().setModel(TestManyToManyModel.MODEL_MODEL));
        } catch (Exception e) {
            Assert.assertEquals("nested exception is org.apache.ibatis.exceptions.PersistenceException: \n" +
                    "### Error querying database.  Cause: com.baomidou.mybatisplus.core.exceptions.MybatisPlusException: 非法SQL，必须要有where条件\n" +
                    "### The error may exist in pro/shushi/pamirs/framework/connectors/data/mapper/GenericMapper.java (best guess)\n" +
                    "### The error may involve pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper.selectOne\n" +
                    "### The error occurred while executing a query\n" +
                    "### Cause: com.baomidou.mybatisplus.core.exceptions.MybatisPlusException: 非法SQL，必须要有where条件", e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("测试分批次查询")
    @Transactional
    public void testBatchQueryList() {
        final Long batchQueryInitialValue = 2000000000L;
        final String defaultDescription = "测试分批次查询";
        Models.data().deleteByWrapper(Pops.<ModuleCategory>lambdaQuery()
                .like(ModuleCategory::getName, "test")
                .or()
                .eq(ModuleCategory::getDescription, "测试分批次查询")
                .setModel(ModuleCategory.MODEL_MODEL));
        Models.data().createOrUpdateBatch(new ArrayList<ModuleCategory>() {{
            for (int i = 1; i <= 11; i++) {
                this.add((ModuleCategory) new ModuleCategory()
                        .setName("test" + i)
                        .setDescription(defaultDescription)
                        .setId(batchQueryInitialValue + i));
            }
        }});
        List<ModuleCategory> result = Models.data().queryListByWrapper(Pops.<ModuleCategory>lambdaQuery()
                .like(ModuleCategory::getName, "test")
                .setBatchSize(2)
                .setModel(ModuleCategory.MODEL_MODEL));
        assert result.size() == 11;
        result = Models.data().queryListByEntity((ModuleCategory) new ModuleCategory()
                .setDescription(defaultDescription));
        assert result.size() == 11;
    }

}
