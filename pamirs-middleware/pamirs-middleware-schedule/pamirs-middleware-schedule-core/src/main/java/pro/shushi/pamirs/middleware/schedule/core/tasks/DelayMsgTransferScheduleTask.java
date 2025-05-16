package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.core.util.ExceptionHelper;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleDayWeek;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleTable;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


/**
 * 迁移任务(未完成,失败)
 */
public class DelayMsgTransferScheduleTask extends AbstractPamirsScheduleTaskDealSingle<ScheduleItem> implements IScheduleTaskDealSingle<ScheduleItem> {

    private static final Logger log = LoggerFactory.getLogger(DelayMsgTransferScheduleTask.class);

    /**
     * 查询任务项
     *
     * @param taskParameter    任务的自定义参数
     * @param ownSign          当前环境名称
     * @param taskItemNum      当前任务类型的任务队列数量
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return <pre>List<ScheduleItem></pre>
     * @throws Exception exp
     */
    @Override
    public List<ScheduleItem> selectTasks(String taskParameter, String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList, int eachFetchDataNum) throws Exception {
        ScheduleQuery query = new ScheduleQuery();
        query.setNextExecuteTime(System.currentTimeMillis());
        Integer[] taskList = new Integer[taskItemList.size()];
        int       i        = 0;
        for (TaskItemDefine taskDefine : taskItemList) {
            taskList[i] = Integer.valueOf(taskDefine.getTaskItemId());
            i++;
        }
//		query.setOwnSign(CommonScheduleSystemInfo.ownSign);
        query.setData(taskList);
        query.setPageSize(eachFetchDataNum);
        query.setOrderBy("next_execute_time");
        query.setTaskItemNum(taskItemNum);
        int dayWeek       = ScheduleDayWeek.getDayWeek();
        int ampm          = ScheduleDayWeek.getAmPm();
        int nowTableNum   = ScheduleTable.getNum(dayWeek, ampm);
        int delayTableNum = nowTableNum - 1;
        if (delayTableNum == -1) {
            delayTableNum = 13;
        }
        query.setTableNum(delayTableNum);
        List<ScheduleItem> result = scheduleManager.selectDelayList(query);
        log.info("selectTasks delay size :" + result.size());
        return result;
    }

    @Override
    public Comparator<ScheduleItem> getComparator() {
        return null;
    }

    @Override
    public boolean execute(final ScheduleItem task, String ownSign) throws Exception {
        final Result<Void> result  = new Result<>();
        int                dayWeek = ScheduleDayWeek.getDayWeek(task.getCreateDate());
        int                ampm    = ScheduleDayWeek.getAmPm();
        task.setDayWeek(dayWeek);
        task.setAmpm(ampm);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    int i = scheduleManager.update2TransferByPrimaryKey(task);
                    if (i != 1) {
                        result.setSuccess(false);
                        result.setErrorMessage("最后更新schedule失败");
                        status.setRollbackOnly();
                        return;
                    }
                    task.setRetryNumber(0);
                    String remark = task.getRemark() != null ? task.getRemark() + ",迁移过来的id" + task.getId() : "迁移过来的id" + task.getId();
                    if (remark.length() > 1024) {
                        remark = remark.substring(remark.length() - 1024, remark.length());
                    }
                    task.setRemark(remark);
                    task.setCreateDate(LocalDateTime.now());
                    task.setId(null);
                    int dayWeek = ScheduleDayWeek.getDayWeek(task.getCreateDate());
                    int ampm    = ScheduleDayWeek.getAmPm();
                    task.setDayWeek(dayWeek);
                    task.setAmpm(ampm);
                    task.setTableNum(ScheduleTable.getNum(dayWeek, ampm));
                    scheduleManager.insert(task);
                } catch (Throwable e) {
                    if (ExceptionHelper.isDuplicateKeyException(e)) {
                        return;
                    }
                    result.setSuccess(Boolean.FALSE);
                    result.setErrorMessage(ExceptionUtils.getStackTrace(e));
                    log.error(ExceptionUtils.getStackTrace(e));
                    status.setRollbackOnly();
                }
            }
        });
        return true;
    }

}
