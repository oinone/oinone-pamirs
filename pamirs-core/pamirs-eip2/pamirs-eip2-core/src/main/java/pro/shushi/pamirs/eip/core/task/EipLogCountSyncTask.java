package pro.shushi.pamirs.eip.core.task;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogDailyCountService;
import pro.shushi.pamirs.eip.core.task.abs.EipAbstractScheduledJob;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 接口日志统计定时任务，每12小时执行一次
 * @deprecated 6.x please using {@link EipLogDailyCountSyncTask} ，大版本升级时使用升级sql取消此任务
 */
@Slf4j
@Component
@Deprecated
@Fun(EipLogCountSyncTask.FUN_NAMESPACE)
public class EipLogCountSyncTask extends EipAbstractScheduledJob {

    public static final String FUN_NAMESPACE = "eip.EipLogCountSyncTask";
    public static final String TASK_DISPLAY_NAME = "接口日志汇总统计定时任务";

    @Resource
    private EipLogDailyCountService eipLogDailyCountService;

    @Override
    protected String getDisplayName() {
        return TASK_DISPLAY_NAME;
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
        log.error("接口日志汇总统计定时任务已废弃");
    }

    @Override
    protected void doSubmit(ScheduleTaskAction task) {
        scheduleTaskActionService.cancel(task.getTechnicalName());
    }
}