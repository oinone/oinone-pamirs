package com.taobao.pamirs.schedule.taskmanager;

interface IScheduleProcessor {

    /**
     * 是否已经处理完内存中所有的数据，在进行队列切换的时候， 必须保证所有内存的数据处理完毕
     */
    boolean isDealFinishAllData();

    /**
     * 判断进程是否处于休眠状态
     */
    boolean isSleeping();

    /**
     * 停止任务处理器
     */
    void stopSchedule() throws Exception;

    /**
     * 清除所有已经取到内存中的数据，在心跳线程失败的时候调用，避免数据重复
     */
    void clearAllHasFetchData();
}
