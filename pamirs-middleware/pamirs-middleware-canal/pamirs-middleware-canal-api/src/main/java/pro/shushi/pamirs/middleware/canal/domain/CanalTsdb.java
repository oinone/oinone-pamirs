package pro.shushi.pamirs.middleware.canal.domain;

import java.io.Serializable;

/**
 * CanalTsdb
 *
 * @author yakir on 2020/05/25 14:36.
 */
public class CanalTsdb implements Serializable {

    private static final long serialVersionUID = 7898032153411168676L;

    private boolean enable;
    private String jdbcUrl;
    private String userName;
    private String password;
    private Integer snapshotInterval = 1; // 小时
    private Integer snapshotExpire = 36000; // 小时

    public boolean isEnable() {
        return enable;
    }

    public CanalTsdb setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public CanalTsdb setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public CanalTsdb setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CanalTsdb setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getSnapshotInterval() {
        return snapshotInterval;
    }

    public CanalTsdb setSnapshotInterval(Integer snapshotInterval) {
        this.snapshotInterval = snapshotInterval;
        return this;
    }

    public Integer getSnapshotExpire() {
        return snapshotExpire;
    }

    public CanalTsdb setSnapshotExpire(Integer snapshotExpire) {
        this.snapshotExpire = snapshotExpire;
        return this;
    }

    @Override
    public String toString() {
        return "CanalTsdb{" +
                "enable=" + enable +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", snapshotInterval=" + snapshotInterval +
                ", snapshotExpire=" + snapshotExpire +
                '}';
    }
}