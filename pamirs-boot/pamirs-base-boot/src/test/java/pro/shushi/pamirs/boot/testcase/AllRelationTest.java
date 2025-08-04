package pro.shushi.pamirs.boot.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.boot.test.model.TestRelationModel;
import pro.shushi.pamirs.framework.configure.staticloader.TableInfoFetcher;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.util.FieldUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 关联关系测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@SuppressWarnings({"rawtypes", "ConstantConditions"})
@DisplayName("关联关系测试")
public class AllRelationTest extends AbstractBaseTest {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Test
    @Order(0)
    @DisplayName("测试关系")
    @PamirsTransactional
    public void testRelation() {

        TestRelationModel testRelationModel = new TestRelationModel().setName("test" + System.nanoTime());

        ModuleCategory children1 = new ModuleCategory().setName("test1").setCode(ModuleCategory.MODEL_MODEL + 2);
        ModuleCategory children2 = new ModuleCategory().setName("test2").setCode(ModuleCategory.MODEL_MODEL + 3)/*.setId(initialValue + 3L)不能设置id*/;
        List<ModuleCategory> dataList = new ArrayList<>();
        dataList.add(children1);
        dataList.add(children2);

        // 多对多
        testRelationModel.setCategoryList(dataList);

        // 一对多
        testRelationModel.setChildren(dataList);

        // 多对一
        testRelationModel.setParent(children1);

        // 一对一
        testRelationModel.setOne(children2);

        TestRelationModel result = defaultWriteWithFieldApi.createOrUpdateWithField(testRelationModel);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "categoryList")).get(0), "id"));
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "categoryList")).get(1), "id"));
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "children")).get(0), "id"));
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "children")).get(1), "id"));
        Assert.assertNotNull(FieldUtils.getFieldValue((FieldUtils.getFieldValue(result, "parent")), "id"));
        Assert.assertNotNull(FieldUtils.getFieldValue((FieldUtils.getFieldValue(result, "one")), "id"));

        result = Models.data().fieldQuery(result, TestRelationModel::getCategoryList);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "categoryList")).get(1), "createDate"));

        result = Models.data().fieldQuery(result, TestRelationModel::getChildren);
        Assert.assertNotNull(FieldUtils.getFieldValue(((List) FieldUtils.getFieldValue(result, "children")).get(1), "createDate"));

        result = Models.data().fieldQuery(result, TestRelationModel::getParent);
        Assert.assertNotNull(FieldUtils.getFieldValue((FieldUtils.getFieldValue(result, "parent")), "createDate"));

        result = Models.data().fieldQuery(result, TestRelationModel::getOne);
        Assert.assertNotNull(FieldUtils.getFieldValue((FieldUtils.getFieldValue(result, "one")), "createDate"));

    }

    @Test
    @Order(1)
    @DisplayName("测试LambdaQuery对M2O和O2O的支持")
    public void testLambdaM2OAndO2OQueryWrapper() {
        String queryValue = "test";
        TableInfoFetcher.initStaticModelConfig(TestRelationModel.class);
        LambdaQueryWrapper<TestRelationModel> q = Pops.<TestRelationModel>lambdaQuery()
                .select(TestRelationModel::getParent, TestRelationModel::getOne, TestRelationModel::getParent1, TestRelationModel::getOne1)
                .eq(TestRelationModel::getParent, queryValue)
                .eq(TestRelationModel::getOne, queryValue)
                .in(TestRelationModel::getParent1, queryValue)
                .in(TestRelationModel::getParent1, new ModuleCategory().setCode(queryValue).setName(queryValue))
                .in(TestRelationModel::getParent1, new ModuleCategory().setCode(queryValue).setName(queryValue))
                .orderByAsc(TestRelationModel::getParent1);
        System.out.println(1);
//        AssertionErrors.assertEquals("生成查询列名列表", "`key`,`value`,`camel_field`,`as_property_field` AS asPropertyField", q.getSqlSelect());
//        AssertionErrors.assertEquals("生成where条件", "(`key` = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY `key` ASC", q.getSqlSegment());
//        AssertionErrors.assertEquals("获取变量", queryValue, q.getParamNameValuePairs().get("MPGENVAL1"));
    }

}
