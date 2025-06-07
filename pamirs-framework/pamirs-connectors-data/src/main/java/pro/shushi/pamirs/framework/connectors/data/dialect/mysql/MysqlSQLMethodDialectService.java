package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.AbstractSQLMethodDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLMethodDialectService;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 脚本执行方言服务
 * <p>
 * 2023/06/25
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Dialect.component
@SPI.Service
@Component
public class MysqlSQLMethodDialectService extends AbstractSQLMethodDialectService implements SQLMethodDialectService {
}
