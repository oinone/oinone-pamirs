package pro.shushi.pamirs.record.sql.manager;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SQLRecordAsyncManager
 *
 * @author yakir on 2023/07/03 16:31.
 */
@Slf4j
public class SQLRecordAsyncManager {

    private final static ExecutorService executorService = Executors.newFixedThreadPool(4, r -> {
                Thread t = new Thread(r);
                t.setName("sql.record");
                return t;
            }
    );

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static void shutdown() {
        executorService.shutdown();
    }
}
