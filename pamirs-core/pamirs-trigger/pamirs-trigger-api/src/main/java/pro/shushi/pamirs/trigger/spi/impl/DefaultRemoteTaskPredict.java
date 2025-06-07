package pro.shushi.pamirs.trigger.spi.impl;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.trigger.spi.RemoteTaskPredictApi;

/**
 * 默认强制使用远程任务执行
 *
 * @author Adamancy Zhang at 14:49 on 2021-08-10
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@SPI.Service
public class DefaultRemoteTaskPredict implements RemoteTaskPredictApi {

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public String converterTaskType(String taskTypeString) {
        TaskType taskType = TaskType.valueOfNullable(taskTypeString);
        if (taskType != null) {
            switch (taskType) {
                case BASE_SCHEDULE_TASK:
                case BASE_SCHEDULE_NO_TRANSACTION_TASK:
                case CYCLE_SCHEDULE_NO_TRANSACTION_TASK:
                    taskType = TaskType.REMOTE_SCHEDULE_TASK;
                    break;
                case SERIAL_BASE_SCHEDULE_TASK:
                case SERIAL_BASE_SCHEDULE_NO_TRANSACTION_TASK:
                case SERIAL_BASE_SCHEDULE_TYPES_TASK:
                    taskType = TaskType.SERIAL_REMOTE_SCHEDULE_TASK;
                    break;
            }
            return taskType.getValue();
        }
        return taskTypeString;
    }
}
