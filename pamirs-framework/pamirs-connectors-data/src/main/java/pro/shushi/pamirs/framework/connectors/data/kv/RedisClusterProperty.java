package pro.shushi.pamirs.framework.connectors.data.kv;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.connectors.data.condition.RedisClusterModeCondition;

import java.util.List;

/**
 * RedisClusterProperty
 *
 * @author yakir on 2023/04/18 18:05.
 */
@Configuration
@Conditional(RedisClusterModeCondition.class)
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisClusterProperty {

    private List<String> nodes;
    private long timeout;
    private int maxRedirects;

    public List<String> getNodes() {
        return nodes;
    }

    public RedisClusterProperty setNodes(List<String> nodes) {
        this.nodes = nodes;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public RedisClusterProperty setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public RedisClusterProperty setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
        return this;
    }

    @Override
    public String toString() {
        return "RedisClusterProperty{" +
                "nodes=" + nodes +
                ", timeout=" + timeout +
                ", maxRedirects=" + maxRedirects +
                '}';
    }
}
