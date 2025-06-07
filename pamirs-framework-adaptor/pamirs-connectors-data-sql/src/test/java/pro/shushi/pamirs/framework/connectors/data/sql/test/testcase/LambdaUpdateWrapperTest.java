package pro.shushi.pamirs.framework.connectors.data.sql.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.configure.staticloader.TableInfoFetcher;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.test.model.TestCamelAndUpperQueryModel;
import pro.shushi.pamirs.framework.connectors.data.sql.test.model.TestQueryModel;
import pro.shushi.pamirs.framework.connectors.data.sql.test.model.TestUpperQueryModel;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;

/**
 * lambda update wrapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("Lambda update配置测试")
public class LambdaUpdateWrapperTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("测试基本功能")
    public void testLambdaWrapper() {
        String queryValue = "test";
        TableInfoFetcher.initStaticModelConfig(TestQueryModel.class);
        LambdaUpdateWrapper<TestQueryModel> u = Pops.<TestQueryModel>lambdaUpdate()
                .set(TestQueryModel::getKey, 123)
                .set(TestQueryModel::getValue, 123)
                .set(TestQueryModel::getCamelField, 123)
                .set(TestQueryModel::getAsPropertyField, 123)
                .eq(TestQueryModel::getKey, queryValue)
                .orderByAsc(TestQueryModel::getKey);
        AssertionErrors.assertEquals("生成set", "`key`=#{ew.paramNameValuePairs.MPGENVAL1},`value`=#{ew.paramNameValuePairs.MPGENVAL2},`camel_field`=#{ew.paramNameValuePairs.MPGENVAL3},`as_property_field` AS asPropertyField=#{ew.paramNameValuePairs.MPGENVAL4}", u.getSqlSet());
        AssertionErrors.assertEquals("生成where条件", "(`key` = #{ew.paramNameValuePairs.MPGENVAL5}) ORDER BY `key` ASC", u.getSqlSegment());
        AssertionErrors.assertEquals("获取变量", queryValue, u.getParamNameValuePairs().get("MPGENVAL5"));
    }

    @Test
    @Order(1)
    @DisplayName("测试大写字段功能")
    public void testLambdaUpperWrapper() {
        String queryValue = "test";
        TableInfoFetcher.initStaticModelConfig(TestUpperQueryModel.class);
        LambdaUpdateWrapper<TestUpperQueryModel> u = Pops.<TestUpperQueryModel>lambdaUpdate()
                .set(TestUpperQueryModel::getKey, 123)
                .set(TestUpperQueryModel::getCamelField, 123)
                .eq(TestUpperQueryModel::getKey, "test")
                .orderByAsc(TestUpperQueryModel::getKey);
        AssertionErrors.assertEquals("生成set", "`KEY`=#{ew.paramNameValuePairs.MPGENVAL1},`CAMELFIELD`=#{ew.paramNameValuePairs.MPGENVAL2}", u.getSqlSet());
        AssertionErrors.assertEquals("生成where条件", "(`KEY` = #{ew.paramNameValuePairs.MPGENVAL3}) ORDER BY `KEY` ASC", u.getSqlSegment());
        AssertionErrors.assertEquals("获取变量", queryValue, u.getParamNameValuePairs().get("MPGENVAL3"));
    }

    @Test
    @Order(1)
    @DisplayName("测试驼峰与大写字段功能")
    public void testLambdaCamelAndUpperWrapper() {
        TableInfoFetcher.initStaticModelConfig(TestCamelAndUpperQueryModel.class);
        LambdaUpdateWrapper<TestCamelAndUpperQueryModel> u = Pops.<TestCamelAndUpperQueryModel>lambdaUpdate()
                .set(TestCamelAndUpperQueryModel::getKey, 123)
                .set(TestCamelAndUpperQueryModel::getCamelField, 123)
                .eq(TestCamelAndUpperQueryModel::getCamelField, "qwer")
                .orderByAsc(TestCamelAndUpperQueryModel::getKey);
        AssertionErrors.assertEquals("生成set", "`KEY`=#{ew.paramNameValuePairs.MPGENVAL1},`CAMEL_FIELD`=#{ew.paramNameValuePairs.MPGENVAL2}", u.getSqlSet());
        AssertionErrors.assertEquals("生成where条件", "(`CAMEL_FIELD` = #{ew.paramNameValuePairs.MPGENVAL3}) ORDER BY `KEY` ASC", u.getSqlSegment());
    }

}
