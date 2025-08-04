package pro.shushi.pamirs.trigger.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.trigger.enmu.TriggerTimeAnchorEnum;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;
import pro.shushi.pamirs.trigger.service.AbstractTaskActionService;
import pro.shushi.pamirs.trigger.service.ScheduleTaskActionService;

import java.util.*;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 15:24
 */
@Service
@Fun(ScheduleTaskActionService.FUN_NAMESPACE)
public class ScheduleTaskActionServiceImpl extends AbstractTaskActionService<ScheduleTaskAction> implements ScheduleTaskActionService {

    @Function
    @Override
    public List<ScheduleTaskAction> selectList(ScheduleQuery wrapper) {
        return new ArrayList<>();
    }

    @Function
    @Override
    public Boolean submit(ScheduleTaskAction taskItem) {
        if (pamirsScheduleService.createOrUpdateScheduleTaskByTechnicalName(generatorScheduleItem(generatorScheduleItem(taskItem), taskItem)) == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Function
    @Override
    public Boolean delete(String technicalName) {
        return pamirsScheduleService.deleteScheduleTaskByTechnicalName(technicalName) == 1;
    }

    @Function
    @Override
    public Boolean deleteBatch(Collection<String> technicalNames) {
        int count = pamirsScheduleService.deleteScheduleTaskByTechnicalNameBatch(technicalNames);
        return count == 1 || count == technicalNames.size();
    }

    @Function
    @Override
    public Boolean active(String technicalName) {
        return pamirsScheduleService.activeScheduleTaskByTechnicalName(technicalName) == 1;
    }

    @Function
    @Override
    public Boolean activeBatch(Collection<String> technicalNames) {
        int count = pamirsScheduleService.activeScheduleTaskByTechnicalNameBatch(technicalNames);
        return count == 1 || count == technicalNames.size();
    }

    @Function
    @Override
    public Boolean cancel(String technicalName) {
        return pamirsScheduleService.cancelScheduleTaskByTechnicalName(technicalName) == 1;
    }

    @Function
    @Override
    public Boolean cancelBatch(Collection<String> technicalNames) {
        int count = pamirsScheduleService.cancelScheduleTaskByTechnicalNameBatch(technicalNames);
        return count == 1 || count == technicalNames.size();
    }

    @Override
    protected ScheduleItem generatorScheduleItem(ScheduleItem scheduleItem, ScheduleTaskAction taskItem) {
        initExecuteTaskAction(scheduleItem, taskItem);

        //set and verification limitRetryNumber
        Integer limitExecuteNumber = taskItem.getLimitExecuteNumber();
        if (limitExecuteNumber == null) {
            limitExecuteNumber = -1;
        } else {
            if (limitExecuteNumber <= -1) {
                limitExecuteNumber = -1;
            }
        }
        scheduleItem.setLimitExecuteNumber(limitExecuteNumber);

        if (limitExecuteNumber != 1) {
            String cron = taskItem.getCron();
            if (StringUtils.isBlank(cron)) {
                //set and verification periodTimeValue
                if (taskItem.getPeriodTimeValue() == null) {
                    throw new IllegalArgumentException("Invalid period time value");
                } else {
                    scheduleItem.setPeriodTimeValue(taskItem.getPeriodTimeValue());
                }

                //set and verification periodTimeUnit
                if (taskItem.getPeriodTimeUnit() == null) {
                    throw new IllegalArgumentException("Invalid period time unit");
                } else {
                    scheduleItem.setPeriodTimeUnit(taskItem.getPeriodTimeUnit().getCalendarValue());
                }
            } else {
                //set and verification cron
                if (CronExpression.isValidExpression(cron)) {
                    scheduleItem.setCron(cron);
                } else {
                    throw new IllegalArgumentException("Invalid cron expression");
                }
            }
        }

        //set and verification periodTimeAnchor
        TriggerTimeAnchorEnum periodTimeAnchor = taskItem.getPeriodTimeAnchor();
        if (taskItem.getPeriodTimeAnchor() == null) {
            periodTimeAnchor = TriggerTimeAnchorEnum.START;
        }
        scheduleItem.setPeriodTimeAnchor(periodTimeAnchor.getTimeAnchor().intValue());

        //set and verification technicalName
        String technicalName = taskItem.getTechnicalName();
        if (StringUtils.isBlank(technicalName)) {
            throw new IllegalArgumentException("Invalid technical name");
        }
        scheduleItem.setTechnicalName(technicalName);

        scheduleItem.setIsCycle(Boolean.TRUE);
        return scheduleItem;
    }

    @Override
    protected ScheduleTaskAction generatorScheduleTaskAction(ScheduleItem scheduleItem) {
        ScheduleTaskAction taskAction = new ScheduleTaskAction();
        taskAction.setTechnicalName(scheduleItem.getTechnicalName());
        taskAction.setLimitExecuteNumber(scheduleItem.getLimitExecuteNumber());
        taskAction.setPeriodTimeValue(scheduleItem.getPeriodTimeValue());
//        taskAction.setPeriodTimeUnit(scheduleItem.getPeriodTimeUnit());
//        taskAction.setPeriodTimeAnchor(scheduleItem.getPeriodTimeAnchor());
        taskAction.setTaskType(scheduleItem.getTaskType());
//        taskAction.setDelayTimeValue(scheduleItem.getDelayTimeValue());
//        taskAction.setDelayTimeUnit(scheduleItem.getDelayTimeUnit());
        taskAction.setLimitRetryNumber(scheduleItem.getLimitRetryNumber());
        taskAction.setNextRetryTimeValue(scheduleItem.getNextRetryTimeValue());
//        taskAction.setNextRetryTimeUnit(scheduleItem.getNextRetryTimeUnit());
        taskAction.setBizId(scheduleItem.getBizId());
        taskAction.setBizCode(scheduleItem.getBizCode());
        return taskAction;
    }

    @Override
    protected ScheduleItem generatorScheduleItemQueryEntity(ScheduleTaskAction entity) {
        ScheduleItem scheduleItem = new ScheduleItem();
        scheduleItem.setTechnicalName(entity.getTechnicalName());
        scheduleItem.setLimitExecuteNumber(entity.getLimitExecuteNumber());
        scheduleItem.setPeriodTimeValue(entity.getPeriodTimeValue());
//        scheduleItem.setPeriodTimeUnit(entity.getPeriodTimeUnit());
//        scheduleItem.setPeriodTimeAnchor(entity.getPeriodTimeAnchor());
        scheduleItem.setTaskType(entity.getTaskType());
//        scheduleItem.setDelayTimeValue(entity.getDelayTimeValue());
//        scheduleItem.setDelayTimeUnit(entity.getDelayTimeUnit());
        scheduleItem.setLimitRetryNumber(entity.getLimitRetryNumber());
        scheduleItem.setNextRetryTimeValue(entity.getNextRetryTimeValue());
//        scheduleItem.setNextRetryTimeUnit(entity.getNextRetryTimeUnit());
        scheduleItem.setBizId(entity.getBizId());
        scheduleItem.setBizCode(entity.getBizCode());
        return scheduleItem;
    }
}
