package pro.shushi.pamirs.boot.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import pro.shushi.pamirs.AbstractBaseTest;

/**
 * 跨模块枚举测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("跨模块枚举测试")
public class TestAcrossModuleEnum extends AbstractBaseTest {

    @MockBean(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @MockBean
    private RedisKeyValueAdapter redisKeyValueAdapter;

    @Test
    @Order(1)
    @DisplayName("测试枚举")
    public void testEnum() {

        Assert.assertEquals("配置正确", true, true);

    }

}
