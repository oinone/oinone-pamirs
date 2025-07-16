package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.dialect;

import org.apache.ibatis.type.JdbcType;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * BooleanTypeHandler Dialect
 *
 * @author Adamancy Zhang at 17:56 on 2025-07-16
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BooleanTypeHandlerDialect {

    void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException;

}
