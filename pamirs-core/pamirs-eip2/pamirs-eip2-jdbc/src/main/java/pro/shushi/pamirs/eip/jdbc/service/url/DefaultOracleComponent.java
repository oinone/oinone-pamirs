package pro.shushi.pamirs.eip.jdbc.service.url;

import com.alibaba.druid.DbType;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;

/**
 * DefaultOracleComponent
 *
 * @author yakir on 2025/06/17 14:02.
 */
public class DefaultOracleComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return DbType.oracle.name();
    }

    @Override
    public String paramSeparator() {
        return ";";
    }

    @Override
    public String urlTemplate() {
        return "jdbc:oracle:thin:@%s:%s/%s";
    }

    @Override
    public String jdbcUrl(EipConnector connector) {
        String url = String.format(urlTemplate(), connector.getHost(), connector.getPort(), connector.getSid());
        // 附加参数
        if (StringUtils.isNotBlank(connector.getExtParam())) {
            url = url + paramSeparator() + connector.getExtParam();
        }
        return url;
    }
}
