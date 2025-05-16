package com.taobao.pamirs.schedule.taskmanager;

/**
 * 任务队列类型
 *
 * @author xuannan
 */
public class ScheduleTaskItem {

    public enum TaskItemSts {
        ACTIVTE, FINISH, HALT
    }

    /**
     * 处理任务类型
     */
    private String taskType;

    /**
     * 原始任务类型
     */
    private String baseTaskType;

    /**
     * 完成状态
     */
    private TaskItemSts sts = TaskItemSts.ACTIVTE;

    /**
     * 任务处理需要的参数
     */
    private String dealParameter = "";

    /**
     * 任务处理情况,用于任务处理器会写一些信息
     */
    private String dealDesc = "";

    public String getBaseTaskType() {
        return baseTaskType;
    }

    public void setBaseTaskType(String baseTaskType) {
        this.baseTaskType = baseTaskType;
    }

    /**
     * 队列的环境标识
     */
    private String ownSign;

    /**
     * 任务队列ID
     */
    private String taskItem;
    /**
     * 持有当前任务队列的任务处理器
     */
    private String currentScheduleServer;
    /**
     * 正在申请此任务队列的任务处理器
     */
    private String requestScheduleServer;

    /**
     * 数据版本号
     */
    private long version;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskItem() {
        return taskItem;
    }

    public void setTaskItem(String aTaskItem) {
        this.taskItem = aTaskItem;
    }

    public String getCurrentScheduleServer() {
        return currentScheduleServer;
    }

    public void setCurrentScheduleServer(String currentScheduleServer) {
        this.currentScheduleServer = currentScheduleServer;
    }

    public String getRequestScheduleServer() {
        return requestScheduleServer;
    }

    public void setRequestScheduleServer(String requestScheduleServer) {
        this.requestScheduleServer = requestScheduleServer;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getOwnSign() {
        return ownSign;
    }

    public void setOwnSign(String ownSign) {
        this.ownSign = ownSign;
    }

    public String toString() {
        return "TASK_TYPE=" + this.taskType + ":TASK_ITEM=" + this.taskItem + ":CUR_SERVER="
            + this.currentScheduleServer + ":REQ_SERVER=" + this.requestScheduleServer
            + ":DEAL_PARAMETER=" + this.dealParameter;
    }

    public void setDealDesc(String dealDesc) {
        this.dealDesc = dealDesc;
    }

    public String getDealDesc() {
        return dealDesc;
    }

    public void setSts(TaskItemSts sts) {
        this.sts = sts;
    }

    public TaskItemSts getSts() {
        return sts;
    }

    public void setDealParameter(String dealParameter) {
        this.dealParameter = dealParameter;
    }

    public String getDealParameter() {
        return dealParameter;
    }

}
