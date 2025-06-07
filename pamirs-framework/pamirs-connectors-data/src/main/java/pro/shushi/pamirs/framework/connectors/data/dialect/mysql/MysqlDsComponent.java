package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.AbstractDsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_CREATE_CONN_BY_DS_KEY_RETURN_NULL_ERROR;
import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_EXIST_TABLE_ERROR;

/**
 * ds操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Order(0)
@Dialect.component
@Component
public class MysqlDsComponent extends AbstractDsDialectComponent implements DsDialectComponent {

    @Override
    public DbType getDbType(String dsKey, Connection connection) throws SQLException {
        return DbType.MYSQL;
    }

    @Override
    public void createDatabase(String dsKey) {
        initDatabase(dsKey);
    }

    @Override
    protected Conn getInitDatabaseConnection(String dsKey) throws SQLException {
        return getConnection(dsKey);
    }

    @Override
    protected String initDatabaseSQL(Conn c) {
        return "create schema if not exists `" + Objects.requireNonNull(c).getDatabase() + "` default character set utf8mb4 COLLATE utf8mb4_bin;";
    }

    @Override
    protected boolean checkDatabaseIsExist(Conn c, Statement stmt) throws SQLException {
        return false;
    }

    @Override
    protected String checkDatabaseIsExistSQL(Conn c) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Conn getInitSchemaConnection(String dsKey) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String initSchemaSQL(Conn c) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String checkSchemaIsExistSQL(Conn c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existTable(String dsKey, String tableName) {
        PamirsDataConfiguration pamirsDataConfiguration = pamirsMapperConfiguration.fetchPamirsDataConfiguration(dsKey);
        boolean tableNameCaseInsensitive = null == pamirsDataConfiguration || !pamirsDataConfiguration.isTableNameCaseSensitive();
        try (Conn c = getConnection(dsKey)) {
            if (c != null) {
                try (Connection conn = c.getConnection();
                     ResultSet rs = conn.getMetaData().getTables(c.getDatabase(), null, tableName, new String[]{"TABLE"})
                ) {
                    if (rs.next()) {
                        if (tableNameCaseInsensitive) {
                            if (tableName.equalsIgnoreCase(rs.getString(3))) {
                                return Boolean.TRUE;
                            }
                        } else {
                            if (tableName.equals(rs.getString(3))) {
                                return Boolean.TRUE;
                            }
                        }
                    }
                }
            } else {
                String errorMsg = String.format("DsDialectComponent existTable method error, conn is null，plz check dsKey：[%s] is exist in application-*.yml", dsKey);
                throw PamirsException.construct(BASE_CREATE_CONN_BY_DS_KEY_RETURN_NULL_ERROR).appendMsg(errorMsg).errThrow();
            }
        } catch (PamirsException e) {
            throw e;
        } catch (Exception e) {
            throw PamirsException.construct(BASE_EXIST_TABLE_ERROR, e).errThrow();
        }
        return Boolean.FALSE;
    }
}
