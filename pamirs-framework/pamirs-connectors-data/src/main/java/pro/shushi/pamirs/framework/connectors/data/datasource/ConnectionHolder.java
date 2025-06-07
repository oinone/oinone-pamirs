package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.driver.jdbc.core.connection.ShardingSphereConnection;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DataSourceApi;
import pro.shushi.pamirs.framework.connectors.data.configure.persistence.PamirsPersistenceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.util.DataConfigurationHelper;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.*;

/**
 * 连接持有者
 * <p>
 * 2020/7/6 11:26 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ConnectionHolder implements Connection {

    private final Map<Object, ConnectionWrapper> connectionMap = new ConcurrentHashMap<>();

    private final Map<Savepoint, Map<Connection, Savepoint>> connectionSavepointMap = new ConcurrentHashMap<>();

    private boolean autoCommit;

    private final PamirsPersistenceConfiguration pamirsPersistenceConfiguration;

    private final PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    public ConnectionHolder(PamirsPersistenceConfiguration pamirsPersistenceConfiguration,
                            PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration) {
        super();
        this.pamirsFrameworkDataConfiguration = pamirsFrameworkDataConfiguration;
        this.pamirsPersistenceConfiguration = pamirsPersistenceConfiguration;
        autoCommit = true;
    }

    public ConnectionHolder(String username,
                            String password,
                            PamirsPersistenceConfiguration pamirsPersistenceConfiguration,
                            PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration) {
        super();
        this.pamirsFrameworkDataConfiguration = pamirsFrameworkDataConfiguration;
        this.pamirsPersistenceConfiguration = pamirsPersistenceConfiguration;
        ConnectionWrapper connectionWrapper = getCurrentConnectionWrapper().setAuth(true).setPassword(username).setPassword(password);
        Object dsKey = getCurrentDsKey();
        int timeout = pamirsPersistenceConfiguration.fetchPamirsPersistenceConfiguration(String.valueOf(dsKey)).getConnectionValidTimeout();
        connectionWrapper.setTimeout(timeout);
        this.put(dsKey, connectionWrapper);
        autoCommit = true;
    }

    protected void put(Object ds, ConnectionWrapper connection) {
        connectionMap.put(DataSourceHolder.getLookupKeyByDsKey(ds), connection);
        autoCommit = true;
    }

    public ConnectionWrapper get(Object ds) {
        String dsKey;
        String defaultDsKey = pamirsFrameworkDataConfiguration.getDefaultDsKey();
        if (null == ds) {
            dsKey = defaultDsKey;
        } else {
            dsKey = String.valueOf(ds);
        }
        String lookupKey = DataSourceHolder.getLookupKeyByDsKey(dsKey);
        ConnectionWrapper connection = getConnectionMap().get(lookupKey);
        if (null == connection && !StringUtils.isBlank(defaultDsKey)) {
            defaultDsKey = DataSourceHolder.getLookupKeyByDsKey(defaultDsKey);
            return getConnectionMap().get(defaultDsKey);
        }
        return connection;
    }

    protected Map<Object, ConnectionWrapper> getConnectionMap() {
        return this.connectionMap;
    }

    protected Object getCurrentDsKey() {
        return DataConfigurationHelper.getDsKey();
    }

    public ConnectionWrapper getCurrentConnectionWrapper() {
        return getConnectionMap().get(getCurrentDsKey());
    }

    public Connection getCurrentConnection() throws SQLException {
        Object dsKey = getCurrentDsKey();
        ConnectionWrapper connectionWrapper = getCurrentConnectionWrapper();
        if (null == connectionWrapper) {
            connectionWrapper = new ConnectionWrapper();
            getConnectionMap().put(dsKey, connectionWrapper);
        }
        int timeout = pamirsPersistenceConfiguration.fetchPamirsPersistenceConfiguration(String.valueOf(dsKey)).getConnectionValidTimeout();
        connectionWrapper.setTimeout(timeout);
        Connection connection = connectionWrapper.getConnection();
        if (null == connection) {
            DataSource dataSource = CommonApiFactory.getApi(DataSourceApi.class).get(dsKey);
            if (null == dataSource) {
                throw PamirsException.construct(BASE_NO_DATA_SOURCE_ERROR)
                        .appendMsg("dataSource:" + dsKey).errThrow();
            }
            if (connectionWrapper.isAuth()) {
                connection = dataSource.getConnection(connectionWrapper.getUsername(), connectionWrapper.getPassword());
            } else {
                connection = dataSource.getConnection();
            }
        }
        connectionWrapper.setConnection(connection);
        if (!autoCommit) {
            connection.setAutoCommit(false);
        }
        return connection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return getCurrentConnection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getCurrentConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return getCurrentConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return getCurrentConnection().nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                connection.setAutoCommit(autoCommit);
            }
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        boolean autoCommit = true;
        boolean exist = false;
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                autoCommit = autoCommit && connection.getAutoCommit();
                exist = true;
            }
        }
        return exist ? autoCommit : this.autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        boolean isValid = true;
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                if (connection instanceof ShardingSphereConnection) {
                    isValid = true;
                } else {
                    isValid = isValid && connection.isValid(connectionWrapper.getTimeout());
                }
            }
        }
        if (!isValid) {
            rollback();
        }
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                connection.commit();
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                connection.rollback();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                connection.close();
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getCurrentConnection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getCurrentConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                connection.setReadOnly(readOnly);
            }
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return getCurrentConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        getCurrentConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return getCurrentConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                connection.setTransactionIsolation(level);
            }
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return getCurrentConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getCurrentConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        getCurrentConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getCurrentConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getCurrentConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getCurrentConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getCurrentConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getCurrentConnection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        getCurrentConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getCurrentConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        Map<Connection, Savepoint> savepointMap = new HashMap<>();
        Savepoint savepoint = null;
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                savepoint = connection.setSavepoint();
                savepointMap.putIfAbsent(connection, savepoint);
            }
        }
        this.connectionSavepointMap.putIfAbsent(savepoint, savepointMap);
        return savepoint;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        Map<Connection, Savepoint> savepointMap = new HashMap<>();
        Savepoint savepoint = null;
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            if (null != connection) {
                savepoint = connection.setSavepoint(name);
                savepointMap.putIfAbsent(connection, savepoint);
            }
        }
        this.connectionSavepointMap.putIfAbsent(savepoint, savepointMap);
        return savepoint;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        Map<Connection, Savepoint> savepointMap = this.connectionSavepointMap.get(savepoint);
        if (MapUtils.isEmpty(savepointMap)) {
            return;
        }
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            Savepoint savepoint1 = savepointMap.get(connection);
            if (null != connection && null != savepoint1) {
                connection.rollback(savepoint1);
            }
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        Map<Connection, Savepoint> savepointMap = this.connectionSavepointMap.get(savepoint);
        if (MapUtils.isEmpty(savepointMap)) {
            return;
        }
        for (ConnectionWrapper connectionWrapper : getConnectionMap().values()) {
            Connection connection = connectionWrapper.getConnection();
            Savepoint savepoint1 = savepointMap.get(connection);
            if (null != connection) {
                connection.releaseSavepoint(savepoint1);
            }
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getCurrentConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getCurrentConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getCurrentConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getCurrentConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getCurrentConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getCurrentConnection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return getCurrentConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return getCurrentConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return getCurrentConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return getCurrentConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        Connection connection = getCurrentConnection();
        if (connection instanceof ShardingSphereConnection) {
            return true;
        }
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            getCurrentConnection().setClientInfo(name, value);
        } catch (SQLClientInfoException se) {
            throw se;
        } catch (SQLException e) {
            throw PamirsException.construct(BASE_CONNECTION_ERROR, e).errThrow();
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            getCurrentConnection().setClientInfo(properties);
        } catch (SQLClientInfoException se) {
            throw se;
        } catch (SQLException e) {
            throw PamirsException.construct(BASE_CONNECTION2_ERROR, e).errThrow();
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return getCurrentConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return getCurrentConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getCurrentConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getCurrentConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        getCurrentConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return getCurrentConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        getCurrentConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getCurrentConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return getCurrentConnection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getCurrentConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getCurrentConnection().isWrapperFor(iface);
    }

}
