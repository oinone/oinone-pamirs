package pro.shushi.pamirs.eip.core.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.eip.api.strategy.service.EipCircuitBreakerRecordService;
import pro.shushi.pamirs.eip.core.task.abs.EipAbstractScheduledJob;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 熔断记录定时落库-每日凌晨1点15同步
 *
 * @author yeshenyue on 2025/4/17 11:18.
 */
@Slf4j
@Component
@Fun(EipCircuitBreakerRecordSyncTask.FUN_NAMESPACE)
public class EipCircuitBreakerRecordSyncTask extends EipAbstractScheduledJob {

    public static final String FUN_NAMESPACE = "eip.EipCircuitBreakerRecordSyncTask";

    @Autowired
    private EipCircuitBreakerRecordService eipCircuitBreakerRecordService;

    @Override
    protected String getDisplayName() {
        return I18nUtils.getMessage("EipCircuitBreakerRecordSyncTask.taskDisplayName");
    }

    @Override
    protected Integer getPeriodTime() {
        return 24;
    }

    @Override
    protected TimeUnitEnum getTimeUnit() {
        return TimeUnitEnum.HOUR_OF_DAY;
    }

    @Override
    protected Long getFirstExecuteTime() {
        ZoneId zone = ZoneId.systemDefault();
        return LocalDate.now(zone)
                .plusDays(1)
                .atTime(1, 15)
                .atZone(zone)
                .toInstant()
                .toEpochMilli();
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
