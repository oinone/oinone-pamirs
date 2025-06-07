package pro.shushi.pamirs.trigger.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.meta.common.constants.FunctionDefaultsConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskStatus;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeUnit;
import pro.shushi.pamirs.trigger.model.AbstractTaskAction;
import pro.shushi.pamirs.trigger.model.ExecuteTaskAction;
import pro.shushi.pamirs.trigger.spi.RemoteTaskPredictApi;
import pro.shushi.pamirs.trigger.util.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 16:46
 */
public abstract class AbstractTaskActionService<T extends AbstractTaskAction> implements TaskActionService<T> {

    /**
     * generator Schedule Item
     *
     * @param scheduleItem schedule task item
     * @param taskItem     task item
     * @return ScheduleItem model
     */
    protected abstract ScheduleItem generatorScheduleItem(ScheduleItem scheduleItem, T taskItem);

    /**
     * generator TaskAction
     *
     * @param scheduleItem schedule task item
     * @return TaskAction
     */
    protected abstract T generatorScheduleTaskAction(ScheduleItem scheduleItem);

    protected abstract ScheduleItem generatorScheduleItemQueryEntity(T entity);

    @Autowired
    protected ScheduleOperateService pamirsScheduleService;

    @Override
    public Long countByEntity(T entity) {
        return pamirsScheduleService.countByEntity(afterProperties(generatorScheduleItemQueryEntity(entity), entity));
    }

    @Override
    public List<T> selectListByEntity(T entity) {
        return pamirsScheduleService.selectListByEntity(afterProperties(generatorScheduleItemQueryEntity(entity), entity))
                .stream()
                .map(item -> afterProperties(generatorScheduleTaskAction(item), item))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean submit(T taskItem) {
        if (pamirsScheduleService.addScheduleTask(generatorScheduleItem(generatorScheduleItem(taskItem), taskItem)) == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    protected ScheduleItem generatorScheduleItem(T taskItem) {
        FunctionDefinition functionDefinition = taskItem.getExecuteFunction();
        String group = Optional.ofNullable(functionDefinition)
                .map(FunctionDefinition::getGroup)
                .filter(StringUtils::isNotBlank)
                .orElse(FunctionDefaultsConstants.GROUP);
        String version = Optional.ofNullable(functionDefinition)
                .map(FunctionDefinition::getVersion)
                .filter(StringUtils::isNotBlank)
                .orElse(FunctionDefaultsConstants.VERSION);
        int timeout = Optional.ofNullable(functionDefinition)
                .map(FunctionDefinition::getTimeout)
                .orElse(FunctionDefaultsConstants.TIMEOUT);
        String namespace = taskItem.getExecuteNamespace();
        String fun = taskItem.getExecuteFun();
        TaskStatus taskStatus = TaskStatus.WAITING;
        Boolean isActive = taskItem.getActive();
        if (isActive != null && !isActive) {
            taskStatus = TaskStatus.CANCELED;
        }
        return new ScheduleItem()
                .setInterfaceName(namespace)
                .setMethodName(fun)
                .setGroup(group)
                .setVersion(version)
                .setTimeout(timeout)
                .setContext(Optional.ofNullable(taskItem.getContext()).filter(StringUtils::isNotBlank).orElse(null))
                .setIsCycle(false)
                .setLimitRetryNumber(-1)
                .setNextRetryTimeValue(3)
                .setNextRetryTimeUnit(TimeUnit.SECOND.intValue())
                .setNextExecuteTime(taskItem.getFirstExecuteTime())
                .setRemark(taskItem.getDisplayName())
                .setTaskStatus(taskStatus.intValue());
    }

    protected void initExecuteTaskAction(ScheduleItem scheduleItem, ExecuteTaskAction taskItem) {
        //set and verification nextRetryTimeValue
        TimeUnitEnum nextRetryTimeUnit = taskItem.getNextRetryTimeUnit();
        if (nextRetryTimeUnit != null) {
            scheduleItem.setNextRetryTimeUnit(nextRetryTimeUnit.getCalendarValue());

            //set and verification limitRetryNumber
            Integer limitRetryNumber = taskItem.getLimitRetryNumber();
            if (limitRetryNumber == null) {
                limitRetryNumber = -1;
            } else {
                if (limitRetryNumber <= -1) {
                    limitRetryNumber = -1;
                }
            }
            scheduleItem.setLimitRetryNumber(limitRetryNumber);

            //set and verification nextRetryTimeValue
            Integer nextRetryTimeValue = taskItem.getNextRetryTimeValue();
            if (nextRetryTimeValue == null) {
                // No guessing about intention
                throw new IllegalArgumentException("Invalid next retry time value.");
            } else {
                if (nextRetryTimeValue <= 0) {
                    nextRetryTimeValue = 0;
                }
            }
            scheduleItem.setNextRetryTimeValue(nextRetryTimeValue);
        }

        String taskTypeString = taskItem.getTaskType();
        if (StringUtils.isBlank(taskTypeString)) {
            taskTypeString = TaskType.BASE_SCHEDULE_NO_TRANSACTION_TASK.getValue();
        }
        RemoteTaskPredictApi remoteTaskPredict = Spider.getDefaultExtension(RemoteTaskPredictApi.class);
        if (remoteTaskPredict.isRemote()) {
            taskTypeString = remoteTaskPredict.converterTaskType(taskTypeString);
        }
        scheduleItem.setTaskType(taskTypeString);

        Integer delayTimeValue = taskItem.getDelayTimeValue();
        TimeUnitEnum delayTimeUnit = taskItem.getDelayTimeUnit();
        if (delayTimeValue != null && delayTimeUnit != null) {
            Date nextExecuteTime;
            Long firstExecuteTime = taskItem.getFirstExecuteTime();
            if (firstExecuteTime == null) {
                nextExecuteTime = new Date();
            } else {
                nextExecuteTime = new Date(firstExecuteTime);
            }
            scheduleItem.setNextExecuteTime(DateUtil.computeNextExecuteTime(nextExecuteTime, delayTimeUnit, delayTimeValue).getTime());
        }

        scheduleItem.setBizId(taskItem.getBizId())
                .setBizCode(taskItem.getBizCode());
    }

    protected T afterProperties(T task, ScheduleItem scheduleItem) {
//        task.setDisplayName(scheduleItem.getDisplayName());
//        task.setDescription(scheduleItem.getDescription());
        task.setTenant(scheduleItem.getTenant());
        task.setEnv(scheduleItem.getEnv());
        task.setOwnSign(scheduleItem.getOwnSign());
        task.setApplication(scheduleItem.getApplication());
        task.setExecuteNamespace(scheduleItem.getInterfaceName());
        task.setExecuteFun(scheduleItem.getMethodName());
        task.setContext(scheduleItem.getContext());
        task.setId(scheduleItem.getId());
//        task.setCreateDate(scheduleItem.getCreateDate());
//        task.setWriteDate(scheduleItem.getWriteDate());
//        task.setCreateUid(scheduleItem.getCreateUid());
//        task.setWriteUid(scheduleItem.getWriteUid());
        return task;
    }

    protected ScheduleItem afterProperties(ScheduleItem scheduleItem, T task) {
//        scheduleItem.setDisplayName(task.getDisplayName());
//        scheduleItem.setDescription(task.getDescription());
        scheduleItem.setTenant(task.getTenant());
        scheduleItem.setEnv(task.getEnv());
        scheduleItem.setOwnSign(task.getOwnSign());
        scheduleItem.setApplication(task.getApplication());
        scheduleItem.setInterfaceName(task.getExecuteNamespace());
        scheduleItem.setMethodName(task.getExecuteFun());
        scheduleItem.setContext(task.getContext());
        scheduleItem.setId(task.getId());
//        scheduleItem.setCreateDate(task.getCreateDate());
//        scheduleItem.setWriteDate(task.getWriteDate());
//        scheduleItem.setCreateUid(task.getCreateUid());
//        scheduleItem.setWriteUid(task.getWriteUid());
        return scheduleItem;
    }
}
