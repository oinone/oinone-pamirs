package pro.shushi.pamirs.record.sql.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.record.sql.manager.FilterWatcherManagerFactory;
import pro.shushi.pamirs.record.sql.manager.RecordFilterManager;

/**
 * SQLRecordSysBootAfterInit
 *
 * @author yakir on 2023/07/03 21:30.
 */
@Slf4j
@Component
public class SQLRecordSysBootAfterInit implements SystemBootAfterInit {

    @Autowired
    private RecordFilterManager recordFilterManager;

    @Override
    public boolean init(AppLifecycleCommand command) {
        recordFilterManager.initFilterCache();
        FilterWatcherManagerFactory.getInstance().init();
        return false;
    }

    @Override
    public int priority() {
        return 0;
    }
}
