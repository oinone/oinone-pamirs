package pro.shushi.pamirs.eip.jdbc.service.url;

import com.alibaba.druid.DbType;

/**
 * DefaultSqlServerComponent
 *
 * @author yakir on 2025/06/17 14:04.
 */
public class DefaultSqlServerComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return DbType.sqlserver.name();
    }

    @Override
    public String urlTemplate() {
        return "jdbc:sqlserver://%s:%s;databaseName=%s";
    }

    @Override
    public String paramSeparator() {
        return ";";
    }
}
