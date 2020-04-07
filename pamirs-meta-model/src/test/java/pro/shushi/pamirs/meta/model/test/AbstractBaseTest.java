package pro.shushi.pamirs.meta.model.test;

import org.junit.jupiter.api.*;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@DisplayName("基础服务测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract
public class AbstractBaseTest {

    @BeforeAll
    @Order(0)
    @DisplayName("全部开始之前")
    public static void lifecycle() {

    }

    @BeforeEach
    @DisplayName("单个测试方法开始之前")
    public void beforeEach() {

    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
