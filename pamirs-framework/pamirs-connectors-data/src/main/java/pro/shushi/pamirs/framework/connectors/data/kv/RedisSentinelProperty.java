package pro.shushi.pamirs.framework.connectors.data.kv;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import pro.shushi.pamirs.framework.connectors.data.condition.RedisSentinelModeCondition;

import java.util.Set;

/**
 * Redis Sentinel Supported
 * <p>
 * 参考: {@link RedisSentinelConfiguration}
 *
 * @author shihao
 */
@Configuration
@Conditional(RedisSentinelModeCondition.class)
@ConfigurationProperties(prefix = "spring.data.redis.sentinel")
public class RedisSentinelProperty {
    /*
     * 哨兵监控的主节点名称（必填，对应sentinel的master-name）
     */
    private String master;
    /*
     * 哨兵节点列表（格式：host:port,host:port）
     */
    private Set<String> nodes;

    private String username;

    private String password;

    private DataNode dataNode;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataNode getDataNode() {
        return dataNode;
    }

    public void setDataNode(DataNode dataNode) {
        this.dataNode = dataNode;
    }

    public static class DataNode {

        private String username;

        private String password;

        private int database = 0;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }
    }
}
