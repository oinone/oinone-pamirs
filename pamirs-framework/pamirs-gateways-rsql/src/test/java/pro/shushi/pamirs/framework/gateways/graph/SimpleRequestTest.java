package pro.shushi.pamirs.framework.gateways.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;

/**
 * rsql基本测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("rsql基本测试")
public class SimpleRequestTest extends AbstractBaseTest {

    @MockBean
    private GenericMapper genericMapper;

    @Test
    @Order(0)
    @DisplayName("测试rsql基本请求")
    public void testRequest() {

    }

}
