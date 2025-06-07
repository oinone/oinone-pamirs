package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLExecuteDialectService;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * MYSQL脚本执行方言服务
 *
 * @author Adamancy Zhang at 15:31 on 2023-06-26
 */
@Dialect.component
@SPI.Service
@Component
public class MysqlSQLExecuteDialectService implements SQLExecuteDialectService {

    @Override
    public String resolve(String sql, ModelConfig modelConfig) {
        return sql;
    }
}
