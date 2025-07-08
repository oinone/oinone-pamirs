package pro.shushi.pamirs.eip.jdbc.service.url;

import com.alibaba.druid.util.JdbcUtils;

/**
 * DefaultKingbaseComponent
 *
 * @author yakir on 2025/06/17 10:53.
 */
public class DefaultKingbaseComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return JdbcUtils.KINGBASE;
    }

    @Override
    public String paramSeparator() {
        return "?";
    }

    @Override
    public String urlTemplate() {
        return "jdbc:kingbase8://%s:%s/%s";
    }
}
