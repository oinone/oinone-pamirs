package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.util.List;

/**
 * 基础任务执行器（无事务控制）
 */
public class BaseScheduleNoTransactionTask extends AbstractLocalScheduleTaskDealSingle implements IScheduleTaskDealSingle<ScheduleItem> {

    @Override
    protected void execute0(ScheduleItem task, List<ScheduleAction> taskActions, Result<Void> result) {
        for (ScheduleAction taskAction : taskActions) {
            Result<Void> taskExecuteRs = taskAction.execute(task);
            result.setErrorMessage(taskExecuteRs.getErrorMessage());
            if (!taskExecuteRs.isSuccess()) {
                result.setFail(taskExecuteRs.getErrorMessage());
                result.setErrorCode(taskExecuteRs.getErrorCode());
                result.setErrorName(taskExecuteRs.getErrorName());
                return;
            }
        }
    }
}
