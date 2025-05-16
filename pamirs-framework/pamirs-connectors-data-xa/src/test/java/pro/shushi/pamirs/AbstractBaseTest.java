package pro.shushi.pamirs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.framework.connectors.data.datasource.ddl.DdlManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.common.util.FileUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
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
@Slf4j
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {PamirsDataApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract
public class AbstractBaseTest {

    private final static String ds = "base";

    @Autowired
    protected WebApplicationContext context;

    @BeforeAll
    @DisplayName("全部开始之前")
    static void lifecycle() {
        log.info("begin.");
    }

    @BeforeEach
    @DisplayName("单个测试方法开始之前")
    public void beforeEach() {

    }

    @AfterAll
    @DisplayName("全部结束之后")
    static void clear() throws SQLException {
        String ddl = FileUtils.read("classpath:sql/test/drop.sql");
        ddl(ddl);
        ddl = FileUtils.read("classpath:sql/multids/drop.sql");
        ddl(ddl);
    }

    @SuppressWarnings("unused")
    @PostConstruct
    void init() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

        clear();

        CommonApiFactory.getApi(DdlManager.class).createDatabase(ds);
        String ddl = FileUtils.read("classpath:sql/test/create.sql");
        ddl(ddl);
        ddl = FileUtils.read("classpath:sql/multids/create.sql");
        ddl(ddl);
    }

    private static void ddl(String ddl) throws SQLException {
        try (Connection conn = CommonApiFactory.getApi(DataSource.class).getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(5);
                int result = stmt.executeUpdate(ddl);
                log.info("database operation rt: [{}]", result);
            }
        }
    }

    protected DataMap one(String modelModel){
        // 准备数据
        DataMap map = new DataMap(modelModel);
        map.put("module", "yihui");
        map.put("relation", "test");
        map.put("version", "1.0.0");

        return map;
    }

}
