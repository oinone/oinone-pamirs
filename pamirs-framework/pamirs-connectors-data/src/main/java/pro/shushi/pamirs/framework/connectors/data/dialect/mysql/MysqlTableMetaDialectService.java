package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.AbstractTableMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * MYSQL脚本执行方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect.component
@SPI.Service
@Component
public class MysqlTableMetaDialectService extends AbstractTableMetaDialectService implements TableMetaDialectService {
}
