package pro.shushi.pamirs.middleware.canal.domain;

import pro.shushi.pamirs.middleware.canal.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Row
 *
 * @author yakir on 2020/05/25 14:26.
 */
public class Row implements Serializable {

    private static final long serialVersionUID = -2778312760876922356L;

    private EventType eventType;
    private String schema;
    private String table;
    private Object id;
    private List<String> pkNames = new ArrayList<>();
    private String sql;
    private List<Column> before;
    private List<Column> after;
    private LocalDateTime changeDate;
    private String env; // 环境
    private String tenant; // 租户
    private String destination;
    private Long executeTime;
    private Long deloyTime;

    public Row addPkName(String pkName) {
        pkNames.add(pkName);
        return this;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Row setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public Row setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getTable() {
        return table;
    }

    public Row setTable(String table) {
        this.table = table;
        return this;
    }

    public String getEnv() {
        return env;
    }

    public Row setEnv(String env) {
        this.env = env;
        return this;
    }

    public String getTenant() {
        return tenant;
    }

    public Row setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public Object getId() {
        return id;
    }

    public Row setId(Object id) {
        this.id = id;
        return this;
    }

    public List<String> getPkNames() {
        return pkNames;
    }

    public String getSql() {
        return sql;
    }

    public Row setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public List<Column> getBefore() {
        return before;
    }

    public Row setBefore(List<Column> before) {
        this.before = before;
        return this;
    }

    public List<Column> getAfter() {
        return after;
    }

    public Row setAfter(List<Column> after) {
        this.after = after;
        return this;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public Row setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
        return this;
    }

    public Row setPkNames(List<String> pkNames) {
        this.pkNames = pkNames;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public Row setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public Row setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
        return this;
    }

    public Long getDeloyTime() {
        return deloyTime;
    }

    public Row setDeloyTime(Long deloyTime) {
        this.deloyTime = deloyTime;
        return this;
    }

    @Override
    public String toString() {
        return "Row{" +
                "eventType=" + eventType +
                ", schema='" + schema + '\'' +
                ", table='" + table + '\'' +
                ", id=" + id +
                ", pkNames=" + pkNames +
                ", sql='" + sql + '\'' +
                ", before=" + before +
                ", after=" + after +
                ", changeDate=" + changeDate +
                ", env='" + env + '\'' +
                ", tenant='" + tenant + '\'' +
                ", destination='" + destination + '\'' +
                ", executeTime=" + executeTime +
                ", deloyTime=" + deloyTime +
                '}';
    }
}
