package pro.shushi.pamirs.eip.core.task;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.eip.api.strategy.service.EipCircuitBreakerRecordService;
import pro.shushi.pamirs.eip.core.task.abs.EipAbstractScheduledJob;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import jakarta.annotation.Resource;

/**
 * 熔断记录定时落库-每12小时同步一次
 *
 * @author yeshenyue on 2025/4/17 11:18.
 */
@Slf4j
@Component
@Fun(EipCircuitBreakerRecordSyncTask.FUN_NAMESPACE)
public class EipCircuitBreakerRecordSyncTask extends EipAbstractScheduledJob {

    public static final String FUN_NAMESPACE = "eip.EipCircuitBreakerRecordSyncTask";
    public static final String TASK_DISPLAY_NAME = "熔断记录定时同步";

    @Resource
    private EipCircuitBreakerRecordService eipCircuitBreakerRecordService;

    @Override
    protected String getDisplayName() {
        return EipCircuitBreakerRecordSyncTask.TASK_DISPLAY_NAME;
    }

    @Override
    protected Integer getPeriodTime() {
        return 12;
    }

    @Override
    protected TimeUnitEnum getTimeUnit() {
        return TimeUnitEnum.HOUR_OF_DAY;
    }

    @Override
    public String getInterfaceName() {
        return EipCircuitBreakerRecordSyncTask.FUN_NAMESPACE;
    }

    @Override
    public void doExecute(ScheduleItem scheduleItem) {
        eipCircuitBreakerRecordService.saveRecord();
    }
}
