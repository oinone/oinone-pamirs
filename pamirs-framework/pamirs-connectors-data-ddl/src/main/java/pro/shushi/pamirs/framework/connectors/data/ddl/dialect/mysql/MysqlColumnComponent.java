package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.mysql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.ColumnDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 列操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Dialect.component
@SPI.Service(DataProductVersion.PRODUCT_MYSQL)
@Component
public class MysqlColumnComponent implements ColumnDialectComponent {

}
