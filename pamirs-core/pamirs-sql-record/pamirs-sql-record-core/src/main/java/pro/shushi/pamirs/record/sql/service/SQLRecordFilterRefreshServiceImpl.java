package pro.shushi.pamirs.record.sql.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.record.sql.api.SQLRecordFilterRefreshService;
import pro.shushi.pamirs.record.sql.manager.FilterWatcherManagerFactory;
import pro.shushi.pamirs.record.sql.model.RecordFilter;

/**
 * SQLRecordFilterRefreshServiceImpl
 *
 * @author yakir on 2024/07/01 10:27.
 */
@Slf4j
@Component
@Fun(SQLRecordFilterRefreshService.FUN_NAMESPACE)
public class SQLRecordFilterRefreshServiceImpl implements SQLRecordFilterRefreshService {

    @Override
    @Function
    public RecordFilter refresh(RecordFilter data) {
        FilterWatcherManagerFactory.getInstance()
                .allRefresh();
        return data;
    }
}
