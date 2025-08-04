package pro.shushi.pamirs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.shushi.pamirs.boot.common.initial.PamirsBootMainInitial;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.common.util.FileUtils;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimeZone;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@DisplayName("基础服务测试")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {PamirsApplication.class})
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
    void init() throws SQLException {
        String ddl = FileUtils.read("classpath:sql/drop.sql");
        ddl(ddl);

        ddl = FileUtils.read("classpath:sql/create.sql");
        ddl(ddl);

        ddl = FileUtils.read("classpath:sql/create_test.sql");
        ddl(ddl);

        CommonApiFactory.getApi(PamirsBootMainInitial.class).installOrLoad();
    }

    @AfterAll
    @DisplayName("全部结束之后")
    static void clear() throws SQLException {
        String ddl = FileUtils.read("classpath:sql/drop.sql");
        ddl(ddl);
    }

    private static void ddl(String ddl) throws SQLException {
        try (Connection conn = CommonApiFactory.getApi(SqlSessionTemplate.class).getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(5);
                int result = stmt.executeUpdate(ddl);
                log.info("database operation rt: [{}]", result);
            }
        }
    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
