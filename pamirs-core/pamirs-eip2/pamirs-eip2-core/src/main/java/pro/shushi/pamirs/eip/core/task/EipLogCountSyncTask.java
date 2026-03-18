package pro.shushi.pamirs.eip.core.task;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogDailyCountService;
import pro.shushi.pamirs.eip.core.task.abs.EipAbstractScheduledJob;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Interface log statistics scheduled task, executed every 12 hours
 * @deprecated 6.x please using {@link EipLogDailyCountSyncTask} , When upgrading the major version, use the upgrade sql to cancel this task
 */
@Slf4j
@Component
@Deprecated
@Fun(EipLogCountSyncTask.FUN_NAMESPACE)
public class EipLogCountSyncTask extends EipAbstractScheduledJob {

    public static final String FUN_NAMESPACE = "eip.EipLogCountSyncTask";

    @Resource
    private EipLogDailyCountService eipLogDailyCountService;

    @Override
    protected String getDisplayName() {
        return I18nUtils.getMessage("EipLogCountSyncTask.taskDisplayName");
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
                .atTime(1, 45)
                .atZone(zone)
                .toInstant()
                .toEpochMilli();
    }

    @Override
    public String getInterfaceName() {
        return EipLogCountSyncTask.FUN_NAMESPACE;
    }

    @Override
    public void doExecute(ScheduleItem scheduleItem) {
        log.error("The scheduled task for interface log summary statistics has been deprecated");
    }

    @Override
    protected void doSubmit(ScheduleTaskAction task) {
        scheduleTaskActionService.cancel(task.getTechnicalName());
    }
}