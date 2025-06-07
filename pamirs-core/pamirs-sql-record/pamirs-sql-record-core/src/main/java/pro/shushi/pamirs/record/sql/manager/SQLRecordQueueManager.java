package pro.shushi.pamirs.record.sql.manager;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * SQLRecordQueueManager
 *
 * @author yakir on 2023/08/04 15:40.
 */
public class SQLRecordQueueManager {

    private SQLRecordQueueManager() {}

    public static SQLRecordQueueManager get() {
        return SQLRecordQueueInner.SQL_RECORD_QUEUE_MANAGER;
    }

    private final LinkedBlockingQueue<SQLRecord> changeDataQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<SQLRecord> binlogEventQueue = new LinkedBlockingQueue<>();

    // change data ---
    public boolean changeDataPut(SQLRecord data) {
        try {
            String old = data.getOld();
            String now = data.getNow();
            if (StringUtils.isNotBlank(old)) {
                Map<String, Object> map = JsonUtils.parseMap(old);
                Object obj = BeanDefinitionUtils.findFirst(DataConverter.class).out(data.getModel(), map);
                if (obj instanceof D) {
                    old = JsonUtils.toJSONString(((D) obj).get_d());
                } else {
                    old = JsonUtils.toJSONString(obj);
                }
                data.setOld(old);
            }
            if (StringUtils.isNotBlank(now)) {
                Map<String, Object> map = JsonUtils.parseMap(now);
                Object obj = BeanDefinitionUtils.findFirst(DataConverter.class).out(data.getModel(), map);
                if (obj instanceof D) {
                    now = JsonUtils.toJSONString(((D) obj).get_d());
                } else {
                    now = JsonUtils.toJSONString(obj);
                }
                data.setNow(now);
            }
            changeDataQueue.put(data);
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    public SQLRecord changeDataPoll() {
        return changeDataQueue.poll();
    }

    public boolean changeDataCompleted() {
        return changeDataQueue.isEmpty();
    }


    // binlog event ---
    public boolean binlogEventPut(SQLRecord data) {
        try {
            binlogEventQueue.put(data);
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    public SQLRecord binlogEventPoll() {
        return binlogEventQueue.poll();
    }

    public boolean binlogEventCompleted() {
        return binlogEventQueue.isEmpty();
    }


    private static class SQLRecordQueueInner {
        static final SQLRecordQueueManager SQL_RECORD_QUEUE_MANAGER = new SQLRecordQueueManager();
    }

}
