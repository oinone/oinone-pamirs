package pro.shushi.pamirs.middleware.schedule.eunmeration;

import pro.shushi.pamirs.middleware.schedule.constant.ScheduleConstant;

public enum TaskType {

    REMOTE_SCHEDULE_TASK(ScheduleConstant.REMOTE_SCHEDULE_TASK_KEY, "基础任务执行器（远程调度任务）"),
    BASE_SCHEDULE_TASK(ScheduleConstant.BASE_SCHEDULE_TASK_KEY, "基础任务执行器（事务控制）"),
    BASE_SCHEDULE_NO_TRANSACTION_TASK(ScheduleConstant.BASE_SCHEDULE_NO_TRANSACTION_TASK_KEY, "基础任务执行器（无事务控制）（单个）"),
    CYCLE_SCHEDULE_NO_TRANSACTION_TASK(ScheduleConstant.CYCLE_SCHEDULE_NO_TRANSACTION_TASK_KEY, "基础任务执行器（无事务控制）（循环）"),
    SERIAL_REMOTE_SCHEDULE_TASK(ScheduleConstant.SERIAL_REMOTE_SCHEDULE_TASK_KEY, "根据bizId分片（远程调度任务）"),
    SERIAL_BASE_SCHEDULE_TASK(ScheduleConstant.SERIAL_BASE_SCHEDULE_TASK_KEY, "根据bizId分片（有事务控制）"),
    SERIAL_BASE_SCHEDULE_NO_TRANSACTION_TASK(ScheduleConstant.SERIAL_BASE_SCHEDULE_NO_TRANSACTION_TASK_KEY, "根据bizId分片（无事务控制）"),
    SERIAL_BASE_SCHEDULE_TYPES_TASK(ScheduleConstant.SERIAL_BASE_SCHEDULE_TYPES_TASK_KEY, "根据bizId分片(多个任务类型)"),
    DELAY_MSG_TRANSFER_SCHEDULE_TASK(ScheduleConstant.DELAY_MSG_TRANSFER_SCHEDULE_TASK_KEY, "迁移任务(未完成,失败)"),
    DELETE_TRANSFER_SCHEDULE_TASK(ScheduleConstant.DELETE_TRANSFER_SCHEDULE_TASK_KEY, "删除历史成功数据任务"),
    CUSTOM(ScheduleConstant.CUSTOM_SCHEDULE_TASK, "自定义调度任务");

    private String value;
    private String name;

    TaskType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static TaskType valueOfNullable(String value) {
        for (TaskType item : TaskType.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
