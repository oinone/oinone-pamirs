package pro.shushi.pamirs.eip.core.task;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.eip.api.service.CircuitBreakerRecordService;
import pro.shushi.pamirs.meta.annotation.Fun;
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
import java.util.Date;

/**
 * 熔断记录定时落库-每12小时同步一次
 * @author yeshenyue on 2025/4/17 11:18.
 */
@Slf4j
@Component
@Fun(EipCircuitBreakerRecordSyncTask.FUN_NAMESPACE)
public class EipCircuitBreakerRecordSyncTask implements ScheduleAction {
    public static final String FUN_NAMESPACE = "eip.EipCircuitBreakerRecordSyncTask";
    public static final String METHOD_NAME = "execute";
    public static final String TASK_DISPLAY_NAME = "熔断记录定时同步";

    @Resource
    private ScheduleTaskActionService scheduleTaskActionService;
    @Resource
    private CircuitBreakerRecordService circuitBreakerRecordService;

    public void initTask() {
        ScheduleTaskAction scheduleTaskAction = new ScheduleTaskAction();
        scheduleTaskAction.setDisplayName(TASK_DISPLAY_NAME);
        scheduleTaskAction.setTechnicalName(FUN_NAMESPACE);
        scheduleTaskAction.setLimitExecuteNumber(-1);
        scheduleTaskAction.setPeriodTimeValue(12);
        scheduleTaskAction.setPeriodTimeUnit(TimeUnitEnum.HOUR_OF_DAY);
        scheduleTaskAction.setPeriodTimeAnchor(TriggerTimeAnchorEnum.START);
        scheduleTaskAction.setLimitRetryNumber(0);
        scheduleTaskAction.setNextRetryTimeValue(0);
        scheduleTaskAction.setNextRetryTimeUnit(TimeUnitEnum.MINUTE);
        scheduleTaskAction.setExecuteNamespace(FUN_NAMESPACE);
        scheduleTaskAction.setExecuteFun(METHOD_NAME);
        scheduleTaskAction.setExecuteFunction(new FunctionDefinition().setTimeout(5000));
        scheduleTaskAction.setTaskType(TaskType.CYCLE_SCHEDULE_NO_TRANSACTION_TASK.getValue());
        scheduleTaskAction.setContext(null);
        scheduleTaskAction.setActive(true);
        scheduleTaskAction.setFirstExecuteTime(new Date().getTime());
        Long count = scheduleTaskActionService.countByEntity(scheduleTaskAction);
        if (count == null || count == 0) {
            scheduleTaskActionService.submit(scheduleTaskAction);
        }
    }

    @Override
    public String getInterfaceName() {
        return FUN_NAMESPACE;
    }

    @Override
    public String getMethodName() {
        return METHOD_NAME;
    }

    @Override
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Result<Void> execute(ScheduleItem scheduleItem) {
        log.info("开始-{}", TASK_DISPLAY_NAME);
        long start = System.currentTimeMillis();
        circuitBreakerRecordService.saveRecord();
        long end = System.currentTimeMillis();
        log.info("结束-{},耗时[{}ms]", TASK_DISPLAY_NAME, (end - start));
        Result<Void> result = new Result<>();
        result.setSuccess(true);
        return result;
    }
}
