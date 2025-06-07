package pro.shushi.pamirs.record.sql.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootSystemInitApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.record.sql.config.SQLRecordConfig;
import pro.shushi.pamirs.record.sql.manager.SQLRecordBinlogEventManager;
import pro.shushi.pamirs.record.sql.manager.SQLRecordChangeDataManager;

/**
 * SQLRecordBootSysInit
 *
 * @author yakir on 2024/03/22 11:06.
 */
@Order(33)
@Component
@SPI.Service(value = "sqlRecordBootSysInit")
public class SQLRecordBootSysInit implements BootSystemInitApi {

    @Autowired
    private SQLRecordConfig sqlRecordConfig;

    @Override
    public void init(AppLifecycleCommand command) {
        SQLRecordChangeDataManager.init(sqlRecordConfig);
        SQLRecordBinlogEventManager.init(sqlRecordConfig);
    }
}
