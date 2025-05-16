package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleManager;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleDayWeek;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleTable;

import java.util.Comparator;
import java.util.List;


/**
 * 删除历史成功数据任务
 */
public class DeleteTransferScheduleTask extends AbstractPamirsScheduleTaskDealSingle<ScheduleItem> implements IScheduleTaskDealSingle<ScheduleItem> {

    private static final Logger log = LoggerFactory.getLogger(DeleteTransferScheduleTask.class);

    @Autowired
    private ScheduleManager scheduleManager;

    /**
     * 查询任务项
     *
     * @param taskParameter    任务的自定义参数
     * @param ownSign          当前环境名称
     * @param taskItemNum      当前任务类型的任务队列数量
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return <pre>List<ScheduleItem></pre>
     * @throws Exception Exception
     */
    @Override
    public List<ScheduleItem> selectTasks(String taskParameter, String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList, int eachFetchDataNum) throws Exception {
        int dayWeek = ScheduleDayWeek.getDayWeek();
        int ampm = ScheduleDayWeek.getAmPm();
        int nowTableNum = ScheduleTable.getNum(dayWeek, ampm);
        //删除后面一天的数据,需要删除两次
        deleteScheduleData(nowTableNum, 1 + ampm);
        deleteScheduleData(nowTableNum, 2 + ampm);
        log.info("待迁移数据表清空已经完成！");
        return null;
    }

    private void deleteScheduleData(int nowTableNum, int offset) {
        //没数据了删除表数据
        int deleteTableNum = (nowTableNum + offset) % 14;
        String tableNum = deleteTableNum + "";
        scheduleManager.deleteBySeparate(tableNum);
    }

    @Override
    public Comparator<ScheduleItem> getComparator() {
        return null;
    }

    @Override
    public boolean execute(final ScheduleItem task, String ownSign) throws Exception {
        return true;
    }

}
