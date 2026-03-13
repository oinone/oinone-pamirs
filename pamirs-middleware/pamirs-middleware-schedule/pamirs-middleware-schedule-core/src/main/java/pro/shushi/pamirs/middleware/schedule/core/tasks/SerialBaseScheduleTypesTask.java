package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.constant.ErrorCodeConstants;
import pro.shushi.pamirs.middleware.schedule.core.conf.ScheduleSystemInfo;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleDayWeek;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleTable;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 根据bizId分片(多个任务类型)
 */
public class SerialBaseScheduleTypesTask extends AbstractPamirsScheduleTaskDealSingle<List<ScheduleItem>> implements IScheduleTaskDealSingle<List<ScheduleItem>> {

    private static final Logger log = LoggerFactory.getLogger(SerialBaseScheduleTypesTask.class);

    //任务类型
    private Map<String, String> taskTypeMap;

    /**
     * 查询任务项
     *
     * @param taskParameter    任务的自定义参数
     * @param ownSign          当前环境名称
     * @param taskItemNum      当前任务类型的任务队列数量
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return
     * @throws Exception
     */
    @Override
    public List<List<ScheduleItem>> selectTasks(String taskParameter, String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList, int eachFetchDataNum) throws Exception {
        ScheduleQuery query = new ScheduleQuery();
        query.setNextExecuteTime(System.currentTimeMillis());
        Integer[] taskList = new Integer[taskItemList.size()];
        int i = 0;
        for (TaskItemDefine taskDefine : taskItemList) {
            taskList[i] = Integer.valueOf(taskDefine.getTaskItemId());
            i++;
        }
        query.setOwnSign(ScheduleSystemInfo.STATIC_OWN_SIGN_VALUE);
        query.setData(taskList);
        query.setPageSize(eachFetchDataNum);
        query.setOrderBy("next_retry_time");
        query.setTaskItemNum(taskItemNum);
        query.setTaskTypes(this.getTaskTypeMap().keySet().toArray(new String[this.getTaskTypeMap().size()]));
        int dayWeek = ScheduleDayWeek.getDayWeek();
        int ampm = ScheduleDayWeek.getAmPm();
        query.setTableNum(ScheduleTable.getNum(dayWeek, ampm));
        List<ScheduleItem> resultTemp = scheduleManager.selectTypesListForSerial(query);
        if (resultTemp == null || resultTemp.size() == 0) {
            log.debug("query String: {}", query.toString());
            return null;
        }
        Map<Long, List<ScheduleItem>> result = new HashMap<>();
        for (ScheduleItem scheduleItem : resultTemp) {
            Long bizId = scheduleItem.getBizId();
            List<ScheduleItem> scheduleItems = result.get(bizId);
            if (scheduleItems == null) {
                scheduleItems = new ArrayList<ScheduleItem>();
                result.put(bizId, scheduleItems);
            }
            scheduleItems.add(scheduleItem);
        }
        return new ArrayList<>(result.values());
    }

    @Override
    public Comparator<List<ScheduleItem>> getComparator() {
        return null;
    }

    @Override
    public boolean execute(final List<ScheduleItem> tasks, String ownSign) throws Exception {
        for (final ScheduleItem task : tasks) {
            final Result<Void> result = new Result<Void>();
            log.info(task.getBizId() + ":" + task.getTaskType() + ":" + task.getTaskStatus() + ":" + ScheduleSystemInfo.STATIC_OWN_SIGN_VALUE);
            final List<ScheduleAction> taskActions = scheduleTaskActionManager.getTaskAction(task.getTaskType());
            if (taskActions == null || taskActions.size() == 0) {
                log.error("taskActions==null||taskActions.size()==0");
                return false;
            }
            String transaction = this.getTaskTypeMap().get(task.getTaskType());
            if ("transaction".equals(transaction)) {
                try {
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            try {
                                for (ScheduleAction taskAction : taskActions) {
                                    Result<Void> taskExecuteRs = taskAction.execute(task);
                                    //如果错误代码为BKCYCLE88888888 跳出循环
                                    if (!taskExecuteRs.isSuccess() && ErrorCodeConstants.BKCYCLE88888888.getErrorCode() == taskExecuteRs.getErrorCode() && task.getIsCycle().equals(1)) {
                                        result.setSuccess(true);
                                        return;
                                    }
                                    if (!taskExecuteRs.isSuccess()) {
                                        result.setSuccess(false);
                                        result.setErrorMessage(taskAction.getActionName() + " execution failed: " + taskExecuteRs.getErrorCode() + "," + taskExecuteRs.getErrorName() + "," + taskExecuteRs.getErrorMessage());
                                        status.setRollbackOnly();
                                        return;
                                    }
                                }
                                int i = 0;
                                task.setWriteDate(LocalDateTime.now());
                                i = scheduleManager.update2SucessByPrimaryKey(task);
                                if (i != 1) {
                                    result.setSuccess(false);
                                    result.setErrorMessage("最后更新schedule失败");
                                    status.setRollbackOnly();
                                    return;
                                }
                            } catch (Throwable e) {
                                result.setSuccess(false);
                                result.setErrorMessage(ExceptionUtils.getStackTrace(e));
                                log.error(ExceptionUtils.getStackTrace(e));
                                status.setRollbackOnly();
                            }
                        }
                    });
                } catch (Throwable e) {
                    result.setSuccess(false);
                    result.setErrorMessage(ExceptionUtils.getStackTrace(e));
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            } else {
                try {
                    for (ScheduleAction taskAction : taskActions) {
                        Result<Void> taskExecuteRs = taskAction.execute(task);
                        if (!taskExecuteRs.isSuccess()) {
                            result.setSuccess(false);
                            result.setErrorMessage(taskAction.getActionName() + "执行失败: " + taskExecuteRs.getErrorCode() + "," + taskExecuteRs.getErrorName() + "," + taskExecuteRs.getErrorMessage());
                        }
                    }
                    int i = 0;
                    task.setWriteDate(LocalDateTime.now());
                    i = scheduleManager.update2SucessByPrimaryKey(task);
                    if (i != 1) {
                        result.setSuccess(false);
                        result.setErrorMessage("最后更新schedule失败");
                    }
                } catch (Throwable e) {
                    result.setSuccess(false);
                    result.setErrorMessage(ExceptionUtils.getStackTrace(e));
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
            if (!result.isSuccess()) {
                task.setNextExecuteTime(System.currentTimeMillis() + (1000 * 60 * (task.getRetryNumber() + 1)));
                task.setErrorLog(result.getErrorMessage().substring(0, Math.min(result.getErrorMessage().length(), 2048)));
                int i = scheduleManager.update2RetryByPrimaryKey(task);
                if (i != 1) {
                    log.error("Set retry failed: task.bid:" + task.getBizId() + ",task.id:" + task.getId());
                    return true;
                }
            }
        }
        return true;
    }

    public Map<String, String> getTaskTypeMap() {
        return taskTypeMap;
    }

    public void setTaskTypeMap(Map<String, String> taskTypeMap) {
        this.taskTypeMap = taskTypeMap;
    }

}
