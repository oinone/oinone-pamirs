package com.taobao.pamirs.schedule.taskmanager;

import com.taobao.pamirs.schedule.IScheduleTaskDeal;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务调度器，在TBScheduleManager的管理下实现多线程数据处理
 *
 * @param <T> 修改记录： 为了简化处理逻辑，去处版本概率，增加可能重复的数据列表 by 扶苏 20110310
 * @author xuannan
 */
class TBScheduleProcessorNotSleep<T> implements IScheduleProcessor, Runnable {

    private static transient Logger logger = LoggerFactory.getLogger(TBScheduleProcessorNotSleep.class);

    List<Thread> threadList = new CopyOnWriteArrayList<>();
    /**
     * 任务管理器
     */
    protected TBScheduleManager scheduleManager;
    /**
     * 任务类型
     */
    ScheduleTaskType taskTypeInfo;

    /**
     * 任务处理的接口类
     */
    protected IScheduleTaskDeal<T> taskDealBean;

    /**
     * 任务比较器
     */
    Comparator<T> taskComparator;

    StatisticsInfo statisticsInfo;

    protected List<T> taskList = new CopyOnWriteArrayList<T>();
    /**
     * 正在处理中的任务队列
     */
    protected List<Object> runningTaskList = new CopyOnWriteArrayList<>();
    /**
     * 在重新取数据，可能会重复的数据。在重新去数据前，从runningTaskList拷贝得来
     */
    protected List<T> maybeRepeatTaskList = new CopyOnWriteArrayList<T>();

    Lock lockFetchID = new ReentrantLock();
    Lock lockFetchMutilID = new ReentrantLock();
    Lock lockLoadData = new ReentrantLock();
    /**
     * 是否可以批处理
     */
    boolean isMutilTask = false;

    /**
     * 是否已经获得终止调度信号: 用户停止队列调度
     */
    boolean isStopSchedule = false;
    boolean isSleeping = false;

    /**
     * 创建一个调度处理器
     */
    public TBScheduleProcessorNotSleep(TBScheduleManager aManager, IScheduleTaskDeal<T> aTaskDealBean,
        StatisticsInfo aStatisticsInfo) {
        this.scheduleManager = aManager;
        this.statisticsInfo = aStatisticsInfo;
        this.taskTypeInfo = this.scheduleManager.getTaskTypeInfo();
        this.taskDealBean = aTaskDealBean;
        this.taskComparator = new MYComparator(this.taskDealBean.getComparator());
        if (this.taskDealBean instanceof IScheduleTaskDealSingle<?>) {
            if (taskTypeInfo.getExecuteNumber() > 1) {
                taskTypeInfo.setExecuteNumber(1);
            }
            isMutilTask = false;
        } else {
            isMutilTask = true;
        }
        if (taskTypeInfo.getFetchDataNumber() < taskTypeInfo.getThreadNumber() * 10) {
            logger.warn("参数设置不合理，系统性能不佳。【每次从数据库获取的数量fetchnum】 >= 【线程数量threadnum】 *【最少循环次数10】 ");
        }
        for (int i = 0; i < taskTypeInfo.getThreadNumber(); i++) {
            this.startThread(i);
        }
    }

    /**
     * 需要注意的是，调度服务器从配置中心注销的工作，必须在所有线程退出的情况下才能做
     */
    @Override
    public void stopSchedule() {
        // 设置停止调度的标志,调度线程发现这个标志，执行完当前任务后，就退出调度
        this.isStopSchedule = true;
        // 清除所有未处理任务,但已经进入处理队列的，需要处理完毕
        this.taskList.clear();
    }

    private void startThread(int index) {
        Thread thread = new Thread(this);
        threadList.add(thread);
        String threadName =
            this.scheduleManager.getScheduleServer().getTaskType() + "-" + this.scheduleManager.getCurrentSerialNumber()
                + "-exe" + index;
        thread.setName(threadName);
        thread.start();
    }

    @SuppressWarnings("unchecked")
    protected boolean isDealing(T aTask) {
        if (this.maybeRepeatTaskList.size() == 0) {
            return false;
        }
        T[] tmpList = (T[]) this.maybeRepeatTaskList.toArray();
        for (int i = 0; i < tmpList.length; i++) {
            if (this.taskComparator.compare(aTask, tmpList[i]) == 0) {
                this.maybeRepeatTaskList.remove(tmpList[i]);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取单个任务，注意lock是必须， 否则在maybeRepeatTaskList的数据处理上会出现冲突
     */
    public T getScheduleTaskId() {
        lockFetchID.lock();
        try {
            T result = null;
            while (true) {
                if (this.taskList.size() > 0) {
                    // 按正序处理
                    result = this.taskList.remove(0);
                } else {
                    return null;
                }
                if (this.isDealing(result) == false) {
                    return result;
                }
            }
        } finally {
            lockFetchID.unlock();
        }
    }

    /**
     * 获取单个任务，注意lock是必须， 否则在maybeRepeatTaskList的数据处理上会出现冲突
     */
    @SuppressWarnings("unchecked")
    public T[] getScheduleTaskIdMulti() {
        lockFetchMutilID.lock();
        try {
            if (this.taskList.size() == 0) {
                return null;
            }
            int size =
                taskList.size() > taskTypeInfo.getExecuteNumber() ? taskTypeInfo.getExecuteNumber() : taskList.size();

            List<T> result = new ArrayList<T>();
            int point = 0;
            T tmpObject = null;
            while (point < size && ((tmpObject = this.getScheduleTaskId()) != null)) {
                result.add(tmpObject);
                point = point + 1;
            }
            if (result.size() == 0) {
                return null;
            } else {
                return result.toArray((T[]) Array.newInstance(result.get(0).getClass(), 0));
            }
        } finally {
            lockFetchMutilID.unlock();
        }
    }

    @Override
    public void clearAllHasFetchData() {
        this.taskList.clear();
    }

    @Override
    public boolean isDealFinishAllData() {
        return this.taskList.size() == 0 && this.runningTaskList.size() == 0;
    }

    @Override
    public boolean isSleeping() {
        return this.isSleeping;
    }

    /**
     * 装载数据
     */
    protected int loadScheduleData() {
        lockLoadData.lock();
        try {
            // 判断是否有别的线程已经装载过了
            if (this.taskList.size() > 0 || this.isStopSchedule == true) {
                return this.taskList.size();
            }
            // 在每次数据处理完毕后休眠固定的时间
            try {
                if (this.taskTypeInfo.getSleepTimeInterval() > 0) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("处理完一批数据后休眠：" + this.taskTypeInfo.getSleepTimeInterval());
                    }
                    this.isSleeping = true;
                    Thread.sleep(taskTypeInfo.getSleepTimeInterval());
                    this.isSleeping = false;

                    if (logger.isTraceEnabled()) {
                        logger.trace("处理完一批数据后休眠后恢复");
                    }
                }
            } catch (Throwable ex) {
                logger.error("休眠时错误", ex);
            }

            putLastRunningTaskList();// 将running队列的数据拷贝到可能重复的队列中

            try {
                List<TaskItemDefine> taskItems = this.scheduleManager.getCurrentScheduleTaskItemList();
                // 根据队列信息查询需要调度的数据，然后增加到任务列表中
                if (taskItems.size() > 0) {
                    List<TaskItemDefine> tmpTaskList = new ArrayList<TaskItemDefine>();
                    synchronized (taskItems) {
                        for (TaskItemDefine taskItemDefine : taskItems) {
                            tmpTaskList.add(taskItemDefine);
                        }
                    }
                    List<T> tmpList = this.taskDealBean
                        .selectTasks(taskTypeInfo.getTaskParameter(), scheduleManager.getScheduleServer().getOwnSign(),
                            this.scheduleManager.getTaskItemCount(), tmpTaskList, taskTypeInfo.getFetchDataNumber());
                    scheduleManager.getScheduleServer()
                        .setLastFetchDataTime(new Timestamp(scheduleManager.scheduleCenter.getSystemTime()));
                    if (tmpList != null) {
                        this.taskList.addAll(tmpList);
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("没有任务分配");
                    }
                }
                addFetchNum(taskList.size());
                if (taskList.size() <= 0) {
                    // 判断当没有数据的是否，是否需要退出调度
                    if (this.scheduleManager.isContinueWhenData() == true) {
                        if (taskTypeInfo.getSleepTimeNoData() > 0) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("没有读取到需要处理的数据,sleep " + taskTypeInfo.getSleepTimeNoData());
                            }
                            this.isSleeping = true;
                            Thread.sleep(taskTypeInfo.getSleepTimeNoData());
                            this.isSleeping = false;
                        }
                    }
                }
                return this.taskList.size();
            } catch (Throwable ex) {
                logger.error("获取任务数据错误", ex);
            }
            return 0;
        } finally {
            lockLoadData.unlock();
        }
    }

    /**
     * 将running队列的数据拷贝到可能重复的队列中
     */
    @SuppressWarnings("unchecked")
    public void putLastRunningTaskList() {
        lockFetchID.lock();
        try {
            this.maybeRepeatTaskList.clear();
            if (this.runningTaskList.size() == 0) {
                return;
            }
            Object[] tmpList = this.runningTaskList.toArray();
            for (int i = 0; i < tmpList.length; i++) {
                if (this.isMutilTask == false) {
                    this.maybeRepeatTaskList.add((T) tmpList[i]);
                } else {
                    T[] aTasks = (T[]) tmpList[i];
                    for (int j = 0; j < aTasks.length; j++) {
                        this.maybeRepeatTaskList.add(aTasks[j]);
                    }
                }
            }
        } finally {
            lockFetchID.unlock();
        }
    }

    /**
     * 运行函数
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        long startTime = 0;
        long sequence = 0;
        Object executeTask = null;
        AtomicInteger fetchDataNum = new AtomicInteger(0);
        while (true) {
            try {
                // 停止队列调度
                if (this.isStopSchedule == true) {
                    synchronized (this.threadList) {
                        this.threadList.remove(Thread.currentThread());
                        if (this.threadList.size() == 0) {
                            this.scheduleManager.unRegisterScheduleServer();
                        }
                    }
                    return;
                }
                // 加载调度任务
                if (this.isMutilTask == false) {
                    executeTask = this.getScheduleTaskId();
                } else {
                    executeTask = this.getScheduleTaskIdMulti();
                }

                if (executeTask == null) {
                    if (fetchDataNum.intValue() >= this.taskTypeInfo.getFetchDataCountEachSchedule()
                            && this.taskTypeInfo.getFetchDataCountEachSchedule() != -1) {
                        this.scheduleManager.pause("达到调度次数上限，暂停调度");
                        if (logger.isTraceEnabled()) {
                            logger.trace("达到执行次数上限{}，暂停调度", this.taskTypeInfo.getFetchDataCountEachSchedule());
                        }
                    } else {
                        this.loadScheduleData();
                        fetchDataNum.addAndGet(1);
                    }
                    continue;
                }

                try { // 运行相关的程序
                    this.runningTaskList.add(executeTask);
                    startTime = scheduleManager.scheduleCenter.getSystemTime();
                    sequence = sequence + 1;
                    if (this.isMutilTask == false) {
                        boolean executeSuccessful = ((IScheduleTaskDealSingle<Object>) this.taskDealBean)
                            .execute(executeTask, scheduleManager.getScheduleServer().getOwnSign());
                        if (executeSuccessful) {
                            addSuccessNum(1, scheduleManager.scheduleCenter.getSystemTime() - startTime);
                        } else {
                            addFailNum(1, scheduleManager.scheduleCenter.getSystemTime() - startTime);
                        }
                    } else {
                        boolean executeSuccessful = ((IScheduleTaskDealMulti<Object>) this.taskDealBean)
                            .execute((Object[]) executeTask, scheduleManager.getScheduleServer().getOwnSign());
                        if (executeSuccessful) {
                            addSuccessNum(((Object[]) executeTask).length,
                                scheduleManager.scheduleCenter.getSystemTime() - startTime);
                        } else {
                            addFailNum(((Object[]) executeTask).length,
                                scheduleManager.scheduleCenter.getSystemTime() - startTime);
                        }
                    }
                } catch (Throwable ex) {
                    if (this.isMutilTask == false) {
                        addFailNum(1, scheduleManager.scheduleCenter.getSystemTime() - startTime);
                    } else {
                        addFailNum(((Object[]) executeTask).length,
                            scheduleManager.scheduleCenter.getSystemTime() - startTime);
                    }
                    logger.error("Task :" + executeTask + " 处理失败", ex);
                } finally {
                    this.runningTaskList.remove(executeTask);
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addFetchNum(long num) {
        this.statisticsInfo.addFetchDataCount(1);
        this.statisticsInfo.addFetchDataNum(num);
    }

    public void addSuccessNum(long num, long spendTime) {
        this.statisticsInfo.addDealDataSucess(num);
        this.statisticsInfo.addDealSpendTime(spendTime);
    }

    public void addFailNum(long num, long spendTime) {
        this.statisticsInfo.addDealDataFail(num);
        this.statisticsInfo.addDealSpendTime(spendTime);
    }

    class MYComparator implements Comparator<T> {

        Comparator<T> comparator;

        public MYComparator(Comparator<T> aComparator) {
            this.comparator = aComparator;
        }

        @Override
        public int compare(T o1, T o2) {
            statisticsInfo.addOtherCompareCount(1);
            return this.comparator.compare(o1, o2);
        }

        @Override
        public boolean equals(Object obj) {
            return this.comparator.equals(obj);
        }
    }

}
