package pro.shushi.pamirs.eip.core.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.eip.api.config.PamirsEipProperties;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogDailyCountService;
import pro.shushi.pamirs.eip.core.task.abs.EipAbstractScheduledJob;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 接口日志每日汇总统计定时任务，每日凌晨1点45执行统计次日
 */
@Slf4j
@Component
@Fun(EipLogDailyCountSyncTask.FUN_NAMESPACE)
public class EipLogDailyCountSyncTask extends EipAbstractScheduledJob {

    public static final String FUN_NAMESPACE = "eip.EipLogDailyCountSyncTask";
    public static final String TASK_DISPLAY_NAME = "接口日志每日汇总统计定时任务";

    @Autowired
    private EipLogDailyCountService eipLogDailyCountService;

    @Autowired
    private PamirsEipProperties pamirsEipProperties;

    @Override
    protected String getDisplayName() {
        return I18nUtils.getMessage("EipLogDailyCountSyncTask.taskDisplayName");
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
        return EipLogDailyCountSyncTask.FUN_NAMESPACE;
    }

    @Override
    public void doExecute(ScheduleItem scheduleItem) {
        eipLogDailyCountService.syncYesterday();
    }

    @Override
    protected void doSubmit(ScheduleTaskAction task) {
        if (Boolean.FALSE.equals(pamirsEipProperties.getEnableLogCount())) {
            scheduleTaskActionService.cancel(task.getTechnicalName());
        } else {
            super.doSubmit(task);
        }
    }
}