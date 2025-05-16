package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.util.List;

/**
 * 基础任务执行器（事务控制）
 */
public class BaseScheduleTask extends AbstractLocalScheduleTaskDealSingle implements IScheduleTaskDealSingle<ScheduleItem> {

    @Override
    protected void execute0(ScheduleItem task, List<ScheduleAction> taskActions, Result<Void> result) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
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
        });
    }
}
