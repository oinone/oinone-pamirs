package pro.shushi.pamirs.eip.jdbc.service.url;

import com.alibaba.druid.util.JdbcUtils;

/**
 * DefaultPostgreSQLComponent
 *
 * @author yakir on 2025/06/17 14:05.
 */
public class DefaultPostgreSQLComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return JdbcUtils.POSTGRESQL;
    }

    @Override
    public String urlTemplate() {
        return "jdbc:postgresql://%s:%s/%s";
    }

    @Override
    public String paramSeparator() {
        return "?";
    }
}
