package pro.shushi.pamirs.eip.core.task.abs;

import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.trigger.enmu.TriggerTimeAnchorEnum;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;
import pro.shushi.pamirs.trigger.service.ScheduleTaskActionService;

import javax.annotation.Resource;

/**
 * @author yeshenyue on 2025/4/24 09:33.
 */
@Slf4j
public abstract class EipAbstractScheduledJob implements ScheduleAction {

    @Resource
    protected ScheduleTaskActionService scheduleTaskActionService;

    protected abstract String getDisplayName();

    protected abstract Integer getPeriodTime();

    protected abstract TimeUnitEnum getTimeUnit();

    protected Integer getTimeoutMillis() {
        return 5000;
    }

    public void initTask() {
        ScheduleTaskAction task = new ScheduleTaskAction();
        task.setDisplayName(getDisplayName());
        task.setTechnicalName(getInterfaceName());
        task.setLimitExecuteNumber(-1);
        task.setPeriodTimeValue(getPeriodTime());
        task.setPeriodTimeUnit(getTimeUnit());
        task.setPeriodTimeAnchor(TriggerTimeAnchorEnum.START);
        task.setLimitRetryNumber(0);
        task.setNextRetryTimeValue(0);
        task.setNextRetryTimeUnit(TimeUnitEnum.MINUTE);
        task.setExecuteNamespace(getInterfaceName());
        task.setExecuteFun(getMethodName());
        task.setTaskType(TaskType.CYCLE_SCHEDULE_NO_TRANSACTION_TASK.getValue());
        task.setContext(null);
        task.setActive(true);
        task.setExecuteFunction(new FunctionDefinition().setTimeout(getTimeoutMillis()));
        task.setFirstExecuteTime(System.currentTimeMillis());
        scheduleTaskActionService.submit(task);
        log.info("定时任务注册成功：[{}]", getDisplayName());
    }

    @Override
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Result<Void> execute(ScheduleItem scheduleItem) {
        log.info("开始执行定时任务-{}", getDisplayName());
        long start = System.currentTimeMillis();
        doExecute(scheduleItem);
        long end = System.currentTimeMillis();
        log.info("结束执行定时任务-{},耗时[{}ms]", getDisplayName(), (end - start));
        Result<Void> result = new Result<>();
        result.setSuccess(true);
        return result;
    }

    public abstract void doExecute(ScheduleItem scheduleItem);
}
