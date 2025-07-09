package pro.shushi.pamirs.framework.connectors.data.dialect;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.configure.datasource.DataSourceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.persistence.PamirsPersistenceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.constant.SystemBeanConstants;
import pro.shushi.pamirs.framework.connectors.data.datasource.ddl.DdlManager;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.entity.DataSourceInfo;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.annotation.Resource;
import java.net.URI;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_CREATE_DATABASE_ERROR;
import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_DRIVER_NOT_FOUND_ERROR;

/**
 * 抽象数据源方言服务
 *
 * @author Adamancy Zhang at 17:53 on 2023-06-21
 */
@Slf4j
public abstract class AbstractDsDialectComponent implements DsDialectComponent {

    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private static final String DRIVER_CLASS_NAME = "driverClassName";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String SSLMODE = "useSSL";
    private static final String SSLMODE_DISABLED = "false";
    private static final String ALLOW_PUBLIC_KEY_RETRIEVAL = "allowPublicKeyRetrieval";

    @Resource
    protected DdlManager ddlManager;

    @Resource
    protected DataSourceConfiguration dataSourceConfiguration;

    @Resource
    protected PamirsMapperConfiguration pamirsMapperConfiguration;

    @Override
    public DataSourceInfo getDataSourceInfo(String dsKey) {
        String url = getUrl(dsKey);
        URI uri = getUriFromUrl(url);
        Map<String, List<String>> queryParameters = getQueryParameters(uri.getQuery());
        return new DataSourceInfo()
                .setUrl(url)
                .setDatabase(getDatabase(uri, queryParameters))
                .setProtocol(getProtocolFromUrl(url))
                .setHost(uri.getHost())
                .setPort(uri.getPort())
                .setParameters(queryParameters);
    }

    protected String getUrl(String dsKey) {
        return ddlManager.getUrl(dsKey);
    }

    protected URI getUri(String dsKey) {
        String url = getUrl(dsKey);
        return getUriFromUrl(url);
    }

    protected URI getUriFromUrl(String url) {
        return DdlManager.getUriFromUrl(url);
    }

    protected Map<String, List<String>> getQueryParameters(String query) {
        return DdlManager.getQueryParameters(query);
    }

    @Override
    public String getDatabase(String dsKey) {
        URI uri = getUri(dsKey);
        return getDatabase(uri, getQueryParameters(uri.getQuery()));
    }

    protected String getDatabase(URI uri, Map<String, List<String>> queryParameters) {
        return DdlManager.getDatabase(uri);
    }

    @Override
    public String getProtocol(String dsKey) {
        return ddlManager.getProtocol(dsKey);
    }

    protected String getProtocolFromUrl(String url) {
        return DdlManager.getProtocolFromUrl(url);
    }

    @Override
    public void createDatabase(String dsKey) {
        initDatabase(dsKey);
        initSchema(dsKey);
    }

    protected final void initDatabase(String dsKey) {
        try (Conn conn = getInitDatabaseConnection(dsKey)) {
            if (conn == null || StringUtils.isBlank(conn.getDatabase())) {
                log.info("create database ignored.");
                return;
            }
            final String SQL = initDatabaseSQL(conn);
            try (Connection connection = conn.getConnection()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.setQueryTimeout(getPamirsPersistenceConfiguration().fetchPamirsPersistenceConfiguration(dsKey).getCreateDatabaseTimeout());
                    if (checkDatabaseIsExist(conn, stmt)) {
                        log.info("database is exist. rt: [0]");
                    } else {
                        log.info("create database sql: {}", SQL);
                        int result = stmt.executeUpdate(SQL);
                        log.info("create database rt: [{}]", result);
                    }
                }
            }
        } catch (Exception e) {
            log.error("init database error. dsKey: {}", dsKey);
            throw PamirsException.construct(BASE_CREATE_DATABASE_ERROR, e).errThrow();
        }
    }

    protected abstract Conn getInitDatabaseConnection(String dsKey) throws SQLException;

    protected abstract String initDatabaseSQL(Conn c);

    protected boolean checkDatabaseIsExist(Conn c, Statement stmt) throws SQLException {
        String SQL = checkDatabaseIsExistSQL(c);
        log.info("check database is exist sql: {}", SQL);
        ResultSet resultSet = stmt.executeQuery(SQL);
        if (resultSet.next()) {
            return resultSet.getBoolean(1);
        }
        return false;
    }

    protected abstract String checkDatabaseIsExistSQL(Conn c);

    protected final void initSchema(String dsKey) {
        try (Conn conn = getInitSchemaConnection(dsKey)) {
            if (conn == null || StringUtils.isBlank(conn.getSchema())) {
                log.info("create schema ignored.");
                return;
            }
            final String SQL = initSchemaSQL(conn);
            try (Connection connection = conn.getConnection()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.setQueryTimeout(getPamirsPersistenceConfiguration().fetchPamirsPersistenceConfiguration(dsKey).getCreateDatabaseTimeout());
                    if (checkSchemaIsExist(conn, stmt)) {
                        log.info("schema is exist. rt: [0]");
                    } else {
                        log.info("create schema sql: {}", SQL);
                        int result = stmt.executeUpdate(SQL);
                        log.info("create schema rt: [{}]", result);
                    }
                }
            }
        } catch (Exception e) {
            log.error("init schema error. dsKey: {}", dsKey);
            throw PamirsException.construct(DataExpEnumerate.BASE_CREATE_SCHEMA_ERROR, e).errThrow();
        }
    }

    protected abstract Conn getInitSchemaConnection(String dsKey) throws SQLException;

    protected abstract String initSchemaSQL(Conn c);

    protected boolean checkSchemaIsExist(Conn c, Statement stmt) throws SQLException {
        String SQL = checkSchemaIsExistSQL(c);
        log.info("check schema is exist sql: {}", SQL);
        ResultSet resultSet = stmt.executeQuery(SQL);
        if (resultSet.next()) {
            return resultSet.getBoolean(1);
        }
        return false;
    }

    protected abstract String checkSchemaIsExistSQL(Conn c);

    protected String getDriverClassName(String dsKey) {
        return Optional.ofNullable(ddlManager.getDsConfig(dsKey)).map(v -> v.get(DRIVER_CLASS_NAME)).orElse(DRIVER);
    }

    protected Conn getConnection(String dsKey) throws SQLException {
        Map<String, String> dataSource = ddlManager.getDsConfig(dsKey);
        if (MapUtils.isEmpty(dataSource)) {
            return null;
        }
        URI uri = getUri(dsKey);
        if (null == uri) {
            return null;
        }
        String database = DdlManager.getDatabase(uri);
        String user = dataSource.get("username");
        if (StringUtils.isBlank(user)) {
            user = dataSource.get("xa-properties.user");
        }
        String password = dataSource.get("password");
        if (StringUtils.isBlank(password)) {
            password = dataSource.get("xa-properties.password");
        }
        final String DB_URL = getConnectionUrl(dsKey);
        try {
            Class.forName(getDriverClassName(dsKey));
        } catch (ClassNotFoundException e) {
            throw PamirsException.construct(BASE_DRIVER_NOT_FOUND_ERROR, e).errThrow();
        }
        Properties info = new Properties();
        info.put(USER, user);
        info.put(PASSWORD, password);
        info.put(ALLOW_PUBLIC_KEY_RETRIEVAL, "true");
        info.put(SSLMODE, SSLMODE_DISABLED);
        Conn conn = new Conn();
        conn.setConnection(DriverManager.getConnection(DB_URL, info));
        conn.setDatabase(database);
        return conn;
    }

    protected String getConnectionUrl(String dsKey) {
        URI uri = getUri(dsKey);
        String host = uri.getHost();
        int port = uri.getPort();
        return "jdbc:".concat(uri.getScheme()).concat("://").concat(host).concat(":").concat(String.valueOf(port)).concat("/");
    }

    protected PamirsPersistenceConfiguration getPamirsPersistenceConfiguration() {
        return BeanDefinitionUtils.getBean(SystemBeanConstants.PAMIRS_PERSISTENCE_CONFIGURATION, PamirsPersistenceConfiguration.class);
    }

    public static class Conn implements AutoCloseable {

        private Connection connection;

        private String database;

        private String schema;

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        @Override
        public void close() throws Exception {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
