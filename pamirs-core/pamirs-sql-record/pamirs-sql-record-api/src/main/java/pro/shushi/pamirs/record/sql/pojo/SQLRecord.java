package pro.shushi.pamirs.record.sql.pojo;

import pro.shushi.pamirs.record.sql.enmu.FilterType;

import java.io.Serializable;
import java.util.Date;

/**
 * SQLRecord
 *
 * @author yakir on 2023/06/29 10:06.
 */
public class SQLRecord implements Serializable {

    private static final long serialVersionUID = 6024452668798353110L;

    private FilterType filterType;

    private String model;

    private String schema;

    private String table;

    private String old;

    private String now;

    private String eventType;

    private Date uT; // update time

    private Date cT; // commit time

    private Date wT; // write time

    private String t;

    public FilterType getFilterType() {
        return filterType;
    }

    public SQLRecord setFilterType(FilterType filterType) {
        this.filterType = filterType;
        return this;
    }

    public String getModel() {
        return model;
    }

    public SQLRecord setModel(String model) {
        this.model = model;
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public SQLRecord setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getTable() {
        return table;
    }

    public SQLRecord setTable(String table) {
        this.table = table;
        return this;
    }

    public String getOld() {
        return old;
    }

    public SQLRecord setOld(String old) {
        this.old = old;
        return this;
    }

    public String getNow() {
        return now;
    }

    public SQLRecord setNow(String now) {
        this.now = now;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public SQLRecord setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public Date getuT() {
        return uT;
    }

    public SQLRecord setuT(Date uT) {
        this.uT = uT;
        return this;
    }

    public Date getcT() {
        return cT;
    }

    public SQLRecord setcT(Date cT) {
        this.cT = cT;
        return this;
    }

    public Date getwT() {
        return wT;
    }

    public SQLRecord setwT(Date wT) {
        this.wT = wT;
        return this;
    }

    public String getT() {
        return t;
    }

    public SQLRecord setT(String t) {
        this.t = t;
        return this;
    }

    @Override
    public String toString() {
        return "SQLRecord{" +
                "filterType=" + filterType +
                ", model='" + model + '\'' +
                ", schema='" + schema + '\'' +
                ", table='" + table + '\'' +
                ", old='" + old + '\'' +
                ", now='" + now + '\'' +
                ", eventType='" + eventType + '\'' +
                ", uT=" + uT +
                ", cT=" + cT +
                ", wT=" + wT +
                ", t='" + t + '\'' +
                '}';
    }
}
