package pro.shushi.pamirs.record.sql.api;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.record.sql.model.RecordFilter;

/**
 * SQLRecordFilterRefreshService
 *
 * @author yakir on 2023/06/30 14:08.
 */
@Fun(SQLRecordFilterRefreshService.FUN_NAMESPACE)
public interface SQLRecordFilterRefreshService {

    String FUN_NAMESPACE = "sqlRecord.SQLRecordFilterRefreshService";

    @Function
    RecordFilter refresh(RecordFilter data);

}
