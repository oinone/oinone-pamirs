package pro.shushi.pamirs.middleware.canal.entity;

import pro.shushi.pamirs.middleware.canal.domain.DBInfo;

import java.io.Serializable;

/**
 * @author Adamancy Zhang
 * @date 2020-12-24 10:54
 */
public class RefreshFilterEntity implements Serializable {

    private static final long serialVersionUID = -4580949824450034868L;

    private String destination;

    private String oldFilter;

    private String newFilter;

    private DBInfo dbInfo;

    private String mqTopic;

    public String getDestination() {
        return destination;
    }

    public RefreshFilterEntity setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getOldFilter() {
        return oldFilter;
    }

    public RefreshFilterEntity setOldFilter(String oldFilter) {
        this.oldFilter = oldFilter;
        return this;
    }

    public String getNewFilter() {
        return newFilter;
    }

    public RefreshFilterEntity setNewFilter(String newFilter) {
        this.newFilter = newFilter;
        return this;
    }

    public DBInfo getDbInfo() {
        return dbInfo;
    }

    public RefreshFilterEntity setDbInfo(DBInfo dbInfo) {
        this.dbInfo = dbInfo;
        return this;
    }

    public String getMqTopic() {
        return mqTopic;
    }

    public RefreshFilterEntity setMqTopic(String mqTopic) {
        this.mqTopic = mqTopic;
        return this;
    }
}
