package pro.shushi.pamirs.framework.connectors.data.sql.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.test.model.TestQueryModel;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;

/**
 * query wrapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("query wrapper静态测试")
public class BaseQueryWrapperTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("测试QueryWrapper基本功能")
    public void testQueryWrapper(){
        QueryWrapper<?> q = Pops.query().eq("`name`", "qwer").select("`name`", "`code`").orderByAsc("`name`");
        AssertionErrors.assertEquals("query wrapper功能", "`name`,`code`", q.getSqlSelect());
        AssertionErrors.assertEquals("query wrapper功能", "(`name` = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY `name` ASC", q.getSqlSegment());
    }

    @Test
    @Order(1)
    @DisplayName("测试QueryWrapper声明模型类功能")
    public void testQueryWrapperWithClass(){
        QueryWrapper<TestQueryModel> q = Pops.<TestQueryModel>query().eq("`name`", "qwer").select("`name`", "`code`").orderByAsc("`name`");
        AssertionErrors.assertEquals("query wrapper功能", "`name`,`code`", q.getSqlSelect());
        AssertionErrors.assertEquals("query wrapper功能", "(`name` = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY `name` ASC", q.getSqlSegment());
    }

}
