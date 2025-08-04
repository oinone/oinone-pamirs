package pro.shushi.pamirs.eip.jdbc.service.url;

import com.alibaba.druid.DbType;

/**
 * DefaultDMComponent
 *
 * @author yakir on 2025/06/17 10:53.
 */
public class DefaultDMComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return DbType.dm.name();
    }

    @Override
    public String paramSeparator() {
        return "?";
    }

    @Override
    public String urlTemplate() {
        return "jdbc:dm://%s:%s/%s";
    }
}
