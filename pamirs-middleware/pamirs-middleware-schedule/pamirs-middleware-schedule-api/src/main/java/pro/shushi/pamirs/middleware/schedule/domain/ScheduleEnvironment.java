package pro.shushi.pamirs.middleware.schedule.domain;

import pro.shushi.pamirs.middleware.schedule.util.ScheduleDayWeek;
import pro.shushi.pamirs.middleware.schedule.util.ScheduleTable;

/**
 * @author Adamancy Zhang on 2021-04-27 12:35
 */
public class ScheduleEnvironment {

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
     * 用户名
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

    /**
     * 数据表number
     */
    private Integer tableNum;

    /**
     * 判断今日星期几
     */
    private Integer dayWeek;

    /**
     * 判断当前时间上午下午
     */
    private Integer ampm;

    public String getTenant() {
        return tenant;
    }

    public ScheduleEnvironment setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public String getEnv() {
        return env;
    }

    public ScheduleEnvironment setEnv(String env) {
        this.env = env;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public ScheduleEnvironment setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ScheduleEnvironment setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getOwnSign() {
        return ownSign;
    }

    public ScheduleEnvironment setOwnSign(String ownSign) {
        this.ownSign = ownSign;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public ScheduleEnvironment setApplication(String application) {
        this.application = application;
        return this;
    }

    public Integer getTableNum() {
        return tableNum;
    }

    public ScheduleEnvironment setTableNum(Integer tableNum) {
        this.tableNum = tableNum;
        return this;
    }

    public Integer getDayWeek() {
        return dayWeek;
    }

    public ScheduleEnvironment setDayWeek(Integer dayWeek) {
        this.dayWeek = dayWeek;
        return this;
    }

    public Integer getAmpm() {
        return ampm;
    }

    public ScheduleEnvironment setAmpm(Integer ampm) {
        this.ampm = ampm;
        return this;
    }

    public void init() {
        int dayWeek = ScheduleDayWeek.getDayWeek();
        this.setDayWeek(dayWeek);
        int ampm = ScheduleDayWeek.getAmPm();
        this.setAmpm(ampm);
        this.setTableNum(ScheduleTable.getNum(dayWeek, ampm));
    }

    public void transferFrom(ScheduleItem task) {
        this.setTenant(task.getTenant())
                .setEnv(task.getEnv())
                .setUserId(task.getUserId())
                .setUsername(task.getUsername())
                .setOwnSign(task.getOwnSign())
                .setApplication(task.getApplication())
                .setDayWeek(task.getDayWeek())
                .setAmpm(task.getAmpm())
                .setTableNum(task.getTableNum());
    }

    public void transferTo(ScheduleItem task) {
        task.setTenant(tenant)
                .setEnv(env)
                .setOwnSign(ownSign)
                .setApplication(application)
                .setDayWeek(dayWeek)
                .setAmpm(ampm)
                .setTableNum(tableNum);
        if (task.getUserId() == null) {
            task.setUserId(userId);
        }
        if (task.getUsername() == null) {
            task.setUsername(username);
        }
    }
}
