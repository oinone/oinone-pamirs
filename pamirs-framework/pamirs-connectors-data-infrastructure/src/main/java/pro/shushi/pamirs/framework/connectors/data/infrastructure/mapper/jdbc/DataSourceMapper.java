package pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.jdbc;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DataSourceApi;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Database;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.DatabaseInstance;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.annotation.Resource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.framework.connectors.data.infrastructure.enmu.InfExpEnumerate.BASE_FETCH_DATABASE_INSTANCE_INFO_ERROR;
import static pro.shushi.pamirs.framework.connectors.data.infrastructure.enmu.InfExpEnumerate.BASE_GET_DATABASES_ERROR;

/**
 * 物理基础设施DAO实现
 * <p>
 * 2020/7/31 4:00 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class DataSourceMapper implements IDataSourceMapper {

    @Resource
    private DataSourceApi dataSourceApi;

    @Override
    public DatabaseInstance fetchDatabaseInstanceInfo(String dsKey) {
        DatabaseInstance databaseInstance = new DatabaseInstance();
        try {
            DatabaseMetaData metaData = getDatabaseMetaData(dsKey);
            return convertDatabaseInstance(databaseInstance, metaData);
        } catch (SQLException e) {
            throw PamirsException.construct(BASE_FETCH_DATABASE_INSTANCE_INFO_ERROR, e).errThrow();
        }
    }

    @Override
    public Database fetchDatabaseInfo(String dsKey) {
        List<Database> databaseList = getDatabases(dsKey);
        if (CollectionUtils.isEmpty(databaseList)) {
            return null;
        }
        return databaseList.get(0);
    }

    private DatabaseMetaData getDatabaseMetaData(String dsKey) throws SQLException {
        return dataSourceApi.get(dsKey).getConnection().getMetaData();
    }

    private List<Database> getDatabases(String dsKey) {
        List<Database> databaseList = new ArrayList<>();
        Database database;
        try {
            DatabaseMetaData metaData = getDatabaseMetaData(dsKey);
            ResultSet rs = metaData.getCatalogs();
            while (rs.next()) {
                database = new Database();
                String schemaName = rs.getString("TABLE_CAT");
                database.setSchemaName(schemaName);
                databaseList.add(database);
            }
            return databaseList;
        } catch (SQLException e) {
            throw PamirsException.construct(BASE_GET_DATABASES_ERROR, e).errThrow();
        }
    }

    private DatabaseInstance convertDatabaseInstance(DatabaseInstance databaseInstance, DatabaseMetaData metaData) throws SQLException {
        databaseInstance.setProductName(metaData.getDatabaseProductName());
        databaseInstance.setProductVersion(metaData.getDatabaseProductVersion());
        databaseInstance.setUserName(metaData.getUserName());
        databaseInstance.setUrl(metaData.getURL());
        databaseInstance.setDriverName(metaData.getDriverName());
        databaseInstance.setDriverVersion(metaData.getDriverVersion());
        databaseInstance.setDriverMajorVersion(metaData.getDriverMajorVersion());
        databaseInstance.setDriverMinorVersion(metaData.getDriverMinorVersion());
        databaseInstance.setJdbcMajorVersion(metaData.getJDBCMajorVersion());
        databaseInstance.setJdbcMinorVersion(metaData.getJDBCMinorVersion());
        databaseInstance.setReadOnly(metaData.isReadOnly());
        databaseInstance.setSupportsTransactions(metaData.supportsTransactions());
        return databaseInstance;
    }

}
