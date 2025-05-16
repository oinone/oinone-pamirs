package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;

/**
 * ds操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Order(88)
@Dialect.component(version = DataProductVersion.DEFAULT_MYSQL_NDS_VERSION)
@Component
public class MysqlNdsDsComponent extends MysqlDsComponent implements DsDialectComponent {

    @Override
    protected String getConnectionUrl(String dsKey) {
        return ddlManager.getUrl(dsKey);
    }

}
