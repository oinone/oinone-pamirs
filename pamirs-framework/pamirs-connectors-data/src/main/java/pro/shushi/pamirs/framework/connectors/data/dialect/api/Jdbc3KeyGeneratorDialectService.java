package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.session.Configuration;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Jdbc3KeyGenerator方言服务
 *
 * @author Adamancy Zhang at 10:48 on 2023-06-29
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface Jdbc3KeyGeneratorDialectService {

    default KeyAssigner generatorKeyAssigner(Executor executor, Configuration configuration, ResultSetMetaData rsmd, int columnPosition, String paramName,
                                             String propertyName, String columnName) {
        return generatorKeyAssigner(configuration, rsmd, columnPosition, paramName, propertyName, columnName);
    }

    KeyAssigner generatorKeyAssigner(Configuration configuration, ResultSetMetaData rsmd, int columnPosition, String paramName,
                                     String propertyName, String columnName);

    interface KeyAssigner {
        void assign(ResultSet rs, Object param);
    }
}
