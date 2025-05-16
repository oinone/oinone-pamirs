package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.core.manager.ScheduleTaskActionManager;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-12-25 20:46
 */
public abstract class AbstractLocalSerialScheduleTaskDealSingle extends AbstractSerialScheduleTaskDealSingle implements IScheduleTaskDealSingle<List<ScheduleItem>> {

    @Autowired
    protected ScheduleTaskActionManager scheduleTaskActionManager;

    /**
     * 外部实现的执行方法
     *
     * @param task        任务
     * @param taskActions 任务动作
     * @param result      执行结果
     */
    protected abstract void execute0(ScheduleItem task, List<ScheduleAction> taskActions, Result<Void> result);

    @Override
    protected Result<Void> execute0(ScheduleItem task, String ownSign) {
        Result<Void> result = new Result<>();
        final List<ScheduleAction> taskActions = scheduleTaskActionManager.getTaskAction(task.getInterfaceName() + ScheduleAction.SEPARATOR_OCTOTHORPE + task.getMethodName());
        if (taskActions == null || taskActions.size() == 0) {
            log.debug("taskActions==null||taskActions.size()==0");
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage("找不到指定的执行方法");
            return result;
        }
        execute0(task, taskActions, result);
        return result;
    }
}
