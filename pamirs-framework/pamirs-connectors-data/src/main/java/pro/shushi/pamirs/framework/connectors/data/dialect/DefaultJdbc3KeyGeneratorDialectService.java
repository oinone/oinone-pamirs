package pro.shushi.pamirs.framework.connectors.data.dialect;

import org.apache.ibatis.session.Configuration;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Jdbc3KeyGeneratorDialectService;

import java.sql.ResultSetMetaData;

/**
 * 默认Jdbc3KeyGenerator方言服务
 *
 * @author Adamancy Zhang at 13:52 on 2023-06-29
 */
public class DefaultJdbc3KeyGeneratorDialectService implements Jdbc3KeyGeneratorDialectService {

    public static final Jdbc3KeyGeneratorDialectService INSTANCE = new DefaultJdbc3KeyGeneratorDialectService();

    @Override
    public KeyAssigner generatorKeyAssigner(Configuration configuration, ResultSetMetaData rsmd, int columnPosition, String paramName, String propertyName, String columnName) {
        return new DefaultKeyAssigner(configuration, rsmd, columnPosition, paramName, propertyName, columnName);
    }
}
