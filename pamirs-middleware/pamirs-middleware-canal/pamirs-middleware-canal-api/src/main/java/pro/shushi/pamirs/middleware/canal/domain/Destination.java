package pro.shushi.pamirs.middleware.canal.domain;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

public class Destination implements Serializable {

    private static final long serialVersionUID = -2199467649001193885L;

    private String name;
    private String desc;
    private String destination;
    private String dbUserName;
    private String dbPassword;
    private String filter = ".*\\..*";
    private Long slaveId;
    private String rdsInstanceId;
    private String rdsAccesskey;
    private String secretkey;
    private List<DBInfo> dbs;
    private int memoryStorageBufferSize = 2 << 14; // 32768
    private int receiveBufferSize = 2 << 12; // 8192
    private int sendBufferSize = 2 << 12; // 8192
    private String topic; // queue ? partition ? 先叫 topic吧
    private Boolean dynamicTopic = true; // 动态topic
    private String schemaPattern; // 动态topic匹配规则，用来获取库名合表名
    private String tablePattern; // 动态topic匹配规则，用来获取库名合表名

    public String getName() {
        if (StringUtils.isBlank(name)) {
            return destination;
        }

        return name;
    }

    public Destination setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        if (StringUtils.isBlank(desc)) {
            return destination;
        }
        return desc;
    }

    public Destination setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public Destination setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public Destination setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
        return this;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public Destination setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }

    public String getFilter() {
        return filter;
    }

    public Destination setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public Long getSlaveId() {
        return slaveId;
    }

    public Destination setSlaveId(Long slaveId) {
        this.slaveId = slaveId;
        return this;
    }

    public List<DBInfo> getDbs() {
        return dbs;
    }

    public Destination setDbs(List<DBInfo> dbs) {
        this.dbs = dbs;
        return this;
    }

    public String getRdsInstanceId() {
        return rdsInstanceId;
    }

    public Destination setRdsInstanceId(String rdsInstanceId) {
        this.rdsInstanceId = rdsInstanceId;
        return this;
    }

    public String getRdsAccesskey() {
        return rdsAccesskey;
    }

    public Destination setRdsAccesskey(String rdsAccesskey) {
        this.rdsAccesskey = rdsAccesskey;
        return this;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public Destination setSecretkey(String secretkey) {
        this.secretkey = secretkey;
        return this;
    }

    public int getMemoryStorageBufferSize() {
        return memoryStorageBufferSize;
    }

    public Destination setMemoryStorageBufferSize(int memoryStorageBufferSize) {
        this.memoryStorageBufferSize = memoryStorageBufferSize;
        return this;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public Destination setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
        return this;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public Destination setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Destination setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Boolean getDynamicTopic() {
        return dynamicTopic;
    }

    public Destination setDynamicTopic(Boolean dynamicTopic) {
        this.dynamicTopic = dynamicTopic;
        return this;
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }

    public Destination setSchemaPattern(String schemaPattern) {
        this.schemaPattern = schemaPattern;
        return this;
    }

    public String getTablePattern() {
        return tablePattern;
    }

    public Destination setTablePattern(String tablePattern) {
        this.tablePattern = tablePattern;
        return this;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", destination='" + destination + '\'' +
                ", dbUserName='" + dbUserName + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", filter='" + filter + '\'' +
                ", slaveId=" + slaveId +
                ", rdsInstanceId='" + rdsInstanceId + '\'' +
                ", rdsAccesskey='" + rdsAccesskey + '\'' +
                ", secretkey='" + secretkey + '\'' +
                ", dbs=" + dbs +
                ", memoryStorageBufferSize=" + memoryStorageBufferSize +
                ", receiveBufferSize=" + receiveBufferSize +
                ", sendBufferSize=" + sendBufferSize +
                ", topic='" + topic + '\'' +
                ", dynamicTopic=" + dynamicTopic +
                ", schemaPattern='" + schemaPattern + '\'' +
                ", tablePattern='" + tablePattern + '\'' +
                '}';
    }
}