package pro.shushi.pamirs.framework.connectors.data.kv;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.sentinel")
public class RedisSentinelProperty {
    /** 哨兵监控的主节点名称（必填，对应sentinel的master-name） */
    private String master;
    /** 哨兵节点列表（格式：host:port,host:port） */
    private Set<String> nodes;
    /** 连接超时时间（毫秒） */
    private Long timeout;

}
