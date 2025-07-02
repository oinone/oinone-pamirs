package pro.shushi.pamirs.eip.jdbc.service.url;

import com.alibaba.druid.util.JdbcUtils;

/**
 * DefaultOracleComponent
 *
 * @author yakir on 2025/06/17 14:02.
 */
public class DefaultOracleComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return JdbcUtils.ORACLE;
    }

    @Override
    public String paramSeparator() {
        return ";";
    }

    @Override
    public String urlTemplate() {
        return "jdbc:oracle:thin:@%s:%s/%s";
    }
}
