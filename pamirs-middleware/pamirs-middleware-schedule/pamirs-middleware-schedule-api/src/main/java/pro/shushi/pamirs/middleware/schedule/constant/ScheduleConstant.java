package pro.shushi.pamirs.middleware.schedule.constant;

public interface ScheduleConstant {

    String PAMIRS_SCHEDULE_CONFIG_PREFIX = "pamirs.event.schedule";

    String PAMIRS_SCHEDULE_DIALECT_CONFIG_PREFIX = "pamirs.event.schedule.dialect";

    String SCHEDULE_SWITCH_CONFIG = PAMIRS_SCHEDULE_CONFIG_PREFIX + ".enabled";

    String SCHEDULE_AUTO_INIT_DATA_CONFIG = PAMIRS_SCHEDULE_CONFIG_PREFIX + ".auto-init";

    String SCHEDULE_AUTO_CREATE_CONFIG_FILE = PAMIRS_SCHEDULE_CONFIG_PREFIX + ".auto-create-config-file";

    String SCHEDULE_OWN_SIGN_CONFIG = PAMIRS_SCHEDULE_CONFIG_PREFIX + ".ownSign";

    String SCHEDULE_DATABASE_TYPE_CONFIG = PAMIRS_SCHEDULE_CONFIG_PREFIX + ".databaseType";

    String DEFAULT_OWN_SIGN = "BASE";

    String BASE_SCHEDULE_TASK_KEY = "baseScheduleTask";

    String BASE_SCHEDULE_NO_TRANSACTION_TASK_KEY = "baseScheduleNoTransactionTask";

    String CYCLE_SCHEDULE_NO_TRANSACTION_TASK_KEY = "cycleScheduleNoTransactionTask";

    String SERIAL_BASE_SCHEDULE_TASK_KEY = "serialBaseScheduleTask";

    String SERIAL_BASE_SCHEDULE_NO_TRANSACTION_TASK_KEY = "serialBaseScheduleNoTransactionTask";

    String SERIAL_BASE_SCHEDULE_TYPES_TASK_KEY = "serialBaseScheduleTypesTask";

    String DELAY_MSG_TRANSFER_SCHEDULE_TASK_KEY = "delayMsgTransferScheduleTask";

    String DELETE_TRANSFER_SCHEDULE_TASK_KEY = "deleteTransferScheduleTask";

    String REMOTE_SCHEDULE_TASK_KEY = "remoteScheduleTask";

    String SERIAL_REMOTE_SCHEDULE_TASK_KEY = "serialRemoteScheduleTask";

    String CUSTOM_SCHEDULE_TASK = "customTask";

    String JSON_CONFIG_FILE_NAME = "schedule.json";

    String JSON_CONFIG_FILE_PATH = "init/" + JSON_CONFIG_FILE_NAME;

    String TASK_NODE_CONFIG_BEAN_NAMES = "beanNames";

    String TASK_NODE_CONFIG_BEAN_CLASS_NAME = "beanClassName";

    String TASK_NODE_CONFIG_TASK_TYPE_KEY = "taskType";

    String TASK_NODE_CONFIG_VALUES_KEY = "values";

    String BASE_TASK_TYPE_KEY = "baseTaskType";

    String BASE_TASK_TYPE_DEAL_BEAN_NAME_KEY = "dealBeanName";

    String STRATEGY_KEY = "strategy";

    String STRATEGY_NAME_KEY = "strategyName";

    String STRATEGY_TASK_NAME_KEY = "taskName";
}
