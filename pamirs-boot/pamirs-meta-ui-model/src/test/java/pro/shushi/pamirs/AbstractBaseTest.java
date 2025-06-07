package pro.shushi.pamirs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@DisplayName("基础服务测试")
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {PamirsBaseTestApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract
public class AbstractBaseTest {

    @Autowired
    protected WebApplicationContext context;

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
