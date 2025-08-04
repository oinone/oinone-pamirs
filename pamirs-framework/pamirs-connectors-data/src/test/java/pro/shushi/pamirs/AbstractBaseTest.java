package pro.shushi.pamirs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import pro.shushi.pamirs.framework.connectors.data.datasource.ddl.DdlManager;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.meta.TestDatabase;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.common.util.FileUtils;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

    }

    @BeforeEach
    @DisplayName("单个测试方法开始之前")
    public void beforeEach() {

    }

    @AfterAll
    @DisplayName("全部结束之后")
    static void clear() throws SQLException {
        String ddl = FileUtils.read("classpath:sql/system/drop.sql");
        ddl(ddl);
        ddl = FileUtils.read("classpath:sql/test/drop.sql");
        ddl(ddl);
        ddl = FileUtils.read("classpath:sql/multipk/drop.sql");
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
        String ddl = FileUtils.read("classpath:sql/system/create.sql");
        ddl(ddl);
        ddl = FileUtils.read("classpath:sql/test/create.sql");
        ddl(ddl);
        ddl = FileUtils.read("classpath:sql/multipk/create.sql");
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

    protected DataMap one(String modelModel) {
        // 准备数据
        DataMap map = new DataMap(modelModel);
        map.put("module", "yihui");
        map.put("relation", "test");
        map.put("version", "1.0.0");

        return map;
    }

    protected List<DataMap> batchList(String modelModel) {
        List<DataMap> list = new ArrayList<>();
        DataMap map = new DataMap(modelModel);
        map.put("module", "yihui1");
        map.put("relation", "test");
        map.put("version", "1.0.0");
        map.put("optVersion", 0);
        list.add(map);
        DataMap map2 = new DataMap(modelModel);
        map2.put("module", "yihui2");
        map2.put("relation", "test");
        map2.put("version", "1.0.0");
        map2.put("optVersion", 0);
        list.add(map2);
        DataMap map3 = new DataMap(modelModel);
        map3.put("module", "yihui3");
        map3.put("relation", "test");
        map3.put("version", "1.0.0");
        map3.put("optVersion", 0);
        list.add(map3);
        return list;
    }

    protected List<DataMap> randomOrderBatchList(String modelModel) {
        List<DataMap> list = new ArrayList<>();
        DataMap map3 = new DataMap(modelModel);
        map3.put("module", "yihui3");
        map3.put("relation", "test");
        map3.put("version", "1.0.0");
        list.add(map3);
        DataMap map2 = new DataMap(modelModel);
        map2.put("module", "yihui2");
        map2.put("relation", "test");
        map2.put("version", "1.0.0");
        list.add(map2);
        DataMap map = new DataMap(modelModel);
        map.put("module", "yihui1");
        map.put("relation", "test");
        map.put("version", "1.0.0");
        list.add(map);
        return list;
    }

    protected final static String databaseName = "test911";
    protected final static String DEFAULT_CHARACTER_SET_NAME = "utf8mb4";
    protected final static String DEFAULT_COLLATION_NAME = "utf8mb4_bin";

    protected TestDatabase one() {
        // 准备数据
        return new TestDatabase()
                .setSchemaName(databaseName)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME)
                ;
    }

    protected List<TestDatabase> batchList() {
        List<TestDatabase> list = new ArrayList<>();
        list.add(new TestDatabase()
                .setSchemaName(databaseName + 1)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new TestDatabase()
                .setSchemaName(databaseName + 2)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new TestDatabase()
                .setSchemaName(databaseName + 3)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        return list;
    }

    protected List<TestDatabase> randomOrderBatchList() {
        List<TestDatabase> list = new ArrayList<>();
        list.add(new TestDatabase()
                .setSchemaName(databaseName + 3)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new TestDatabase()
                .setSchemaName(databaseName + 2)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new TestDatabase()
                .setSchemaName(databaseName + 1)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        return list;
    }

}
