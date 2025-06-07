package pro.shushi.pamirs;

import com.alibaba.fastjson.TypeReference;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModuleIndex;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.LogicSchemaService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model.DatabaseSchema;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model.SchemaPlayBack;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.util.FileUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
//        String ddl = FileUtils.read("classpath:sql/drop.sql");
//        ddl(ddl);
    }

    @SuppressWarnings("unused")
    @PostConstruct
    void init() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

        clear();

        // 初始化系统表
        LogicSchemaService.get().initSystemSchema(new String[]{
                ModelTable.MODEL_MODEL,
                FieldColumn.MODEL_MODEL,
                ModuleIndex.MODEL_MODEL,
                SchemaPlayBack.MODEL_MODEL,
        }, true);

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

    protected final static String databaseName = "test911";
    protected final static String DEFAULT_CHARACTER_SET_NAME = "utf8mb4";
    protected final static String DEFAULT_COLLATION_NAME = "utf8mb4_bin";
    protected final static String SCHEMA_PLAY_BACK_DS_KEY = "test911";
    protected final static String SCHEMA_PLAY_BACK_DDL = "utf8mb4";
    protected final static String SCHEMA_PLAY_BACK_ERROR = "utf8mb4_bin";

    protected DatabaseSchema one() {
        // 准备数据
        return new DatabaseSchema()
                .setSchemaName(databaseName)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME)
                ;
    }

    protected SchemaPlayBack onePlayBack() {
        // 准备数据
        return new SchemaPlayBack()
                .setDsKey(SCHEMA_PLAY_BACK_DS_KEY)
                .setDdl(SCHEMA_PLAY_BACK_DDL)
                .setError(SCHEMA_PLAY_BACK_ERROR)
                .setOptVersion(0L)
                ;
    }

    protected List<DatabaseSchema> batchList() {
        List<DatabaseSchema> list = new ArrayList<>();
        list.add(new DatabaseSchema()
                .setSchemaName(databaseName + 1)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new DatabaseSchema()
                .setSchemaName(databaseName + 2)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new DatabaseSchema()
                .setSchemaName(databaseName + 3)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        return list;
    }

    protected List<SchemaPlayBack> playBackBatchList() {
        List<SchemaPlayBack> list = new ArrayList<>();
        list.add(new SchemaPlayBack()
                .setDsKey(SCHEMA_PLAY_BACK_DS_KEY + 1)
                .setDdl(SCHEMA_PLAY_BACK_DDL)
                .setError(SCHEMA_PLAY_BACK_ERROR)
                .setOptVersion(0L)
        );
        list.add(new SchemaPlayBack()
                .setDsKey(SCHEMA_PLAY_BACK_DS_KEY + 2)
                .setDdl(SCHEMA_PLAY_BACK_DDL)
                .setError(SCHEMA_PLAY_BACK_ERROR)
                .setOptVersion(0L)
        );
        list.add(new SchemaPlayBack()
                .setDsKey(SCHEMA_PLAY_BACK_DS_KEY + 3)
                .setDdl(SCHEMA_PLAY_BACK_DDL)
                .setError(SCHEMA_PLAY_BACK_ERROR)
                .setOptVersion(0L)
        );
        return list;
    }

    protected List<DatabaseSchema> randomOrderBatchList() {
        List<DatabaseSchema> list = new ArrayList<>();
        list.add(new DatabaseSchema()
                .setSchemaName(databaseName + 3)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new DatabaseSchema()
                .setSchemaName(databaseName + 2)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        list.add(new DatabaseSchema()
                .setSchemaName(databaseName + 1)
                .setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)
                .setDefaultCollationName(DEFAULT_COLLATION_NAME));
        return list;
    }

    protected List<SchemaPlayBack> playBackRandomOrderBatchList() {
        List<SchemaPlayBack> list = new ArrayList<>();
        list.add(new SchemaPlayBack()
                .setDsKey(SCHEMA_PLAY_BACK_DS_KEY + 3)
                .setDdl(SCHEMA_PLAY_BACK_DDL)
                .setError(SCHEMA_PLAY_BACK_ERROR));
        list.add(new SchemaPlayBack()
                .setDsKey(SCHEMA_PLAY_BACK_DS_KEY + 2)
                .setDdl(SCHEMA_PLAY_BACK_DDL)
                .setError(SCHEMA_PLAY_BACK_ERROR));
        list.add(new SchemaPlayBack()
                .setDsKey(SCHEMA_PLAY_BACK_DS_KEY + 1)
                .setDdl(SCHEMA_PLAY_BACK_DDL)
                .setError(SCHEMA_PLAY_BACK_ERROR));
        return list;
    }

    protected Meta fetchMeta(String module, Map<String, MetaData> dependencyMetaDataMap) {
        Meta meta = new Meta();
        MetaData metaData = fetchMetaData(module);

        meta.setModule(module);
        //noinspection unchecked,rawtypes
        meta.setData(new HashMap() {
            private static final long serialVersionUID = 3306759822757398418L;

            {   //noinspection unchecked
                put(module, metaData);
                //noinspection unchecked
                putAll(dependencyMetaDataMap);
            }
        });
        return meta;
    }

    protected Meta fetchMeta(@SuppressWarnings("SameParameterValue") String module) {
        Meta meta = new Meta();
        MetaData metaData = fetchMetaData(module);

        meta.setModule(module);
        //noinspection unchecked,rawtypes
        meta.setData(new HashMap() {
            private static final long serialVersionUID = -7595381457277605768L;

            {   //noinspection unchecked
                put(module, metaData);
            }
        });
        return meta;
    }

    protected MetaData fetchMetaData(String module) {
        MetaData metaData = new MetaData();

        String pathPrefix = module + "/" + module;

        String path = "Model.json";
        String fullDictPath = "classpath:meta/" + pathPrefix + path;
        String json = FileUtils.read(fullDictPath);
        Map<String, ModelDefinition> modelDefinitionMap
                = JsonUtils.parseObject(json, new TypeReference<Map<String, ModelDefinition>>() {
        }.getType());
        if (null != modelDefinitionMap) modelDefinitionMap.values().forEach(metaData::addData);

        path = "Module.json";
        fullDictPath = "classpath:meta/" + pathPrefix + path;
        json = FileUtils.read(fullDictPath);
        Map<String, ModuleDefinition> moduleDefinitionMap
                = JsonUtils.parseObject(json, new TypeReference<Map<String, ModuleDefinition>>() {
        }.getType());
        if (null != moduleDefinitionMap) moduleDefinitionMap.values().forEach(metaData::addData);

        return metaData;
    }

    protected void testDDLUnit(List<Meta> meta, ChangeMeta changeMeta) {

        // 初始化系统表
        LogicSchemaService.get().initSystemSchema(true);

        // 按模块处理业务表
        LogicSchemaService.get().buildTable(meta);

        // 修改meta
        changeMeta.change();

        // 再次处理业务表
        LogicSchemaService.get().buildTable(meta);

    }

    protected interface ChangeMeta {
        void change();
    }

}
