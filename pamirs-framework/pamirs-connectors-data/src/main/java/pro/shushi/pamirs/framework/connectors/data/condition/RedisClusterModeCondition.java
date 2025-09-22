package pro.shushi.pamirs.framework.connectors.data.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * RedisClusterModeCondition
 *
 * @author yakir on 2023/04/18 16:47.
 */
public class RedisClusterModeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String redisNodes = context.getEnvironment().getProperty("spring.data.redis.cluster.nodes[0]");
        return StringUtils.isNotBlank(redisNodes);
    }
}
