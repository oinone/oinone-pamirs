package pro.shushi.pamirs.framework.faas.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.meta.util.ExpressionUtils;

/**
 * 表达式测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("表达式测试")
public class ExpressionTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("测试工具类")
    public void testEnum() {
        String content = "fillid(\"pro.shushi.pamirs.base.model.meta.Model\",{\"model\":\"pro.shushi.pamirs.base.model.meta.ServerAction\"} abc ).name + 1";
        System.out.println(ExpressionUtils.fetchStrictVariable(content));
    }

}
