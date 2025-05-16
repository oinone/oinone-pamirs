package pro.shushi.pamirs.record.sql.manager;

import pro.shushi.pamirs.record.sql.lock.SQLRecordLockFactory;

import java.util.concurrent.TimeUnit;

/**
 * SQLRecordShutdownHook
 *
 * @author yakir on 2023/08/04 18:40.
 */
public class SQLRecordShutdownHook extends Thread {

    @Override
    public void run() {
        while (SQLRecordQueueManager.get().changeDataCompleted() != SQLRecordQueueManager.get().binlogEventCompleted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Throwable ignored) {

            }
        }
        SQLRecordAsyncManager.shutdown();
        SQLRecordLockFactory.shutdown();
    }
}
