package com.taobao.pamirs.schedule.taskmanager;

import com.taobao.pamirs.schedule.TaskItemDefine;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import java.util.List;
import java.util.Map;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TBScheduleManagerStatic extends TBScheduleManager {

    private static transient Logger log = LoggerFactory.getLogger(TBScheduleManagerStatic.class);
    /**
     * 总的任务数量
     */
    protected int taskItemCount = 0;

    protected long lastFetchVersion = -1;

    private final Object NeedReloadTaskItemLock = new Object();

    public TBScheduleManagerStatic(TBScheduleManagerFactory aFactory, String baseTaskType, String ownSign,
        IScheduleDataManager aScheduleCenter) throws Exception {
        super(aFactory, baseTaskType, ownSign, aScheduleCenter);
    }

    public void initialRunningInfo() throws Exception {
        scheduleCenter.clearExpireScheduleServer(this.currenScheduleServer.getTaskType(),
            this.taskTypeInfo.getJudgeDeadInterval());
        List<String> list = scheduleCenter.loadScheduleServerNames(this.currenScheduleServer.getTaskType());
        if (scheduleCenter.isLeader(this.currenScheduleServer.getUuid(), list)) {
            // 是第一次启动，先清楚所有的垃圾数据
            log.debug(this.currenScheduleServer.getUuid() + ":" + list.size());
            this.scheduleCenter.initialRunningInfo4Static(this.currenScheduleServer.getBaseTaskType(),
                this.currenScheduleServer.getOwnSign(), this.currenScheduleServer.getUuid());
        }
    }

    @Override
    public void initial() {
        new Thread(this.currenScheduleServer.getTaskType() + "-" + this.currentSerialNumber + "-StartProcess") {
            @Override
            @SuppressWarnings("static-access")
            public void run() {
                try {
                    log.info("开始获取调度任务队列...... of " + currenScheduleServer.getUuid());
                    while (isRuntimeInfoInitial == false) {
                        if (isStopSchedule == true) {
                            log.debug("外部命令终止调度,退出调度队列获取：" + currenScheduleServer.getUuid());
                            return;
                        }
                        try {
                            initialRunningInfo();
                            isRuntimeInfoInitial = scheduleCenter
                                .isInitialRunningInfoSucuss(currenScheduleServer.getBaseTaskType(),
                                    currenScheduleServer.getOwnSign());
                        } catch (Throwable e) {
                            // 忽略初始化的异常
                            log.error(e.getMessage(), e);
                        }
                        if (isRuntimeInfoInitial == false) {
                            Thread.sleep(1000);
                        }
                    }
                    int count = 0;
                    lastReloadTaskItemListTime = scheduleCenter.getSystemTime();
                    while (getCurrentScheduleTaskItemListNow().size() <= 0) {
                        if (isStopSchedule == true) {
                            log.debug("外部命令终止调度,退出调度队列获取：" + currenScheduleServer.getUuid());
                            return;
                        }
                        Thread.sleep(1000);
                        count = count + 1;
                    }
                    String tmpStr = "TaskItemDefine:";
                    for (int i = 0; i < currentTaskItemList.size(); i++) {
                        if (i > 0) {
                            tmpStr = tmpStr + ",";
                        }
                        tmpStr = tmpStr + currentTaskItemList.get(i);
                    }
                    log.info("获取到任务处理队列，开始调度：" + tmpStr + "  of  " + currenScheduleServer.getUuid());

                    // 任务总量
                    taskItemCount = scheduleCenter.queryTaskItemCount(currenScheduleServer.getTaskType());
                    // 只有在已经获取到任务处理队列后才开始启动任务处理器
                    computerStart();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    String str = e.getMessage();
                    if (str.length() > 300) {
                        str = str.substring(0, 300);
                    }
                    startErrorInfo = "启动处理异常：" + str;
                }
            }
        }.start();
    }

    /**
     * 定时向数据配置中心更新当前服务器的心跳信息。 如果发现本次更新的时间如果已经超过了，
     * 服务器死亡的心跳周期，则不能在向服务器更新信息。 而应该当作新的服务器，进行重新注册。
     */
    @Override
    public void refreshScheduleServerInfo() throws Exception {
        try {
            rewriteScheduleInfo();
            // 如果任务信息没有初始化成功，不做任务相关的处理
            if (this.isRuntimeInfoInitial == false) {
                return;
            }

            // 重新分配任务
            this.assignScheduleTask();

            // 判断是否需要重新加载任务队列，避免任务处理进程不必要的检查和等待
            boolean tmpBoolean = this.isNeedReLoadTaskItemList();
            if (tmpBoolean != this.isNeedReloadTaskItem) {
                // 只要不相同，就设置需要重新装载，因为在心跳异常的时候，做了清理队列的事情，恢复后需要重新装载。
                synchronized (NeedReloadTaskItemLock) {
                    this.isNeedReloadTaskItem = true;
                }
                rewriteScheduleInfo();
            }

            if (this.isPauseSchedule == true || this.processor != null && processor.isSleeping() == true) {
                // 如果服务已经暂停了，则需要重新定时更新 cur_server 和 req_server
                // 如果服务没有暂停，一定不能调用的
                this.getCurrentScheduleTaskItemListNow();
            }
        } catch (Throwable e) {
            // 清除内存中所有的已经取得的数据和任务队列,避免心跳线程失败时候导致的数据重复
            this.clearMemoInfo();
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e.getMessage(), e);
            }
        }
    }

    /**
     * 在leader重新分配任务，在每个server释放原来占有的任务项时，都会修改这个版本号
     */
    public boolean isNeedReLoadTaskItemList() throws Exception {
        return this.lastFetchVersion < this.scheduleCenter
            .getReloadTaskItemFlag(this.currenScheduleServer.getTaskType());
    }

    /**
     * 判断某个任务对应的线程组是否处于僵尸状态。 true 表示有线程组处于僵尸状态。需要告警。
     */
    private boolean isExistZombieServ(String type, Map<String, Stat> statMap) {
        boolean exist = false;
        for (String key : statMap.keySet()) {
            Stat s = statMap.get(key);
            if (this.scheduleCenter.getSystemTime() - s.getMtime() > this.taskTypeInfo.getHeartBeatRate() * 40) {
                log.error("zombie serverList exists! serv=" + key + " ,type=" + type + "超过40次心跳周期未更新");
                exist = true;
            }
        }
        return exist;

    }

    /**
     * 根据当前调度服务器的信息，重新计算分配所有的调度任务 任务的分配是需要加锁，避免数据分配错误。
     * 为了避免数据锁带来的负面作用，通过版本号来达到锁的目的
     * <p>
     * 1、获取任务状态的版本号
     * 2、获取所有的服务器注册信息和任务队列信息
     * 3、清除已经超过心跳周期的服务器注册信息
     * 4、重新计算任务分配
     * 5、更新任务状态的版本号【乐观锁】
     * 6、根系任务队列的分配信息
     * </p>
     */
    @Override
    public void assignScheduleTask() throws Exception {
        scheduleCenter.clearExpireScheduleServer(this.currenScheduleServer.getTaskType(),
            this.taskTypeInfo.getJudgeDeadInterval());
        List<String> serverList = scheduleCenter.loadScheduleServerNames(this.currenScheduleServer.getTaskType());

        if (scheduleCenter.isLeader(this.currenScheduleServer.getUuid(), serverList) == false) {
            if (log.isDebugEnabled()) {
                log.debug(this.currenScheduleServer.getUuid() + ":不是负责任务分配的Leader,直接返回");
            }
            return;
        }
        // 设置初始化成功标准，避免在leader转换的时候，新增的线程组初始化失败
        scheduleCenter.setInitialRunningInfoSucuss(this.currenScheduleServer.getBaseTaskType(),
            this.currenScheduleServer.getTaskType(), this.currenScheduleServer.getUuid());
        scheduleCenter.clearTaskItem(this.currenScheduleServer.getTaskType(), serverList);
        scheduleCenter
            .assignTaskItem(this.currenScheduleServer.getTaskType(), this.currenScheduleServer.getUuid(),
                this.taskTypeInfo.getMaxTaskItemsOfOneThreadGroup(), serverList);
    }

    /**
     * 重新加载当前服务器的任务队列 1、释放当前服务器持有，但有其它服务器进行申请的任务队列 2、重新获取当前服务器的处理队列
     * <p>
     * 为了避免此操作的过度，阻塞真正的数据处理能力。系统设置一个重新装载的频率。例如1分钟
     * <p>
     * 特别注意： 此方法的调用必须是在当前所有任务都处理完毕后才能调用，否则是否任务队列后可能数据被重复处理
     */
    @Override
    public List<TaskItemDefine> getCurrentScheduleTaskItemList() {
        try {
            if (this.isNeedReloadTaskItem == true) {
                // 特别注意：需要判断数据队列是否已经空了，否则可能在队列切换的时候导致数据重复处理
                // 主要是在线程不休眠就加载数据的时候一定需要这个判断
                if (this.processor != null) {
                    while (this.processor.isDealFinishAllData() == false) {
                        Thread.sleep(50);
                    }
                }
                // 真正开始处理数据
                synchronized (NeedReloadTaskItemLock) {
                    this.getCurrentScheduleTaskItemListNow();
                    this.isNeedReloadTaskItem = false;
                }
            }
            this.lastReloadTaskItemListTime = this.scheduleCenter.getSystemTime();
            return this.currentTaskItemList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 由于上面在数据执行时有使用到synchronized ，但是心跳线程并没有对应加锁。
     * 所以在此方法上加一下synchronized。
     */
    protected synchronized List<TaskItemDefine> getCurrentScheduleTaskItemListNow() throws Exception {
        // 如果已经稳定了，理论上不需要加载去扫描所有的叶子结点
        // 20151019 by kongxuan.zlj
        try {
            Map<String, Stat> statMap = this.scheduleCenter
                .getCurrentServerStatList(this.currenScheduleServer.getTaskType());
            // server下面的机器节点的运行时环境是否在刷新，如果
            isExistZombieServ(this.currenScheduleServer.getTaskType(), statMap);
        } catch (Exception e) {
            log.error("zombie serverList exists， Exception:", e);
        }

        // 获取最新的版本号
        this.lastFetchVersion = this.scheduleCenter.getReloadTaskItemFlag(this.currenScheduleServer.getTaskType());
        log.debug(
            " this.currenScheduleServer.getTaskType()=" + this.currenScheduleServer.getTaskType() + ",  need reload="
                + isNeedReloadTaskItem);
        try {
            // 是否被人申请的队列
            this.scheduleCenter
                .releaseDealTaskItem(this.currenScheduleServer.getTaskType(), this.currenScheduleServer.getUuid());
            // 重新查询当前服务器能够处理的队列
            // 为了避免在休眠切换的过程中出现队列瞬间的不一致，先清除内存中的队列
            this.currentTaskItemList.clear();
            this.currentTaskItemList = this.scheduleCenter
                .reloadDealTaskItem(this.currenScheduleServer.getTaskType(), this.currenScheduleServer.getUuid());

            // 如果超过20个心跳周期还没有获取到调度队列，则报警
            if (this.currentTaskItemList.size() == 0 && scheduleCenter.getSystemTime() - this.lastReloadTaskItemListTime
                > this.taskTypeInfo.getHeartBeatRate() * 20) {
                StringBuffer buf = new StringBuffer();
                buf.append("调度服务器");
                buf.append(this.currenScheduleServer.getUuid());
                buf.append("[TASK_TYPE=");
                buf.append(this.currenScheduleServer.getTaskType());
                buf.append("]自启动以来，超过20个心跳周期，还没有获取到分配的任务队列;");
                buf.append("  currentTaskItemList.size() =" + currentTaskItemList.size());
                buf.append(" ,scheduleCenter.getSystemTime()=" + scheduleCenter.getSystemTime());
                buf.append(" ,lastReloadTaskItemListTime=" + lastReloadTaskItemListTime);
                buf.append(" ,taskTypeInfo.getHeartBeatRate()=" + taskTypeInfo.getHeartBeatRate() * 10);
                log.warn(buf.toString());
            }

            if (this.currentTaskItemList.size() > 0) {
                // 更新时间戳
                this.lastReloadTaskItemListTime = scheduleCenter.getSystemTime();
            }

            return this.currentTaskItemList;
        } catch (Throwable e) {
            // 必须把把版本号设置小，避免任务加载失败
            this.lastFetchVersion = -1;
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e);
            }
        }
    }

    @Override
    public int getTaskItemCount() {
        return this.taskItemCount;
    }

}
