package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.mysql;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.ScriptDialectService;

import jakarta.validation.constraints.NotBlank;

/**
 * NDS脚本执行方言服务（MYSQL）
 *
 * @author Adamancy Zhang at 21:03 on 2024-10-11
 */
@Order(88)
@Dialect.component(version = DataProductVersion.DEFAULT_MYSQL_NDS_VERSION)
@Component
public class MysqlNdsScriptDialectService extends MysqlScriptDialectService implements ScriptDialectService {

    @Override
    protected void tryUnlock(String dsKey) {
    }

    @Override
    public void unlockTables(@NotBlank String dsKey) {
    }
}
