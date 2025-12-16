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
@ConfigurationProperties(prefix = "spring.data.redis.cluster")
public class RedisClusterProperty {

    private List<String> nodes;
    private Integer maxRedirects;

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public Integer getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(Integer maxRedirects) {
        this.maxRedirects = maxRedirects;
    }
}
