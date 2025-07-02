package pro.shushi.pamirs.eip.jdbc.service.url;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.jdbc.service.EipJdbcComponent;
import pro.shushi.pamirs.eip.jdbc.spring.EipJdbcComponentManager;

/**
 * AbstractJdbcComponent
 *
 * @author yakir on 2025/06/17 14:07.
 */
abstract
public class AbstractJdbcComponent implements EipJdbcComponent {

    public abstract String paramSeparator();

    public abstract String urlTemplate();

    @Override
    public String jdbcUrl(EipConnector connector) {
        String url = String.format(urlTemplate(), connector.getHost(), connector.getPort(), connector.getDatabase());
        // 附加参数
        if (StringUtils.isNotBlank(connector.getExtParam())) {
            url = url + paramSeparator() + connector.getExtParam();
        }
        return url;
    }

    public void init() {
        EipJdbcComponentManager.register(this);
    }
}
