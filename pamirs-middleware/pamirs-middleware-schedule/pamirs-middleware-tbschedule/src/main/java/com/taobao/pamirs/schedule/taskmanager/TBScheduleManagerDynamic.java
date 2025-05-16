package com.taobao.pamirs.schedule.taskmanager;

import com.taobao.pamirs.schedule.TaskItemDefine;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import java.util.List;

public class TBScheduleManagerDynamic extends TBScheduleManager {

    TBScheduleManagerDynamic(TBScheduleManagerFactory aFactory, String baseTaskType, String ownSign, int managerPort,
        String jxmUrl, IScheduleDataManager aScheduleCenter)
        throws Exception {
        super(aFactory, baseTaskType, ownSign, aScheduleCenter);
    }

    @Override
    public void initial() throws Exception {
        if (scheduleCenter.isLeader(this.currenScheduleServer.getUuid(),
            scheduleCenter.loadScheduleServerNames(this.currenScheduleServer.getTaskType()))) {
            // 是第一次启动，检查对应的zk目录是否存在
            this.scheduleCenter.initialRunningInfo4Dynamic(this.currenScheduleServer.getBaseTaskType(),
                this.currenScheduleServer.getOwnSign());
        }
        computerStart();
    }

    @Override
    public void refreshScheduleServerInfo() throws Exception {
        throw new Exception("暂未实现");
    }

    public boolean isNeedReLoadTaskItemList() throws Exception {
        throw new Exception("暂未实现");
    }

    @Override
    public void assignScheduleTask() throws Exception {
        throw new Exception("暂未实现");

    }

    @Override
    public List<TaskItemDefine> getCurrentScheduleTaskItemList() {
        throw new RuntimeException("暂未实现");
    }

    @Override
    public int getTaskItemCount() {
        throw new RuntimeException("暂未实现");
    }
}
