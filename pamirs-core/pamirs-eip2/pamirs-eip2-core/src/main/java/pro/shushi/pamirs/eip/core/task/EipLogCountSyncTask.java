package pro.shushi.pamirs.eip.core.task;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogCountService;
import pro.shushi.pamirs.eip.core.task.abs.EipAbstractScheduledJob;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

import javax.annotation.Resource;

/**
 * 接口日志统计定时任务，每12小时执行一次
 */
@Slf4j
@Component
@Fun(EipLogCountSyncTask.FUN_NAMESPACE)
public class EipLogCountSyncTask extends EipAbstractScheduledJob {

    public static final String FUN_NAMESPACE = "eip.EipLogCountSyncTask";
    public static final String TASK_DISPLAY_NAME = "接口日志汇总统计定时任务";

    @Resource
    private EipLogCountService eipLogCountService;

    @Override
    protected String getDisplayName() {
        return TASK_DISPLAY_NAME;
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
        return EipLogCountSyncTask.FUN_NAMESPACE;
    }

    @Override
    public void doExecute(ScheduleItem scheduleItem) {
        eipLogCountService.syncEipLogCount();
    }
}