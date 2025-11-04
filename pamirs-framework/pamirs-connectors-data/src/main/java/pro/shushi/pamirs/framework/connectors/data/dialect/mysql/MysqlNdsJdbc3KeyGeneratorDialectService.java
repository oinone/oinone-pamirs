package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.DefaultJdbc3KeyGeneratorDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.DefaultKeyAssigner;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Nds Jdbc3KeyGenerator方言服务
 *
 * @author Adamancy Zhang at 17:10 on 2025-10-31
 */
@Order(88)
@Dialect.component(version = DataProductVersion.DEFAULT_MYSQL_NDS_VERSION)
@SPI.Service(DataProductVersion.DEFAULT_MYSQL_NDS_VERSION)
@Component
public class MysqlNdsJdbc3KeyGeneratorDialectService extends DefaultJdbc3KeyGeneratorDialectService {

    @Override
    public KeyAssigner generatorKeyAssigner(Configuration configuration, ResultSetMetaData rsmd, int columnPosition, String paramName, String propertyName, String columnName) {
        return new MysqlNdsKeyAssigner(configuration, rsmd, columnPosition, paramName, propertyName, columnName);
    }

    public static class MysqlNdsKeyAssigner extends DefaultKeyAssigner {

        protected MysqlNdsKeyAssigner(Configuration configuration, ResultSetMetaData rsmd, int columnPosition, String paramName, String propertyName, String columnName) {
            super(configuration, rsmd, columnPosition, paramName, propertyName, columnName);
        }

        @Override
        public void assign(ResultSet rs, Object param) {
            MetaObject metaParam = configuration.newMetaObject(param);
            if (metaParam.getValue(propertyName) == null) {
                super.assign(rs, param);
            }
        }
    }
}
