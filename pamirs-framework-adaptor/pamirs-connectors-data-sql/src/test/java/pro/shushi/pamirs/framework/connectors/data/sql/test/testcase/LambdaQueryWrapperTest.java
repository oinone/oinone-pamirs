package pro.shushi.pamirs.framework.connectors.data.sql.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.configure.staticloader.TableInfoFetcher;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.test.model.TestCamelAndUpperQueryModel;
import pro.shushi.pamirs.framework.connectors.data.sql.test.model.TestQueryModel;
import pro.shushi.pamirs.framework.connectors.data.sql.test.model.TestUpperQueryModel;

/**
 * lambda query wrapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("Lambda query配置测试")
public class LambdaQueryWrapperTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("测试基本功能")
    public void testLambdaWrapper() {
        String queryValue = "test";
        TableInfoFetcher.initStaticModelConfig(TestQueryModel.class);
        LambdaQueryWrapper<TestQueryModel> q = Pops.<TestQueryModel>lambdaQuery()
                .select(TestQueryModel::getKey, TestQueryModel::getValue, TestQueryModel::getCamelField, TestQueryModel::getAsPropertyField)
                .eq(TestQueryModel::getKey, queryValue)
                .orderByAsc(TestQueryModel::getKey);
        AssertionErrors.assertEquals("生成查询列名列表", "`key`,`value`,`camel_field`,`as_property_field` AS asPropertyField", q.getSqlSelect());
        AssertionErrors.assertEquals("生成where条件", "(`key` = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY `key` ASC", q.getSqlSegment());
        AssertionErrors.assertEquals("获取变量", queryValue, q.getParamNameValuePairs().get("MPGENVAL1"));
    }

    @Test
    @Order(1)
    @DisplayName("测试Predicate功能")
    public void testLambdaStaticWrapper() {
        String queryValue = "test";
        TableInfoFetcher.initStaticModelConfig(TestQueryModel.class);
        LambdaQueryWrapper<TestQueryModel> q = Pops.lambdaQuery(new TestQueryModel())
                .select(TestQueryModel.class, i -> i.getLname().startsWith("va"))
                .eq(TestQueryModel::getKey, queryValue)
                .orderByAsc(TestQueryModel::getKey);
        AssertionErrors.assertEquals("生成查询列名列表", "`value`", q.getSqlSelect());
        AssertionErrors.assertEquals("生成where条件", "(`key` = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY `key` ASC", q.getSqlSegment());
        AssertionErrors.assertEquals("获取变量", queryValue, q.getParamNameValuePairs().get("MPGENVAL1"));
        q = Pops.lambdaQuery(new TestQueryModel())
                .select(i -> i.getLname().startsWith("va"))
                .eq(TestQueryModel::getKey, queryValue)
                .orderByAsc(TestQueryModel::getKey);
        AssertionErrors.assertEquals("生成查询列名列表", "`value`", q.getSqlSelect());
    }

    @Test
    @Order(1)
    @DisplayName("测试大写字段功能")
    public void testLambdaUpperWrapper() {
        String queryValue = "test";
        TableInfoFetcher.initStaticModelConfig(TestUpperQueryModel.class);
        LambdaQueryWrapper<TestUpperQueryModel> q = Pops.<TestUpperQueryModel>lambdaQuery()
                .select(TestUpperQueryModel::getKey, TestUpperQueryModel::getValue, TestUpperQueryModel::getCamelField, TestUpperQueryModel::getAsPropertyField)
                .eq(TestUpperQueryModel::getKey, "test")
                .orderByAsc(TestUpperQueryModel::getKey);
        AssertionErrors.assertEquals("生成查询列名列表", "`KEY`,`VALUE`,`CAMELFIELD`,`ASPROPERTYFIELD` AS asPropertyField", q.getSqlSelect());
        AssertionErrors.assertEquals("生成where条件", "(`KEY` = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY `KEY` ASC", q.getSqlSegment());
        AssertionErrors.assertEquals("获取变量", queryValue, q.getParamNameValuePairs().get("MPGENVAL1"));
    }

    @Test
    @Order(1)
    @DisplayName("测试驼峰与大写字段功能")
    public void testLambdaCamelAndUpperWrapper() {
        TableInfoFetcher.initStaticModelConfig(TestCamelAndUpperQueryModel.class);
        LambdaQueryWrapper<TestCamelAndUpperQueryModel> q = Pops.<TestCamelAndUpperQueryModel>lambdaQuery()
                .select(TestCamelAndUpperQueryModel::getKey, TestCamelAndUpperQueryModel::getValue, TestCamelAndUpperQueryModel::getCamelField, TestCamelAndUpperQueryModel::getAsPropertyField)
                .eq(TestCamelAndUpperQueryModel::getKey, "qwer")
                .orderByAsc(TestCamelAndUpperQueryModel::getKey);
        AssertionErrors.assertEquals("生成查询列名列表", "`KEY`,`VALUE`,`CAMEL_FIELD`,`AS_PROPERTY_FIELD` AS asPropertyField", q.getSqlSelect());
        AssertionErrors.assertEquals("生成where条件", "(`KEY` = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY `KEY` ASC", q.getSqlSegment());
    }

}
