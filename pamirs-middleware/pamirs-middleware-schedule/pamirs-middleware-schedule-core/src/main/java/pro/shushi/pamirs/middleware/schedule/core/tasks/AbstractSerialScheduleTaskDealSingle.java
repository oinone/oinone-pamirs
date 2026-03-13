package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.core.util.DateHelper;
import pro.shushi.pamirs.middleware.schedule.directive.IntValueEnumerationHelper;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskStatus;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TimeUnit;
import pro.shushi.pamirs.middleware.schedule.spi.ScheduleTaskActionExecuteAroundApi;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleDayWeek;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleTable;

import java.util.*;

/**
 * @author Adamancy Zhang
 * @date 2020-12-25 20:31
 */
public abstract class AbstractSerialScheduleTaskDealSingle extends AbstractPamirsScheduleTaskDealSingle<List<ScheduleItem>> implements IScheduleTaskDealSingle<List<ScheduleItem>> {

    /**
     * 外部实现的执行方法
     *
     * @param task    任务列表
     * @param ownSign 所有者标记
     * @return 执行结果
     */
    protected abstract Result<Void> execute0(ScheduleItem task, String ownSign);

    @Override
    public boolean execute(List<ScheduleItem> taskList, String ownSign) throws Exception {
        for (ScheduleItem task : taskList) {
            Result<Void> result = new Result<>();
            //设置最新执行时间
            task.setLastExecuteTime(System.currentTimeMillis())
                    .setExecuteStatus(computeExecuteStatus(task.getExecuteNumber(), task.getRetryNumber(), task.getLimitRetryNumber(), task.getIsCycle()));
            try {
                log.info(getTaskMessage(task));
                Result<Void> taskExecuteResult = Spider.getDefaultExtension(ScheduleTaskActionExecuteAroundApi.class)
                        .around(task, ownSign, () -> execute0(task, ownSign));
                if (taskExecuteResult == null) {
                    result.setSuccess(Boolean.FALSE);
                    result.setErrorMessage("Execution failed: No return result");
                } else {
                    if (!taskExecuteResult.isSuccess()) {
                        result.setSuccess(Boolean.FALSE);
                        result.setErrorMessage("Execution failed: " + taskExecuteResult.getErrorCode() + "," + taskExecuteResult.getErrorName() + "," + taskExecuteResult.getErrorMessage());
                    }
                }
            } catch (Throwable e) {
                result.setSuccess(Boolean.FALSE);
                String errorMessage = ExceptionUtils.getStackTrace(e);
                result.setErrorMessage(errorMessage);
                log.error(e.getMessage(), e);
            }
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    task.setErrorLog(result.getErrorMessage());
                    if (result.isSuccess()) {
                        task.setExecuteNumber(task.getExecuteNumber() + 1);
                        if (task.getIsCycle() && task.getLimitExecuteNumber() != 1) {
                            replayCycleScheduleTask(task, false);
                        } else {
                            task.setTaskStatus(TaskStatus.FINISHED.intValue());
                        }
                    } else {
                        errorHandlerCallback(task);
                        task.setRetryNumber(task.getRetryNumber() + 1);
                        Integer limitRetryNumber = task.getLimitRetryNumber();
                        Integer retryNumber = task.getRetryNumber();
                        if (task.getIsCycle() && task.getLimitExecuteNumber() != 1) {
                            replayCycleScheduleTask(task, true);
                        } else {
                            boolean isNotRetry = limitRetryNumber == 0 || limitRetryNumber != -1 && retryNumber >= limitRetryNumber;
                            if (isNotRetry) {
                                task.setTaskStatus(TaskStatus.ERROR.intValue());
                            } else {
                                task.setTaskStatus(TaskStatus.WAITING.intValue())
                                        .setNextExecuteTime(DateHelper.computeNextExecuteTime(new Date(task.getLastExecuteTime()), IntValueEnumerationHelper.intValueOf(TimeUnit.class, task.getNextRetryTimeUnit()), task.getNextRetryTimeValue()).getTime());
                            }
                        }
                    }
                    if (scheduleManager.updateTaskStatusById(task) != 1) {
                        String errorMessage = getTaskMessage(task);
                        log.error("Task final update failed: " + errorMessage);
                        result.setFail("Task final update failed: " + errorMessage);
                        status.setRollbackOnly();
                    }
                }
            });
        }
        return true;
    }

    @Override
    public List<List<ScheduleItem>> selectTasks(String taskParameter, String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList, int eachFetchDataNum) throws Exception {
        ScheduleQuery wrapper = new ScheduleQuery();
        //设置分片参数
        Integer[] taskList = new Integer[taskItemList.size()];
        int i = 0;
        for (TaskItemDefine taskDefine : taskItemList) {
            taskList[i] = Integer.valueOf(taskDefine.getTaskItemId());
            i++;
        }
        wrapper.setTaskType(this.getTaskType())
                .setNextExecuteTime(System.currentTimeMillis())
                .setOwnSign(ownSign)
                .setTaskItemNum(taskItemNum)
                .setData(taskList)
                .setOrderBy("next_execute_time")
                .setPageSize(eachFetchDataNum);
        int dayWeek = ScheduleDayWeek.getDayWeek();
        wrapper.setDayWeek(dayWeek);
        int ampm = ScheduleDayWeek.getAmPm();
        wrapper.setAmpm(ampm);
        wrapper.setTableNum(ScheduleTable.getNum(dayWeek, ampm));

        List<ScheduleItem> resultTemp = scheduleManager.selectListForSerial(wrapper);
        if (resultTemp == null || resultTemp.size() == 0) {
            log.debug("wrapper String: {}", wrapper.toString());
            return null;
        }
        Map<Long, List<ScheduleItem>> result = new HashMap<>();
        for (ScheduleItem scheduleItem : resultTemp) {
            Long bizId = scheduleItem.getBizId();
            result.computeIfAbsent(bizId, k -> new ArrayList<>())
                    .add(scheduleItem);
        }
        return new ArrayList<>(result.values());
    }
}
