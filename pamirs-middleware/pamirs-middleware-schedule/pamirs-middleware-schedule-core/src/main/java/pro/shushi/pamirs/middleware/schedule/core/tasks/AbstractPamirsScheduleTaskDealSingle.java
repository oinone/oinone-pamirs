package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleManager;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleService;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.core.function.impl.DefaultReturnResultConverter;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;
import pro.shushi.pamirs.middleware.schedule.core.manager.ScheduleTaskActionManager;
import pro.shushi.pamirs.middleware.schedule.core.util.DateHelper;
import pro.shushi.pamirs.middleware.schedule.directive.DirectiveHelper;
import pro.shushi.pamirs.middleware.schedule.directive.IntValueEnumerationHelper;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskExecuteStatus;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskStatus;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeAnchor;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeUnit;
import pro.shushi.pamirs.middleware.schedule.spi.ScheduleTaskErrorCallbackApi;

import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 22:03
 */
public abstract class AbstractPamirsScheduleTaskDealSingle<T> implements IScheduleTaskDealSingle<T> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String[] DEFAULT_FUNCTION_PARAMETER_TYPES = new String[]{ScheduleItem.class.getName()};

    @Autowired
    protected ScheduleManager scheduleManager;

    @Autowired
    protected ScheduleService scheduleService;

    @Autowired
    protected ScheduleTaskActionManager scheduleTaskActionManager;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    private String taskType;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    @Override
    public Comparator<T> getComparator() {
        return null;
    }

    protected void replayCycleScheduleTask(ScheduleItem task, boolean isRetry) {
        ScheduleItem newTask = task.cloneTask();
        if (isRetry) {
            newTask.setTaskStatus(TaskStatus.EXECUTE_ERROR.intValue())
                    .setRemark(task.getTechnicalName() + ":" + task.getId() + " cycle task execute error record");
            Integer limitRetryNumber = task.getLimitRetryNumber();
            Integer retryNumber = task.getRetryNumber();
            boolean isNotRetry = limitRetryNumber == 0 || limitRetryNumber != -1 && retryNumber >= limitRetryNumber;
            if (isNotRetry) {
                setCycleScheduleTaskNextExecuteTime(task);
            } else {
                task.setTaskStatus(TaskStatus.WAITING.intValue())
                        .setNextExecuteTime(DateHelper.computeNextExecuteTime(new Date(task.getLastExecuteTime()), IntValueEnumerationHelper.intValueOf(TimeUnit.class, task.getNextRetryTimeUnit()), task.getNextRetryTimeValue()).getTime());
            }
        } else {
            newTask.setTaskStatus(TaskStatus.FINISHED.intValue())
                    .setRemark(task.getTechnicalName() + ":" + task.getId() + " cycle task execute record");
            Integer limitExecuteNumber = task.getLimitExecuteNumber();
            Integer executeNumber = task.getExecuteNumber();
            boolean isNotExecute = limitExecuteNumber == 0 || limitExecuteNumber != -1 && executeNumber >= limitExecuteNumber;
            if (isNotExecute) {
                task.setTaskStatus(TaskStatus.FINISHED.intValue());
            } else {
                setCycleScheduleTaskNextExecuteTime(task);
            }
        }
        scheduleService.addScheduleTaskRecord(newTask);
    }

    protected void setCycleScheduleTaskNextExecuteTime(ScheduleItem task) {
        Date anchorDate;
        if (task.getPeriodTimeAnchor() == TimeAnchor.AFTER.intValue()) {
            anchorDate = new Date(task.getLastExecuteTime());
        } else {
            anchorDate = new Date(task.getAnchorExecuteTime());
        }
        String cron = task.getCron();
        if (StringUtils.isBlank(cron)) {
            task.setNextExecuteTime(DateHelper.computeNextExecuteTime(anchorDate, IntValueEnumerationHelper.intValueOf(TimeUnit.class, task.getPeriodTimeUnit()), task.getPeriodTimeValue()).getTime());
        } else {
            task.setNextExecuteTime(DateHelper.computeNextExecuteTime(anchorDate, cron).getTime());
        }
        task.setAnchorExecuteTime(task.getNextExecuteTime());
        task.setTaskStatus(TaskStatus.WAITING.intValue());
    }

    protected String getTaskMessage(ScheduleItem task) {
        return task.getOwnSign() + ":"
                + task.getApplication() + ":"
                + task.getTenant() + ":"
                + task.getEnv() + ":"
                + task.getInterfaceName() + ":"
                + task.getMethodName() + ":"
                + task.getBizId() + ":"
                + task.getBizCode() + ":"
                + task.getExecuteStatus() + ":"
                + task.getTaskStatus();
    }

    protected int computeExecuteStatus(int executeNumber, int retryNumber, int limitRetryNumber, boolean isCycle) {
        if (executeNumber == 0) {
            return TaskExecuteStatus.FIRST_EXECUTE.intValue();
        }
        int result = 0;
        if (retryNumber >= 1) {
            result = DirectiveHelper.enable(result, TaskExecuteStatus.RETRY_EXECUTE);
            if (isCycle) {
                result = DirectiveHelper.enable(result, TaskExecuteStatus.RETRY_LOOP_EXECUTE);
            }
        } else {
            result = DirectiveHelper.enable(result, TaskExecuteStatus.LOOP_EXECUTE);
        }
        if (limitRetryNumber == 0 || limitRetryNumber == -1 || retryNumber + 1 == limitRetryNumber) {
            result = DirectiveHelper.enable(result, TaskExecuteStatus.LAST_EXECUTE);
        }
        return result;
    }

    protected FunctionDefinition<Result<Void>> getFunctionDefinition(ScheduleItem task) {
        FunctionDefinition<Result<Void>> functionDefinition = new FunctionDefinition<>(task.getInterfaceName(), task.getMethodName(), DEFAULT_FUNCTION_PARAMETER_TYPES);
        Optional.ofNullable(task.getGroup()).filter(StringUtils::isNotBlank).ifPresent(functionDefinition::setGroup);
        Optional.ofNullable(task.getVersion()).filter(StringUtils::isNotBlank).ifPresent(functionDefinition::setVersion);
        Optional.ofNullable(task.getTimeout()).ifPresent(functionDefinition::setTimeout);
        functionDefinition.setReturnResultConverter(DefaultReturnResultConverter.INSTANCE);
        return functionDefinition;
    }

    protected void errorHandlerCallback(ScheduleItem task) {
        try {
            Spider.getDefaultExtension(ScheduleTaskErrorCallbackApi.class).handleWhenExecuteError(task);
        } catch (Throwable e) {
            log.error("schedule error handler callback execute error.", e);
        }
    }
}
