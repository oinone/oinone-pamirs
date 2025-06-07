package pro.shushi.pamirs.middleware.schedule.domain;

import pro.shushi.pamirs.middleware.schedule.common.Query;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleQuery extends Query<Integer[]> {

    private static final long serialVersionUID = 1364417822901706084L;

    private Long id;

    private String tenant;              // 租户
    private String env;                 // 环境
    private String model;               // model
    private String group;               // 组
    private Long nextExecuteTime;
    private String application;
    private Long bizId;
    private String ownSign;
    private String taskType;
    private String actionName;
    private String[] taskTypes;
    private Integer taskItemNum;
    private Integer bizStatus;
    private LocalDateTime createDate;
    private LocalDateTime queryDateStart;
    private LocalDateTime queryDateEnd;
    private LocalDateTime preCdate;
    private Integer dayWeek;
    private Integer ampm;
    private Integer tableNum;

    private List<Long> ids;

    public Long getId() {
        return id;
    }

    public ScheduleQuery setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTenant() {
        return tenant;
    }

    public ScheduleQuery setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public String getEnv() {
        return env;
    }

    public ScheduleQuery setEnv(String env) {
        this.env = env;
        return this;
    }

    public String getModel() {
        return model;
    }

    public ScheduleQuery setModel(String model) {
        this.model = model;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public ScheduleQuery setGroup(String group) {
        this.group = group;
        return this;
    }

    public Long getNextExecuteTime() {
        return nextExecuteTime;
    }

    public ScheduleQuery setNextExecuteTime(Long nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public ScheduleQuery setApplication(String application) {
        this.application = application;
        return this;
    }

    public Long getBizId() {
        return bizId;
    }

    public ScheduleQuery setBizId(Long bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getOwnSign() {
        return ownSign;
    }

    public ScheduleQuery setOwnSign(String ownSign) {
        this.ownSign = ownSign;
        return this;
    }

    public String getTaskType() {
        return taskType;
    }

    public ScheduleQuery setTaskType(String taskType) {
        this.taskType = taskType;
        return this;
    }

    public String getActionName() {
        return actionName;
    }

    public ScheduleQuery setActionName(String actionName) {
        this.actionName = actionName;
        return this;
    }

    public String[] getTaskTypes() {
        return taskTypes;
    }

    public ScheduleQuery setTaskTypes(String[] taskTypes) {
        this.taskTypes = taskTypes;
        return this;
    }

    public Integer getTaskItemNum() {
        return taskItemNum;
    }

    public ScheduleQuery setTaskItemNum(Integer taskItemNum) {
        this.taskItemNum = taskItemNum;
        return this;
    }

    public Integer getBizStatus() {
        return bizStatus;
    }

    public ScheduleQuery setBizStatus(Integer bizStatus) {
        this.bizStatus = bizStatus;
        return this;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public ScheduleQuery setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public LocalDateTime getQueryDateStart() {
        return queryDateStart;
    }

    public ScheduleQuery setQueryDateStart(LocalDateTime queryDateStart) {
        this.queryDateStart = queryDateStart;
        return this;
    }

    public LocalDateTime getQueryDateEnd() {
        return queryDateEnd;
    }

    public ScheduleQuery setQueryDateEnd(LocalDateTime queryDateEnd) {
        this.queryDateEnd = queryDateEnd;
        return this;
    }

    public LocalDateTime getPreCdate() {
        return preCdate;
    }

    public ScheduleQuery setPreCdate(LocalDateTime preCdate) {
        this.preCdate = preCdate;
        return this;
    }

    public Integer getDayWeek() {
        return dayWeek;
    }

    public ScheduleQuery setDayWeek(Integer dayWeek) {
        this.dayWeek = dayWeek;
        return this;
    }

    public Integer getAmpm() {
        return ampm;
    }

    public ScheduleQuery setAmpm(Integer ampm) {
        this.ampm = ampm;
        return this;
    }

    public Integer getTableNum() {
        return tableNum;
    }

    public ScheduleQuery setTableNum(Integer tableNum) {
        this.tableNum = tableNum;
        return this;
    }

    public List<Long> getIds() {
        return ids;
    }

    public ScheduleQuery setIds(List<Long> ids) {
        this.ids = ids;
        return this;
    }
}
