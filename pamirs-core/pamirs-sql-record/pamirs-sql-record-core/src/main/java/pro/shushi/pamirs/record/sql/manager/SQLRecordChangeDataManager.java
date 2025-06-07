package pro.shushi.pamirs.record.sql.manager;

import pro.shushi.pamirs.record.sql.config.SQLRecordConfig;

import static pro.shushi.pamirs.record.sql.common.Constants.CHANGE_DATA_EVENT_TOPIC;

/**
 * SQLRecordChangeDataManager
 *
 * @author yakir on 2023/08/04 14:51.
 */
public class SQLRecordChangeDataManager extends SQLRecordManager {

    public static final String CHANGE_DATA = "change_data.bin";
    public static final String CHANGE_DATA_CURRENT = "change_data.current";

    private static volatile SQLRecordChangeDataManager instance;

    private SQLRecordChangeDataManager(SQLRecordConfig sqlRecordConfig) {
        super(sqlRecordConfig);
        this.metaFile();
    }

    public static void init(SQLRecordConfig sqlRecordConfig) {
        if (null == instance) {
            synchronized (SQLRecordChangeDataManager.class) {
                if (null == instance) {
                    instance = new SQLRecordChangeDataManager(sqlRecordConfig);
                }
            }
        }
    }

    public static SQLRecordChangeDataManager init() {
        return instance;
    }

    @Override
    public String techName() {
        return "ChangeData";
    }

    @Override
    public String getBinName() {
        return CHANGE_DATA;
    }

    @Override
    public String getCurrName() {
        return CHANGE_DATA_CURRENT;
    }

    @Override
    public String topic() {
        return CHANGE_DATA_EVENT_TOPIC;
    }

}
