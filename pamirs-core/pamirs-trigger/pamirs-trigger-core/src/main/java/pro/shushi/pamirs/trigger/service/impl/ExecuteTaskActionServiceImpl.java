package pro.shushi.pamirs.trigger.service.impl;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.trigger.model.ExecuteTaskAction;
import pro.shushi.pamirs.trigger.service.AbstractTaskActionService;
import pro.shushi.pamirs.trigger.service.ExecuteTaskActionService;

import java.util.Collection;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 15:44
 */
@Service
@Fun(ExecuteTaskActionService.FUN_NAMESPACE)
public class ExecuteTaskActionServiceImpl extends AbstractTaskActionService<ExecuteTaskAction> implements ExecuteTaskActionService {

    @Function
    @Override
    public List<ExecuteTaskAction> selectList(ScheduleQuery wrapper) {
        throw new UnsupportedOperationException();
    }

    @Function
    @Override
    public Boolean delete(String technicalName) {
        throw new UnsupportedOperationException();
    }

    @Function
    @Override
    public Boolean deleteBatch(Collection<String> technicalNames) {
        throw new UnsupportedOperationException();
    }

    @Function
    @Override
    public Boolean active(String technicalName) {
        throw new UnsupportedOperationException();
    }

    @Function
    @Override
    public Boolean activeBatch(Collection<String> technicalNames) {
        throw new UnsupportedOperationException();
    }

    @Function
    @Override
    public Boolean cancel(String technicalName) {
        throw new UnsupportedOperationException();
    }

    @Function
    @Override
    public Boolean cancelBatch(Collection<String> technicalNames) {
        throw new UnsupportedOperationException();
    }

    @Function
    @Override
    public Boolean submit(ExecuteTaskAction taskItem) {
        return super.submit(taskItem);
    }

    @Override
    protected ScheduleItem generatorScheduleItem(ScheduleItem scheduleItem, ExecuteTaskAction taskItem) {
        initExecuteTaskAction(scheduleItem, taskItem);
        return scheduleItem;
    }

    @Override
    protected ExecuteTaskAction generatorScheduleTaskAction(ScheduleItem scheduleItem) {
        ExecuteTaskAction taskAction = new ExecuteTaskAction();
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
    protected ScheduleItem generatorScheduleItemQueryEntity(ExecuteTaskAction entity) {
        ScheduleItem scheduleItem = new ScheduleItem();
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
