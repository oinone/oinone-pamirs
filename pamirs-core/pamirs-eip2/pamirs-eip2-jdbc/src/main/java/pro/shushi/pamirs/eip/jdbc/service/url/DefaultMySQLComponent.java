package pro.shushi.pamirs.eip.jdbc.service.url;

import com.alibaba.druid.util.JdbcUtils;

/**
 * DefaultMySQLComponent
 *
 * @author yakir on 2025/06/17 10:53.
 */
public class DefaultMySQLComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return JdbcUtils.MYSQL;
    }

    @Override
    public String paramSeparator() {
        return "?";
    }

    @Override
    public String urlTemplate() {
        return "jdbc:mysql://%s:%s/%s";
    }
}
