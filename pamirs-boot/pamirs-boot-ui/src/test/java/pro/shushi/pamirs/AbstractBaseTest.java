package pro.shushi.pamirs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@DisplayName("基础服务测试")
//@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
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
    void init() {

    }

    @AfterAll
    @DisplayName("全部结束之后")
    static void clear() {

    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
