package pro.shushi.pamirs.framework.connectors.data.sql.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.update.UpdateWrapper;

/**
 * update wrapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("update wrapper静态测试")
public class BaseUpdateWrapperTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("测试UpdateWrapper功能")
    public void testUpdateWrapper() {
        UpdateWrapper<?> u = Pops.update().eq("name", 123).eq("model", "test").set("name", 234).set("haha", 567);
        AssertionErrors.assertEquals("update wrapper功能", "name=#{ew.paramNameValuePairs.MPGENVAL1},haha=#{ew.paramNameValuePairs.MPGENVAL2}", u.getSqlSet());
        AssertionErrors.assertEquals("update wrapper功能", "(name = #{ew.paramNameValuePairs.MPGENVAL3} AND model = #{ew.paramNameValuePairs.MPGENVAL4})", u.getSqlSegment());
    }

}
