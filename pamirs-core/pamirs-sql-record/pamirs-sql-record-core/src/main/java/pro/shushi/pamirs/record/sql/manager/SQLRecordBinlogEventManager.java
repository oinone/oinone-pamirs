package pro.shushi.pamirs.record.sql.manager;

import pro.shushi.pamirs.record.sql.config.SQLRecordConfig;

import static pro.shushi.pamirs.record.sql.common.Constants.BINLOG_EVENT_TOPIC;

/**
 * SQLRecordBinlogEventManager
 *
 * @author yakir on 2023/08/04 14:51.
 */
public class SQLRecordBinlogEventManager extends SQLRecordManager {

    public static final String BINLOG_EVENT = "binlog_event.bin";
    public static final String BINLOG_EVENT_CURRENT = "binlog_event.current";


    private static volatile SQLRecordBinlogEventManager instance;

    private SQLRecordBinlogEventManager(SQLRecordConfig sqlRecordConfig) {
        super(sqlRecordConfig);
        this.metaFile();
    }

    public static void init(SQLRecordConfig sqlRecordConfig) {
        if (null == instance) {
            synchronized (SQLRecordChangeDataManager.class) {
                if (null == instance) {
                    instance = new SQLRecordBinlogEventManager(sqlRecordConfig);
                }
            }
        }
    }

    public static SQLRecordBinlogEventManager init() {
        return instance;
    }

    @Override
    public String techName() {
        return "Binlog";
    }

    @Override
    public String getBinName() {
        return BINLOG_EVENT;
    }

    @Override
    public String getCurrName() {
        return BINLOG_EVENT_CURRENT;
    }

    @Override
    public String topic() {
        return BINLOG_EVENT_TOPIC;
    }
}
