package pro.shushi.pamirs.framework.connectors.data.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * RedisSimpleModeCondition
 *
 * @author yakir on 2023/04/18 16:47.
 */
public class RedisSimpleModeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String redisHost = context.getEnvironment().getProperty("spring.redis.host");
        return StringUtils.isNotBlank(redisHost);
    }
}
