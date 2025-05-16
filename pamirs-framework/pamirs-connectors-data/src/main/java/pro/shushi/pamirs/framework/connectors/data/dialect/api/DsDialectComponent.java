package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import pro.shushi.pamirs.framework.connectors.data.entity.DataSourceInfo;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DsDialectComponent {

    default DbType getDbType(String dsKey, Connection connection) throws SQLException {
        return JdbcUtils.getDbType(connection.getMetaData().getURL());
    }

    DataSourceInfo getDataSourceInfo(String dsKey);

    String getDatabase(String dsKey);

    String getProtocol(String dsKey);

    void createDatabase(String dsKey);

    boolean existTable(String dsKey, String tableName);
}
