package pro.shushi.pamirs.record.sql.processor;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.record.sql.manager.SQLRecordManager;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * SQLRecordStoreProcessor
 *
 * @author yakir on 2023/08/04 17:09.
 */
@Slf4j
public class SQLRecordStoreProcessor<SRM extends SQLRecordManager> implements Runnable {

    private final SRM sqlRecordManager;
    private final Supplier<SQLRecord> getter;

    public SQLRecordStoreProcessor(SRM sqlRecordManager, Supplier<SQLRecord> getter) {
        this.sqlRecordManager = sqlRecordManager;
        this.getter = getter;
    }

    @Override
    public void run() {

        while (true) {
            try {
                SQLRecord sqlRecord = getter.get();
                if (null != sqlRecord) {
                    sqlRecordManager.appendBytes(sqlRecord);
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException ignored) {
                    }
                }
            } catch (Throwable e) {
                log.error("store error ", e);
            }
        }
    }
}
