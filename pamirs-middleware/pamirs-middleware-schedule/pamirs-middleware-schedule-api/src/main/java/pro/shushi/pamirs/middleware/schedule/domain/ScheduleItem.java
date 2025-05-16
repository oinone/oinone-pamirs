package pro.shushi.pamirs.middleware.schedule.domain;

import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ScheduleItem 任务项
 *
 * @author yakir on 2020/06/22 11:06.
 */
public class ScheduleItem implements Serializable {

    private static final long serialVersionUID = 8535836884696337453L;

    //region Task Parameters

    /**
     * 任务Id
     */
    private Long id;

    /**
     * 技术名称
     */
    private String technicalName;

    /**
     * 数据表number
     */
    private Integer tableNum;

    /**
     * 任务类型
     */
    private String taskType;

    //endregion

    //region FunctionDefinition Parameters

    /**
     * model -> FunctionDefinition.interfaceName
     */
    private String interfaceName;

    /**
     * actionName -> FunctionDefinition.methodName
     */
    private String methodName;

    /**
     * version -> FunctionDefinition.version
     */
    private String version;

    /**
     * group -> FunctionDefinition.group
     */
    private String group;

    /**
     * timeout -> FunctionDefinition.timeout
     */
    private Integer timeout;

    //endregion

    //region Environment Parameters

    /**
     * 租户
     */
    private String tenant;

    /**
     * 环境
     */
    private String env;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 所有者标记（隔离环境域）
     */
    private String ownSign;

    /**
     * 应用名
     */
    private String application;

    //endregion

    //region Biz Parameters

    /**
     * 业务id
     */
    private Long bizId;

    /**
     * 父业务id
     */
    private Long parentBizId;

    /**
     * 业务code
     */
    private String bizCode;

    /**
     * 任务上下文
     */
    private String context;

    //endregion

    //region Strategy Parameters

    /**
     * 是否循环任务
     */
    private Boolean isCycle;

    /**
     * 最大执行次数
     */
    private Integer limitExecuteNumber;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 执行周期时间
     * if {@link ScheduleItem#isCycle} is true, it is must gt 0.
     */
    private Integer periodTimeValue;

    /**
     * 执行周期时间单位
     * if {@link ScheduleItem#isCycle} is true, it is required.
     *
     * @see pro.shushi.pamirs.middleware.schedule.eunmeration.TimeUnit
     */
    private Integer periodTimeUnit;

    /**
     * 执行周期时间锚点
     * if {@link ScheduleItem#isCycle} is true, it is required.
     *
     * @see pro.shushi.pamirs.middleware.schedule.eunmeration.TimeAnchor
     */
    private Integer periodTimeAnchor;

    /**
     * 最大重试次数
     */
    private Integer limitRetryNumber;

    /**
     * 下次重试间隔时间
     */
    private Integer nextRetryTimeValue;

    /**
     * 下次重试间隔时间单位
     *
     * @see pro.shushi.pamirs.middleware.schedule.eunmeration.TimeUnit
     */
    private Integer nextRetryTimeUnit;

    //endregion

    //region Control Parameters

    /**
     * 执行次数
     */
    private Integer executeNumber;

    /**
     * 下次执行时间
     */
    private Long nextExecuteTime;

    /**
     * 最新执行时间
     */
    private Long lastExecuteTime;

    /**
     * 锚定执行时间（执行成功后，推算下次执行时间使用的标准时间）
     */
    private Long anchorExecuteTime;

    /**
     * 重试次数
     */
    private Integer retryNumber;

    /**
     * 任务执行状态
     *
     * @see pro.shushi.pamirs.middleware.schedule.eunmeration.TaskExecuteStatus
     */
    private Integer executeStatus;

    /**
     * 任务状态
     *
     * @see pro.shushi.pamirs.middleware.schedule.eunmeration.TaskStatus
     */
    private Integer taskStatus;

    /**
     * 是否已迁移
     */
    private Boolean isTransfer;

    /**
     * 是否已迁移
     */
    private Boolean isCanceled;

    /**
     * 删除标记（时间戳）
     */
    private Integer isDeleted;

    /**
     * 备注
     */
    private String remark;

    /**
     * 异常日志
     */
    private String errorLog;

    //endregion

    //region Basic Parameters

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    private LocalDateTime writeDate;

    //endregion

    //region Extend Parameters (not store)

    /**
     * 查询参数ids for update
     */
    private List<Long> ids;

    /**
     * 判断今日星期几
     */
    private Integer dayWeek;

    /**
     * 判断当前时间上午下午
     */
    private Integer ampm;

    //endregion

    public Long getId() {
        return id;
    }

    public ScheduleItem setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTechnicalName() {
        return technicalName;
    }

    public ScheduleItem setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
        return this;
    }

    public Integer getTableNum() {
        return tableNum;
    }

    public ScheduleItem setTableNum(Integer tableNum) {
        this.tableNum = tableNum;
        return this;
    }

    public String getTaskType() {
        return taskType;
    }

    public ScheduleItem setTaskType(String taskType) {
        this.taskType = taskType;
        return this;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public ScheduleItem setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ScheduleItem setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ScheduleItem setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public ScheduleItem setGroup(String group) {
        this.group = group;
        return this;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public ScheduleItem setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getTenant() {
        return tenant;
    }

    public ScheduleItem setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public String getEnv() {
        return env;
    }

    public ScheduleItem setEnv(String env) {
        this.env = env;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public ScheduleItem setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ScheduleItem setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getOwnSign() {
        return ownSign;
    }

    public ScheduleItem setOwnSign(String ownSign) {
        this.ownSign = ownSign;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public ScheduleItem setApplication(String application) {
        this.application = application;
        return this;
    }

    public Long getBizId() {
        return bizId;
    }

    public ScheduleItem setBizId(Long bizId) {
        this.bizId = bizId;
        return this;
    }

    public Long getParentBizId() {
        return parentBizId;
    }

    public ScheduleItem setParentBizId(Long parentBizId) {
        this.parentBizId = parentBizId;
        return this;
    }

    public String getBizCode() {
        return bizCode;
    }

    public ScheduleItem setBizCode(String bizCode) {
        this.bizCode = bizCode;
        return this;
    }

    public String getContext() {
        return context;
    }

    public ScheduleItem setContext(String context) {
        this.context = context;
        return this;
    }

    public Boolean getIsCycle() {
        return isCycle;
    }

    public ScheduleItem setIsCycle(Boolean isCycle) {
        this.isCycle = isCycle;
        return this;
    }

    public Integer getLimitExecuteNumber() {
        return limitExecuteNumber;
    }

    public ScheduleItem setLimitExecuteNumber(Integer limitExecuteNumber) {
        this.limitExecuteNumber = limitExecuteNumber;
        return this;
    }

    public String getCron() {
        return cron;
    }

    public ScheduleItem setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public Integer getPeriodTimeValue() {
        return periodTimeValue;
    }

    public ScheduleItem setPeriodTimeValue(Integer periodTimeValue) {
        this.periodTimeValue = periodTimeValue;
        return this;
    }

    public Integer getPeriodTimeUnit() {
        return periodTimeUnit;
    }

    public ScheduleItem setPeriodTimeUnit(Integer periodTimeUnit) {
        this.periodTimeUnit = periodTimeUnit;
        return this;
    }

    public Integer getPeriodTimeAnchor() {
        return periodTimeAnchor;
    }

    public ScheduleItem setPeriodTimeAnchor(Integer periodTimeAnchor) {
        this.periodTimeAnchor = periodTimeAnchor;
        return this;
    }

    public Integer getLimitRetryNumber() {
        return limitRetryNumber;
    }

    public ScheduleItem setLimitRetryNumber(Integer limitRetryNumber) {
        this.limitRetryNumber = limitRetryNumber;
        return this;
    }

    public Integer getNextRetryTimeValue() {
        return nextRetryTimeValue;
    }

    public ScheduleItem setNextRetryTimeValue(Integer nextRetryTimeValue) {
        this.nextRetryTimeValue = nextRetryTimeValue;
        return this;
    }

    public Integer getNextRetryTimeUnit() {
        return nextRetryTimeUnit;
    }

    public ScheduleItem setNextRetryTimeUnit(Integer nextRetryTimeUnit) {
        this.nextRetryTimeUnit = nextRetryTimeUnit;
        return this;
    }

    public Integer getExecuteNumber() {
        return executeNumber;
    }

    public ScheduleItem setExecuteNumber(Integer executeNumber) {
        this.executeNumber = executeNumber;
        return this;
    }

    public Long getNextExecuteTime() {
        return nextExecuteTime;
    }

    public ScheduleItem setNextExecuteTime(Long nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
        return this;
    }

    public Long getLastExecuteTime() {
        return lastExecuteTime;
    }

    public ScheduleItem setLastExecuteTime(Long lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
        return this;
    }

    public Long getAnchorExecuteTime() {
        return anchorExecuteTime;
    }

    public ScheduleItem setAnchorExecuteTime(Long anchorExecuteTime) {
        this.anchorExecuteTime = anchorExecuteTime;
        return this;
    }

    public Integer getRetryNumber() {
        return retryNumber;
    }

    public ScheduleItem setRetryNumber(Integer retryNumber) {
        this.retryNumber = retryNumber;
        return this;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public ScheduleItem setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
        return this;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public ScheduleItem setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }

    public Boolean getIsTransfer() {
        return isTransfer;
    }

    public ScheduleItem setIsTransfer(Boolean isTransfer) {
        this.isTransfer = isTransfer;
        return this;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }

    public ScheduleItem setIsCanceled(Boolean isCanceled) {
        this.isCanceled = isCanceled;
        return this;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public ScheduleItem setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public ScheduleItem setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public ScheduleItem setErrorLog(String errorLog) {
        this.errorLog = errorLog;
        return this;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public ScheduleItem setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public LocalDateTime getWriteDate() {
        return writeDate;
    }

    public ScheduleItem setWriteDate(LocalDateTime writeDate) {
        this.writeDate = writeDate;
        return this;
    }

    public List<Long> getIds() {
        return ids;
    }

    public ScheduleItem setIds(List<Long> ids) {
        this.ids = ids;
        return this;
    }

    public Integer getDayWeek() {
        return dayWeek;
    }

    public ScheduleItem setDayWeek(Integer dayWeek) {
        this.dayWeek = dayWeek;
        return this;
    }

    public Integer getAmpm() {
        return ampm;
    }

    public ScheduleItem setAmpm(Integer ampm) {
        this.ampm = ampm;
        return this;
    }

    public ScheduleItem cloneTask() {
        ScheduleItem newCycleTask = new ScheduleItem();
        newCycleTask.setTechnicalName(technicalName);
        newCycleTask.setTaskType(taskType);
        newCycleTask.setInterfaceName(interfaceName);
        newCycleTask.setMethodName(methodName);
        newCycleTask.setVersion(version);
        newCycleTask.setGroup(group);
        newCycleTask.setTimeout(timeout);
        newCycleTask.setTenant(tenant);
        newCycleTask.setEnv(env);
        newCycleTask.setUserId(userId);
        newCycleTask.setUsername(username);
        newCycleTask.setOwnSign(ownSign);
        newCycleTask.setApplication(application);
        newCycleTask.setBizId(bizId);
        newCycleTask.setParentBizId(parentBizId);
        newCycleTask.setBizCode(bizCode);
        newCycleTask.setContext(context);
        newCycleTask.setIsCycle(isCycle);
        newCycleTask.setLimitExecuteNumber(limitExecuteNumber);
        newCycleTask.setPeriodTimeValue(periodTimeValue);
        newCycleTask.setPeriodTimeUnit(periodTimeUnit);
        newCycleTask.setPeriodTimeAnchor(periodTimeAnchor);
        newCycleTask.setLimitRetryNumber(limitRetryNumber);
        newCycleTask.setNextRetryTimeValue(nextRetryTimeValue);
        newCycleTask.setNextRetryTimeUnit(nextRetryTimeUnit);
        newCycleTask.setExecuteNumber(executeNumber);
        newCycleTask.setNextExecuteTime(nextExecuteTime);
        newCycleTask.setLastExecuteTime(lastExecuteTime);
        newCycleTask.setAnchorExecuteTime(anchorExecuteTime);
        newCycleTask.setRetryNumber(retryNumber);
        newCycleTask.setExecuteStatus(executeStatus);
        newCycleTask.setTaskStatus(TaskStatus.WAITING.intValue());
        newCycleTask.setRemark(remark);
        newCycleTask.setErrorLog(errorLog);
        return newCycleTask;
    }
}
