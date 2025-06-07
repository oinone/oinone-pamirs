package pro.shushi.pamirs.record.sql.manager;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SQLRecordSessionManager
 *
 * @author yakir on 2023/06/29 10:05.
 */
@Component
public class SQLRecordSessionManager {

    private final ThreadLocal<CopyOnWriteArrayList<SQLRecord>> threadLocal = ThreadLocal.withInitial(CopyOnWriteArrayList::new);

    public List<SQLRecord> set(List<SQLRecord> record) {
        threadLocal.get().addAll(record);
        return record;
    }

    public List<SQLRecord> get() {
        try {
            return threadLocal.get();
        } finally {
            threadLocal.remove();
        }
    }
}
