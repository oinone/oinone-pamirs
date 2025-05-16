package pro.shushi.pamirs.record.sql.api;

import pro.shushi.pamirs.middleware.canal.entity.RefreshFilterEntity;
import pro.shushi.pamirs.middleware.canal.entity.SimpleResult;

/**
 * SQLRecordFilterService
 *
 * @author yakir on 2023/06/30 14:08.
 */
//@Fun(SQLRecordFilterService.FUN_NAMESPACE)
public interface SQLRecordFilterService {

    String FUN_NAMESPACE = "sqlRecord.SQLRecordFilterService";

    SimpleResult<RefreshFilterEntity> appendFilter(RefreshFilterEntity data);

    SimpleResult<RefreshFilterEntity> removeFilter(RefreshFilterEntity data);

}
